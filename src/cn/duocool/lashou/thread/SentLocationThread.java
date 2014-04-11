package cn.duocool.lashou.thread;

import com.tencent.mm.sdk.platformtools.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LocationService;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.LocationData;
import cn.duocool.lashou.net.client.NetClient;

/**
 * 发送用户位置的线程
 * @author 杞桅
 *
 */
public class SentLocationThread extends Thread {
//	private final static String TAG = SentLocationThread.class.getName();
//	public boolean sentLocationThread_is_exit = false;//发送用户足迹的线程是否退出，true表示退出
//	public int userID;//用户ID
//	private int time;//足迹上传间隔时间
//	private Context context;
//
//	public SentLocationThread(Context context) {
//		this.context = context;
//	}
//	@Override
//	public void run() {		
////		Looper.prepare();
//		//获取设定的间隔时间
//		
//
//		NetClient netClient = new NetClient();
//		LocationData data = new LocationData();
//		long startTime = System.currentTimeMillis();
//		while (!sentLocationThread_is_exit && LashouService.sentLocationThreadFlag) {
//			long endTime = System.currentTimeMillis();
//			Log.i(TAG, "发送位置1");
//			try {
//				SharedPreferences settings = context.getSharedPreferences("setting", 0);
//				String temp_time = settings.getString("time","2"); // 默认2
//				time = Integer.valueOf(temp_time);
//				Log.i(TAG, "发送位置2");
//				if(Tools.getApplication(context).getLogin()){
//					userID = Tools.getApplication(context).getMyInfo().getUserId();
//					Log.i(TAG, "发送位置3");
//					if((endTime - startTime) > (time * 60 * 1000)) {
//						startTime = endTime;
//						if (null != LocationService.locationList && LocationService.locationList.size() > 0) {
//							Log.i(TAG, "发送位置4");
//							data.setLocationOrder(111);//无作用了
//							data.setAddress(LocationService.locationList.get(LocationService.locationList.size()-1).getAddress());
//							data.setLatitude(LocationService.locationList.get(LocationService.locationList.size()-1).getLatitude());
//							data.setLongitude(LocationService.locationList.get(LocationService.locationList.size()-1).getLongitude());							
//							data.setTime(LocationService.locationList.get(LocationService.locationList.size()-1).getTime());
//							data.setUserID(userID);
//	
//	//						netClient.uploadLocation(888,Integer.toString(userID),data);
//							netClient.uploadLocationSync(Integer.toString(userID),data);
//						}
//					}
//				} else {
//					Thread.sleep(2000);
//					continue;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
}
