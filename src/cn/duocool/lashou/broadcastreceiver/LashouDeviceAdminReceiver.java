package cn.duocool.lashou.broadcastreceiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.net.client.PushData;
import cn.duocool.lashou.net.client.UserData;
import cn.duocool.lashou.R;

public class LashouDeviceAdminReceiver extends DeviceAdminReceiver {

	@Override
	public void onEnabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onEnabled(context, intent);
		DataBaseHelper dbh=new DataBaseHelper(context);
		SQLiteDatabase db2=dbh.getWritableDatabase();
		ContentValues value=new ContentValues();
		 value.put("DeviceAdminAdd", 1);
		 db2.update("lockall", value, null, null);
		 if(db2!=null)
			{
			db2.close();
			}
	
		Toast.makeText(context, context.getString(R.string.deviceAdminEnable), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onDisabled(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onDisabled(context, intent);
		Toast.makeText(context, context.getString(R.string.deviceAdminDisable), Toast.LENGTH_SHORT).show();
	}
	
	
	
	

}
