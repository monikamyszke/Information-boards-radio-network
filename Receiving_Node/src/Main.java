//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;

public class Main {
	
	static AppWindow frame;
	
	public static void main(String[] args) {
		
		Bluetooth bluetooth = new Bluetooth();
		
		//utworzenie okna aplikacji
		try {
			frame = new AppWindow();
			frame.setVisible(true);
			frame.setResizable(true);
			frame.setSize(1000, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		//zablokowanie widocznoœci urz¹dzenia po wyjœciu z okna aplikacji
//		frame.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent we) {
//				try {
//					LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.NOT_DISCOVERABLE);
//				} catch (BluetoothStateException e) {
//					e.printStackTrace();
//				}
//			}
//		});
	
		//zmiana trybu widocznoœci urz¹dzenia tak, aby by³o ono mo¿liwe do wykrycia przez ca³y czas dzia³ania aplikacji
		try {
			int mode = LocalDevice.getLocalDevice().getDiscoverable();
			System.out.println(mode); //tryb po uruchomieniu aplikacji
			LocalDevice.getLocalDevice().setDiscoverable(DiscoveryAgent.GIAC);
			mode = LocalDevice.getLocalDevice().getDiscoverable();
			System.out.println(mode); //tryb po powy¿szej zmianie
		} catch (BluetoothStateException e1) {
			e1.printStackTrace();
		}
		
		bluetooth.run();
	
	}

}
