import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		
		Bluetooth bluetooth = new Bluetooth();

		// zmiana trybu widocznoœci urz¹dzenia tak, aby by³o ono mo¿liwe do wykrycia przez ca³y czas dzia³ania aplikacji
		String[] discoverableCommand = new String[] {"hciconfig", "hci0", "piscan"};
		try {
			new ProcessBuilder(discoverableCommand).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// wy³¹czenie widocznoœci urz¹dzenia w momencie zakoñczenie dzia³ania programu
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				String[] nonDiscoverableCommand = new String[] {"hciconfig", "hci0", "noscan"};
				try {
					new ProcessBuilder(nonDiscoverableCommand).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
		
		bluetooth.run();

	}
}
