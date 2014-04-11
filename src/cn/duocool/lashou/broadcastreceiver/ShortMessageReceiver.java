package cn.duocool.lashou.broadcastreceiver;

import java.util.Map;

import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.utils.Tools;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class ShortMessageReceiver extends BroadcastReceiver{

	private String number;
	SQLiteDatabase db;
	String content;//短信内容
	Context context;
	
	private LashouService lashouService;
	private DaoBase dao;
	
     // 当接收到短信时被触发  
	@Override
	public void onReceive(final Context context, Intent intent) 
	{
		this.context=context;
		// 如果是接收到短信   
        if (intent.getAction().equals(   
            "android.provider.Telephony.SMS_RECEIVED"))   
        {   
           
            StringBuilder sb = new StringBuilder();   
            // 接收由SMS传过来的数据   
            Bundle bundle = intent.getExtras();   
            // 判断是否有数据   
            if (bundle != null)   
            {   
                //  通过pdus可以获得接收到的所有短信消息   
                Object[] pdus = (Object[]) bundle.get("pdus");   
                // 构建短信对象array,并依据收到的对象长度来创建array的大小    
                SmsMessage[] messages = new SmsMessage[pdus.length];   
                for (int i = 0; i < pdus.length; i++)   
                {   
                    messages[i] = SmsMessage   
                        .createFromPdu((byte[]) pdus[i]);   
                }   
                // 将送来的短信合并自定义信息于StringBuilder当中    
                for (SmsMessage message : messages)   
                {   
                    sb.append("短信来源:");
                    // 获得接收短信的电话号码   
                    sb.append(message.getDisplayOriginatingAddress());   
                    sb.append("\n------短信内容------\n");   
                    // 获得短信的内容   
                    sb.append(message.getDisplayMessageBody()); 
                 // 获得接收短信的电话号码   
                    number=message.getDisplayOriginatingAddress();
                    if (number.length() > 11) { // 13507485587
                    	number = number.substring(number.length() - 11, number.length());
                    }
                    
                    // 获得短信的内容   
                    content=message.getDisplayMessageBody();
                  if(  judgeNumber(number))
                  {
                	  lashouService = Tools.getApplication(context).getLashouService();
                	  dao = lashouService.getLockService().getDao();
                	  LockDao lockDao = new LockDao(context, dao);
                	  
                	  Map<String, LockAppInfo> dataMap = lashouService.getLockService().getAllAppMap();
            		
            		if(content.startsWith("unlock"))
                    {
            			  // 取消广播（这行代码将会让系统收不到短信）   
                        abortBroadcast();  
                    	String appName=content.substring(6);
                    	for (String key : dataMap.keySet()) {
                    		LockAppInfo lockAppInfo = dataMap.get(key);
                    		 if(findApp(appName, lockAppInfo.getAppName())){
                    			lockAppInfo.setIslock(0);
                    			lockDao.updateAppIsLockByAppId(0, lockAppInfo.getAppId());
                    			break;
                    		 }
                    	}

                    	
                    }else if(content.startsWith("lock")) {
                    	  // 取消广播（这行代码将会让系统收不到短信）   
                    	 abortBroadcast();  
                     	String appName=content.substring(4);
                     	
                     	for (String key : dataMap.keySet()) {
                     		LockAppInfo lockAppInfo = dataMap.get(key);
                     		if (findApp(appName, lockAppInfo.getAppName())) {
                     			lockAppInfo.setIslock(1);
                     			lockDao.updateAppIsLockByAppId(1, lockAppInfo.getAppId());
                     			break;
                     		}
                     	}
                    }
                }
                }   
            }   
//            Toast.makeText(context, sb.toString()   
//                , 5000).show();   
        } 
        
	}
	/**
	 * 匹配目标应用程序
	 * @param appName 短信应用程序名
	 * @param aplicationName 目标程序名
	 * @return
	 */
	public  boolean  findApp(String appName,String aplicationName){
		if (appName.length()<3&&appName.equals(aplicationName)) {
			 return true;
		}else if(appName.length()>=3){
			int j=0;
			char c=appName.charAt(j);
			for (int i = 0; i < aplicationName.length(); i++) {
				if(aplicationName.charAt(i)==c){
					if(j==appName.length()-1) {
						return true;
					}
					c=appName.charAt(++j);
				}
			}
		} 
			return false;
		 
	}	
	/**
	 * @return
	 * 判断此号码可不可以加锁解锁程序 
	 */
	public boolean judgeNumber(String number)
	{
		   DataBaseHelper databaseHelper = new DataBaseHelper(context);
   		SQLiteDatabase	db = databaseHelper.getWritableDatabase();
   		Cursor cursor2 = db.rawQuery(
				"select * from numbers where number=?",
				new String[] { number });
   		boolean retFlag = false;
		if(cursor2.moveToNext())
		{
			retFlag =  true;
		}else
		{
			retFlag =  false;
		}
		if (null != cursor2) {
			cursor2.close();
		}
		
		if (null != db) {
			db.close();
		}
		return retFlag;
	}

}
