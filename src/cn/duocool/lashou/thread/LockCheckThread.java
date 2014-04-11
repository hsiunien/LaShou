package cn.duocool.lashou.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.logic.LockLogic;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockCheckInfo;
import cn.duocool.lashou.model.LockSettingInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class LockCheckThread extends Thread{

	public static final String TAG =  LockCheckThread.class.getName(); 
    // service context
	private Context mContext;
    // 所有程序的信息
    private Map<String, LockAppInfo> mAllAppMap;
    // 例外程序的信息(不用监控的信息)
    private Map<String, LockAppInfo> mExceptionAppMap;
    
    // 锁的配置信息
    public LockSettingInfo lockSettingInfo;
   
    // 拦截例外列表(特别列表：包名是拦截的，但是一个包中有些是不想拦截的)  有些是通过扫描扫不到的。
    List<String> otherExceptionList = new ArrayList<String>();
    
    // 是否运行
    public boolean isRunning = true;
    // 锁业务逻辑
    private LockLogic lockLogic = null;
    // dao层
    private DaoBase dao = null;
    
    private ActivityManager mActivityManager;
    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;

    
//    public String topActivityPackageName="";
//    public String topActivityClassName="";
//    
//    
//    boolean haveVerify=false;//�Ƿ���֤
//    
//    String packageName;
//    int tag=0;//锁定指定类名
//     int appindex=0;//��Ҫ�� ��APP��applist���±�
    
    public LockCheckThread(
    		Context context,
    		Map<String, LockAppInfo> pAllAppMap,
    		Map<String, LockAppInfo> pExceptionAppMap,
    		LockSettingInfo lockSettingInfo,
    		DaoBase dao) {
    	
        mContext = context;  
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        this.mAllAppMap = pAllAppMap;
        this.mExceptionAppMap = pExceptionAppMap;
        
        this.lockLogic = new LockLogic();
        this.dao = dao;
        this.lockSettingInfo = lockSettingInfo;
//        this.appLockTipService = appLockTipService;
        
        // 初期化 拦截例外列表
        otherExceptionList.add("cn.duocool.lashou.activity.ImageLockActivity");
        otherExceptionList.add("cn.duocool.lashou.activity.PasswordActivity");
    }
//    int wifi=0;
//    int mobilenet=0;
    
   
    
	@Override
	public void run() {
		while(isRunning) {
			try{
				// 更具当前运行的程序检查是否弹出密码界面
				LockCheckInfo lockCheckInfo =  lockLogic.checkLock(mContext,mActivityManager,otherExceptionList,mAllAppMap,mExceptionAppMap,lockSettingInfo,dao);
				//Log.d(TAG, "~~~~~isShowLockUI:"+lockCheckInfo.isShowLockUI());
				if (lockCheckInfo.isShowLockUI()) {
					 Log.d(TAG, "~~~~~显示密码输入框");
					lockLogic.showLockUI(mContext,lockCheckInfo,dao);
				}
				
				// 检查网络
				lockLogic.checkNet(mContext, lockSettingInfo, dao,mConnectivityManager,mWifiManager);
				
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	
	
//	
//	public void lock(Context context,String nowPackageName) {
//		
////		Intent intent = new Intent();
////		LockAppInfo appInfo = mAllAppMap.get(nowPackageName);
////		
////    	intent.putExtra("lockName", appInfo.getLockName());
////        intent.putExtra("password",appInfo.getPassword());
////        
////        if(LockService.appList.get(appindex).getPasswordtype().equals("image")){
////        	intent.setClass(mContext, ImageLockActivity.class);
////        } else if (LockService.appList.get(appindex).getPasswordtype().equals("figure")) {
////        	intent.setClass(mContext, PasswordActivity.class); 
////        }
////        
////        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        // 启动密码输入界面
////        mContext.startActivity(intent);
//		
////		Intent intent = new  Intent();
////		intent.putExtra("lockName", LockService.lock1Name);
////		//intent.putExtra("gotoActivity", "ActivityHome");
////		if(LockService.lock1Pwd.equals("")) {
////			intent.setClass(context, SetImagePasswordActivity.class);
////		} else {
////			if(LockService.lock1Type.equals("image")) {
////				intent.setClass(context, ImageLockActivity.class);
////			} else {
////				intent.setClass(context, PasswordActivity.class);
////			}
////			intent.putExtra("password",LockService.lock1Pwd);
////		}
////		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
////		context.startActivity(intent);
//	}
//
//	public boolean isTodayNeedLock(int index)
//	{
//		Calendar c = Calendar.getInstance(); 
//	   int week=c.get(Calendar.DAY_OF_WEEK);
//	   week-=1;
//	   
//			  for(int j=0;j<LockService.appList.get(index).getIgnoreWeek().size();j++)
//			  {
//				  Log.d("tag", "��4");
//				  if(LockService.appList.get(index).getIgnoreWeek().get(j)==week)
//				  {
//					  return false;
//				  }
//			  }
////			 
//		 
//	  
//	   return true;
//	}
//	
//     /**
//      * �ж�����ʱ���ǲ�������Щʱ�����
//     * @param list
//     * @return
//     */
//    public boolean isNowinlist( List<Map<String, Object>> list)
//    {
//    	SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss"); 
//    	 int nowsecond=stringToInt(sdf.format(new Date()));
//    	 int begin,end;
//    	 for(int i=0;i<list.size();i++)
//    	 {
//    		 //Log.d("tag", "begin="+(String) list.get(i).get("beginTV"));
//    		 begin=stringToInt((String) list.get(i).get("beginTV")) ;
//    		
//    		 end=stringToInt((String) list.get(i).get("endTV")) ;
//    		 if(nowsecond>=begin&&nowsecond<=end)
//    		 {
//    			 return true;
//    		 }
//    	 }
//    	 return false;
//	}
//    /**
//	 * ��xx:xx���͵�ʱ��ת���벢����
//	 * @param time
//	 * @return
//	 */
//	public int stringToInt(String time)
//	{
//		String bs[]=time.split(":");
//	  
//		 int begint=Integer.parseInt(bs[0])*60*60+Integer.parseInt(bs[1])*60;
//		 if(bs.length>=3)
//		 {
//			
//			 begint+=Integer.parseInt(bs[2]);
//			// Log.d("tag", "bs[3]="+Integer.parseInt(bs[2]));
//		 }
//		 return begint;
//	}

}

