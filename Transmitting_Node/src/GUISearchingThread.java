import javax.swing.JOptionPane;

/*W�tek stworzony w celu wy�wietlania urz�dze� podczas ich wyszukiwania w oknie aplikacji
 * - nie powoduje blokowania GUI*/

public class GUISearchingThread implements Runnable {
	
	private Bluetooth bluetooth;
	private AppWindow frame;
	
	public GUISearchingThread (Bluetooth bluetooth, AppWindow frame) {
		this.bluetooth = bluetooth;
		this.frame = frame;
	}

	@Override
	public void run() {
		
		synchronized(bluetooth) { // synchronizacja w�tk�w
			try {
				frame.clearLabel();
				frame.clearListOfDevices();
				
				Thread infoThread = new Thread(() -> {
					JOptionPane.showMessageDialog(frame, "Rozpocz�to wyszukiwanie urz�dze�", "Komunikat", JOptionPane.INFORMATION_MESSAGE);
				});
				
				infoThread.start();
				
				int i = 0;
				while (bluetooth.allDiscovered == false) {
					bluetooth.wait(); // czekanie na powiadomienie o wykryciu urz�dzenia z metody deviceDiscovered()
					if (bluetooth.allDiscovered == true) {
						break;
					}
				frame.setLabel(bluetooth.discoveredDevices.get(i).getRemoteDevice(), bluetooth.discoveredDevices.get(i).getName());
				frame.setListOfDevices(bluetooth.discoveredDevices.get(i).getName()); // dodanie urz�dzenia do listy w GUI
					i++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		JOptionPane.showMessageDialog(frame, "Zako�czono wyszukiwanie urz�dze�", "Komunikat", JOptionPane.INFORMATION_MESSAGE);	
		frame.getSendingButton().setEnabled(true);
	}
}
