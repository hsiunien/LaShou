package cn.duocool.lashou.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
 

import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.PushData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;
import cn.duocool.lashou.utils.Tools;

public class PushReceiver extends BroadcastReceiver implements NetTranListener {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d(getClass().toString(), "ddddddddddddd"+intent.getAction());
		
		Bundle bundle=intent.getExtras();
		PushData data=(PushData) bundle.get("data");
		final UserData userdata=data.getAttachmentUser();
//		 0:加好友 1:响应加好友信息 同意  2:响应加好友信息 不同意  3:请求查看位置权限  4:响应请求查看权限 同意   5:响应请求查看权限 不同意    6:匿名消息   7:广告推送
		if(data.getPushType()==0){
			final MyDialog dialog = new MyDialog(context );
			dialog.setTitle("家人申请");
			dialog.setContent(userdata.getNick() + "申请加您为家人");
			dialog.setTag(userdata);
			dialog.show();
			dialog.setButton1("同意", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.close();
					NetClient nc = new NetClient();
					nc.setOnNetTranListener(PushReceiver.this);
					nc.responseAddFriend(888,
							Tools.getApplication(context)
									.getMyInfo().getUserId()
									+ "", ((UserData)dialog.getTag()).getUserId()  + "",
							"Y");
				}
			});
			dialog.setButton2("拒绝", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.close();
					NetClient nc = new NetClient();
					nc.setOnNetTranListener(PushReceiver.this );
					nc.responseAddFriend(888,
							Tools.getApplication(context )
									.getMyInfo().getUserId()
									+ "", ((UserData)dialog.getTag()).getUserId() + "",
							"N");
				}
			});
		}else if(data.getPushType()==1){
			final MyDialog dialog=new MyDialog(context );
			dialog.setTitle("家人申请");
			dialog.setContent(userdata.getNick()+"已经同意了您的申请");
			dialog.show();
			dialog.setButton1("我知道了", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.close();
				}
			});
		}else if(data.getPushType()==2){
			final MyDialog dialog=new MyDialog(context );
			dialog.setTitle("家人申请");
			dialog.setContent(userdata.getNick()+" 拒绝 了您的申请");
			dialog.show();
			dialog.setButton1("我知道了", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.close();
				}
			});
		}else if(data.getPushType()==3){
			final MyDialog dialog=new MyDialog(context );
			dialog.setTitle("权限申请");
			dialog.setContent(userdata.getNick()+" 向您申请位置查看");
			dialog.show();
			dialog.setButton1("同意", new OnClickListener() {
				@Override
				public void onClick(View v) {
					NetClient nc=new NetClient();
					nc.changViewRole(789, userdata.getUserId()+"",  Tools.getApplication(context ).getMyInfo().getUserId()+"", 3);
					dialog.close();
				}
			});
			dialog.setButton2("拒绝", new OnClickListener() {
				@Override
				public void onClick(View v) {
					NetClient nc=new NetClient();
					nc.changViewRole(789, userdata.getUserId()+"",  Tools.getApplication(context).getMyInfo().getUserId()+"", 1);
					dialog.close();
				}
			});
					
		}	
	
	}

 
	public void onTransmitted(int requestCode, ResponseData data) {
		// TODO Auto-generated method stub
		
	}

}
