import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.swing.JFileChooser;

public class Main {
	
	static AppWindow frame;
	static File fileToSend;
	
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
			public void actionPerformed(ActionEvent a) {
				try {
					bluetooth.allDiscovered = false;
					System.out.println("Device discovery");
					bluetooth.agent.startInquiry(DiscoveryAgent.GIAC, bluetooth); // rozpoczêcie wyszukiwania urz¹dzeñ
					Thread searcher = new Thread(new GUISearchingThread(bluetooth, frame));
					searcher.start();
				} catch (BluetoothStateException e) {
					System.out.println(e.toString());
				}
			}
		});	
		
		frame.getFileChooserButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				JFileChooser fileChooser = new JFileChooser();
				String filename;
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend = fileChooser.getSelectedFile();
					filename = fileToSend.getName();
					frame.getFileChooserButton().setText(filename);
				}
			}
		});
			
		frame.getSendingButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				int deviceNumber = frame.getDeviceNumber(); // pozycja urz¹dzenia na liœcie, z którym ma byæ nawi¹zane po³¹czenie
				bluetooth.sendFile(fileToSend, deviceNumber);
			}
		});	
	}	
}
