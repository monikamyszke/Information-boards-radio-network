import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

public class SearchingThread implements Runnable {
	
	DeviceAndServiceDiscovery discoverer;
	DiscoveryAgent agent;
	AppWindow frame;
	
	public SearchingThread (DeviceAndServiceDiscovery discoverer, DiscoveryAgent agent, AppWindow frame)
	{
		this.discoverer = discoverer;
		this.agent = agent;
		this.frame = frame;
	}

	@Override
	public void run() {
		try {
			System.out.println("Device discovery");
			agent.startInquiry(DiscoveryAgent.GIAC, discoverer); //rozpoczêcie wyszukiwania urz¹dzeñ
			
			synchronized(discoverer) { //synchronizacja w¹tków
				try {
					int i = 0;
					while (discoverer.allDiscovered == false) {
						discoverer.wait(); // czekanie na powiadomienie o wykryciu urz¹dzenia
						frame.setLabel("Wykryto urz¹dzenie:          Adres MAC: " + discoverer.discoveredDevices.get(i) +  "     Nazwa: " + discoverer.friendlyNames.get(i)  );
						frame.setListOfDevices(discoverer.friendlyNames.get(i));
						i ++;
					}
				} catch(Exception e) {}
			}
		} catch(BluetoothStateException e) {
			System.out.println(e.toString());
		}
		
		frame.setLabel(" \n\n Wyszukiwanie urz¹dzeñ zakoñczone.");
		
		//wyszukiwanie serwisów (nie jest to konieczne)
		UUID[] uuidSet = new UUID[1];
		uuidSet[0] = new UUID(0x1101); // UUID SPP
		int[] attrIdSet = new int[] {0x0100}; // atrybut - Service Name ID
		System.out.println("Service discovery");
		
		for(int i=0; i<discoverer.discoveredDevices.size(); i++) {
			RemoteDevice remoteDevice = discoverer.discoveredDevices.get(i);
			try {
				agent.searchServices(attrIdSet, uuidSet, remoteDevice, discoverer);
				synchronized(discoverer) { 
					try {
						discoverer.wait(); 
					} catch(Exception e) {}
				}
			} catch (BluetoothStateException e1) {
				e1.printStackTrace();
			}
		}		
	}
}
