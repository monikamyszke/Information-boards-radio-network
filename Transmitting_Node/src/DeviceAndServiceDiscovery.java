import java.io.IOException;
import java.util.ArrayList;
import javax.bluetooth.*;

public class DeviceAndServiceDiscovery implements DiscoveryListener{
	
	ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>(); //lista przechowuj¹ca wykryte urz¹dzenia (adresy)
	ArrayList<String> friendlyNames = new ArrayList<>(); //lista przechowuj¹ca nazwy wykrytych urz¹dzeñ
	public boolean allDiscovered = false;
	//String url = "btspp://B827EB0A77A7:1";

	//funkcja wywo³ywana w chwili wykrycia urz¹dzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true);
		} catch(IOException e) {
			System.out.println("B³¹d odczytu nazwy urz¹dzenia");
		}
		discoveredDevices.add(remoteDevice);
		friendlyNames.add(name);
		System.out.println("Wykryto urz¹dzenie. Adres: " + address + " Nazwa: " + name);
		
		synchronized(this) {
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
	
	//funkcja wywo³ywana w chwili wykrycia serwisu pasuj¹cego do danego UUID
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for(int i=0; i<serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100);
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if(serviceName != null) 
				System.out.println((String)serviceName.getValue());
			else
				System.out.println("Nieznana us³uga");
			
			System.out.println(connectionURL);
		}
	}

	// funkcja wywo³ywana w chwili zakoñczenia wykrywania serwisów
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {	
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}

}
