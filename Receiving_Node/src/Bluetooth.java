import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Bluetooth {
	
	StreamConnectionNotifier notifier;
	StreamConnection conn;
	File receivedFile;
	String fileName;
	byte[] fileNameBytes;
	byte[] bytesArray;
	byte[] frame;
	String responseAddress;
	byte[] ack;
	
	public void run() {
		try {
			System.out.println("Nas�uchuj�");
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( ));//obiekt oczekuj�cy po��czenia przychodz�cego do serwera, reprezentuje nas�uchuj�ce gniazdo
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			listenForConnection();
		}
	}
	
	public void listenForConnection() {
		try {
			conn = (StreamConnection)notifier.acceptAndOpen(); //akceptacja i ustanowienie po stronie serwera po��czenia przychodz�cego od klienta
			System.out.println("Otwarto po��czenie");
			receiveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void receiveData() {
		InputStream is;
		try {
			is = conn.openInputStream(); //otwarcie strumienia wej�ciowego danych
			frame = IOUtils.toByteArray(is); //zapisanie danych ze strumienia do tablicy bajt�w
			is.close();
			if(frame.length == 0) {
				System.out.println("It's a ping!");
				conn.close();
			}
			else {
				System.out.println("Odebrano plik");
				responseAddress = "btspp://40E230F23BCE:1"; //wpisany na sta�e adres mojego komputera
//				saveBytesToFile();
				decodeFrame();
				saveFile();
				conn.close();
				System.out.println("Zako�czono po��czenie");		
				sendResponse(responseAddress);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void decodeFrame() {
		byte[] numberOfBytesArray = new byte[4];
		System.arraycopy(frame, 0, numberOfBytesArray, 0, 4);
		int numberOfBytes = ByteBuffer.wrap(numberOfBytesArray).getInt();
		
		int fileNameLength = frame[4];
		fileNameBytes = new byte[fileNameLength];
		bytesArray = new byte[frame.length - fileNameLength - 5];
		System.arraycopy(frame, 5, fileNameBytes, 0, fileNameLength);
		try {
			fileName = new String(fileNameBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(frame, fileNameLength + 5, bytesArray, 0, bytesArray.length);
		System.out.println("Zdekodowano ramk�. Liczba bajt�w ramki: " + (numberOfBytes));
		ack = new byte[1];
		if(frame.length == numberOfBytes) {
			System.out.println("Dane odebrano poprawnie");
			ack[0] = 1;
		}
		else {
			System.out.println("Podczas po��czenia wyst�pi� b��d");
			ack[0] = 0;
		}
	}
	
	public void saveFile() {
		FileOutputStream os;
		try {
			os = new FileOutputStream("/home/pi/Desktop/" + fileName);
			os.write(bytesArray); //zapisanie bajt�w do strumienia wyj�ciowego -> w efekcie do pliku
			os.close();
			System.out.println("Zapisano plik");
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
			for(byte b : frame) {
				bytesWriter.print(String.format("0x%02X ", b));
			}
			bytesWriter.close();
			System.out.println("Zapisano bajty do pliku tekstowego");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(String address) {
		
		StreamConnection conn;
			try {
				conn = (StreamConnection) Connector.open(address);
				OutputStream os = conn.openOutputStream();
				os.write(ack);
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				os.close();
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn.close();
				System.out.println("Wys�ano odpowiedz");
			} catch (IOException e1) {
			}
	}


}
