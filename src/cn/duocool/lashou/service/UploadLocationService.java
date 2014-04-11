package cn.duocool.lashou.service;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.net.client.LocationData;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.utils.Tools;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 用于记录用户位置和发送用户位置信息给服务器的Service
 *
 */
public class UploadLocationService extends Service implements BDLocationListener {
	
	private static final String TAG = UploadLocationService.class.getName();

	
	private LocationClient mLocationClient = null;//位置服务连接
	public static int time = 20;//发起定位请求的间隔时间,单位秒，默认20秒
	public static int userID = 0;//用户ID
	private int count = 0;//定位次数,list中当前条数

	private SharedPreferences location;//存放位置信息的SharedPreferences
	
	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			if (msg.what == 198) {
				requestRet = mLocationClient.requestLocation();
				Log.e(TAG,"请求定位 开始 : "+requestRet);
			}
			
			super.handleMessage(msg);
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG,"into onBind");
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG,"into onCreate");
		super.onCreate();
		
		//初始化地图管理类，定位需要
		if (MyApplication.mBMapManager == null) {
			MyApplication.mBMapManager = new BMapManager(getApplication());
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			MyApplication.mBMapManager.init(MyApplication.strKey,new MyApplication.MyGeneralListener());
		}

	

		mLocationClient = new LocationClient(this);//声明LocationClient类 
		mLocationClient.registerLocationListener( this );//注册监听函数
		
		

		//设置定位参数包括：定位模式（单次定位，定时定位），返回坐标类型，是否打开GPS等等
		LocationClientOption option = new LocationClientOption();
		option.disableCache(true);//设置是否启用缓存定位，true表示禁用缓存定位，false表示启用缓存定位。
		//判断用户是否打开GPS
		if(Tools.check_GPS_is_open(this)){
			//用户已经打开GPS
			option.setOpenGps(true);//设置打开GPS
		}else{
			option.setOpenGps(false);
		}
		option.setAddrType("all");//返回的定位结果不包含地址信息 all表示包含
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		//option.setScanSpan(time*1000);//设置发起定位请求的间隔时间为time*1000ms
		option.setPoiNumber(0);	//最多返回POI个数		
//		option.setPriority(LocationClientOption.GpsFirst);
		mLocationClient.setLocOption(option);
		
		//mLocationClient.requestLocation();
		//mLocationClient.
		mLocationClient.start();//启动获取位置的连接
	}
	
	int requestRet = 0;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG,"into onStartCommand");
		super.onStartCommand(intent, flags, startId);
		
		//获取mLocationClient，若未连接，则连接
		if(mLocationClient!=null){
			if(!mLocationClient.isStarted()){
				mLocationClient.start();
			}
//			
//			// 请求定位
//			long startTime = SystemClock.elapsedRealtime();
//			do {
//				
//				Log.e(TAG,"请求定位 开始");
				requestRet = mLocationClient.requestLocation();
				Log.e(TAG,"请求定位 开始1:"+requestRet);
				
//				long endTime = SystemClock.elapsedRealtime();
//				
//				while((endTime - startTime) <= 1000) {
//					endTime = SystemClock.elapsedRealtime();
//				}
//				Log.e(TAG,"请求定位 startTime endTime " + startTime + " " +endTime +"时间差："+ (endTime - startTime));
//				startTime = endTime;
//			} while(requestRet != 0);
			
			
//			Log.e(TAG,"请求定位 ret : "+requestRet);
		}
		
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG,"into onDestroy");
		super.onDestroy();
		
