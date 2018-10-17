import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import org.apache.commons.io.IOUtils;

public class Bluetooth implements DiscoveryListener {
	
	LocalDevice localDevice;
	DiscoveryAgent agent;
	ArrayList<DiscoveredDevice> discoveredDevices; //tablica przechowuj¹ca wykryte urz¹dzenia
	boolean allDiscovered;
	
	byte[] bytesArray;
	byte[] frame;
	
	public Bluetooth(){
		try {
			this.localDevice = LocalDevice.getLocalDevice(); //lokalny adapter Bluetooth
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} 
		this.agent = localDevice.getDiscoveryAgent();
		this.discoveredDevices = new ArrayList<DiscoveredDevice>();
		this.allDiscovered = false;
	}
	
	//funkcja wywo³ywana w chwili wykrycia urz¹dzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true); //zapytanie urz¹dzenia o nazwê ('true' - zawsze pytaj)
		} catch(IOException e) {
			System.out.println("B³¹d odczytu nazwy urz¹dzenia");
		}
		discoveredDevices.add(new DiscoveredDevice(remoteDevice, name));
		System.out.println("Wykryto urz¹dzenie. Adres: " + address + " Nazwa: " + name);
			
		synchronized(this) { //synchronizacja z w¹tkiem GUISearchingThread
			try {
				this.notifyAll();
			} catch(Exception e) {};
		}
	}
		
	//funkcja wywo³ywana w chwili zakoñczenia wykrywania urz¹dzeñ
	@Override
	public void inquiryCompleted(int status) {
		System.out.println("Wyszukiwanie urz¹dzeñ zakoñczone.");
		synchronized(this) {
			try {
				this.notifyAll();
				allDiscovered = true;
			} catch(Exception e) {};
		}
	}

	//funkcja wywo³ywana w chwili wykrycia serwisu pasuj¹cego do danego UUID (tu - UUID Serial Port Profile, czyli 0x1101)
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for(int i=0; i<serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100); //pobranie nazwy serwisu - wartoœci atrybutu o ID 0x0100
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if(serviceName != null) 
				System.out.println((String)serviceName.getValue());
			else
				System.out.println("Nieznana us³uga");
				
			System.out.println(connectionURL);
		}
	}

	//funkcja wywo³ywana w chwili zakoñczenia wykrywania serwisów
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {	
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}
	
	public void sendFile(File fileToSend, int deviceNumber) {
		try {
			FileInputStream is = new FileInputStream(fileToSend);
			bytesArray = IOUtils.toByteArray(is); //zapisanie pliku wejœciowego do tablicy bajtów
			is.close();
			buildFrame(fileToSend);
//			saveBytesToFile();
			startBluetoothConnection(deviceNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//funkcja tworz¹ca ramkê - iloœæ bajtów nazwy pliku + nazwa pliku + w³aœciwe dane
		public void buildFrame(File fileToSend) {
			String fileName = new String(fileToSend.getName());
			byte[] fileNameBytes = null;
			try {
				fileNameBytes = fileName.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			frame = new byte[1 + fileNameBytes.length + bytesArray.length];
			frame[0] = (byte) fileNameBytes.length;
			System.arraycopy(fileNameBytes, 0, frame, 1, fileNameBytes.length);
			System.arraycopy(bytesArray, 0, frame, fileNameBytes.length + 1, bytesArray.length);
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
		//otwarcie po³¹czenie i zapisanie tablicy bajtów do strumienia wyjœciowego
		StreamConnection conn;
		try {
			String urlAddress = "btspp://" + discoveredDevices.get(deviceNumber).getRemoteDevice() + ":1";
			conn = (StreamConnection) Connector.open(urlAddress);
			OutputStream os = conn.openOutputStream();
			os.write(frame);
			os.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
		
}
