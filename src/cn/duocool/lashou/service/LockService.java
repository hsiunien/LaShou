package cn.duocool.lashou.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.MainActivity;
import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ActivityHome;
import cn.duocool.lashou.activity.ImageLockActivity;
import cn.duocool.lashou.activity.Main;
import cn.duocool.lashou.activity.PasswordActivity;
import cn.duocool.lashou.activity.ApplockSingleSetting;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.logic.LockLogic;
import cn.duocool.lashou.model.AppModel;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.LockSettingInfo;
import cn.duocool.lashou.model.LockTimeSegmentBean;
import cn.duocool.lashou.thread.LockCheckThread;
import cn.duocool.lashou.utils.Tools;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;
import android.widget.Filterable;

public class LockService extends ServiceBase {
	private final static String TAG = LockService.class.getName();
	
    // 所有程序的信息
    public Map<String, LockAppInfo> allAppMap=new LinkedHashMap<String, LockAppInfo>();
    // 例外程序的信息（不用监控的信息）
    public Map<String, LockAppInfo> exceptionAppMap=new LinkedHashMap<String, LockAppInfo>();
    
    // 锁的配置信息
    public LockSettingInfo lockSettingInfo;
	
    public static final int FOREGROUND_ID = 1;  
    public static ArrayList<AppModel> appList=new ArrayList<AppModel>();// 需要锁定的程序
    public static List<Map<String, Object>> list=new ArrayList<Map<String, Object>>(); // 所有程序的信息
    
    // 当前锁的的内容
    public List<LockInfo> lockInfoList = new ArrayList<LockInfo>();
    
    // 服务的环境
    private Context serviceContext;
   
    private LockLogic lockLogic;
    

    
    /**
     * 锁检查线程
     */
    private LockCheckThread lockCheckThread = null;
    
//    public static String lock1Pwd="";	//点击主页面的图标进去时的密码
//    public static String lock1Type="";
//    public static String lock1Name="";
    

//    static DatabaseHelper sdbHelper;
//    public static  List<LockTimeSegmentBean> wifiLimitTimeList=new ArrayList<LockTimeSegmentBean>();
//    public static  List<LockTimeSegmentBean> netLimitTimeList=new ArrayList<LockTimeSegmentBean>();
    
//    public static int lockallstate;//记录总开关状态
    public static  Intent notificationIntent ;
  
    /**
     * 启动程序锁检查线程
     */
	private void startCheckThrad() {
	  	Log.d(TAG, "startCheckThread~~~~~~~~~~~~~~");
	      if (null == lockCheckThread) {  
	          lockCheckThread = new LockCheckThread(
	        		  serviceContext,
	        		  allAppMap,
	        		  exceptionAppMap,
	        		  lockSettingInfo,
	        		  getDao());
	          lockCheckThread.start();
	      }
	}
	
	public LockService() {
	}
	
	public LockService(Context serviceContext) {
		this.serviceContext = serviceContext;
	}
	
	public Map<String, LockAppInfo> getAllAppMap() {
		return allAppMap;
	}

	public Map<String, LockAppInfo> getExceptionAppMap() {
		return exceptionAppMap;
	}

	public void initService() {
		Log.d(TAG, "锁服务 初期化开始");
		
		lockLogic = new LockLogic();
		lockSettingInfo = new LockSettingInfo();
		
		// 提示框业务
//		appLockTipService = new AppLockTipService(serviceContext);
		
		//  初始化 数据库里面的锁信息
		lockLogic.initLockInfo(serviceContext,getDao(),lockSettingInfo);
		
		if (Tools.getApplication(serviceContext).getLashouEdition() == CommDef.EDITION_CHILD) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Log.d(TAG, "Tools.getApplication(serviceContext).getLashouEdition():"+Tools.getApplication(serviceContext).getLashouEdition());
					LockLogic lockLogicThread = new LockLogic();
					// TODO Auto-generated method stub
					// Log.d("tag", "begin");
					if (list.size() == 0) {
						Log.d(TAG, "isnull");
					}
					// 初始化wifi,3g可用时间段 ()
	//				getLimitTimeSegment(serviceContext);
					
					// 获取程序列表
					lockLogicThread.getAllAppInfoData(serviceContext, allAppMap,exceptionAppMap,getDao());
					
					
					startCheckThrad();
					Log.d(TAG, "done!");
				}
			}).start();
	      
	         //sdbHelper=new DatabaseHelper(this);
	       
	          //把com.android.packageinstaller这个包名加入数据库
	//          addToDatabase();
		 //初始化 applist
	//       resetApplist();
	    // Service如果要防止尽可能不被系统杀掉，需要设置为在前台运行。
	    	
			startFore(serviceContext);
		}
