import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import org.apache.commons.io.IOUtils;

import com.intel.bluetooth.RemoteDeviceHelper;

public class Bluetooth implements DiscoveryListener {
	
	LocalDevice localDevice;
	DiscoveryAgent agent;
	ArrayList<DiscoveredDevice> discoveredDevices; //tablica przechowuj�ca wykryte urz�dzenia
	boolean allDiscovered;
	
	byte[] bytesArray;
	byte[] frame;
	byte[] ack;
	
	Date date;
	DateFormat dateFormat;
    String time1;
	String time2;
	
	FileWriter fileWriter;
	int counter;

	StreamConnectionNotifier notifier;
	
	public Bluetooth() {
		try {
			this.localDevice = LocalDevice.getLocalDevice(); //lokalny adapter Bluetooth
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} 
		this.agent = localDevice.getDiscoveryAgent();
		this.discoveredDevices = new ArrayList<DiscoveredDevice>();
		this.allDiscovered = false;
		this.dateFormat = new SimpleDateFormat("HH:mm:ss");
		try {
			this.fileWriter = new FileWriter("testy_PC.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.counter = 1;
	}
	
	//funkcja wywo�ywana w chwili wykrycia urz�dzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true); //zapytanie urz�dzenia o nazw� ('true' - zawsze pytaj)
		} catch(IOException e) {
			System.out.println("B��d odczytu nazwy urz�dzenia");
		}
		System.out.println("Wykryto urz�dzenie. Adres: " + address + " Nazwa: " + name);
		
		//ograniczenie dalszych dzia�a� do adres�w MAC Raspberry Pi
		//sprawdzenie, czy tablica jest w zasi�gu
		if(address.startsWith("B827EB")) {
			boolean isNearby = sendPing(address);
			if(isNearby == true) {
				discoveredDevices.add(new DiscoveredDevice(remoteDevice, name));
				System.out.println("Nawi�zano po��czenie");
				synchronized(this) { //synchronizacja z w�tkiem GUISearchingThread
					try {
						this.notifyAll();
					} catch(Exception e) {};
				}	
			}
			else {
				System.out.println("Tablica poza zasi�giem");
			}				
		}
	}
		
	//funkcja wywo�ywana w chwili zako�czenia wykrywania urz�dze�
	@Override
	public void inquiryCompleted(int status) {
		System.out.println("Wyszukiwanie urz�dze� zako�czone.");
		synchronized(this) {
			try {
				allDiscovered = true;
				this.notifyAll();
			} catch(Exception e) {};
		}
	}

	//funkcja wywo�ywana w chwili wykrycia serwisu pasuj�cego do danego UUID (tu - UUID Serial Port Profile, czyli 0x1101)
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for(int i=0; i<serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100); //pobranie nazwy serwisu - warto�ci atrybutu o ID 0x0100
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if(serviceName != null) 
				System.out.println((String)serviceName.getValue());
			else
				System.out.println("Nieznana us�uga");
				
