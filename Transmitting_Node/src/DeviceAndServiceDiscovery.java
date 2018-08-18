import java.io.IOException;
import java.util.ArrayList;
import javax.bluetooth.*;

public class DeviceAndServiceDiscovery implements DiscoveryListener{
	
	ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>(); //lista przechowuj�ca wykryte urz�dzenia (adresy)
	ArrayList<String> friendlyNames = new ArrayList<>(); //lista przechowuj�ca nazwy wykrytych urz�dze�
	public boolean allDiscovered = false;
	//String url = "btspp://B827EB0A77A7:1";

	//funkcja wywo�ywana w chwili wykrycia urz�dzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true);
		} catch(IOException e) {
			System.out.println("B��d odczytu nazwy urz�dzenia");
		}
		discoveredDevices.add(remoteDevice);
		friendlyNames.add(name);
		System.out.println("Wykryto urz�dzenie. Adres: " + address + " Nazwa: " + name);
		
		synchronized(this) {
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
	
	//funkcja wywo�ywana w chwili wykrycia serwisu pasuj�cego do danego UUID
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for(int i=0; i<serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100);
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if(serviceName != null) 
				System.out.println((String)serviceName.getValue());
			else
				System.out.println("Nieznana us�uga");
			
			System.out.println(connectionURL);
		}
	}

	// funkcja wywo�ywana w chwili zako�czenia wykrywania serwis�w
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {	
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}

}
