package cn.duocool.lashou.activity;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.broadcastreceiver.ShortMessageReceiver;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.service.ElectronicFenceService;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LocationService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.service.PushService;
import cn.duocool.lashou.service.PushService.LocalService;
import cn.duocool.lashou.thread.SentLocationThread;
import cn.duocool.lashou.utils.AppTools;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.PushData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Main extends BaseActivity 
implements OnClickListener,ServiceConnection,NetTranListener {
	private final static String TAG = Main.class.getName();

	PushService pushService;
	private boolean connectedService=false;
	private Handler handler=null; 
	MyDialog dialog;
	
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// 检查是否设定版本信息
		SharedPreferences sp 
			= getSharedPreferences(CommDef.PREFERENCE_NAME,Context.MODE_PRIVATE);
		
		int editionInfo = sp.getInt(CommDef.EDITION_KEY, -1);
		if (editionInfo==-1) { // 没有版本信息
			// 去版本选择界面
			startActivity(new Intent(this, EditionSettingActivity.class));
			this.finish();
			return;
		} else {
			Tools.getApplication(this).setLashouEdition(editionInfo);
		}
		
		TextView tvEditionName = (TextView)findViewById(R.id.editionName);
		if (editionInfo == CommDef.EDITION_CHILD) {
			tvEditionName.setText(R.string.EditionChildName);
		} else {
			tvEditionName.setText(R.string.EditionParentName);
		}
		//其实是判断应用程序有没有被杀掉
		if(!Tools.getApplication(this).isFirstRun){
			startActivity(new Intent(this, ActivityHome.class));
			this.finish();
			return ;
		} 
		
		Tools.getApplication(this).isFirstRun=false;
		DataBaseHelper dataHelper=new DataBaseHelper(this);
		//从本地加载用户
		dataHelper.loadUser();
		
		startService(new Intent(this, LashouService.class));
//		startService(new Intent(this, LocationService.class));
		startService(new Intent(this, PushService.class));
		startService(new Intent(this, ElectronicFenceService.class));
		
		Intent lashouIntent=new Intent(this, LashouService.class);
		bindService(lashouIntent, this, Context.BIND_AUTO_CREATE);
		
//		//如果已经登录则创建服务push
//		if(Tools.getApplication(this).getLogin()){
////			Intent service=new Intent(this, PushService.class);
////			bindService(service, this, Context.BIND_AUTO_CREATE);
//			
//			//用户已登录，开启发送用户位置的线程
//			SentLocationThread sentLocationThread = new SentLocationThread(this);
//			SentLocationThread.sentLocationThread_is_exit = false;
//			SentLocationThread.userID = Tools.getApplication(this).getMyInfo().getUserId();
//			sentLocationThread.start();
//		} else {
//			// 跳转页面
//			handler=new Handler();
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//// 					Intent intent = new  Intent(Main.this,ActivityHome.class);
//// 					startActivity(intent);
//// 					Main.this.finish();
//				}
//			}, 2000);
//		}
		findViewById(R.id.ivStartIcon).setOnClickListener(this);
		
		AppTools appTools  = new AppTools(this);
		// 有弹出窗口的检查
		appTools.checkUpdate.setCheckModel(AppTools.CHECK_MODE_BACK);
		appTools.checkUpdate.checkUpdateStart();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(connectedService){
			unbindService(this);
		}
	}
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		Log.d(TAG,"~~~~Main onServiceConnected~~~~~~");
		LashouService lashouService = ((LashouService.LocalServer)service).getService();
		
		Tools.getApplication(this).setLashouService(lashouService);
		
//		pushService=((LocalService) service).getService();
		connectedService=true;
//		handler=new Handler();
//		// 跳转页面
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
////				Intent intent = new  Intent(Main.this,ActivityHome.class);
////				startActivity(intent);
////				Main.this.finish();
//			}
//		}, 2000);
//
//
//		pushService.setHandler(handler);
	}
	@Override
	public void onServiceDisconnected(ComponentName name) {
//		connectedService=false;
	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) {

		case R.id.ivStartIcon:
//			Intent intent = new  Intent();
//			intent.putExtra("lockName", LockService.lock1Name);
//			intent.putExtra("gotoActivity", "ActivityHome");
//			if(LockService.lock1Pwd.equals(""))
//			{
//				
//				intent.setClass(this, SetImagePasswordActivity.class);
//			
//			}else
//			{
//				if(LockService.lock1Type.equals("image"))
//				{
//				intent.setClass(this, ImageLockActivity.class);
//				}else
//				{
//					intent.setClass(this, PasswordActivity.class);
//				}
//				intent.putExtra("password",LockService.lock1Pwd);
//			}
//			   startActivity(intent);
//			   Log.d("tag", "lock1Pwd="+LockService.lock1Pwd);
//			finish();
		SharedPreferences sp 
			= getSharedPreferences(CommDef.PREFERENCE_NAME,Context.MODE_PRIVATE);
		 
		  boolean isfirst= sp.getBoolean(CommDef.FIRSTOPEN_KEY, true);
		  Intent intent;
		  if(isfirst){
			  intent = new  Intent(this,StartActivity.class); 
		  }else{
			  intent = new  Intent(this,ActivityHome.class);
		  }
 			Editor editor=sp.edit();
 			editor.putBoolean(CommDef.FIRSTOPEN_KEY, false);
 			editor.commit();
			
 			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {


	} 
}
