import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class ResponseListener implements Runnable {
	
	private volatile boolean wasResponse;
	private byte[] data;
	
	public ResponseListener() {
		this.wasResponse = false;
	}
	
	@Override
	public void run() {
		StreamConnectionNotifier notifier;
		
		try {
			// utworzenie obiektu nas�uchuj�cego po��czenia - odpowiedzi od w�z�a podrz�dnego
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID(0x1101).toString());			
			
			while (true) {
				InputStream is;
				StreamConnection conn;
				
				wasResponse = false;
				conn = (StreamConnection)notifier.acceptAndOpen();
				
				try {
					// pobranie danych ze strumienia wej�ciowego (pobranie odpowiedzi)
	    			is = conn.openInputStream();
	    			data = IOUtils.toByteArray(is);
	    			is.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				conn.close();
				wasResponse = true;
				System.out.println("Otrzymano odpowiedz");
				
				while (wasResponse == true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// funkcja sprawdzaj�ca, czy otrzymano odpowiedz
	public boolean checkIfWasResponse() {
		return wasResponse;
	}
	
	// funkcja zwracaj�ca otrzyman� odpowiedz w postaci tablicy bajt�w
	public byte[] getResponse() {
		wasResponse = false;
		return data;
	}
}
