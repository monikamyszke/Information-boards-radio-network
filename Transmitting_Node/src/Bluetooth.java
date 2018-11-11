import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import org.apache.commons.io.IOUtils;

public class Bluetooth implements DiscoveryListener {
	
	private LocalDevice localDevice;
	public DiscoveryAgent agent;
	public ArrayList<DiscoveredDevice> discoveredDevices; // tablica przechowuj¹ca wykryte urz¹dzenia
	public boolean allDiscovered;

	private ResponseListener responseListener;
	
	public Bluetooth() {
		try {
			this.localDevice = LocalDevice.getLocalDevice(); // lokalny adapter Bluetooth
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} 
		this.agent = localDevice.getDiscoveryAgent();
		this.discoveredDevices = new ArrayList<DiscoveredDevice>();
		this.allDiscovered = false;
		
		// uruchamiamy w¹tek, który nas³uchuje odpowiedzi od wêz³ów podrzêdnych
		responseListener = new ResponseListener();
		new Thread(responseListener).start();
	}
	
	// funkcja wywo³ywana w chwili wykrycia urz¹dzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true); // zapytanie urz¹dzenia o nazwê ('true' - zawsze pytaj)
		} catch (IOException e) {
			System.out.println("B³¹d odczytu nazwy urz¹dzenia");
		}
		System.out.println("Wykryto urz¹dzenie. Adres: " + address + " Nazwa: " + name);
		
		// ograniczenie dalszych dzia³añ do adresów MAC Raspberry Pi
		// sprawdzenie, czy tablica jest w zasiêgu
//		if (address.startsWith("B827EB")) {
//			boolean isNearby = sendPing(address);
//			if (isNearby == true) {
				discoveredDevices.add(new DiscoveredDevice(remoteDevice, name));
//				System.out.println("Nawi¹zano po³¹czenie");
				synchronized(this) { //synchronizacja z w¹tkiem GUISearchingThread
					try {
						this.notifyAll();
					} catch(Exception e) {
						e.printStackTrace();
					};
				}	
//			} else {
//				System.out.println("Tablica poza zasiêgiem");
//			}				
//		}
	}
		
	// funkcja wywo³ywana w chwili zakoñczenia wykrywania urz¹dzeñ
	@Override
	public void inquiryCompleted(int status) {
		System.out.println("Wyszukiwanie urz¹dzeñ zakoñczone.");
		synchronized(this) {
			try {
				allDiscovered = true;
				this.notifyAll();
			} catch(Exception e) {
				e.printStackTrace();
			};
		}
	}

	// funkcja nieu¿ywana
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {

	}

	// funkcja nieu¿ywana
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {
	
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
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
	}
	
	public void sendFile(File fileToSend, int deviceNumber) {
		byte[] frame;
		
		frame = buildFrame(fileToSend);
		startBluetoothConnection(deviceNumber, frame);
		System.out.println("Czekam na odpowiedz");
		waitForResponse(frame.length);
	}
	
	// funkcja tworz¹ca ramkê - liczba bajtów do wys³ania + liczba bajtów nazwy pliku + nazwa pliku + w³aœciwe dane
	public byte[] buildFrame(File fileToSend) {
		byte[] frame;
		byte[] numberOfBytesArray;
		byte[] filenameBytes = null;
		byte[] bytesArray = null;
		
		int numberOfBytes; // liczba bajtów, na któr¹ sk³ada siê 4 + 1 + liczba bajtów nazwy pliku + liczba bajtów danych
		String filename;
		
		try {
			FileInputStream is = new FileInputStream(fileToSend);
			bytesArray = IOUtils.toByteArray(is); // zapisanie pliku wejœciowego do tablicy bajtów
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Budujê ramkê");
		filename = new String(fileToSend.getName());

		try {
			filenameBytes = filename.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		numberOfBytes = 4 + 1 + filenameBytes.length + bytesArray.length;
		frame = new byte[numberOfBytes]; // ramka zawiera na pocz¹tku 4-bajtowe pole przeznaczone do zapisu liczby bajtów do wys³ania (maksymalnie 2^32 bajtów)
		numberOfBytesArray = ByteBuffer.allocate(4).putInt(numberOfBytes).array();
		// uzupe³nienie odpowiednich pól ramki
		System.arraycopy(numberOfBytesArray, 0, frame, 0, 4);
		frame[4] = (byte) filenameBytes.length;
		System.arraycopy(filenameBytes, 0, frame, 5, filenameBytes.length);
		System.arraycopy(bytesArray, 0, frame, filenameBytes.length + 5, bytesArray.length);
		
		return frame;
	}

	public void startBluetoothConnection(int deviceNumber, byte[] frame) {
		// otwarcie po³¹czenie i zapisanie tablicy bajtów do strumienia wyjœciowego
		StreamConnection conn;
		OutputStream os;
		String urlAddress;
		
		try {
			urlAddress = "btspp://" + discoveredDevices.get(deviceNumber).getRemoteDevice() + ":1";
			conn = (StreamConnection) Connector.open(urlAddress);
			System.out.println("Otwarto po³¹czenie");
			os = conn.openOutputStream();
			os.write(frame);
			os.close();
			
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
			System.out.println("Wys³ano " + frame.length + " B danych");
			System.out.println("Zamkniêto po³¹czenie");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void waitForResponse(int frameLength) {
		int timeout = (int) Math.ceil((0.0117*(frameLength/1024) + 3.3223));
		System.out.println(timeout);

		while (timeout != 0 && responseListener.checkIfWasResponse() == false) {
			try {
				System.out.println("Czekam...");
				TimeUnit.SECONDS.sleep(1);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			timeout --;
		}

		if (responseListener.checkIfWasResponse() == true) {
			byte [] response = responseListener.getResponse();
			checkResponse(response);
		} else {
			System.out.println("Brak odpowiedzi, czas min¹³...");
		}
	}
	
	public void checkResponse(byte[] ack) {
		if (ack[0] == 1) {
			System.out.println("Transmisja danych przebieg³a pomyœlnie");
		} else {
			System.out.println("Podczas transmisji wyst¹pi³ b³¹d");
		}
	}
		
}
