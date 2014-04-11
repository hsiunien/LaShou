package cn.duocool.lashou.service;

import cn.duocool.lashou.mywidget.Applock_CountdownTipView;
import cn.duocool.lashou.mywidget.Applock_CountdownTipView.ServiceListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class AppLockTipService extends ServiceBase {

	private Context context;
	private Applock_CountdownTipView actv;

	public AppLockTipService(Context context) {
		this.context = context;
	}

	// public void onCreate() {
	// super.onCreate();
	//
	// new Applock_CountdownTipView(this,this).fun();
	//
	// }

	public void showTip(long time) {
		if (null == actv) {
			actv = new Applock_CountdownTipView(context);
			actv.fun();
		}

//		Intent intent = new Intent();
//		time = intent.getIntExtra("time", 0);
//
//		Bundle data = new Bundle();
//		data.putString("tip", turntotime(time));
//		Message msg = new Message();
//		msg.setData(data);
		actv.sendMsg( turntotime(time));

	}

	// @Override
	// public int onStartCommand(Intent intent, int flags, int startId)
	// {
	// // TODO Auto-generated method stub
	// mIntent = intent;
	// time=intent.getIntExtra("time", 0);
	//
	//
	//
	// Bundle data=new Bundle();
	// data.putString("tip", turntotime(time));
	// Message msg=new Message();
	// msg.setData(data);
	// Applock_CountdownTipView.handler.sendMessage(msg);
	//
	//
	//
	//
	// return super.onStartCommand(intent, flags, startId);
	// }

	public String turntotime(long time) {
		double t = time;
		int second = (int) (t % 60000 / 1000);
		int min = (int) (t / 60000);
		Log.e("tag", "second=" + second + "min=" + min);
		if (min > 0) {
			return min + "分" + second + "秒" + "后程序将被锁定！";
		} else {
			return second + "秒" + "后程序将被锁定！";
		}
	}

	public void tryClose() {
		 if (null != actv) {
			 try{
			 Applock_CountdownTipView.mWManager.removeView(Applock_CountdownTipView.mTableTip);
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
			 actv = null;
		 }
	 }
	
	// @Override
	// public void OnCloseService(boolean isClose) {
	// // TODO Auto-generated method stub
	// stopService(mIntent);
	// }
}