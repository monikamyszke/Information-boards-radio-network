import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.bluetooth.RemoteDevice;
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

	RemoteDevice device;
	
	Date date;
	DateFormat dateFormat;
    String time1;
    
    FileWriter fileWriter;
    int counter;
	
	public void run() {
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		counter = 1;
//		try {
//			this.fileWriter = new FileWriter("/home/pi/Desktop/testy_RPi.csv");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		try {
			System.out.println("Nas³uchujê");
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID( 0x1101 ).toString( ));//obiekt oczekuj¹cy po³¹czenia przychodz¹cego do serwera, reprezentuje nas³uchuj¹ce gniazdo
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true) {
			listenForConnection();
		}
	}
	
	public void listenForConnection() {
		try {
			conn = (StreamConnection)notifier.acceptAndOpen(); //akceptacja i ustanowienie po stronie serwera po³¹czenia przychodz¹cego od klienta
			System.out.println("Otwarto po³¹czenie");
			RemoteDevice device = RemoteDevice.getRemoteDevice(conn);
			responseAddress = "btspp://" + device.getBluetoothAddress() + ":6"; 
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
			if(frame.length == 0) {
				System.out.println("It's a ping!");
				conn.close();
			}
			else {
				System.out.println("Odebrano plik");
				conn.close();
//				saveBytesToFile();
				decodeFrame();
				saveFile();
				
				//czas ODEBRANIA I ODTWORZENIA PLIKU
				date = new Date();
		        time1 = dateFormat.format(date);
				System.out.println(time1);
				
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
		System.out.println("Zdekodowano ramkê. Liczba bajtów ramki: " + (numberOfBytes));
		ack = new byte[1];
		if(frame.length == numberOfBytes) {
			System.out.println("Dane odebrano poprawnie");
			ack[0] = 1;
		}
		else {
			System.out.println("Podczas po³¹czenia wyst¹pi³ b³¹d");
			ack[0] = 0;
		}
	}
	
	public void saveFile() {
		FileOutputStream os;
		try {
			os = new FileOutputStream("/home/pi/Desktop/" + fileName);
			os.write(bytesArray); //zapisanie bajtów do strumienia wyjœciowego -> w efekcie do pliku
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
			StreamConnection conn1;
			try {
				conn1 = (StreamConnection) Connector.open(address);
				OutputStream os = conn1.openOutputStream();
				os.write(ack);
				os.close();
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				conn1.close();
				System.out.println("Wys³ano odpowiedz");
			} catch (IOException e1) {
			}
			
			// zapis czasu do pliku
//			try {
//				fileWriter.append(time1);
//				fileWriter.append("\n");
//				fileWriter.flush();
//				counter = counter + 1;
//				if(counter == 100) {
//					fileWriter.close();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}

	}

}
