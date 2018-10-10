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
		
		bluetooth.run();
	}

}
