package cn.duocool.lashou.net.client;



public class AppData implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	// Fields
	private String versionCode;
	private String versionName;
	private String appinfo;
	private String publishTime;
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getAppinfo() {
		return appinfo;
	}
	public void setAppinfo(String appinfo) {
		this.appinfo = appinfo;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
}