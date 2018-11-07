import javax.bluetooth.RemoteDevice;

public class DiscoveredDevice {
	
	private RemoteDevice remoteDevice;
	private String name;
	
	public DiscoveredDevice(RemoteDevice remoteDevice, String name) {
		this.remoteDevice = remoteDevice;
		this.name = name;
	}
	
	public RemoteDevice getRemoteDevice() {
		return remoteDevice;
	}
	
	public String getName() {
		return name;
	}
}
