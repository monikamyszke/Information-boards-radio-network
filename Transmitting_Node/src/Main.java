import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.bluetooth.BluetoothStateException;

public class Main {
	
	static AppWindow frame;
	static byte[] bytesArray;
	
	public static void main(String[] args) throws BluetoothStateException {
		
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
			
		frame.getSearchingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
			Thread searcher = new Thread(new SearchingThread(bluetooth, frame));
			searcher.start(); //rozpocz�cie wyszukiwania urz�dze�
			}
		});
			
		frame.getSendingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				File fileToSend = new File("C:\\Users\\MonikaM\\Desktop\\colors.png");
				bluetooth.sendFile(fileToSend);
			}	
		});	
		
		}
	
}
