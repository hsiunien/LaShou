package cn.duocool.lashou.model;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.duocool.lashou.dao.DataBaseHelper;

/**
 * 我的位置提醒类，用于记录进出电子围栏的记录
 * @author 杞桅
 *
 */
public class MyLocationRemind implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int ID;//记录编号
	private int EF_ID;//所属电子围栏ID
	private int isIn;//是进入还是离开，0则表示是进入，1则表示是离开
	private int monitoredPerson_ID;//进出人ID
	private String monitoredPerson_Name;//进出人ID
	private String time;//进出时间
	private Double Latitude;//进出点纬度
	private Double Longitude;//进出点经度



	public MyLocationRemind(int eF_ID, int isIn,
			int monitoredPerson_ID, String time, Double latitude,
			Double longitude,String monitoredPerson_Name) {
		super();
		EF_ID = eF_ID;
		this.isIn = isIn;
		this.monitoredPerson_ID = monitoredPerson_ID;
		this.time = time;
		Latitude = latitude;
		Longitude = longitude;
		this.monitoredPerson_Name = monitoredPerson_Name;
	}
	public MyLocationRemind() {
	}
	//get方法
	public int getID() {
		return ID;
	}
	public int getEF_ID() {
		return EF_ID;
	}
	public int getIsIn() {
		return isIn;
	}
	public int getMonitoredPerson_ID() {
		return monitoredPerson_ID;
	}
	public String getTime() {
		return time;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public Double getLongitude() {
		return Longitude;
	}

	//set()方法
	public void setID(int iD) {
		ID = iD;
	}
	public void setEF_ID(int eF_ID) {
		EF_ID = eF_ID;
	}
	public void setIsIn(int isIn) {
		this.isIn = isIn;
	}
	public void setMonitoredPerson_ID(int monitoredPerson_ID) {
		this.monitoredPerson_ID = monitoredPerson_ID;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}


	public String getMonitoredPerson_Name() {
		return monitoredPerson_Name;
	}
	public void setMonitoredPerson_Name(String monitoredPerson_Name) {
		this.monitoredPerson_Name = monitoredPerson_Name;
	}
	/**
	 * 将MyLocationRemind存到数据库中
	 * @author 杞桅
	 *
	 */
	public static boolean SaveMyLocationRemind(MyLocationRemind myLocationRemind,Context context){
		try{
			DataBaseHelper databaseHelper = new DataBaseHelper(context);
			SQLiteDatabase db=databaseHelper.getWritableDatabase();
				
			ContentValues values=new ContentValues();
			values.put("EF_ID", myLocationRemind.getEF_ID());
			values.put("isIn", myLocationRemind.getIsIn());
			values.put("monitoredPerson_ID", myLocationRemind.getMonitoredPerson_ID());
			values.put("time", myLocationRemind.getTime());
			values.put("Latitude", myLocationRemind.getLatitude());
			values.put("Longitude", myLocationRemind.getLongitude());
			values.put("monitoredPerson_Name", myLocationRemind.getMonitoredPerson_Name());
	
			//将电子围栏信息写入数据库
			db.insert("myLocationRemind", null, values);
			if (null != db) {
				db.close();
				db = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 根据传入的电子围栏ID，查询该围栏的进出记录
	 * @author 杞桅
	 *
	 */
	public static ArrayList<MyLocationRemind> QueryRemind(int ID,Context context){
		ArrayList<MyLocationRemind> myLocationReminds = new ArrayList<MyLocationRemind>();

		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		SQLiteDatabase db=databaseHelper.getReadableDatabase();
		String sql="select * from myLocationRemind where EF_ID=?";

		Cursor cursor=db.rawQuery(sql, new String[]{ID+""});
		while (cursor.moveToNext()) {
			MyLocationRemind myLocationRemind = new MyLocationRemind();
			myLocationRemind.setID(cursor.getInt(cursor.getColumnIndex("id")));
			myLocationRemind.setEF_ID(cursor.getInt(cursor.getColumnIndex("EF_ID")));
			myLocationRemind.setIsIn(cursor.getInt(cursor.getColumnIndex("isIn")));
			myLocationRemind.setMonitoredPerson_ID(cursor.getInt(cursor.getColumnIndex("monitoredPerson_ID")));
			myLocationRemind.setTime(cursor.getString(cursor.getColumnIndex("time")));
			myLocationRemind.setLatitude(cursor.getDouble(cursor.getColumnIndex("Latitude")));
			myLocationRemind.setLongitude(cursor.getDouble(cursor.getColumnIndex("Longitude")));
			myLocationRemind.setMonitoredPerson_Name(cursor.getString(cursor.getColumnIndex("monitoredPerson_Name")));

			myLocationReminds.add(myLocationRemind);
		}
		if (null != db) {
			db.close();
		}


		return myLocationReminds;		
	}
	
	
	/**
	 * 根据传入的电子围栏ID，查询该围栏的进出记录
	 * @author 杞桅
	 *
	 */
	public static ArrayList<MyLocationRemind> QueryRemindByTime(String time,Context context){
		ArrayList<MyLocationRemind> myLocationReminds = new ArrayList<MyLocationRemind>();
		// create table myLocationRemind(id  INTEGER PRIMARY KEY AUTOINCREMENT,EF_ID int,isIn int,monitoredPerson_ID int,time varchar(100),Latitude double,Longitude double)
		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		SQLiteDatabase db=databaseHelper.getReadableDatabase();
		String sql="select * from myLocationRemind where time=?";

		Cursor cursor=db.rawQuery(sql, new String[]{time});
		while (cursor.moveToNext()) {
			MyLocationRemind myLocationRemind = new MyLocationRemind();
			myLocationRemind.setID(cursor.getInt(cursor.getColumnIndex("id")));
			myLocationRemind.setEF_ID(cursor.getInt(cursor.getColumnIndex("EF_ID")));
			myLocationRemind.setIsIn(cursor.getInt(cursor.getColumnIndex("isIn")));
			myLocationRemind.setMonitoredPerson_ID(cursor.getInt(cursor.getColumnIndex("monitoredPerson_ID")));
			myLocationRemind.setTime(cursor.getString(cursor.getColumnIndex("time")));
			myLocationRemind.setLatitude(cursor.getDouble(cursor.getColumnIndex("Latitude")));
			myLocationRemind.setLongitude(cursor.getDouble(cursor.getColumnIndex("Longitude")));
			myLocationRemind.setMonitoredPerson_Name(cursor.getString(cursor.getColumnIndex("monitoredPerson_Name")));

			myLocationReminds.add(myLocationRemind);
		}
		if (null != db) {
			db.close();
		}


		return myLocationReminds;		
	}
	
	
}


