import javax.bluetooth.RemoteDevice;

public class DiscoveredDevice {
	
	private RemoteDevice remoteDevice;
	private String name;
	
	public DiscoveredDevice(RemoteDevice remoteDevice, String name) {
		this.remoteDevice = remoteDevice; // adres MAC urz�dzenia
		this.name = name; // nazwa urz�dzenia
	}
	
	public RemoteDevice getRemoteDevice() {
		return remoteDevice;
	}
	
	public String getName() {
		return name;
	}
}
