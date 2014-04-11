package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.MainActivity;
import cn.duocool.lashou.R;
import cn.duocool.lashou.adapter.LockAppListAdapter;
import cn.duocool.lashou.adapter.MySimpleAdapter;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.model.AppModel;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockSystemInfo;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.mywidget.WiperSwitch.OnChangedListener;


import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;
//程序锁主界面
public class AppLockMainActivity extends BaseActivity {
	
	private final static String TAG = AppLockMainActivity.class.getName();

	private LockAppListAdapter adapter;// 声明适配器对象
	private ListView listView;   // 声明列表视图对象
	// 页面加载进度
	private ProgressDialog progressDialog=null;
	
	// 拉手服务
	private LashouService lashouService;
	// 拉手业务逻辑
	private LockService lockService;
	private Map<String, LockAppInfo> allAppMap;
	
	private WiperSwitch wiperSwitch;
	
	List<LockAppInfo> appAdapterList;
	
	/**
	 * 当前界面的handler
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			Log.d(TAG, "handleMessage");
			
			if (msg.what == 100) { // 已经连上了 服务
				appAdapterList = new ArrayList<LockAppInfo>();
				appAdapterList.clear();
				if (null != allAppMap && allAppMap.size() > 0) {
					
					for (String key : allAppMap.keySet()) {
						LockAppInfo lockAppInfo = allAppMap.get(key);
						// 有些包是 不想让用户看到 但是 还是要拦截
						if (!lockAppInfo.getPackageName().equals("com.android.packageinstaller")) { 
							appAdapterList.add(lockAppInfo);
						}
					}
					adapter = new LockAppListAdapter(AppLockMainActivity.this, appAdapterList);
					listView.setAdapter(adapter);
				}
				
		        if(null != progressDialog) {
		        	 progressDialog.dismiss();
		        	 progressDialog = null;
		        }
		        
		        if (null == allAppMap || allAppMap.size() <= 0) {
		        	Toast.makeText(AppLockMainActivity.this, "没有可以锁的应用程序", Toast.LENGTH_SHORT).show();
		        }
		        
		        LockDao lockDao = new LockDao(AppLockMainActivity.this,lockService.getDao());
		        LockSystemInfo lockSystemInfo = lockDao.getLockAllInfo();
				if(lockSystemInfo.getLockallstate()==1) {
					wiperSwitch.setisLock(true);
				} else {
					wiperSwitch.setisLock(false);
				}
		        
			}
	        
	        super.handleMessage(msg);
		}
	};
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			lashouService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			lashouService = ((LashouService.LocalServer)service).getService();
			lockService = lashouService.getLockService();
			
			allAppMap = lockService.getAllAppMap();
			
			handler.sendEmptyMessage(100);
//			lockService.getExceptionAppMap();
		}
	};
	

	
//	public  List<Map<String, Object>> list;// 声明列表容器
//	public static AppLockMainActivity ma;
//	WiperSwitch wiperSwitch;
//	 Dialog dialog;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_activity_main);
		
		Intent lashouIntent = new Intent(this,LashouService.class);
		bindService(lashouIntent, serviceConnection, Context.BIND_AUTO_CREATE);
		
		listView=(ListView) findViewById(R.id.listView1);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) 
			{
				Intent intent=new Intent();
				intent.setClass(AppLockMainActivity.this, ApplockSingleSetting.class);
				intent.putExtra("appName", ((LockAppInfo)arg0.getItemAtPosition(position)).getAppName());//传递应用程序名
				BitmapDrawable bd = (BitmapDrawable) ((LockAppInfo)arg0.getItemAtPosition(position)).getAppIcon();//先把drawabled转化成
            
				Bitmap bm = bd.getBitmap();
				intent.putExtra("appicon", bm);
				intent.putExtra("packageName", ((LockAppInfo)arg0.getItemAtPosition(position)).getPackageName());
				AppLockMainActivity.this.startActivity(intent);
			}
		});
		
		if (null == progressDialog) {
			progressDialog = ProgressDialog.show(
					this, 
					getString(R.string.lockAppMainLoadingTitle), 
					getString(R.string.lockAppMainLoadingMsg),
					true,
					false);
		}
		
		wiperSwitch = (WiperSwitch)findViewById(R.id.wiperSwitch1);

//		if(LockService.lockallstate==1)
//		{
//			wiperSwitch.setisLock(true);
//		}else
//		{
//			wiperSwitch.setisLock(false);
//		}
		wiperSwitch.setOnChangedListener(new OnChangedListener() {
			
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, final boolean checkState) {
				if (null == lashouService){
					return;
				}
				String temp=checkState?"打开":"关闭";
				progressDialog = ProgressDialog.show(AppLockMainActivity.this, "请稍等...", "正在"+temp+"...", true,false);
				   new Thread(new Runnable() {
					
					@Override
					public void run() {
						 int toState = checkState?1:0;
						 LockDao lockDao = new LockDao(AppLockMainActivity.this,lockService.getDao());
					     lockDao.updateLockAllState(toState);
					     
					     Map<String, LockAppInfo> mapData = lockService.getAllAppMap();
					     
					     for (String key : mapData.keySet()) {
					    	LockAppInfo lockAppInfo = mapData.get(key);
					    	lockAppInfo.setIslock(toState);
					    	lockDao.updateAppIsLockByAppId(toState, lockAppInfo.getAppId());
					     }
					     
					     
					     handler.sendEmptyMessage(100);
					}
					
					
				}).start();	
			}
		});
		
//		//显示正在加载动画
//		LayoutInflater inflater = LayoutInflater.from(this);
//		View v = inflater.inflate(R.layout.dialogview, null);
//        
//		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
//         
//		// main.xml中的ImageView
//		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
//		// 加载动画
//		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this,
//				R.anim.loadinganimation);
//		// 使用ImageView显示动画
//		spaceshipImage.startAnimation(hyperspaceJumpAnimation);

		//dialog = new Dialog(AppLockMainActivity.this, R.style.FullHeightDialog);
		//dialog.setCancelable(true);
		//dialog.show();
		//dialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		//LinearLayout.LayoutParams.WRAP_CONTENT));
//		if(LockService.list.size()==0)
//		{
//		progressDialog = ProgressDialog.show(AppLockMainActivity.this, "请稍等...", "正在获取数据...", true,false);
//         new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				boolean isrun=true;
//				while(isrun)
//				{
//				      if(LockService.list.size()!=0)
//				      {
//				    	  isrun=false;
//				    	  list=LockService.list;
//				    	  myHandler.sendEmptyMessage(0);
//				    	 
//				    	 
//				    	  
//				      }
//				}
//				
//			}
//		}).start();
//		}
		
		
		
		
//		 ma = this;
		 
	        // 实例化列表容器
	     //   list = MainActivity.list;
//	       for(int i=0;i<5;i++)
//	       {
//	        // 实例一个列表数据容器
//	        Map<String, Object> map = new HashMap<String, Object>();
//	        // 往列表容器中添加数据
//	        map.put("item1_imageivew", R.drawable.ic_launcher);
//	        map.put("item1_bigtv", "BIGTV"+i);
//	        map.put("item1_smalltv", "SMALLTV"+i);
//	        // 将列表数据添加到列表容器中
//	        list.add(map);
//	        }
	     // list= getInfos(this);//获取系统程序列表
//		 list=LockService.list;
//	        // --使用系统适配器，无法实现组件监听；
//	        // //实例适配器
//	        adapter = new MySimpleAdapter(this, list, R.layout.applock_appitem, new String[] {
//	                "item1_imageivew", "item1_bigtv", "item1_smalltv" }, new int[] {
//	                R.id.iv, R.id.bigtv, R.id.smalltv });
//	        listView.setAdapter(adapter);

	 
	       
//	        
	        
	}

	
	// 获取所有的在手机上安装的应用程序的信息
//      public static   List<Map<String, Object>> getInfos(Context context) 
//	  {  
//		       List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
//		       
//	          
//	        // 获取所有的在手机上安装的应用程序的信息, 包括哪些被卸载了的但是没有清空数据的应用程序  
//		       PackageManager pm=context.getPackageManager();
//	        List<PackageInfo> packageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);  
//	        DatabaseHelper dbHelper=new DatabaseHelper(context);
//			 SQLiteDatabase db=dbHelper.getReadableDatabase();
//	        for (PackageInfo packageInfo : packageinfos)
//	        {  
//	        	Map<String, Object> map = new HashMap<String, Object>();
////	            String packname = packageInfo.packageName;  
////	            info.setPackname(packname);  
////	            String version = packageInfo.versionName;  
////	            info.setVersion(version);  
//	            Drawable appicon = packageInfo.applicationInfo.loadIcon(pm);  
//	            map.put("item1_imageivew", appicon) ;//应用图标
//	            String appname = packageInfo.applicationInfo.loadLabel(pm) + "";  
//	            map.put("item1_bigtv", appname);//应用名称
//	            
//	            String packname=packageInfo.packageName;
//	            map.put("packname", packname);//包名
//	            ApplicationInfo applicationinfo = packageInfo.applicationInfo;  
//	            map.put("item1_smalltv", "");
//	            //判断在数据库里面是否存在，然后是否锁定并改相应的list里面的项
//	            Cursor c = db.rawQuery("SELECT * FROM appList WHERE packageName = ?", new String[]{packname});
//	              if(c.moveToNext())
//	              {
//	            	  if(c.getInt(c.getColumnIndex("islock"))==1)
//	            	  {
//	            	 map.put("islock", 1); //1代表要锁
//	            	  }else
//	            	  {
//	            		  map.put("islock", 0);  //0代表不锁 
//	            	  }
//	              }else
//	              {
//	            	  map.put("islock", 0); 
//	              }
//	            
//	            list.add(map);
//	            map=null;
//	       
//	        }
//	        if(db!=null)
//			{
//			db.close();
//			}
//	   return list;
//	    }
  
	@Override
	protected void onPause() 
	{
		super.onPause();
	//	finish();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(serviceConnection);
	}
	
	
//	public void lockall(boolean checkState)
//	{
//		DatabaseHelper dbHlper=new DatabaseHelper(AppLockMainActivity.this);
//		SQLiteDatabase	db=dbHlper.getWritableDatabase();
//			if(checkState)
//			{
//				
//			   	for(int i=0;i<list.size();i++)
//			   	{
//			   		int islock= (Integer) list.get(i).get("islock");
//			   		if(islock==0)
//			   		{
//			   	     list.get(i).put("islock", 1);
//			   	  Cursor c = db.rawQuery("SELECT * FROM appList WHERE packageName = ?", new String[]{(String)list.get(i).get("packname")});
//					if(!c.moveToNext())
//					{
//			   	   ContentValues values=new ContentValues();
//				   values.put("packageName",(String)list.get(i).get("packname"));//锟斤拷锟斤拷菘锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
//				   values.put("islock", 1);
//				
//				   values.put("week", "请选择星期几可用");
//				   values.put("limitTime", "0"); //0代表没有限制
//				   values.put("lockId", 1);
//				   db.insert("appList", null, values);
//				   //int lockid=cursor.getInt(cursor.getColumnIndex("lockId"));
//					  //把密码和密码类型搞出来，存到applist
//				    int lockid=1;
//					  Cursor lockCursor=db.query("locks", new String[]{"id","name","password","passwordtype"}, "id=?", new String[]{lockid+""}, null, null, null);
//					  String password = null;
//					  String passwordtype= null;
//					  String lockName=null;
//					  if(lockCursor.moveToNext())
//					 {
//					  password=lockCursor.getString(lockCursor.getColumnIndex("password"));
//					  passwordtype=lockCursor.getString(lockCursor.getColumnIndex("passwordtype"));
//					  lockName=lockCursor.getString(lockCursor.getColumnIndex("name"));
//					 }
//				   LockService.appList.add(new AppModel((String)list.get(i).get("packname"),new ArrayList<Integer>(),"0",password,passwordtype,lockName)); 
//			   		}
//					else if(c.getInt(c.getColumnIndex("islock"))==0)
//					   {
//						  int lockid=c.getInt(c.getColumnIndex("lockId"));
//						  Cursor lockCursor=db.query("locks", new String[]{"id","name","password","passwordtype"}, "id=?", new String[]{lockid+""}, null, null, null);
//						  String password = null;
//						  String passwordtype= null;
//						  String lockName=null;
//						  if(lockCursor.moveToNext())
//						 {
//						  password=lockCursor.getString(lockCursor.getColumnIndex("password"));
//						  passwordtype=lockCursor.getString(lockCursor.getColumnIndex("passwordtype"));
//						  lockName=lockCursor.getString(lockCursor.getColumnIndex("name"));
//						 }
//						   ContentValues cv = new ContentValues(); 
//					        cv.put("islock", 1);
//					      
//					        db.update("appList", cv, "packageName = ?", new String[]{(String)list.get(i).get("packname")});  
//					       
//					        LockService.appList.add(new AppModel((String)list.get(i).get("packname"),applock_single_app_setting.stringToInt(c.getString(c.getColumnIndex("week"))),c.getString(c.getColumnIndex("limitTime")),password,passwordtype,lockName));
//					   
//					   }
//			   		}
//			   	}
//			}else
//			{
//				
//				
//				  int index=LockService.getApplistIndex("com.android.packageinstaller");
//				if(index==-1)
//				{
//					 LockService.appList.clear();
//				}else
//				{
//					 AppModel temp=LockService.appList.get(index);
//					
//					 LockService.appList.clear();
//					 LockService.appList.add(temp);
//				}
//				  for(int i=0;i<list.size();i++)
//			   	{
//			   		int islock= (Integer) list.get(i).get("islock");
//			   		
//			   		if(islock==1)
//			   		{
//			   			list.get(i).put("islock", 0);
//			   		  Cursor c = db.rawQuery("SELECT * FROM appList WHERE packageName = ?", new String[]{(String)list.get(i).get("packname")});
//					    if(c.moveToNext())
//					    {
//					    	if(c.getInt(c.getColumnIndex("islock"))==1)//锟窖此筹拷锟斤拷锟轿拷锟斤拷锟�
//							   {
//								   //Log.d("tag", "锟斤拷锟斤拷锟斤拷锟角�);
//								   ContentValues cv = new ContentValues(); 
//							        cv.put("islock", 0);
//							        //锟斤拷锟斤拷锟斤拷锟� 
//							        db.update("appList", cv, "packageName = ?", new String[]{(String)list.get(i).get("packname")});
////							     int index=LockService.getApplistIndex((String)list.get(i).get("packname"));
////							        
////							        LockService.appList.remove(index);
//							        //Log.d("tag", "锟斤拷锟斤拷锟斤拷莺锟�");
////							        for(int i=0;i<LockService.appList.size();i++)
////							 {
////								 Log.d("tag", LockService.appList.get(i).getPackageName());
////							 }
//							   }
//					    }
//			   		  
//			   		}
//			   	}
//			}
//			if(db!=null)
//			{
//			db.close();
//			}
//			
//	}


}
