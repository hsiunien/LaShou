package cn.duocool.lashou.service;

import java.util.ArrayList;
import java.util.List;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.broadcastreceiver.ShortMessageReceiver;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.mywidget.Applock_CountdownTipView.ServiceListener;
import cn.duocool.lashou.thread.SentLocationThread;
import cn.duocool.lashou.utils.Tools;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LashouService extends Service {
	private final static String TAG = LashouService.class.getName();
	
    // 提示框业务
    private AppLockTipService appLockTipService;
    
    private ShortMessageReceiver receiver;
    
    public static PendingIntent mAlarmSender;
    
    public static Context context;
    
    public static int length = 20;//list长度，用于控制list长度
    public static List<MyLocation> locationList =  new ArrayList<MyLocation>(length); //用于记录用户足迹的list
    
    // 上传位置线程
//    private SentLocationThread sentLocationThread;
    
//    public static boolean sentLocationThreadFlag = false;
	
	/**
	 * 数据访问对象
	 */
	private DaoBase dao;
	
	public  Handler handler = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 200:
				appLockTipService.showTip(msg.getData().getLong("tip"));
				break;
			case 201:
				appLockTipService.tryClose();
				break;
			default:
				break;
			}
			
			
			super.handleMessage(msg);
		}
	};
	
	/**
	 * 锁业务逻辑
	 */
	private LockService lockService;
	
	public IBinder mBinder = new LocalServer();
	public class LocalServer extends Binder {
		public LashouService getService() {
			return LashouService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate~~~~~~~~~~~~~~~~~~~~~");
		lockService = new LockService(this);
		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_CHILD) {
			appLockTipService = new AppLockTipService(this);
		}
		
//		sentLocationThreadFlag= false;
		
//		try {
//			acquireWakeLock();
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
	
		
		context=  this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.i(TAG, "onStartCommand~~~~~~~~~~~~~~~~~~~~~");
		
		if (!Tools.getApplication(this).getLogin()) {
			DataBaseHelper dataHelper=new DataBaseHelper(this);
			//从本地加载用户
			dataHelper.loadUser();
			
		}
		
		// 初期化数据库访问对象
		dao = new DaoBase(this);
		dao.openDb();
//		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_CHILD) {
		// 初期化 锁服务
		if (null != lockService) {
			lockService.setDao(dao);
			lockService.initService();
		}
		
//		sentLocationThreadFlag = true;
		
		// 发送位置线程
//		sentLocationThread = new SentLocationThread(this);
//		sentLocationThread.sentLocationThread_is_exit = false;
//		sentLocationThread.start();
		
//		}
		
//		Log.d(TAG, "设定现在的Activity名字："+Main.class.getName());
//		Tools.getApplication(this).setNowActivityName(Main.class.getName());
		
		final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
		IntentFilter filter = new IntentFilter(SMS_RECEIVED); 
		filter.setPriority(Integer.MAX_VALUE);
		receiver = new ShortMessageReceiver();
		registerReceiver(receiver, filter);
		
		
		sendAlarmSetting();
		return Service.START_STICKY;
	}

	public LockService getLockService() {
		return lockService;
	}
	
	public static void sendAlarmSetting() {

		if (null == mAlarmSender) {
			mAlarmSender = PendingIntent.getService(context,
	                0, new Intent(context, UploadLocationService.class), 0);
		} 
		
		AlarmManager am = (AlarmManager)context.getSystemService(ALARM_SERVICE);
		am.cancel(mAlarmSender);
		
		SharedPreferences settings = context.getSharedPreferences("setting", 0);
		String temp_time = settings.getString("time","2"); // 默认2
		int time = Integer.valueOf(temp_time);
		
		long firstTime = SystemClock.elapsedRealtime();
		
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                         firstTime, time*60*1000, mAlarmSender);
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy~~~~~~~~~~~~~~~~~~~~~");
		try {
			if (null != dao) {
				dao.closeDB();
			}
			
			if (null != lockService) {
				lockService.stopLockCheckThread(this);
			}
			
			if (null != receiver) {
				unregisterReceiver(receiver);
			}
			
			AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.cancel(mAlarmSender);
//			
//			if (null != sentLocationThread) {
//				sentLocationThread.sentLocationThread_is_exit = true;
//			}
//			
//			try{
//				releaseWakeLock();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	WakeLock wakeLock;
	private void acquireWakeLock() {       
		  if (wakeLock == null) {         
			  PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);     
			  wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName()); 
			  wakeLock.acquire();  
		  }   
	} 
	
	private void releaseWakeLock() {
	      if (wakeLock != null && wakeLock.isHeld()) {
	           wakeLock.release();
	           wakeLock = null;
	      }
	}

}

