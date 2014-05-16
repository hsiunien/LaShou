package cn.duocool.lashou.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ShowMapActivity;
import cn.duocool.lashou.model.MyElectronicFence;
import cn.duocool.lashou.model.MyLocationRemind;
import cn.duocool.lashou.net.client.LocationData;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.ResponseData;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ElectronicFenceService extends Service {
	
	private static final String TAG = ElectronicFenceService.class.getName();
	private ArrayList<MyElectronicFence> mElectronicFencesList; // 电子围栏List
	private boolean isRunning = true; //是否退出线程标志
	
	private Thread thread;
	
	// 同步用标志
	public String flagSync = "abcedfg";
	
	public IBinder mBinder = new LocalServer();
	public class LocalServer extends Binder {
		public ElectronicFenceService getService() {
			return ElectronicFenceService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void updateData() {
		synchronized (flagSync) {
			// 从数据库中读取(更新检查的数据)
			mElectronicFencesList = MyElectronicFence.GetElectronicFence(ElectronicFenceService.this);
		}
	}


	@Override
	public void onCreate() {
		Log.d(TAG, "into onCreate");
		super.onCreate();

		// 电子围栏List
//		mElectronicFencesList = new ArrayList<MyElectronicFence>();

		//从数据库中读取
		synchronized (flagSync) {
			mElectronicFencesList = MyElectronicFence.GetElectronicFence(this);
			if(null == mElectronicFencesList || mElectronicFencesList.size() <= 0){
				//没有围栏
				return;
			}
		}

		isRunning = true;

		// 开启新线程，用于发送请求
		thread = new Thread(new Runnable() {	
			@Override
			public void run() {
				
				// 处理电子围栏信息
				while(isRunning) {
					
					// 检查位置
					synchronized (flagSync) {
						try{
						checkFences(mElectronicFencesList);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(CommDef.FENCECYCLE);//检测频率
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
			}
		});
		thread.start();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		clearNotification();
	}
	
	/**
	 * 检查围栏
	 */
	private void checkFences(ArrayList<MyElectronicFence> electronicFencesList) {
		if (null == electronicFencesList || electronicFencesList.size() <= 0) {
			return;
		}
		// 把设定好的电子围栏一个个的取出来。挨着个的检查
		for (MyElectronicFence  myElectronicFence : electronicFencesList) {
			
			Double nowLatitude = myElectronicFence.getLatitude();
			Double nowLongitude = myElectronicFence.getLongitude();
			boolean is_In = myElectronicFence.getIs_In();
			boolean is_Out = myElectronicFence.getIs_Out();
			int inoutFlag = myElectronicFence.getInoutFlag();
			
			// 获得检查对象 ID(已经添加了关心的家人)
			List<Integer> userIdList =myElectronicFence.getMonitoredPersonIDs();
			List<String> userNameList = myElectronicFence.getMonitoredPersonNames();
			// 如果么没有数据，就取一下条
			if  (null == userIdList || userIdList.size() <=0) {
				continue;
			}
			
			for (int i = 0;i<userIdList.size();i++) {
				Integer userId = userIdList.get(i);
				if (null == userId) {
					continue;
				}
				String userName = userNameList.get(i);
				// 关心的家人ID
				String userIdStr = userId.toString();
				
				NetClient netClient = new NetClient();
				
				// 获得系统时间
				Calendar sCalendar = Calendar.getInstance();
				sCalendar.setTime(new Date(System.currentTimeMillis()));
				sCalendar.add(Calendar.MINUTE, -2);
				Date dateMin = sCalendar.getTime();
				sCalendar.add(Calendar.MINUTE, 10);
				Date dateMax = sCalendar.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String maxTime = sdf.format(dateMax);
				String minTime = sdf.format(dateMin);
				
				// 使用同步版（子线程里最好不要使用syncTask）
//				ResponseData data  = netClient.getLocationListBySync(userIdStr, "1");
				ResponseData data  = netClient.getLocationByTimeSync(userIdStr, minTime, maxTime);
				if (null != data && null != data.getLocationDataList()) {
					if ( data.getLocationDataList().size() > 0 ) {
						// 获得当前查询用户的位置（最后一条，就是最新的一条）
						LocationData userLocation=data.getLocationDataList().get(0);
						double latitude=userLocation.getLatitude();//纬度
						double longitude=userLocation.getLongitude();//经度】
						// 转成GeoPoint 对象
						GeoPoint gPoint1 = new GeoPoint((int)(latitude*1E6), (int)(longitude*1E6));
						
						ArrayList<MyLocationRemind> remindList = MyLocationRemind.QueryRemindByTime(userLocation.getTime(), this);
						if (null != remindList && remindList.size() >0) {
							return;
						}
						
						// 判断一个点是否在电子围栏里,是则返回true
						boolean isInFence = myElectronicFence.is_In_electronic_fence(gPoint1);
						
						
						
						// is_In;//是进入提醒，存入数据库为int 0,1 1为true
						// is_Out;//是离开提醒，存入数据库为int 0,1 1为true
						if (is_In && isInFence && (inoutFlag == 0 || inoutFlag == 2)) { // 是进入提醒   并且已经进入   记录里面记得是初期值 或者 已经离开
							// 触发进入提醒

							// 通知栏消息提醒
							showNotification(myElectronicFence,userName);
							// 给该围栏添加进出记录 
							MyLocationRemind myLocationRemind = new MyLocationRemind(myElectronicFence.getID(), 0, userId.intValue(), userLocation.getTime(), nowLatitude, nowLongitude,userName);
							MyLocationRemind.SaveMyLocationRemind(myLocationRemind, ElectronicFenceService.this);
							// 该围栏的进出记录+1
							myElectronicFence.setK(myElectronicFence.getK()+1);
							myElectronicFence.setInoutFlag(1);
							MyElectronicFence.UpdateElectronicFence(myElectronicFence, ElectronicFenceService.this);
							Log.d(TAG, "电子围栏进入："+myLocationRemind.getMonitoredPerson_ID()+"");
						} else if (is_Out && !isInFence && (inoutFlag == 0 || inoutFlag == 1))  { // 是离开提醒  并且已经离开 记录里面记得是初期值 或者 已经离开
							//触发离开提醒

							//通知栏消息提醒
							showNotification(myElectronicFence,userName);
							//给该围栏添加进出记录 
							MyLocationRemind myLocationRemind = new MyLocationRemind(myElectronicFence.getID(), 1, userId.intValue(), userLocation.getTime(), nowLatitude, nowLongitude,userName);
							MyLocationRemind.SaveMyLocationRemind(myLocationRemind, ElectronicFenceService.this);
							//该围栏的进出记录+1
							myElectronicFence.setK(myElectronicFence.getK()+1);
							myElectronicFence.setInoutFlag(2);
							MyElectronicFence.UpdateElectronicFence(myElectronicFence, ElectronicFenceService.this);
							Log.d(TAG, "电子围栏离开："+myLocationRemind.getMonitoredPerson_ID()+"");
						}
					}
				}
			}
		}
	}


	/**
	 * 在状态栏显示通知
	 */
	private void showNotification(MyElectronicFence myElectronicFence,String userName){
		// 创建一个NotificationManager的引用   
		NotificationManager notificationManager = (NotificationManager)    
				this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);   

		// 定义Notification的各种属性   
		Notification notification =new Notification(R.drawable.icon_in,   
				myElectronicFence.getTitle(), System.currentTimeMillis()); 
		//FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
		//FLAG_NO_CLEAR      该通知不能被状态栏的清除按钮给清除掉
		//FLAG_ONGOING_EVENT 通知放置在正在运行
		//FLAG_INSISTENT     是否一直进行，比如音乐一直播放，知道用户响应
		notification.flags |= Notification.FLAG_AUTO_CANCEL; // 将此通知放到通知栏的"Ongoing"即"正在运行"组中   
//		notification.flags |= Notification.FLAG_NO_CLEAR; // 表明在点击了通知栏中的"清除通知"后，此通知不清除，经常与FLAG_ONGOING_EVENT一起使用   
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;   
		//DEFAULT_ALL     使用所有默认值，比如声音，震动，闪屏等等
		//DEFAULT_LIGHTS  使用默认闪光提示
		//DEFAULT_SOUNDS  使用默认提示声音
		//DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
		//叠加效果常量
		notification.defaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;
		notification.ledARGB = Color.BLUE;   
		notification.ledOnMS =5000; //闪光时间，毫秒

		// 设置通知的事件消息   
		CharSequence contentTitle =myElectronicFence.getTitle(); // 通知栏标题   
		CharSequence contentText ="新的位置提醒"; // 通知栏内容   
		Intent notificationIntent =new Intent(ElectronicFenceService.this, ShowMapActivity.class); // 点击该通知后要跳转的Activity   
		notificationIntent.putExtra("titlebarString", myElectronicFence.getTitle());
		notificationIntent.putExtra("Latitude", myElectronicFence.getLatitude());
		notificationIntent.putExtra("userName", userName);
		notificationIntent.putExtra("Longitude", myElectronicFence.getLongitude());
		notificationIntent.putExtra("address", myElectronicFence.getAddress());
		notificationIntent.putExtra("r", myElectronicFence.getR());
		PendingIntent contentItent = PendingIntent.getActivity(ElectronicFenceService.this, 0, notificationIntent, 0);   
		notification.setLatestEventInfo(ElectronicFenceService.this, contentTitle, contentText, contentItent);   

		// 把Notification传递给NotificationManager   
		notificationManager.notify(0, notification); 
		//clearNotification();//删除通知
	}

	//删除通知    
	private void clearNotification(){
		// 启动后删除之前我们定义的通知   
		NotificationManager notificationManager = (NotificationManager) ElectronicFenceService.this 
				.getSystemService(NOTIFICATION_SERVICE);   
		notificationManager.cancel(0);  

	}
}
