import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class Bluetooth implements DiscoveryListener{
	
	private StreamConnectionNotifier notifier;
	private StreamConnection conn;
	private FileDisplay fileDisplay;
	private String responseAddress;
	private byte[] ack;
	private volatile boolean servicesSearchingCompleted = false;

	public void run() {
		
		// uruchomienie w¹tku odpowiedzialnego za wyœwietlanie plików
		fileDisplay = new FileDisplay();
		new Thread(fileDisplay).start();
		
		try {
			System.out.println("Nas³uchujê");
			
			// obiekt oczekuj¹cy po³¹czenia przychodz¹cego do serwera, reprezentuje nas³uchuj¹ce gniazdo
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID(0x1101).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (true) {
			listenForConnection();
		}
	}
	
	private void listenForConnection() {
		try {
			boolean isFile;
			// akceptacja i ustanowienie po stronie serwera po³¹czenia przychodz¹cego od klienta
			conn = (StreamConnection)notifier.acceptAndOpen(); 
			System.out.println("Otwarto po³¹czenie");
			RemoteDevice remoteDevice = RemoteDevice.getRemoteDevice(conn); // pobranie adresu MAC kontrolera tablic (obiekt RemoteDevice)
			
			isFile = receiveData();
			
			// sprawdzenie, czy otrzymano dane w postaci pliku
			if (isFile == true) {
				sendResponse(responseAddress); // tak - wys³anie odpowiedzi o przebiegu transmisji
			} else {
				responseAddress = findSocket(remoteDevice); // nie - pobranie numeru portu kontrolera w celu pózniejszego wys³ania odpowiedzi
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// funkcja zwracaj¹ca 'true', je¿eli w strumieniu zwi¹zanym z po³¹czeniem znajduj¹ siê dane
	private boolean receiveData() {
		InputStream is;
		byte[] frame;
		boolean isFile = false;
		
		try {
			is = conn.openInputStream(); // otwarcie strumienia wejœciowego danych
			frame = IOUtils.toByteArray(is); // zapisanie danych ze strumienia do tablicy bajtów
			is.close();
			conn.close();
			if (frame.length == 0) {
				System.out.println("It's a ping!"); // je¿eli strumieñ danych jest pusty, to jest to po³¹czenie próbne w trakcie wyszukiwania urz¹dzeñ
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
	
	// funkcja depakietyzacji na poziomie oprogramowania
	private void decodeFrameAndSave(byte[] frame) {
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
		System.out.println("Zdekodowano ramkê. Liczba bajtów ramki w polu nag³ówka: " + numberOfBytes);
		System.out.println("D³ugoœæ ramki: " + frame.length);
		ack = new byte[1];
		
		// sprawdzenie zgodnoœci d³ugoœci ramki z zawartoœci¹ pierwszego pola ramki
		if (frame.length == numberOfBytes) {
			System.out.println("Dane odebrano poprawnie");
			ack[0] = 1;
			saveFile(filename, bytesArray);
		} else {
			System.out.println("Podczas po³¹czenia wyst¹pi³ b³¹d");
			ack[0] = 0;
		}
	}
	
	// funkcja zapisuj¹ca dane do pliku
	private void saveFile(String filename, byte[] bytesArray) {
		FileOutputStream os;
		try {
			os = new FileOutputStream("/home/pi/Desktop/Received_Files/" + filename);
			os.write(bytesArray); // zapisanie bajtów do strumienia wyjœciowego
			os.close();
			System.out.println("Zapisano plik");
			fileDisplay.displayNewFile("/home/pi/Desktop/Received_Files/" + filename); // wyœwietlenie pliku
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// funkcja wysy³aj¹ca odpowiedz 'ack' o przebiegu transmisji
	private void sendResponse(String address) {
		StreamConnection responseConn;

		try {
			try {
				TimeUnit.MILLISECONDS.sleep(100); // opóznienie w wys³aniu odpowiedzi
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
			System.out.println("Wys³ano odpowiedz");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// pobieranie adresu wraz z numerem portu kontrolera w procesie wyszukiwania seriwsów
	private String findSocket(RemoteDevice remoteDevice) {
		UUID[] uuidSet = new UUID[1];
		uuidSet[0] = new UUID(0x1101); // UUID SPP
		int[] attrIdSet = new int[] {0x0100}; // atrybut - Service Name ID
		LocalDevice localDevice = null;
		DiscoveryAgent agent;
		
		System.out.println("Service Discovery");
		
		try {
			localDevice = LocalDevice.getLocalDevice();
			agent = localDevice.getDiscoveryAgent();
			agent.searchServices(attrIdSet, uuidSet, remoteDevice, this);
		} catch (BluetoothStateException e) {
			e.printStackTrace();
		}
		
		while (servicesSearchingCompleted == false);
		servicesSearchingCompleted = false;
		
		return responseAddress;
	}
	// funkcja nieu¿ywana
	@Override
	public void deviceDiscovered(RemoteDevice arg0, DeviceClass arg1) {
		
	}
	
	// funkcja nieu¿ywana
	@Override
	public void inquiryCompleted(int arg0) {
		
	}

	// funkcja nieu¿ywana
	@Override
	public void serviceSearchCompleted(int arg0, int arg1) {
		
	}
	
	// funkcja wywo³ywana w chwili wykrycia serwisu pasuj¹cego do danego UUID (tu UUID SPP)
	@Override
	public void servicesDiscovered(int transID, ServiceRecord[] serviceRecord) {
		for (int i = 0; i < serviceRecord.length; i++) {
			DataElement serviceName = serviceRecord[i].getAttributeValue(0x0100); //pobranie nazwy serwisu - wartosci atrybutu o ID 0x0100
			String connectionURL = serviceRecord[i].getConnectionURL(0, false);
			if (serviceName != null) {
				System.out.println((String)serviceName.getValue());
			} else {
				System.out.println("Nieznana us³uga");
			}
			
			responseAddress = connectionURL;
			servicesSearchingCompleted = true;
			System.out.println(responseAddress);
		}
	}
}