			System.out.println(connectionURL);
		}
	}

	//funkcja wywo�ywana w chwili zako�czenia wykrywania serwis�w
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {
//		if(responseCode == SERVICE_SEARCH_DEVICE_NOT_REACHABLE) {
//			System.out.println("Urz�dzenie poza zasi�giem");
//		}
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}
	
	public void pairWithDevice(int deviceNumber) throws IOException {
		RemoteDevice remoteDevice = discoveredDevices.get(deviceNumber).getRemoteDevice();
		if(remoteDevice.isTrustedDevice()) {
			System.out.println("Urz�dzenia s� ju� sparowane");
		}
		else {
			String PIN = "00000";
			try {
				RemoteDeviceHelper.authenticate(remoteDevice, PIN);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean sendPing(String address) {
		StreamConnection conn;
		String urlAddress = "btspp://" + address + ":1";
			try {
				conn = (StreamConnection) Connector.open(urlAddress);
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.close();
				return true;
			} catch (IOException e1) {
				return false;
			}
	}
	
	public void sendFile(File fileToSend, int deviceNumber) {
		try {
			//czas WYS�ANIA PLIKU
			date = new Date();
	        time1 = dateFormat.format(date);
			System.out.println(time1);
			FileInputStream is = new FileInputStream(fileToSend);
			bytesArray = IOUtils.toByteArray(is); //zapisanie pliku wej�ciowego do tablicy bajt�w
			is.close();
			buildFrame(fileToSend);
//			saveBytesToFile();
			startBluetoothConnection(deviceNumber);
			waitForResponse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//funkcja tworz�ca ramk� - liczba bajt�w do wys�ania + liczba bajt�w nazwy pliku + nazwa pliku + w�a�ciwe dane
		public void buildFrame(File fileToSend) {
			String fileName = new String(fileToSend.getName());
			byte[] fileNameBytes = null;
			int numberOfBytes; //liczba bajt�w, na kt�r� sk�ada si� 4 + 1 + liczba bajt�w nazwy pliku + liczba bajt�w danych
			try {
				fileNameBytes = fileName.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			numberOfBytes = 4 + 1 + fileNameBytes.length + bytesArray.length;
			frame = new byte[numberOfBytes]; //ramka zawiera na pocz�tku 4-bajtowe pole przeznaczone do zapisu liczby bajt�w do wys�ania (maksymalnie 2^32 bajt�w)
			byte[] numberOfBytesArray = ByteBuffer.allocate(4).putInt(numberOfBytes).array();
			//uzupe�nienie odpowiednich p�l ramki
			System.arraycopy(numberOfBytesArray, 0, frame, 0, 4);
			frame[4] = (byte) fileNameBytes.length;
			System.arraycopy(fileNameBytes, 0, frame, 5, fileNameBytes.length);
			System.arraycopy(bytesArray, 0, frame, fileNameBytes.length + 5, bytesArray.length);
		}
	
	//funkcja pomocnicza - zapisanie ramki w postaci heksalnej do pliku tekstowego
	public void saveBytesToFile() {
		PrintWriter bytesWriter;
		try {
			File bytesTxtFile = new File("C:\\Users\\MonikaM\\Desktop\\sentBytes.txt");
			bytesWriter = new PrintWriter(bytesTxtFile);
			for(byte b : frame) {
				bytesWriter.print(String.format("0x%02X ", b));
			}
			bytesWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startBluetoothConnection(int deviceNumber) {
		//otwarcie po��czenie i zapisanie tablicy bajt�w do strumienia wyj�ciowego
		StreamConnection conn;
		try {
			String urlAddress = "btspp://" + discoveredDevices.get(deviceNumber).getRemoteDevice() + ":1";
			conn = (StreamConnection) Connector.open(urlAddress);
			System.out.println("Otwarto po��czenie");
			OutputStream os = conn.openOutputStream();
			os.write(frame);
			os.close();
			try {
			TimeUnit.MILLISECONDS.sleep(100);
			} catch (Exception e) {
			e.printStackTrace();
			}
			conn.close();
			System.out.println("Wys�ano " + frame.length + " B danych");
			System.out.println("Zamkni�to po��czenie");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void waitForResponse() {
		try {
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( ));			
			System.out.println("Czekam na odpowiedz");
			StreamConnection conn;
			conn = (StreamConnection)notifier.acceptAndOpen();
			getResponse(conn);
			conn.close();
			notifier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getResponse(StreamConnection conn) {
		InputStream is;
		try {
			is = conn.openInputStream();
			ack = IOUtils.toByteArray(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//czas OTRZYMANIA ODPOWIEDZI
		date = new Date();
        String time2 = dateFormat.format(date);
		System.out.println(time2);
		
		if(ack[0] == 1) {
			System.out.println("Transmisja danych przebieg�a pomy�lnie");
		}
		else {
			System.out.println("Podczas transmisji wyst�pi� b��d");
		}
		
		//zapis czas�w do pliku
		try {
			fileWriter.append(time1);
			fileWriter.append(";");
			fileWriter.append(time2);
			fileWriter.append("\n");
			fileWriter.flush();
			counter = counter + 1;
			if(counter == 100) {
				fileWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
}
