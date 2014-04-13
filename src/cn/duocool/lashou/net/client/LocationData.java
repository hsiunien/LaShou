package cn.duocool.lashou.net.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class LocationData  {
	
	private int locationOrder; // 当前位置数据的排序
	private int userID;//用户ID
	private double latitude;//纬度
	private double longitude;//经度
	private String time;//[时间]
	private String address;//经纬度所对应的具体地址
	
	@Override
	public String toString() {
		return "AxbLocation [locationOrder=" + locationOrder + ", userID="
				+ userID + ", latitude=" + latitude + ", longitude="
				+ longitude + ", time=" + time + ", address=" + address + "]";
	}

	public static LocationData valueOf(String axvLocation) {
		JsonParser jsonParser = new JsonParser();
		JsonObject obj = jsonParser.parse(axvLocation).getAsJsonObject();
		Gson gson=new Gson();
		LocationData r = gson.fromJson(obj, LocationData.class);
		return r;
	}

	public int getLocationOrder() {
		return locationOrder;
	}
	
	public void setLocationOrder(int locationOrder) {
		this.locationOrder = locationOrder;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
