import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Main {
	
	static AppWindow frame;
	static byte[] bytesArray;
	
	public static void main(String[] args) {
		//utworzenie okna aplikacji
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
			FileOutputStream fos = new FileOutputStream("/home/pi/Desktop/serce.jpg");
			
			bytesArray = IOUtils.toByteArray(is);
			fos.write(bytesArray);
			fos.close();
			is.close();
			conn.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
