import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Main {
	
	static AppWindow frame;
	
	public static void main(String[] args) {
		
		try {
			frame = new AppWindow();
			frame.setVisible(true);
			frame.setResizable(true);
			frame.setSize(1000, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
//odbieranie danych ze strumienia
		try {
			StreamConnectionNotifier notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( ));
			StreamConnection conn = (StreamConnection)notifier.acceptAndOpen();
			InputStream is = conn.openInputStream();
			
			byte buffer[] = new byte[80];
			int bytes_read = is.read(buffer);
			String received = new String(buffer, 0, bytes_read);
			System.console().writer().println("bytes: " + bytes_read + " received: " + received);
			frame.setMessage(received);
			conn.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
