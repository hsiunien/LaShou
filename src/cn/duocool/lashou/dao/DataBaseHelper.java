package cn.duocool.lashou.dao;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	private Context context;
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context=context;
	}
	
	/**
	 *  构造函数
	 * @param context
	 */
	public DataBaseHelper(Context context) {
		this(context, CommDef.DB_FILE_NAME, null, CommDef.DB_VERSION);
		this.context=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//记录锁定了的程序的表
		db.execSQL("create table appList(_id INTEGER PRIMARY KEY AUTOINCREMENT,appName varchar(256),packageName varchar(300),islock int,week varchar(30),limitTime varchar(30),leftUseTime int,lockId int)");
		//记录三个锁的表
		db.execSQL("create table locks(_id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(30),password varchar(30),passwordtype varchar(30),question varchar(50),answer varchar(50), isDefault int)");
		//号码表
		db.execSQL("create table numbers(_id INTEGER PRIMARY KEY AUTOINCREMENT,name varchar(20),number varchar(20)) ");
	   // 提前提醒时间表
		db.execSQL("create table remind(_id INTEGER PRIMARY KEY AUTOINCREMENT,time int) ");
		// wifi可用时段
		db.execSQL("create table wifiusable(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30)) ");
		// 锁整体信息
		db.execSQL("create table lockall(_id INTEGER PRIMARY KEY AUTOINCREMENT,lockallstate int,isautostart int,DeviceAdminAdd int) ");
		
		// 3G可用时段
		db.execSQL("create table netusable(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30)) ");
		//程序可用时段
		db.execSQL("create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) ");
		// 程序可用周天
		db.execSQL("create table appusableweek(_id INTEGER PRIMARY KEY AUTOINCREMENT,week int,appid int) ");
		// 程序可用日期
		db.execSQL("create table appusableday(_id INTEGER PRIMARY KEY AUTOINCREMENT,day int,appid int) ");
		//个人信息表
		db.execSQL("create table myInfo(id  INTEGER PRIMARY KEY AUTOINCREMENT,userID int,headImg varchar(200) ,email varchar(30),userName varchar(30),tel varchar(30),password varchar(30),sinaToken varchar(256),sinaUid varchar(256),QQUid varchar(256),QQToken varchar(256))");
	
		
		//电子围栏表
		db.execSQL("create table myElectronicFence(id  INTEGER PRIMARY KEY AUTOINCREMENT,drawablePaht varchar(200),title varchar(50),userID int,monitoredPersonIDs varchar(200),monitoredPersonNames varchar(200),address varchar(100),Latitude double,Longitude double,r int,k int,InEF int,OutEF int,inoutFlag int)");
		//电子围栏进出记录表
		db.execSQL("create table myLocationRemind(id  INTEGER PRIMARY KEY AUTOINCREMENT,EF_ID int,isIn int,monitoredPerson_ID int,time varchar(100),Latitude double,Longitude double,monitoredPerson_Name varchar(256))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//电子围栏表
		db.execSQL("DROP TABLE IF EXISTS myElectronicFence");
		//电子围栏进出记录表
		db.execSQL("DROP TABLE IF EXISTS myLocationRemind");
		
		//电子围栏表
		db.execSQL("create table myElectronicFence(id  INTEGER PRIMARY KEY AUTOINCREMENT,drawablePaht varchar(200),title varchar(50),userID int,monitoredPersonIDs varchar(200),monitoredPersonNames varchar(200),address varchar(100),Latitude double,Longitude double,r int,k int,InEF int,OutEF int,inoutFlag int)");
		//电子围栏进出记录表
		db.execSQL("create table myLocationRemind(id  INTEGER PRIMARY KEY AUTOINCREMENT,EF_ID int,isIn int,monitoredPerson_ID int,time varchar(100),Latitude double,Longitude double,monitoredPerson_Name varchar(256))");
		
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public UserInfo loadUser(){
		UserInfo user=null;
		String sql="select id,userId,headImg,email,userName,tel,password,sinaUid,sinaToken,QQToken,QQUid from myInfo";
		SQLiteDatabase db=getReadableDatabase();
		Cursor cursor=db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			user=new UserInfo();
			user.setHeadSrc(cursor.getString(cursor.getColumnIndex("headImg")));
			ImageLoader imgLoader=new ImageLoader(context);
			Bitmap bmp=imgLoader.getImage(user.getHeadSrc());
			if(bmp==null){
				imgLoader.downloadHead(user);
			}else{
				user.setHeadImg(bmp);
			}
			user.setUserId(cursor.getInt(cursor.getColumnIndex("userID")));
			user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
			user.setUserName(cursor.getString(cursor.getColumnIndex("userName")));
			user.setTel(cursor.getString(cursor.getColumnIndex("tel")));
			user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
			user.setSinaUid(cursor.getString(cursor.getColumnIndex("sinaUid")));
			user.setSinaToken(cursor.getString(cursor.getColumnIndex("sinaToken")));
			user.setQQUid(cursor.getString(cursor.getColumnIndex("QQUid")));
			user.setQqToken(cursor.getString(cursor.getColumnIndex("QQToken")));
			Tools.getApplication(context).setLoginState(true);
			Tools.getApplication(context).setMyInfo(user);
		}
		db.close();
		//Tools.getApplication(context).setMyInfo(user);
		return user;
	}
	public void saveUser(UserInfo user){
		SQLiteDatabase db=getWritableDatabase();
		db.delete("myInfo", null, null);
		ContentValues values=new ContentValues();
		values.put("userId", user.getUserId());
		values.put("email", user.getEmail());
		values.put("password", user.getPassword());
		values.put("userName", user.getUserName());
		values.put("tel", user.getTel());
		values.put("sinaUid", user.getSinaUid());
		values.put("sinaToken", user.getSinaToken());
		values.put("QQUid", user.getQQUid());
		values.put("QQToken", user.getQqToken());

		values.put("headImg", user.getHeadSrc());
		db.insert("myInfo", null, values);
		db.close();
		Tools.getApplication(context).setMyInfo(user);
	}

}
