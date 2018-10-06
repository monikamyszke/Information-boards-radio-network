import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Bluetooth {
	
	StreamConnection conn;
	File receivedFile;
	String fileName;
	byte[] fileNameBytes;
	byte[] bytesArray;
	byte[] frame;
	
	public void listenForConnection() {
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
		try {
			is = conn.openInputStream(); //otwarcie strumienia wejœciowego danych
			frame = IOUtils.toByteArray(is); //zapisanie danych ze strumienia do tablicy bajtów
			is.close();
			saveBytesToFile();
			decodeFrame();
			saveFile();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void decodeFrame() {
		int fileNameLength = frame[0];
		fileNameBytes = new byte[fileNameLength];
		bytesArray = new byte[frame.length - fileNameLength - 1];
		System.arraycopy(frame, 1, fileNameBytes, 0, fileNameLength);
		try {
			fileName = new String(fileNameBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(frame, fileNameLength + 1, bytesArray, 0, bytesArray.length);
		System.out.println(fileName);
	}
	
	public void saveFile() {
		FileOutputStream os;
		try {
			os = new FileOutputStream("/home/pi/Desktop/" + fileName);
			os.write(bytesArray); //zapisanie bajtów do strumienia wyjœciowego -> w efekcie do pliku
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//funkcja pomocnicza - zapisanie ramki w postaci heksalnej do pliku tekstowego
	public void saveBytesToFile() {
		PrintWriter bytesWriter;
		try {
			File bytesTxtFile = new File("/home/pi/Desktop/receivedBytes.txt");
			bytesWriter = new PrintWriter(bytesTxtFile);
			for(byte b : bytesArray) {
				bytesWriter.print(String.format("0x%02X ", b));
			}
			bytesWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
