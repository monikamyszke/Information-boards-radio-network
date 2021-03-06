import javax.bluetooth.RemoteDevice;

public class DiscoveredDevice {
	
	private RemoteDevice remoteDevice;
	private String name;
	
	public DiscoveredDevice(RemoteDevice remoteDevice, String name) {
		this.remoteDevice = remoteDevice; // adres MAC urządzenia
		this.name = name; // nazwa urządzenia
	}
	
	public RemoteDevice getRemoteDevice() {
		return remoteDevice;
	}
	
	public String getName() {
		return name;
	}
}
