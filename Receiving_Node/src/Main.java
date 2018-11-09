import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		
		Bluetooth bluetooth = new Bluetooth();

		// zmiana trybu widoczno�ci urz�dzenia tak, aby by�o ono mo�liwe do wykrycia przez ca�y czas dzia�ania aplikacji
		String[] discoverableCommand = new String[] {"hciconfig", "hci0", "piscan"};
		try {
			new ProcessBuilder(discoverableCommand).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// wy��czenie widoczno�ci urz�dzenia w momencie zako�czenie dzia�ania programu
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
