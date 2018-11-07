import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import org.apache.commons.io.IOUtils;

public class ResponseListener implements Runnable{
	
	private volatile boolean wasResponse;
	private byte[] data;
	
	public ResponseListener() {
		this.wasResponse = false;
	}
	
	public boolean checkIfWasResponse() {
		return this.wasResponse;
	}
	
	public byte[] getResponse() {
		this.wasResponse = false;
		return this.data;
	}

	@Override
	public void run() {
		StreamConnectionNotifier notifier;
		try {
			notifier = (StreamConnectionNotifier)Connector.open("btspp://localhost:" + new UUID(0x1101).toString());			
			
			while(true) {
				wasResponse = false;
				StreamConnection conn;
				conn = (StreamConnection)notifier.acceptAndOpen();
				InputStream is;
				try {
	    			is = conn.openInputStream();
	    			data = IOUtils.toByteArray(is);
	    			is.close();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				wasResponse = true;
				System.out.println("Otrzymano odpowiedz");
				conn.close();
				
				while(wasResponse == true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
