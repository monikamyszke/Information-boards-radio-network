import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;

public class Main {
	
	static AppWindow frame;
	static File fileToSend;
	
	public static void main(String[] args) throws BluetoothStateException {
		
		//utworzenie okna aplikacji
		try {
			frame = new AppWindow();
			frame.setVisible(true);
			frame.setResizable(false);
			frame.setSize(1000, 600);
			frame.getSendingButton().setEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Bluetooth bluetooth = new Bluetooth(frame);
	 
		frame.getSearchingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				frame.getSendingButton().setEnabled(false);
				try {
					bluetooth.discoveredDevices.clear();
					bluetooth.allDiscovered = false;
					System.out.println("Device discovery");
					bluetooth.agent.startInquiry(DiscoveryAgent.GIAC, bluetooth); // rozpoczêcie wyszukiwania urz¹dzeñ
					Thread searcher = new Thread(new GUISearchingThread(bluetooth, frame)); // uruchomienie w¹tku odœwie¿aj¹cego informacji w GUI
					searcher.start();
					
				} catch (BluetoothStateException e) {
					System.out.println(e.toString());
				}
			}
		});	
		
		frame.getFileChooserButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				JFileChooser fileChooser = new JFileChooser();
				String filename;
				String fileExtension;
				fileChooser.setDialogTitle("Wybierz plik z dysku");
				
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend = fileChooser.getSelectedFile();
					filename = fileToSend.getName();
					fileExtension = FilenameUtils.getExtension(filename);
					
					if(!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png") || fileExtension.equals("gif") || fileExtension.equals("bmp") || fileExtension.equals("pdf") || fileExtension.equals("txt") || fileExtension.equals("html"))) {
						JOptionPane.showMessageDialog(frame, "Wybrany plik posiada rozszerzenie, które uniemo¿liwia wyœwietlenie go na tablicy informacyjnej!", "Komunikat", JOptionPane.WARNING_MESSAGE);
					}
						
					frame.getFileChooserButton().setText(filename);
					frame.setFileSize(fileToSend.length());
				}
			}
		});
			
		frame.getSendingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				int deviceNumber = frame.getDeviceNumber(); // pozycja urz¹dzenia na liœcie, z którym ma byæ nawi¹zane po³¹czenie
				bluetooth.sendFile(fileToSend, deviceNumber);
			}
		});	
	}	
}
