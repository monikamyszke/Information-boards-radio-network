/*W¹tek stworzony w celu wyœwietlania urz¹dzeñ podczas ich wyszukiwania w oknie aplikacji
 * - nie powoduje blokowania GUI*/

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

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
		synchronized(bluetooth) { //synchronizacja w¹tków
			try {
				int i = 0;
				while (bluetooth.allDiscovered == false) {
					bluetooth.wait(); //czekanie na powiadomienie o wykryciu urz¹dzenia z metody deviceDiscovered()
					frame.setLabel("Wykryto urz¹dzenie:          Adres MAC: " + bluetooth.discoveredDevices.get(i) +  "     Nazwa: " + bluetooth.friendlyNames.get(i));
					frame.setListOfDevices(bluetooth.friendlyNames.get(i));
					i ++;
				}
			} catch(Exception e) {}
		}
		
		frame.setLabel(" \n\n Wyszukiwanie urz¹dzeñ zakoñczone.");
		
		//wyszukiwanie serwisów (nie jest to konieczne)
		UUID[] uuidSet = new UUID[1];
		uuidSet[0] = new UUID(0x1101); // UUID SPP
		int[] attrIdSet = new int[] {0x0100}; // atrybut - Service Name ID
		System.out.println("Service discovery");
		
		for(int i=0; i<bluetooth.discoveredDevices.size(); i++) {
			RemoteDevice remoteDevice = bluetooth.discoveredDevices.get(i);
			try {
				bluetooth.agent.searchServices(attrIdSet, uuidSet, remoteDevice, bluetooth);
				synchronized(bluetooth) { 
					try {
						bluetooth.wait(); 
					} catch(Exception e) {}
				}
			} catch (BluetoothStateException e1) {
				e1.printStackTrace();
			}
		}		
	}
}
