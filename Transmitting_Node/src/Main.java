import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

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
		localDevice = LocalDevice.getLocalDevice(); //lokalny adapter Bluetooth
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
					FileOutputStream fileOS;
					FileOutputStream textFileOS = null;
					PrintWriter saveBytes;
					File fileToSend;
					//próbne przetwarzanie pliku - zapisanie kopii obrazu na pulpicie
					//oraz zapisanie bajtów w postaci heksalnej do pliku txt
					try {
						fileToSend = new File("C:\\Users\\MonikaM\\Desktop\\colors.png");
						is = new FileInputStream(fileToSend);
						bytesArray = IOUtils.toByteArray(is); //zapisanie pliku wejœciowego do tablicy bajtów
						fileOS = new FileOutputStream("C:\\Users\\MonikaM\\Desktop\\colors-2.png");
						fileOS.write(bytesArray); //zapisanie bajtów do strumienia wyjœciowego -> w efekcie do pliku
						 
						textFileOS = new FileOutputStream("C:\\Users\\MonikaM\\Desktop\\colors.txt"); //strumieñ do zapisu bajtów w postaci tekstowej
						saveBytes = new PrintWriter(textFileOS);
						for(byte b : bytesArray) {
							saveBytes.print(String.format("0x%02X ", b)); //zapis bajtów w postaci hekslanej do strumienia
						}
						is.close();
						fileOS.close();
						textFileOS.close();
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
