package cn.duocool.lashou.model;

public class LockSystemInfo {
	// create table lockall(_id INTEGER PRIMARY KEY AUTOINCREMENT,lockallstate int,isautostart int,DeviceAdminAdd int)
   
	private int id;
	
	private int lockallstate;
	
	private int isautostart;
	
	private int deviceAdminAdd;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLockallstate() {
		return lockallstate;
	}

	public void setLockallstate(int lockallstate) {
		this.lockallstate = lockallstate;
	}

	public int getIsautostart() {
		return isautostart;
	}

	public void setIsautostart(int isautostart) {
		this.isautostart = isautostart;
	}

	public int getDeviceAdminAdd() {
		return deviceAdminAdd;
	}

	public void setDeviceAdminAdd(int deviceAdminAdd) {
		this.deviceAdminAdd = deviceAdminAdd;
	}
	
}
