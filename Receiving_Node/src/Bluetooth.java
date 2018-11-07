import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Bluetooth {
	
	private StreamConnectionNotifier notifier;
	private StreamConnection conn;
	private byte[] ack;

	public void run() {
		
		try {
			System.out.println("Nas�uchuj�");
			
			// obiekt oczekuj�cy po��czenia przychodz�cego do serwera, reprezentuje nas�uchuj�ce gniazdo
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID(0x1101).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			listenForConnection();
		}
	}
	
	public void listenForConnection() {
		try {
			String responseAddress;
			// akceptacja i ustanowienie po stronie serwera po��czenia przychodz�cego od klienta
			conn = (StreamConnection)notifier.acceptAndOpen(); 
			System.out.println("Otwarto po��czenie");
			RemoteDevice device = RemoteDevice.getRemoteDevice(conn);
			responseAddress = "btspp://" + device.getBluetoothAddress() + ":6"; 
			
			if (receiveData() == true) {
				sendResponse(responseAddress);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean receiveData() {
		InputStream is;
		byte[] frame;
		boolean isFile = false;
		
		try {
			is = conn.openInputStream(); // otwarcie strumienia wej�ciowego danych
			frame = IOUtils.toByteArray(is); // zapisanie danych ze strumienia do tablicy bajt�w
			is.close();
			conn.close();
			if (frame.length == 0) {
				System.out.println("It's a ping!");
			} else {
				System.out.println("Odebrano plik");
				decodeFrameAndSave(frame);
				isFile = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isFile;
	}
	
	public void decodeFrameAndSave(byte[] frame) {
		String filename = null;
		byte[] filenameBytes;
		byte[] bytesArray;
		byte[] numberOfBytesArray = new byte[4];
		
		System.arraycopy(frame, 0, numberOfBytesArray, 0, 4);
		int numberOfBytes = ByteBuffer.wrap(numberOfBytesArray).getInt();
		int filenameLength = frame[4];
		filenameBytes = new byte[filenameLength];
		bytesArray = new byte[frame.length - filenameLength - 5];
		System.arraycopy(frame, 5, filenameBytes, 0, filenameLength);
		try {
			filename = new String(filenameBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.arraycopy(frame, filenameLength + 5, bytesArray, 0, bytesArray.length);
		System.out.println("Zdekodowano ramk�. Liczba bajt�w ramki w polu nag��wka: " + numberOfBytes);
		System.out.println("D�ugo�� ramki: " + frame.length);
		ack = new byte[1];
		if (frame.length == numberOfBytes) {
			System.out.println("Dane odebrano poprawnie");
			ack[0] = 1;
			saveFile(filename, bytesArray);
		} else {
			System.out.println("Podczas po��czenia wyst�pi� b��d");
			ack[0] = 0;
		}
	}
	
	public void saveFile(String fileName, byte[] bytesArray) {
		FileOutputStream os;
		try {
			os = new FileOutputStream("/home/pi/Desktop/Received Files/" + fileName);
			os.write(bytesArray); //zapisanie bajt�w do strumienia wyj�ciowego -> w efekcie do pliku
			os.close();
			System.out.println("Zapisano plik");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendResponse(String address) {
		StreamConnection responseConn;

		try {
			try {
				TimeUnit.MILLISECONDS.sleep(100); //op�znienie w wys�aniu odpowiedzi
			} catch (Exception e) {
				e.printStackTrace();
			}
			responseConn = (StreamConnection) Connector.open(address);
			OutputStream os = responseConn.openOutputStream();
			os.write(ack);
			os.close();
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			responseConn.close();
			System.out.println("Wys�ano odpowiedz");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
