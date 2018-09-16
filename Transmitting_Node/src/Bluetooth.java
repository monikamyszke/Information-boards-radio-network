import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import org.apache.commons.io.IOUtils;

public class Bluetooth implements DiscoveryListener {
	
	LocalDevice localDevice;
	DiscoveryAgent agent;
	ArrayList<RemoteDevice> discoveredDevices; //lista przechowuj�ca adresy wykrytych urz�dze�
	ArrayList<String> friendlyNames; //lista przechowuj�ca nazwy wykrytych urz�dze�
	boolean allDiscovered;
	String url = "btspp://B827EB0A77A7:1";
	//p�zniej stworzy� list� z adresami url
	
	byte[] bytesArray;
	
	public Bluetooth(){
		try {
			this.localDevice = LocalDevice.getLocalDevice(); //lokalny adapter Bluetooth
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		} 
		this.agent = localDevice.getDiscoveryAgent();
		this.discoveredDevices = new ArrayList<RemoteDevice>();
		this.friendlyNames = new ArrayList<String>();
		this.allDiscovered = false;
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
		discoveredDevices.add(remoteDevice);
		friendlyNames.add(name);
		System.out.println("Wykryto urz�dzenie. Adres: " + address + " Nazwa: " + name);
			
		synchronized(this) { //synchronizacja z w�tkiem GUISearchingThread
			try {
				this.notifyAll();
			} catch(Exception e) {};
		}
	}
		
	//funkcja wywo�ywana w chwili zako�czenia wykrywania urz�dze�
	@Override
	public void inquiryCompleted(int status) {
		System.out.println("Wyszukiwanie urz�dze� zako�czone.");
		synchronized(this) {
			try {
				this.notifyAll();
				allDiscovered = true;
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
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}
	
	public void sendFile(File fileToSend) {
		try {
			FileInputStream is = new FileInputStream(fileToSend);
			bytesArray = IOUtils.toByteArray(is); //zapisanie pliku wej�ciowego do tablicy bajt�w
			saveBytesToFile(bytesArray);
			is.close();
			startBluetoothConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//funkcja pomocnicza - zapisanie bajt�w w postaci heksalnej do pliku tekstowego
	public void saveBytesToFile(byte[] bytesArray) {
		PrintWriter bytesWriter;
		try {
			File bytesTxtFile = new File("C:\\Users\\MonikaM\\Desktop\\colors.txt");
			bytesWriter = new PrintWriter(bytesTxtFile);
			for(byte b : bytesArray) {
				bytesWriter.print(String.format("0x%02X ", b));
			}
			bytesWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startBluetoothConnection() {
		//otwarcie po��czenie i zapisanie tablicy bajt�w do strumienia wyj�ciowego
		StreamConnection conn;
		try {
			conn = (StreamConnection) Connector.open(url);
			OutputStream os = conn.openOutputStream();
			os.write(bytesArray);
			os.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
		
}
