import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

public class Main {
	
	static AppWindow frame;

	public static void main(String[] args) throws BluetoothStateException, InterruptedException {
		
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

			public void actionPerformed(ActionEvent arg0) {
				try {
					StreamConnection con = (StreamConnection) Connector.open(discoverer.url);
					OutputStream os = con.openOutputStream();
					String textToSend = frame.getTextToSend();
					os.write(textToSend.getBytes());
					con.close();
							
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			
		});		
		
		
		}
}
