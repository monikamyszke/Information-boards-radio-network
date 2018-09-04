import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import org.apache.commons.io.IOUtils;

public class Main {
	
	static AppWindow frame;
	static byte[] bytesArray;
	
	public static void main(String[] args) throws BluetoothStateException {
		
		LocalDevice localDevice;
		DiscoveryAgent agent;
		DeviceAndServiceDiscovery discoverer;
			
		//utworzenie okna aplikacji
		try {
			frame = new AppWindow();
			frame.setVisible(true);
			frame.setResizable(true);
			frame.setSize(1000, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		discoverer = new DeviceAndServiceDiscovery();
		localDevice = LocalDevice.getLocalDevice(); // lokalny adapter Bluetooth
		agent = localDevice.getDiscoveryAgent();
		
		frame.getSearchingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			Thread searcher = new Thread(new SearchingThread(discoverer, agent, frame));
			searcher.start();
			}
		});
			
		frame.getSendingButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					InputStream is = null;
					FileOutputStream fos;
					File fileToSend;
					//próbne przetwarzanie pliku - zapisanie kopii na pulpicie przed wys³aniem
					try {
						fileToSend = new File("C:\\Users\\MonikaM\\Desktop\\owczarki.jpg");
						is = new FileInputStream(fileToSend);
						bytesArray = IOUtils.toByteArray(is);
						fos = new FileOutputStream("C:\\Users\\MonikaM\\Desktop\\owczarki-2.jpg");
						fos.write(bytesArray);
						is.close();
						fos.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					
					//otwarcie po³¹czenie i zapisanie tablicy bajtów do strumienia wyjœciowego
					StreamConnection con = (StreamConnection) Connector.open(discoverer.url);
					OutputStream os = con.openOutputStream();
					os.write(bytesArray);
					os.close();
					con.close();
					
					
							
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});		
		
		}
	
}
