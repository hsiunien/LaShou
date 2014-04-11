package cn.duocool.lashou.model;


/**
 * 记录用户位置的类
 * @author 杞桅
 *
 */
public class MyLocation {
	private int UserID;//用户ID
	private double Latitude;//纬度
	private double Longitude;//经度
	private String time;//时间
	private String address;//经纬度所对应的具体地址


	//get()方法
	public int getUserID(){
		return UserID;
	}
	public double getLatitude() {
		return Latitude;
	}
	public double getLongitude() {
		return Longitude;
	}
	public String getTime() {
		return time;
	}
	public String getAddress() {
		return address;
	}

	//set()方法
	public void setUserID(int userID){
		UserID = userID;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public void setLongitude(double longitude) {
		Longitude = longitude;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setAddress(String address) {
		this.address = address;
	}


}
