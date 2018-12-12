import javax.bluetooth.RemoteDevice;

public class DiscoveredDevice {
	
	private RemoteDevice remoteDevice;
	private String name;
	
	public DiscoveredDevice(RemoteDevice remoteDevice, String name) {
		this.remoteDevice = remoteDevice; // adres MAC urz¹dzenia
		this.name = name; // nazwa urz¹dzenia
	}
	
	public RemoteDevice getRemoteDevice() {
		return remoteDevice;
	}
	
	public String getName() {
		return name;
	}
}
