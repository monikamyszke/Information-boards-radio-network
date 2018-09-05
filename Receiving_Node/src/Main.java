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
			StreamConnectionNotifier notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( )); //obiekt "nas³uchuj¹cy" po³¹czenia przychodz¹cego do serwera
			StreamConnection conn = (StreamConnection)notifier.acceptAndOpen(); //akceptacja i ustanowienie po stronie serwera po³¹czenia przychodz¹cego od klienta
			InputStream is = conn.openInputStream(); //otwarcie strumienia wejœciowego danych
			FileOutputStream os = new FileOutputStream("/home/pi/Desktop/owczarki.jpg");
			
			bytesArray = IOUtils.toByteArray(is); //zapisanie danych ze strumienia do tablicy bajtów
			os.write(bytesArray); //zapisanie bajtów do strumienia wyjœciowego -> w efekcie do pliku
			os.close();
			is.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