//    	startTimer();
		
	}
	
	public  void startFore(Context context)
	{
		Notification notification = new Notification(R.drawable.axb,
				"启用拉手程序", System.currentTimeMillis());
		notificationIntent = new Intent();
		notificationIntent.setClass(context,Main.class);
			
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, "拉手程序正在运行", "", pendingIntent);
		
		Service service  = (Service)context;
		service.startForeground(FOREGROUND_ID, notification);
	}
	
	/**
	 * 停止检查线程
	 */
	public void stopLockCheckThread(Context context) {
		Service service  = (Service)context;
		try {
			if (null != lockCheckThread) {
				lockCheckThread.isRunning = false;
			}
			service.stopForeground(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//    /**
//     *如果数据库里面没有com.android.packageinstaller就加进去 
//     */
//    public void addToDatabase()
//    {
//    	
//   	 DatabaseHelper dbHelper=new DatabaseHelper(this);
//		 SQLiteDatabase db=dbHelper.getWritableDatabase();
//		 Cursor c = db.rawQuery("SELECT * FROM appList WHERE packageName = ?", new String[]{"com.android.packageinstaller"});
//		 if(!c.moveToNext())//判断包名是否已经存在
//		   {
//		   ContentValues values=new ContentValues();
//		   values.put("packageName","com.android.packageinstaller");//向数据库里面插入数据
//		   values.put("islock", 1);
//		  
//		   values.put("week", "请选择星期几可用");
//		   values.put("limitTime", "0");
//		   values.put("lockId", 1);
//		   db.insert("appList", null, values);
////		   //这个只有第一次运行的时候才会执行，所以就不先查一遍了
////			//把拉手也加到锁定列表里面去
////            values.put("packageName", "cn.duocool.lashou");
////            db.insert("appList", null, values);
//		   }
//		 if (null != db) 
//		 {
//  		   db.close();
//  	     }
//    }
    

	
//	/**
//     * 初始化 applist
//     */
//    public static void resetApplist()
//       {
//    	appList.clear();
//  	     SQLiteDatabase db=sdbHelper.getReadableDatabase();
//    	   Cursor cursor=db.query("appList", new String[]{"packageName","week","limitTime","lockId"}, "islock=?", new String[]{"1"}, null, null, null);
//    	   while(cursor.moveToNext())
//    	   {
//    		   String temp=cursor.getString(cursor.getColumnIndex("packageName"));
//    		  // Log.d("tag",""+ cursor.getColumnIndex("beginTime"));
//    		
//    		   String lockweek=cursor.getString(cursor.getColumnIndex("week"));
//    		   String limitTime=cursor.getString(cursor.getColumnIndex("limitTime"));
//    		   int lockid=cursor.getInt(cursor.getColumnIndex("lockId"));
//    		  
//    		
//    		  Cursor lockCursor=db.rawQuery("SELECT * FROM locks WHERE id = ?", new String[]{lockid+""});
//    		  String password = null;
//    		  String passwordtype= null;
//    		  String lockName=null;
//    		  if(lockCursor.moveToNext())
//    		 {
//    			
//    		  password=lockCursor.getString(lockCursor.getColumnIndex("password"));
//    		  passwordtype=lockCursor.getString(lockCursor.getColumnIndex("passwordtype"));
//    		  lockName=lockCursor.getString(lockCursor.getColumnIndex("name"));
//    		 }
//    		 
//    		  appList.add(new AppModel(temp,applock_single_app_setting.stringToInt(lockweek),limitTime,password,passwordtype,lockName));
//    	   }
//    	   
//    	   if (null != db) {
//    		   db.close();
//    	   }
//       }
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		stopForeground(true);
//		mTimer.cancel();
//		mTimer.purge();
//		mTimer = null;
//		super.onDestroy();
//	}

	public static int getApplistIndex(String packageName)// �����������applist������±�
	{
		for (int i = 0; i < LockService.appList.size(); i++) {
			if (LockService.appList.get(i).getPackageName().equals(packageName)) {
				return i;
			}
		}
		return -1;
	}

//	// 获取所有的在手机上安装的应用程序的信 息
//	public List<Map<String, Object>> getInfos(Context context) {
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//
//		// 获取所有的在手机上安装的应用程序的信息, 包括哪些被卸载了的但是没有清空数据的应用程序
//		PackageManager pm = context.getPackageManager();
//		List<PackageInfo> packageinfos = pm
//				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
//		DatabaseHelper dbHelper = new DatabaseHelper(context);
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		for (PackageInfo packageInfo : packageinfos) {
//			ApplicationInfo applicationinfo = packageInfo.applicationInfo;
//
//			Map<String, Object> map = new HashMap<String, Object>();
//			// String packname = packageInfo.packageName;
//			// info.setPackname(packname);
//			// String version = packageInfo.versionName;
//			// info.setVersion(version);
//			Drawable appicon = applicationinfo.loadIcon(pm);
//			map.put("item1_imageivew", appicon);// 应用图标
//			String appname = applicationinfo.loadLabel(pm) + "";
//			map.put("item1_bigtv", appname);// 应用名称
//
//			String packname = packageInfo.packageName;
//			//Log.d("tag", "appname="+appname+"packname="+packname);
//			if(appname.startsWith("com.android")&&iszimu(appname))
//			{
//				//Log.d("tag", "appname="+appname+"packageName="+packname);
//				map=null;
//				continue;
//			}
//			if(packname.equals("com.android.packageinstaller")||packname.equals("android")||packname.equals("com.android.defcontainer")||packname.equals("com.android.exchange")||packname.equals("com.android.htmlviewer")||packname.equals("com.android.inputmethod.latin")||packname.equals("com.android.keychain")||packname.equals("com.android.inputmethod.pinyin")||packname.equals("com.android.launcher")
//				||packname.equals("com.android.magicsmoke")	||packname.equals("com.android.musicfx")||packname.equals("com.android.musicvis")||packname.equals("com.android.noisefield")||packname.equals("com.android.packageinstaller")
//				||packname.equals("com.android.phasebeam")||packname.equals("com.android.providers.downloads")||packname.equals("com.android.providers.downloads.ui")
//				||packname.equals("com.android.providers.drm")||packname.equals("com.android.providers.media")||packname.equals("com.android.providers.contacts")||packname.equals("com.android.providers.media")||packname.equals("com.android.providers.contacts")||packname.equals("com.android.providers.telephony")||packname.equals("com.android.providers.settings")||packname.equals("com.android.providers.userdictionary")
//				||packname.equals("com.android.systemui")||packname.equals("com.android.videoeditor")||packname.equals("com.android.voicedialer")||packname.equals("com.android.wallpaper")||packname.equals("com.android.wallpaper.livepicker")||packname.equals("com.example.android.livecubes")||packname.equals("com.svox.pico")||packname.equals("com.android.inputdevices")||packname.equals("com.android.certinstaller")||packname.equals("com.android.providers.applications")
//				||packname.equals("cn.duocool.lashou")
//					)
//			{
//				map=null;
//				continue;
//			}
//			map.put("packname", packname);// 包名
//
//			map.put("item1_smalltv", "");
//			// 判断在数据库里面是否存在，然后是否锁定并改相应的list里面的项
//			Cursor c = db.rawQuery(
//					"SELECT * FROM appList WHERE packageName = ?",
//					new String[] { packname });
//			if (c.moveToNext()) {
//				if (c.getInt(c.getColumnIndex("islock")) == 1) {
//					map.put("islock", 1); // 1代表要锁
//				} else {
//					map.put("islock", 0); // 0代表不锁
//				}
//			} else {
//				map.put("islock", 0);
//			}
//			c.close();
//			list.add(map);
//			map = null;
//			
//		}
//		 if (null != db) {
//			 if(db!=null)
//				{
//				db.close();
//				}
//  	   }
//		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
//		int temp=list.size();
//		int b=0;
//		for(int i=temp-1;i>=0;i--)
//		{
//			//list2.set(b, list.get(i));
//			list2.add(list.get(i));
//		}
//		return list2;
//	}

//	/**
//	 * 过滤一个应用是系统的应用还是第三方应用
//	 */
//	public boolean filterApp(ApplicationInfo info) 
//	{
//		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
//			return true;
//		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
//			return true;
//		}
//		return false;
//	}
//    public static List<Map<String, Object>> initusableTime(Context context,String sql,String[] where)
//    {
//    	List<Map<String, Object>> temp=new ArrayList<Map<String,Object>>();
//    	DataBaseHelper t=new DataBaseHelper(context);
//    	 SQLiteDatabase db=t.getReadableDatabase();
//    	  Cursor c= db.rawQuery(sql, where);
//  	    while(c.moveToNext())
//  	    {
//  	    	String b=c.getString(c.getColumnIndex("beginTime"));
//  	    	String e=c.getString(c.getColumnIndex("endTime"));
//  	    	  Map<String, Object> map = new HashMap<String, Object>();
//  		        // 往列表容器中添加数据
//  	    	  if(e.length()==1)
//  	    	  {
//  	    		  e="0"+e;
//  	    	  }
//  		        map.put("beginTV",b);
//  		        map.put("endTV", e);
//  		     
//  		        // 将列表数据添加到列表容器中
//  		        temp.add(map);
//  	    }
//  	
//  	  if (null != db) {
//		   db.close();
//	   }
//  	    return temp;
//    }
    
//	/**
//	 * 获得限制时间端（wifi 2G、3G）
//	 * @param context
//	 */
//    public static void getLimitTimeSegment(Context context) {
//    	// 连接数据库
//    	DataBaseHelper t=new DataBaseHelper(context);
//    	SQLiteDatabase db=t.getReadableDatabase();
//    	
//    	Cursor cursor = db.query("wifiusable", null, null, null, null, null, null);
//    	if (null != cursor && cursor.getCount() > 0) {
//    		cursor.moveToFirst();
//    		do {
//    			String beginTime =cursor.getString(cursor.getColumnIndex("beginTime"));
//      	    	String endTime=cursor.getString(cursor.getColumnIndex("endTime"));
//      	    	
//      	    	// 保存限制时间
//      	    	LockTimeSegmentBean btsb = new LockTimeSegmentBean();
//      	    	btsb.setStartTime(beginTime);
//      	    	btsb.setEndTime(endTime);
//      	    	wifiLimitTimeList.add(btsb);
//      	    	
//    		} while(cursor.moveToNext());
//    	}
//    	
//    	Cursor cursor2 = db.query("netusable", null, null, null, null, null, null);
//    	if (null != cursor2 && cursor2.getCount() > 0) {
//    		cursor2.moveToFirst();
//    		do {
//    			String beginTime =cursor2.getString(cursor2.getColumnIndex("beginTime"));
//      	    	String endTime=cursor2.getString(cursor2.getColumnIndex("endTime"));
//      	    	
//      	    	// 保存限制时间
//      	    	LockTimeSegmentBean btsb = new LockTimeSegmentBean();
//      	    	btsb.setStartTime(beginTime);
//      	    	btsb.setEndTime(endTime);
//      	    	netLimitTimeList.add(btsb);
//      	    	
//    		} while(cursor2.moveToNext());
//    	}
//    	if (null != cursor) {
//    		cursor.close();
//    		cursor = null;
//    	}
//    	if (null != cursor2) {
//    		cursor2.close();
//    		cursor2 = null;
//    	}
//    	
//    	if (null != db) {
//    		db.close();
//    		db = null;
//    	}
//    }

	public LockSettingInfo getLockSettingInfo() {
		return lockSettingInfo;
	}

	public void setLockSettingInfo(LockSettingInfo lockSettingInfo) {
		this.lockSettingInfo = lockSettingInfo;
	}
    
}
