package cn.duocool.lashou.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.PushClient;
import cn.duocool.lashou.net.client.PushData;
import cn.duocool.lashou.net.client.PushListener;


/**
 * 用于系统推送的服务
 * 
 * @author xwood
 *
 */
public class PushService extends Service implements PushListener {
	
	// 用户系统推送消息的线程
	private PushClient pushClient;
	
	/**
	 * 如果想要得到数据就是用这个接口
	 */
	private PushListener onPushListener;
	
	/**
	 * 如果想要得到数据也可以使用Handler
	 */
	private Handler handler;
	
	/**
	 * 如果使用handler接收数据，请使用这个常量来辨别 what值
	 */
	public final static int HANDLER_WHAT = 900001;
	
	public final static String ACTION_BROADCAST = "cn.duocool.lashou.service.PushService.broadcast";
	
	public synchronized void reStart() {
		pushClient.reStart();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (null != pushClient) {
			pushClient.isRunning = false;
		}
	}
	
	private IBinder mBinder = new LocalService();
	public class LocalService extends Binder{
		public PushService getService() {
			return PushService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public void setUserId(String userId) {
		pushClient.setUserId(userId);
	}
	
	@Override
	public void onCreate() {
		pushClient =  new PushClient(this);
		pushClient.setPushListener(this);
		//pushClient.setUserId("50");
		pushClient.start();
	}

	@Override
	public void onTransmitted(PushData data) {
		Log.d("testssdfas", "sdfsadfsdfsd");
		if (null != onPushListener) {
			onPushListener.onTransmitted(data);
		}
		
		if (null != handler) {
			Message msg =  handler.obtainMessage();
			msg.what = HANDLER_WHAT;
			msg.obj = data;
			handler.sendMessage(msg);
		}
		
		Intent intent = new Intent();
		intent.setAction(ACTION_BROADCAST);
		Bundle bundle = new Bundle();
		bundle.putSerializable("data", data);
		intent.putExtras(bundle);
		sendOrderedBroadcast(intent, null);
		
		
	}
	
	public PushClient getPushClient() {
		return pushClient;
	}

	public void setPushClient(PushClient pushClient) {
		this.pushClient = pushClient;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public PushListener getOnPushListener() {
		return onPushListener;
	}

	public void setOnPushListener(PushListener onPushListener) {
		this.onPushListener = onPushListener;
	}
}
