/*W�tek stworzony w celu wy�wietlania urz�dze� podczas ich wyszukiwania w oknie aplikacji
 * - nie powoduje blokowania GUI*/

public class GUISearchingThread implements Runnable {
	
	Bluetooth bluetooth;
	AppWindow frame;
	
	public GUISearchingThread (Bluetooth bluetooth, AppWindow frame)
	{
		this.bluetooth = bluetooth;
		this.frame = frame;
	}

	@Override
	public void run() {
		synchronized(bluetooth) { //synchronizacja w�tk�w
			try {
				frame.clearLabel();
				frame.clearListOfDevices();
				int i = 0;
				while (bluetooth.allDiscovered == false) {
					bluetooth.wait(); //czekanie na powiadomienie o wykryciu urz�dzenia z metody deviceDiscovered()
					frame.setLabel("Wykryto urz�dzenie:          Adres MAC: " + bluetooth.discoveredDevices.get(i).getRemoteDevice()+  "     Nazwa: " + bluetooth.discoveredDevices.get(i).getName());
					frame.setListOfDevices(bluetooth.discoveredDevices.get(i).getName()); //dodanie urz�dzenia do listy w GUI
					i++;
				}
			} catch(Exception e) {}
		}
		frame.setLabel(" \n\n Wyszukiwanie urz�dze� zako�czone.");
		
		//wyszukiwanie serwis�w (nie jest to konieczne)
//		UUID[] uuidSet = new UUID[1];
//		uuidSet[0] = new UUID(0x1101); // UUID SPP
//		int[] attrIdSet = new int[] {0x0100}; // atrybut - Service Name ID
//		System.out.println("Service discovery");
//		
//		for(int i=0; i<bluetooth.discoveredDevices.size(); i++) {
//			RemoteDevice remoteDevice = bluetooth.discoveredDevices.get(i).getRemoteDevice();
//			try {
//				bluetooth.agent.searchServices(attrIdSet, uuidSet, remoteDevice, bluetooth);
//				synchronized(bluetooth) { 
//					try {
//						bluetooth.wait(); 
//					} catch(Exception e) {}
//				}
//			} catch (BluetoothStateException e1) {
//				e1.printStackTrace();
//			}
//		}		
	}
}
