//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;

import java.io.IOException;
public class Main {
	
	static AppWindow frame;
	
	public static void main(String[] args) {
		
		Bluetooth bluetooth = new Bluetooth();
		
		//utworzenie okna aplikacji
//		try {
//			frame = new AppWindow();
//			frame.setVisible(true);
//			frame.setResizable(true);
//			frame.setSize(1000, 600);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	
		//zmiana trybu widocznoœci urz¹dzenia tak, aby by³o ono mo¿liwe do wykrycia przez ca³y czas dzia³ania aplikacji
		String[] discoverableCommand = new String[] {"sudo", "hciconfig", "hci0", "piscan"};
		try {
			new ProcessBuilder(discoverableCommand).start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//wy³¹czenie widocznoœci urz¹dzenia w momencie zakoñczenie dzia³ania programu
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				String[] nonDiscoverableCommand = new String[] {"sudo", "hciconfig", "hci0", "noscan"};
				try {
					new ProcessBuilder(nonDiscoverableCommand).start();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
			
		})
		);
		
		bluetooth.run();

	}

}
