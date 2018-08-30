import java.io.IOException;
import java.util.ArrayList;
import javax.bluetooth.*;

public class DeviceAndServiceDiscovery implements DiscoveryListener{
	
	ArrayList<RemoteDevice> discoveredDevices = new ArrayList<>(); //lista przechowująca wykryte urządzenia (adresy)
	ArrayList<String> friendlyNames = new ArrayList<>(); //lista przechowująca nazwy wykrytych urządzeń
	public boolean allDiscovered = false;
	String url = "btspp://B827EB0A77A7:1";

	//funkcja wywoływana w chwili wykrycia urządzenia
	@Override
	public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String name = null;
		try {
			name = remoteDevice.getFriendlyName(true);
		} catch(IOException e) {
			System.out.println("Błąd odczytu nazwy urządzenia");
		}
		discoveredDevices.add(remoteDevice);
		friendlyNames.add(name);
		System.out.println("Wykryto urządzenie. Adres: " + address + " Nazwa: " + name);
		
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};
		}
	}

	//funkcja wywoływana w chwili zakończenia wykrywania urządzeń
	@Override
	public void inquiryCompleted(int status) {
		System.out.println("Wyszukiwanie urządzeń zakończone.");
		synchronized(this) {
			try {
				this.notifyAll();
				allDiscovered = true;
			} catch(Exception e) {};
		}
	}
	
	//funkcja wywoływana w chwili wykrycia serwisu pasującego do danego UUID
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for(int i=0; i<serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100);
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if(serviceName != null) 
				System.out.println((String)serviceName.getValue());
			else
				System.out.println("Nieznana usługa");
			
			System.out.println(connectionURL);
		}
	}

	// funkcja wywoływana w chwili zakończenia wykrywania serwisów
	@Override
	public void serviceSearchCompleted(int transID, int responseCode) {	
		synchronized(this) {
			try {
				this.notifyAll();
			} catch(Exception e) {};	
		}	
	}

}
