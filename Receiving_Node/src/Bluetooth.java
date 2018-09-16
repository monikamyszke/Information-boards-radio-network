import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Bluetooth {
	
	StreamConnection conn;
	File receivedFile;
	byte[] bytesArray;
	
	public void waitForConnection() {
		try {
			StreamConnectionNotifier notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( )); //obiekt "nas³uchuj¹cy" po³¹czenia przychodz¹cego do serwera
			conn = (StreamConnection)notifier.acceptAndOpen(); //akceptacja i ustanowienie po stronie serwera po³¹czenia przychodz¹cego od klienta
			receiveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void receiveData() {
		InputStream is;
		FileOutputStream os;
		try {
			is = conn.openInputStream(); //otwarcie strumienia wejœciowego danych
			os = new FileOutputStream("/home/pi/Desktop/colors.png");
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