//		if (null != bMapManager) {
//			bMapManager.stop();
//			bMapManager.destroy();
//		}

		//获取mLocationClient，若存在，则停止
		if(mLocationClient!=null){
			mLocationClient.stop();
		}

		//将用户最后的位置信息保存到SharedPreferences--location
		if(LashouService.locationList.size()!=0){
			location = getSharedPreferences("location", 0);
			SharedPreferences.Editor editor = location.edit();
			String temp=LashouService.locationList.get(LashouService.locationList.size()-1).getLatitude()+","+LashouService.locationList.get(LashouService.locationList.size()-1).getLongitude();
			editor.putString("location",temp);
			editor.commit();
		}
	}


	//以下为百度定位BDLocationListener接口的实现
	@Override
	public void onReceiveLocation(BDLocation location) {
		//接收异步返回的定位结果，参数是BDLocation类型参数
		
//		if (0 != requestRet) {
//			int ret = mLocationClient.requestLocation();
//			Log.i(TAG,"请求定位 retBBB : "+ret);
//			return;
//		}
		
		if (0 != requestRet) {
			Log.i(TAG,"请求定位没成功 不上传 ret : "+requestRet);
			handler.sendEmptyMessageDelayed(198, 1000); // 延迟1秒在次请求
			return;
		}

		//判断当前用户是否已经登录，若已登录，则记录ID，若未登录，则ID=0。
		if(Tools.getApplication(this).getLogin()){
			//已登录
			userID = Tools.getApplication(this).getMyInfo().getUserId();
		}
		Log.d(TAG,"userID:"+Integer.toString(userID));
		//获取定位结果
		MyLocation temp_My_Location = new MyLocation();
		temp_My_Location.setUserID(userID);
		temp_My_Location.setLatitude(location.getLatitude());
		temp_My_Location.setLongitude(location.getLongitude());
		temp_My_Location.setTime(location.getTime());
		temp_My_Location.setAddress(location.getAddrStr());
		//判断是否初次定位
		if(count == 0){
			LashouService.locationList.add(temp_My_Location);
			count = 1;
		}
		//距离判断,移动未超过20米，则默认未移动,结果不记录
		Double juli;
		GeoPoint point1 = new GeoPoint((int)(location.getLatitude()), (int)(location.getLongitude()));//当前位置
		GeoPoint point2 = new GeoPoint((int)(LashouService.locationList.get(count-1).getLatitude()),(int)(LashouService.locationList.get(count-1).getLongitude()));//上一个位置
		juli = DistanceUtil.getDistance(point1, point2);
		if(juli>=20){
			//移动超过20米,增加一条记录
			//增加之前先判断，list是否已满，若已满，则删除第一条记录
			if(count == LashouService.length){
				LashouService.locationList.remove(0);
				count--;
			}
			LashouService.locationList.add(temp_My_Location);
			count++;
		}else{
			//用当前位置取代上一条记录
			LashouService.locationList.set(count-1, temp_My_Location);
		}
		Log.d(TAG,"locationList.size():"+Integer.toString(LashouService.locationList.size()));
		
		
		if(Tools.getApplication(this).getLogin()){
			userID = Tools.getApplication(this).getMyInfo().getUserId();
			Log.i(TAG, "发送位置3");
			if (null != LashouService.locationList && LashouService.locationList.size() > 0) {
				Log.i(TAG, "发送位置4");

				NetClient netClient = new NetClient();
				LocationData data = new LocationData();
				data.setLocationOrder(111);//无作用了
				data.setAddress(LashouService.locationList.get(LashouService.locationList.size()-1).getAddress());
				data.setLatitude(LashouService.locationList.get(LashouService.locationList.size()-1).getLatitude());
				data.setLongitude(LashouService.locationList.get(LashouService.locationList.size()-1).getLongitude());							
				data.setTime(LashouService.locationList.get(LashouService.locationList.size()-1).getTime());
				data.setUserID(userID);

						netClient.uploadLocation(888,Integer.toString(userID),data);
//				netClient.uploadLocationSync(Integer.toString(userID),data);
			}
		}
		
		// 关掉服务（为了省电）
		this.stopSelf();
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		//接收异步返回的POI查询结果，参数是BDLocation类型参数

	}
}
