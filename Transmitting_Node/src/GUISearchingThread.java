/*W¹tek stworzony w celu wyœwietlania urz¹dzeñ podczas ich wyszukiwania w oknie aplikacji
 * - nie powoduje blokowania GUI*/

public class GUISearchingThread implements Runnable {
	
	private Bluetooth bluetooth;
	private AppWindow frame;
	
	public GUISearchingThread (Bluetooth bluetooth, AppWindow frame) {
		this.bluetooth = bluetooth;
		this.frame = frame;
	}

	public void run() {
		synchronized(bluetooth) { // synchronizacja w¹tków
			try {
				frame.clearLabel();
				frame.clearListOfDevices();
				int i = 0;
				while (bluetooth.allDiscovered == false) {
					bluetooth.wait(); // czekanie na powiadomienie o wykryciu urz¹dzenia z metody deviceDiscovered()
					if (bluetooth.allDiscovered == true) {
						break;
					}
					frame.setLabel("Wykryto urz¹dzenie:          Adres MAC: " + bluetooth.discoveredDevices.get(i).getRemoteDevice()+  "     Nazwa: " + bluetooth.discoveredDevices.get(i).getName());
					frame.setListOfDevices(bluetooth.discoveredDevices.get(i).getName()); // dodanie urz¹dzenia do listy w GUI
					i++;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		frame.setLabel(" \n\n Wyszukiwanie urz¹dzeñ zakoñczone.");		
	}
}
