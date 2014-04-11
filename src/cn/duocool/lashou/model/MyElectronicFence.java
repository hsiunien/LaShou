package cn.duocool.lashou.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.duocool.lashou.dao.DataBaseHelper;

import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * 电子围栏类
 * @author 杞桅
 *
 */
public class MyElectronicFence implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int ID;//电子围栏编号
	private String drawablePaht;//电子围栏范围截图存放位置
	private String title;//电子围栏标题
	private int userID;//电子围栏所属人ID
	private ArrayList<Integer> monitoredPersonIDs;//关心对象的ID的List。可以有多个关心对象
	private ArrayList<String> monitoredPersonNames;//关心对象的名字
	private String address;//电子围栏中心地址
	private Double Latitude;//电子围栏中心点纬度
	private Double Longitude;//电子围栏中心点经度
	private int r;//电子围栏半径，单位米
	private int k;//电子围栏记录条数
	private boolean is_In;//是进入提醒，存入数据库为int 0,1 1为true
	private boolean is_Out;//是离开提醒，存入数据库为int 0,1 1为true
	
	private int inoutFlag; // 0 初期值 1 已经进入了  2 已经离开了

	//构造函数

	public MyElectronicFence() {
		super();
	}


	public MyElectronicFence(String drawablePaht, String title, int userID,
			ArrayList<Integer> monitoredPersonIDs,
			ArrayList<String> monitoredPersonNames, String address,
			Double latitude, Double longitude, int r, int k, boolean is_In,
			boolean is_Out) {
		super();
		this.drawablePaht = drawablePaht;
		this.title = title;
		this.userID = userID;
		this.monitoredPersonIDs = monitoredPersonIDs;
		this.monitoredPersonNames = monitoredPersonNames;
		this.address = address;
		Latitude = latitude;
		Longitude = longitude;
		this.r = r;
		this.k = k;
		this.is_In = is_In;
		this.is_Out = is_Out;
	}

	public MyElectronicFence(int id2, String drawablePaht2, String title2,
			Integer userid2, ArrayList<Integer> choose_id,
			ArrayList<String> choose_name, String address2, double latitude2,
			double longitude2, int r2, int k2, boolean is_In2, boolean is_Out2) {

		this.ID = id2;
		this.drawablePaht = drawablePaht2;
		this.title = title2;
		this.userID = userid2;
		this.monitoredPersonIDs = choose_id;
		this.monitoredPersonNames = choose_name;
		this.address = address2;
		Latitude = latitude2;
		Longitude = longitude2;
		this.r = r2;
		this.k = k2;
		this.is_In = is_In2;
		this.is_Out = is_Out2;
	}


	//get()方法
	public int getID() {
		return ID;
	}
	public String getDrawablePaht() {
		return drawablePaht;
	}
	public String getTitle() {
		return title;
	}
	public int getUserID() {
		return userID;
	}
	public ArrayList<Integer> getMonitoredPersonIDs() {
		return monitoredPersonIDs;
	}
	public ArrayList<String> getMonitoredPersonNames() {
		return monitoredPersonNames;
	}
	public String getAddress() {
		return address;
	}
	public Double getLatitude() {
		return Latitude;
	}
	public Double getLongitude() {
		return Longitude;
	}
	public int getR() {
		return r;
	}
	public int getK() {
		return k;
	}
	public boolean getIs_In() {
		return is_In;
	}
	public boolean getIs_Out() {
		return is_Out;
	}

	//set()方法
	public void setID(int iD) {
		ID = iD;
	}
	public void setDrawablePaht(String drawablePaht) {
		this.drawablePaht = drawablePaht;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public void setMonitoredPersonIDs(ArrayList<Integer> monitoredPersonIDs) {
		this.monitoredPersonIDs = monitoredPersonIDs;
	}
	public void setMonitoredPersonNames(ArrayList<String> monitoredPersonNames) {
		this.monitoredPersonNames = monitoredPersonNames;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}
	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}
	public void setR(int r) {
		this.r = r;
	}
	public void setK(int k) {
		this.k = k;
	}
	public void setIs_In(boolean is_In) {
		this.is_In = is_In;
	}
	public void setIs_Out(boolean is_Out) {
		this.is_Out = is_Out;
	}
	public int getInoutFlag() {
		return inoutFlag;
	}
	public void setInoutFlag(int inoutFlag) {
		this.inoutFlag = inoutFlag;
	}


	/**
	 * 判断一个点是否在电子围栏里,是则返回true
	 * @author 杞桅
	 * @param 要判断的位置，GeoPoint对象
	 */
	public boolean is_In_electronic_fence(GeoPoint location){
		double distance;//用于存放location到电子围栏中心点的距离
		GeoPoint geoPoint = new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6));  
		distance = DistanceUtil.getDistance(geoPoint, location);
		if(distance<=r){
			//在围栏里
			return true;
		}
		return false;		
	}


	/**
	 * 将MyElectronicFence存到数据库中
	 * @author 杞桅
	 * @param MyElectronicFence对象
	 */
	public boolean SavaMyElectronicFence(MyElectronicFence myElectronicFence,Context context){
		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		SQLiteDatabase db=databaseHelper.getWritableDatabase();

		ContentValues values=new ContentValues();
		values.put("drawablePaht", myElectronicFence.getDrawablePaht());
		values.put("title", myElectronicFence.getTitle());
		values.put("userID", myElectronicFence.getUserID());
		
		//将monitoredPersonIDs转为一个String存入数据库
		StringBuffer temp_monitoredPersonIDs =new StringBuffer();
		for(int i=0;i<myElectronicFence.getMonitoredPersonIDs().size();i++){
			temp_monitoredPersonIDs.append(myElectronicFence.getMonitoredPersonIDs().get(i).toString()).append(",");
		}
		String temp_id =temp_monitoredPersonIDs.substring(0, temp_monitoredPersonIDs.length()-1);
		values.put("monitoredPersonIDs", temp_id);
		//将monitoredPersonNames转为一个String存入数据库
		StringBuffer temp_monitoredPersonNames =new StringBuffer();
		for(int i=0;i<myElectronicFence.getMonitoredPersonNames().size();i++){
			temp_monitoredPersonNames.append(myElectronicFence.getMonitoredPersonNames().get(i).toString()).append(",");
		}
		String temp_name =temp_monitoredPersonNames.substring(0, temp_monitoredPersonNames.length()-1);
		values.put("monitoredPersonNames", temp_name);		
		values.put("address", myElectronicFence.getAddress());
		values.put("Latitude", myElectronicFence.getLatitude());
		values.put("Longitude", myElectronicFence.getLongitude());
		values.put("r", myElectronicFence.getR());
		values.put("k", myElectronicFence.getK());
		if( myElectronicFence.getIs_In() == true){
			values.put("InEF", 1);
		}else{
			values.put("InEF", 0);
		}if(myElectronicFence.getIs_Out() == true){
			values.put("OutEF", 1);
		}else{
			values.put("OutEF", 0);
		}
		values.put("inoutFlag", 0);
		//将电子围栏信息写入数据库
		db.insert("myElectronicFence", null, values);
		
		if (null != db) {
			db.close();
			db = null;
		}
		return true;

	}

	/**
	 * 从数据库中读取所有ElectronicFence对象
	 * @author 杞桅
	 * @param ArrayList<MyElectronicFence>
	 */
	public static ArrayList<MyElectronicFence> GetElectronicFence(Context context){
		ArrayList<MyElectronicFence> myElectronicFences = new ArrayList<MyElectronicFence>();

		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		SQLiteDatabase db=databaseHelper.getReadableDatabase();
		String sql="select * from myElectronicFence";

		Cursor cursor=db.rawQuery(sql, null);
		
		if (null != cursor && cursor.getCount() > 0) {
			cursor.moveToFirst();
			do {
				MyElectronicFence temp_myElectronicFence = new MyElectronicFence();
				temp_myElectronicFence.setID(cursor.getInt(cursor.getColumnIndex("id")));
				temp_myElectronicFence.setDrawablePaht(cursor.getString(cursor.getColumnIndex("drawablePaht")));
				temp_myElectronicFence.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				temp_myElectronicFence.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
				//将数据库中的monitoredPersonIDs 取出，转换为ArrayList<Integer>
				String temp_id = new String();
				temp_id = cursor.getString(cursor.getColumnIndex("monitoredPersonIDs"));
				String[] temp_monitoredPersonIDs = temp_id.split(",");
				ArrayList<Integer> temp_monitoredPersonIDs_list =new ArrayList<Integer>();		
				for(int i=0;i<temp_monitoredPersonIDs.length;i++){
					temp_monitoredPersonIDs_list.add(Integer.valueOf(temp_monitoredPersonIDs[i]));
				}
				temp_myElectronicFence.setMonitoredPersonIDs(temp_monitoredPersonIDs_list);
				//将数据库中的monitoredPersonNames 取出，转换为ArrayList<Integer>
				String temp_name = new String();
				temp_name = cursor.getString(cursor.getColumnIndex("monitoredPersonNames"));
				String[] temp_monitoredPersonNames = temp_name.split(",");
				ArrayList<String> temp_monitoredPersonNames_list =new ArrayList<String>();		
				for(int i=0;i<temp_monitoredPersonNames.length;i++){
					temp_monitoredPersonNames_list.add(temp_monitoredPersonNames[i]);
				}
				temp_myElectronicFence.setMonitoredPersonNames(temp_monitoredPersonNames_list);
				temp_myElectronicFence.setAddress(cursor.getString(cursor.getColumnIndex("address")));
				temp_myElectronicFence.setLatitude(cursor.getDouble(cursor.getColumnIndex("Latitude")));
				temp_myElectronicFence.setLongitude(cursor.getDouble(cursor.getColumnIndex("Longitude")));
				temp_myElectronicFence.setR(cursor.getInt(cursor.getColumnIndex("r")));
				temp_myElectronicFence.setK(cursor.getInt(cursor.getColumnIndex("k")));
				temp_myElectronicFence.setInoutFlag(cursor.getInt(cursor.getColumnIndex("inoutFlag")));
				int temp_isInOut = 1;
				temp_isInOut = cursor.getInt(cursor.getColumnIndex("InEF"));
				if(temp_isInOut == 1){
					temp_myElectronicFence.setIs_In(true);
				}else{
					temp_myElectronicFence.setIs_In(false);
				}
				temp_isInOut = cursor.getInt(cursor.getColumnIndex("OutEF"));
				if(temp_isInOut == 1){
					temp_myElectronicFence.setIs_Out(true);
				}else{
					temp_myElectronicFence.setIs_Out(false);
				}

				myElectronicFences.add(temp_myElectronicFence);
			} while(cursor.moveToNext());
		}
		
		if  (null != db) {
			db.close();
			db = null;
		}
		return myElectronicFences;		
	}

	/**
	 * 传入一个已有的MyElectronicFence对象，，修改后存入数据库中
	 * @author 杞桅
	 * @param boolean
	 */
	public static boolean UpdateElectronicFence(MyElectronicFence myElectronicFence,Context context) {
		try {
			DataBaseHelper databaseHelper = new DataBaseHelper(context);
			SQLiteDatabase db=databaseHelper.getWritableDatabase();
	
			ContentValues values=new ContentValues();
			values.put("drawablePaht", myElectronicFence.getDrawablePaht());
			values.put("title", myElectronicFence.getTitle());
			values.put("userID", myElectronicFence.getUserID());
			//将monitoredPersonIDs转为一个String存入数据库
			StringBuffer temp_monitoredPersonIDs =new StringBuffer();
			for(int i=0;i<myElectronicFence.getMonitoredPersonIDs().size();i++){
				temp_monitoredPersonIDs.append(myElectronicFence.getMonitoredPersonIDs().get(i).toString()).append(",");
			}
			String temp_id =temp_monitoredPersonIDs.substring(0, temp_monitoredPersonIDs.length()-1);
			values.put("monitoredPersonIDs", temp_id);
			//将monitoredPersonNames转为一个String存入数据库
			StringBuffer temp_monitoredPersonNames =new StringBuffer();
			for(int i=0;i<myElectronicFence.getMonitoredPersonNames().size();i++){
				temp_monitoredPersonNames.append(myElectronicFence.getMonitoredPersonNames().get(i).toString()).append(",");
			}
			String temp_name =temp_monitoredPersonNames.substring(0, temp_monitoredPersonNames.length()-1);
			values.put("monitoredPersonNames", temp_name);		
			values.put("address", myElectronicFence.getAddress());
			values.put("Latitude", myElectronicFence.getLatitude());
			values.put("Longitude", myElectronicFence.getLongitude());
			values.put("r", myElectronicFence.getR());
			values.put("k", myElectronicFence.getK());
			if( myElectronicFence.getIs_In() == true){
				values.put("InEF", 1);
			}else{
				values.put("InEF", 0);
			}if(myElectronicFence.getIs_Out() == true){
				values.put("OutEF", 1);
			}else{
				values.put("OutEF", 0);
			}
			values.put("inoutFlag", myElectronicFence.getInoutFlag());
			db.update("myElectronicFence", values, "id = '"+ myElectronicFence.getID() +"'", null);   //第三个参数为 条件语句
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
	 * 传入一个已有的MyElectronicFence对象，，删除数据库中对应的该对象
	 * @author 杞桅
	 * @param boolean
	 */
	public static boolean DeleteElectronicFence(MyElectronicFence myElectronicFence,Context context){		
		DataBaseHelper databaseHelper = new DataBaseHelper(context);
		SQLiteDatabase db=databaseHelper.getWritableDatabase();

		//删除围栏截图
		File file = new File(myElectronicFence.getDrawablePaht());
		file.delete();
		db.delete("myElectronicFence", "id = '"+ myElectronicFence.getID() +"'", null);

		db.close();

		return true;

	}
}
