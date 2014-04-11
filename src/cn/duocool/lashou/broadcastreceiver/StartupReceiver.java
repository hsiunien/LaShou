package cn.duocool.lashou.broadcastreceiver;

import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.service.LockService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StartupReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		DataBaseHelper dbhlper=new DataBaseHelper(context);
		SQLiteDatabase db=dbhlper.getWritableDatabase();
	Cursor c=db.rawQuery("select * from lockall", null);
		if(c.moveToNext())
		{
			int tmp=c.getInt(c.getColumnIndex("isautostart"));
			if(tmp==0)
			{
				return;
			}
		}
		context.startService(new Intent(context, LockService.class));
		
	}

}
