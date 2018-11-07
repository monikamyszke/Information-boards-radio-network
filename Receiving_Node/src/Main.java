import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		
		Bluetooth bluetooth = new Bluetooth();

		// zmiana trybu widocznoœci urz¹dzenia tak, aby by³o ono mo¿liwe do wykrycia przez ca³y czas dzia³ania aplikacji
		// TODO: sprawdziæ czy sudo jest konieczne
		String[] discoverableCommand = new String[] {"sudo", "hciconfig", "hci0", "piscan"};
		try {
			new ProcessBuilder(discoverableCommand).start();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		// wy³¹czenie widocznoœci urz¹dzenia w momencie zakoñczenie dzia³ania programu
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				String[] nonDiscoverableCommand = new String[] {"sudo", "hciconfig", "hci0", "noscan"};
				try {
					new ProcessBuilder(nonDiscoverableCommand).start();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}));
		
		bluetooth.run();

	}
}
