package cn.duocool.lashou.logic;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ImageLockActivity;
import cn.duocool.lashou.activity.PasswordActivity;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockCheckInfo;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.LockSettingInfo;
import cn.duocool.lashou.model.LockTimeSegmentBean;
import cn.duocool.lashou.mywidget.Applock_CountdownTipView;
import cn.duocool.lashou.service.AppLockTipService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;

/**
 * 锁的业务处理
 * 
 * @author xwood
 *
 */
public class LockLogic {
	
	private final static String TAG = LockLogic.class.getName();
	
	// 上一个检查的包名
	private static String preCheckPackageName = "";
	// 上一次的计时
	private static long preMarkMSTime = 0;
	
	/**
	 * 1.如果数据库里面没有数据就添加初始数据，比如第一次进入 程序时.
	 *  2.初始化 数据库里面的锁信息，锁1密码改变时要一起改变Lockservice里面的密码，和类型
	 */
	public void initLockInfo(Context context ,DaoBase dao,LockSettingInfo  lockSettingInfo) {
		LockDao lockDao = new LockDao(context,dao);
		lockDao.initLocks();
		lockDao.intiRemind();
		lockDao.initLockall();
//		lockDao.intiRemind();
		lockSettingInfo.setRemindTime(lockDao.getRemind());
		
		List<LockTimeSegmentBean> wifiLimitTimeList = new ArrayList<LockTimeSegmentBean>();
		List<LockTimeSegmentBean> netLimitTimeList = new ArrayList<LockTimeSegmentBean>();
		lockDao.get3GTimeSegment(netLimitTimeList);
		lockDao.getWifiTimeSegment(wifiLimitTimeList);
		
		lockSettingInfo.setNetLimitTimeList(netLimitTimeList);
		lockSettingInfo.setWifiLimitTimeList(wifiLimitTimeList);
	}
	
	/**
	 *  获取所有的在手机上安装的应用程序的信息
	 * @param context 环境
	 * @param paramAllAppInfoMap  除了排除信息以外的列表
	 * @param paramExceptionInfoMap 排除列表
	 */
	public void getAllAppInfoData(Context context, Map<String, LockAppInfo> paramAllAppInfoMap,Map<String, LockAppInfo> paramExceptionInfoMap,DaoBase dao) {

		
		Map<String, LockAppInfo> localAppInfoMaps = paramAllAppInfoMap;
		Map<String, LockAppInfo> localExceptionAppInfoMaps = paramExceptionInfoMap;

		LockDao lockDao = new LockDao(context,dao);
		
		// 获取所有的在手机上安装的应用程序的信息, 包括哪些被卸载了的但是没有清空数据的应用程序
		PackageManager pm = context.getPackageManager();
		
		List<PackageInfo> packageinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
//		DataBaseHelper dbHelper = new DataBaseHelper(context);
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		// 获得默认锁
		LockInfo defaultLockInfo = lockDao.getDefaultLock();
		
		for (PackageInfo packageInfo : packageinfos) {

			LockAppInfo lockAppInfo = new LockAppInfo();
			
			// 应用名称
			String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
			// 应用图标
			Drawable appIcon = packageInfo.applicationInfo.loadIcon(pm);
			// 程序包名
			String packageName = packageInfo.packageName;
			
			// 保存信息
//			lockAppInfo.setAppIcon(appIcon);
			lockAppInfo.setAppName(appName);
			lockAppInfo.setPackageName(packageName);
			
			if(appName.startsWith("com.android") && StringUtils.isABC(appName)) {
				Log.d(TAG, "APPName例外:"+appName);
				localExceptionAppInfoMaps.put(packageName, lockAppInfo);
				continue;
			}
			if(packageName.startsWith("android")||packageName.startsWith("com.android.defcontainer")
					||packageName.startsWith("com.android.exchange")||packageName.startsWith("com.android.htmlviewer")
					||packageName.startsWith("com.android.inputmethod.latin")||packageName.startsWith("com.android.keychain")
					||packageName.startsWith("com.android.inputmethod.pinyin")||packageName.startsWith("com.android.launcher")
				||packageName.startsWith("com.android.magicsmoke")	||packageName.startsWith("com.android.musicfx")
				||packageName.startsWith("com.android.musicvis")||packageName.startsWith("com.android.noisefield")
//				||packageName.startsWith("com.android.packageinstaller")
				||packageName.startsWith("com.android.phasebeam")||packageName.startsWith("com.android.providers.downloads")
				||packageName.startsWith("com.android.providers.downloads.ui")
				||packageName.startsWith("com.android.providers.drm")||packageName.startsWith("com.android.providers.media")
				||packageName.startsWith("com.android.providers.contacts")||packageName.startsWith("com.android.providers.media")
				||packageName.startsWith("com.android.providers.contacts")||packageName.startsWith("com.android.providers.telephony")
				||packageName.startsWith("com.android.providers.settings")||packageName.startsWith("com.android.providers.userdictionary")
				||packageName.startsWith("com.android.systemui")||packageName.startsWith("com.android.videoeditor")
				||packageName.startsWith("com.android.voicedialer")||packageName.startsWith("com.android.wallpaper")
				||packageName.startsWith("com.android.wallpaper.livepicker")||packageName.startsWith("com.example.android.livecubes")
				||packageName.startsWith("com.svox.pico")||packageName.startsWith("com.android.inputdevices")
				||packageName.startsWith("com.android.certinstaller")||packageName.startsWith("com.android.providers.applications")
				||packageName.startsWith("cn.duocool.lashou")
					){
				Log.d(TAG, "PackageName例外:"+appName);
				localExceptionAppInfoMaps.put(packageName, lockAppInfo);
				continue;
			}
			
			// 判断在数据库里面是否存在，然后是否锁定并改相应的list里面的项
			LockAppInfo lockAppInfo_DB = lockDao.getAppByPackageName(packageName);
			if (null == lockAppInfo_DB) {
				// 不存在就要 向数据库里添加一条
				lockAppInfo_DB = new LockAppInfo();
				lockAppInfo_DB.setAppName(appName); // 应用程序名
				lockAppInfo_DB.setPackageName(packageName); // 包名
				lockAppInfo_DB.setIslock(0); // 是否上锁 0  不上锁
//				value.put("week",""); // 这个字段不要了。。。
				lockAppInfo_DB.setLimitTime(-1); // -1 就是不要限制
				lockAppInfo_DB.setLeftTime(-1); // 剩余使用时间
				lockAppInfo_DB.setLockId(defaultLockInfo.getLockId()); // 锁的Id
				
				long appId = lockDao.addBaseAppInfo(lockAppInfo_DB);
				lockAppInfo_DB.setAppId(appId);
				lockAppInfo_DB.setAppIcon(appIcon);
				lockAppInfo_DB.setPassword(defaultLockInfo.getPassword());
				lockAppInfo_DB.setPasswordtype(defaultLockInfo.getPasswordType());
				lockAppInfo_DB.setLockName(defaultLockInfo.getLockName());
				lockAppInfo_DB.setCheck(false);
				
				// 设定默认值
				if (packageName.startsWith("com.android.packageinstaller")) {
					lockAppInfo_DB.setIslock(1); // 安装卸载默认 上锁
					lockDao.enableUnInstallPackage(1);
				}
				
				Log.d(TAG, "~~lockAppInfo没有数据的场合:"+lockAppInfo_DB.toString());
			} else {
				lockAppInfo_DB.setAppIcon(appIcon);
				int lockId = lockAppInfo_DB.getLockId();
				LockInfo lockInfo =lockDao.getLockById(lockId);
				lockAppInfo_DB.setPassword(lockInfo.getPassword());
				lockAppInfo_DB.setPasswordtype(lockInfo.getPasswordType());
				lockAppInfo_DB.setLockName(lockInfo.getLockName());
				lockAppInfo_DB.setCheck(false);
				
				
				Log.d(TAG, "~~lockAppInfo有数据的场合:"+lockAppInfo_DB.toString());
			}
						
			// 保存信息
			localAppInfoMaps.put(packageName, lockAppInfo_DB);
		}
	}
	
	
	/**
	 * 检查是否弹出密码界面
	 * 
	 * @return 是否通过检查  false 通过检查  true 没有通过检查
	 */
	public LockCheckInfo checkLock(
			Context pContext,
			ActivityManager pActivityManager,
			List<String> pExceptionActivityList,
			Map<String, LockAppInfo> pAllAppMap,
			Map<String, LockAppInfo> pExceptionAppMap,
			LockSettingInfo lockSettingInfo,
			DaoBase dao) {
		
		LockCheckInfo lockCheckInfo  = new LockCheckInfo();
		// 是否通过检查 false 没有通过检查
		lockCheckInfo.setShowLockUI(false);

		// 获得现在运行的Activity的包名 和类名
		ComponentName topActivity = pActivityManager.getRunningTasks(1).get(0).topActivity;
		String nowClassName = topActivity.getClassName(); 
	    String nowPackageName = topActivity.getPackageName();
	    
	    // 如果是例外的Activity话，就退出（这些是不拦截的）
	    if(pExceptionActivityList.contains(nowClassName)) {
//	    	appLockTipService.tryClose();
	    	try {
    			Tools.getApplication(pContext).getLashouService().handler.sendEmptyMessage(201);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
	    	preCheckPackageName = nowPackageName;
			// 计时
			preMarkMSTime = System.currentTimeMillis();
	    	return lockCheckInfo;
		}
	    
//        Log.d(TAG, "packageName~~"+nowPackageName);
//        Log.d(TAG, "className~~~~"+nowClassName);
        
        // 例外的包名(这些是不拦截的)
        if (null != pExceptionAppMap.get(nowPackageName)) {
//        	Log.d(TAG, "例外 包名1："+nowClassName);
//        	appLockTipService.tryClose();
        	try {
    			Tools.getApplication(pContext).getLashouService().handler.sendEmptyMessage(201);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        	// 计时
        	preMarkMSTime = System.currentTimeMillis();
        	preCheckPackageName = nowPackageName;
        	return lockCheckInfo;
        }
        
        // 获得应用程序信息
        LockAppInfo lockAppInfo =  pAllAppMap.get(nowPackageName);
        if (null == lockAppInfo) {	// 如果这个时候 出现一个空的数据，就是一个新安装的应用程序
        	// 不存在就要 向数据库里添加一条
//        	appLockTipService.tryClose();
        	Tools.getApplication(pContext).getLashouService().handler.sendEmptyMessage(201);
        	PackageManager pm = pContext.getPackageManager();
        	PackageInfo packageInfo;
			try {
				packageInfo = pm.getPackageInfo(nowPackageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			
	        	// 应用名称
				String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
				// 应用图标
				Drawable appIcon = packageInfo.applicationInfo.loadIcon(pm);
				// 程序包名
				String packageName = packageInfo.packageName;
				
				LockDao lockDao = new LockDao(pContext,dao);
				
				// 获得默认锁
				LockInfo defaultLockInfo = lockDao.getDefaultLock();
	        	
	        	LockAppInfo lockAppInfo_DB = new LockAppInfo();
				lockAppInfo_DB.setAppName(appName); // 应用程序名
				lockAppInfo_DB.setPackageName(packageName); // 包名
				lockAppInfo_DB.setIslock(0); // 是否上锁 0  不上锁
	//			value.put("week",""); // 这个字段不要了。。。
				lockAppInfo_DB.setLimitTime(-1); // -1 就是不要限制
				lockAppInfo_DB.setLeftTime(-1); // 剩余使用时间
				lockAppInfo_DB.setLockId(defaultLockInfo.getLockId()); // 锁的Id
				
				long appId = lockDao.addBaseAppInfo(lockAppInfo_DB);
				lockAppInfo_DB.setAppId(appId);
				lockAppInfo_DB.setAppIcon(appIcon);
				
				synchronized (pAllAppMap) {
					pAllAppMap.put(packageName, lockAppInfo_DB);
				}
				
//				Log.d(TAG, "~~checkLock没有数据的场合:"+lockAppInfo_DB.toString());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			// 计时
			preMarkMSTime = System.currentTimeMillis();
			preCheckPackageName = nowPackageName;
			return lockCheckInfo;
        } else {
        	// 设定 应用程序的使用时间
        	if (preCheckPackageName.equals(nowPackageName)) {
        		// OK 持续使用应用 需要计算使用时长
        		// 现在时间
        		long nowMarkMSTime = System.currentTimeMillis();
        		long diffTime = nowMarkMSTime - preMarkMSTime;
        		if (Math.abs(diffTime) > 1000) { // 万一在计时的过程，有人改了系统时间就算为 1秒
        			diffTime = 1000;
        		}
        		// 统计使用时间
        		lockAppInfo.setUsedTime(lockAppInfo.getUsedTime() + diffTime);
        	} else if ( (!preCheckPackageName.equals("cn.duocool.lashou"))) {
        		lockAppInfo.setCheck(false);
        		try {
        			Tools.getApplication(pContext).getLashouService().handler.sendEmptyMessage(201);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
//        		appLockTipService.tryClose();
        		
        	} else if (!preCheckPackageName.equals(nowPackageName) && preCheckPackageName.equals("cn.duocool.lashou")) {
        		if (lockAppInfo.getUsedTime() >=lockAppInfo.getLimitTime() 
        				&& lockAppInfo.getLimitTime() != 0 
        				&& lockAppInfo.getLimitTime() != -1) { // 如果时间到了 重新计时
        			lockAppInfo.setUsedTime(0);
        			lockAppInfo.setCheck(false);
        		}
        	}
        	// 计时
        	preMarkMSTime = System.currentTimeMillis();
        	preCheckPackageName = nowPackageName;
        	
        	// 如果一个包是在列表里， 并且不在例外包里 （这些是需要拦截的）
        	int isLock = lockAppInfo.getIslock(); // 普通应用 根据 是否锁来进行判断 1 锁 0 不锁
        	if (1==isLock && !lockAppInfo.isCheck()) {
        		
        		/**
        		 * 开始检查
        		 */
        		boolean retDay = dayCheck(lockAppInfo);
        		boolean retWeek = weekCheck(lockAppInfo);
        		boolean retTimeSegment = TimeSegmentCheck(lockAppInfo);
        		int retuseTime = checkLeftTime(pContext, lockAppInfo, lockSettingInfo);
        		
        		if (retuseTime==3 || retuseTime == -1) { // true 还有剩余时间 false 没有剩余时间
        			lockCheckInfo.setShowLockUI(false);
        		} else {
        			lockCheckInfo.setShowLockUI(true);
        			lockCheckInfo.setLockAppInfo(lockAppInfo);
        			
        			lockAppInfo.setCheck(true);
        			
        			return lockCheckInfo;
        		}
        		
        		if (retDay) { // 在可用的日期内 ==》不用拦截
        			lockCheckInfo.setShowLockUI(false);
        			return lockCheckInfo; 
        		}
        		if (!retDay && retWeek) { // 不在可用日期内 ，但是在可用星期内  ==》不用拦截
        			lockCheckInfo.setShowLockUI(false);
        			return lockCheckInfo;
        		}
        		if (!retDay && !retWeek && retTimeSegment) { // 不在可用日期内 ，不在可用星期内 ，但是在可用时间端内 ==》不用拦截
        			lockCheckInfo.setShowLockUI(false);
        			return lockCheckInfo;
        		}
        		
        		 // 不在可用日期内 ，不在可用星期内 ，不在可用时间 还有使用时间 端内 ==》不用拦截
        		if (!retDay && !retWeek && !retTimeSegment && retuseTime == 3) {
        			lockCheckInfo.setShowLockUI(false);
        			return lockCheckInfo;
        		}
        		
        		if (!retDay && !retWeek && !retTimeSegment && (retuseTime == 2 || retuseTime == -1)) { // 不在可用日期内 ，不在可用星期内 ，不在可用时间 端内 ==》拦截
        			lockCheckInfo.setShowLockUI(true);
        			lockCheckInfo.setLockAppInfo(lockAppInfo);
        			lockAppInfo.setCheck(true);
        			return lockCheckInfo;
        		}
        	} else if (0==isLock) { // 没有上锁的场合,无论设定任何内容 都 不用拦截
        		return lockCheckInfo;
        	} else {
        		// 与没有上锁一样
        		return lockCheckInfo;
        	}
        }
        
        return lockCheckInfo;
	}
	
	/**
	 * 时间检查:日期检查
	 * @param lockAppInfo
	 * @return true 在可用时间内  false 不在可用时间内
	 */
	private boolean dayCheck(LockAppInfo lockAppInfo) {
		
		boolean dayLimit = false;
		
		// 获得日期限制信息
		List<Integer>  limitDayList = lockAppInfo.getLimitDay();
		if (null == limitDayList || limitDayList.size() <= 0) {
			// 没有日期限制，单独看，就是哪一天都不能用
			dayLimit = false;
		} else {
			// 获得日历
			Calendar calendar = Calendar.getInstance();
			// 获得当前的日期
			int  day = calendar.get(Calendar.DAY_OF_MONTH);
			// 循环检查 今天是否在需要检查的日期里
			if (limitDayList.contains(Integer.valueOf(day))) {
				dayLimit = true;
			}
		}
		return dayLimit;
	}
	
	/**
	 * 时间检查:周检查
	 * @param lockAppInfo
	 * @return true  在可用时间内  false 不在可用时间内
	 */
	private boolean weekCheck(LockAppInfo lockAppInfo) {
		
		boolean limit = false;
		
		// 获得日期限制信息
		List<Integer>  limitWeekList = lockAppInfo.getLimitWeek();
		if (null == limitWeekList || limitWeekList.size() <= 0) {
			// 没有日期限制，单独看，就是哪一天都不能用
			limit = false;
		} else {
			// 获得日历
			Calendar calendar = Calendar.getInstance();
			// 获得当前的日期
			int  day = calendar.get(Calendar.DAY_OF_WEEK);
			// 循环检查 今天是否在需要检查的日期里
			if (limitWeekList.contains(Integer.valueOf(day-1))) {
				limit = true;
			}
		}
		return limit;
	}
	
	
	/**
	 * 时间检查:时间段检查
	 * @param lockAppInfo
	 * @return true  在可用时间内  false 不在可用时间内
	 */
	private boolean TimeSegmentCheck(LockAppInfo lockAppInfo) {
		
		boolean limit = false;
		
		// 获得日期限制信息
		List<LockTimeSegmentBean>  limitTimeSegmentList = lockAppInfo.getLimitTimeSegment();
		if (null == limitTimeSegmentList || limitTimeSegmentList.size() <= 0) {
			// 没有限制，单独看就是没有时间段可以用。。。
			limit = false;
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); 
	    	String nowTime = sdf.format(new Date());
			// 循环检查时间段
			for (LockTimeSegmentBean lockTimeSegmentBean : limitTimeSegmentList) {
				// 在时间段中
				if (nowTime.compareTo(lockTimeSegmentBean.getStartTime()) >= 0 && nowTime.compareTo(lockTimeSegmentBean.getEndTime()) <=0) {
					limit = true;
					break;
				}
			}
		}
		return limit;
	}
	
	/**
	 * 剩余使用时间的检查
	 * 
	 * @param lockAppInfo
	 * @return -1 没有设定剩余时间  2 没有剩余时间  3 还有剩余时间
	 */
	private int checkLeftTime(Context pContext,LockAppInfo lockAppInfo,LockSettingInfo lockSettingInfo) {
		// 限制使用时间
		long limitMs = lockAppInfo.getLimitTime();
		
		// 已经使用时间
		long useMs = lockAppInfo.getUsedTime();
				
		// 剩余使用时间
		long leftMs = lockAppInfo.getLeftTime();
		if (limitMs <= 0) { // 不限制时长
			return -1;
		} else { // 限制时长
			if (useMs >= limitMs) {
				// 到使用时间了，弹出锁
				return 2;
			} else { // 还没有到使用时间
				if (lockSettingInfo.getRemindTime() >= (limitMs - useMs)) { // 到了提醒时间了
//					appLockTipService.showTip(limitMs - useMs);
					Bundle bundle = new Bundle();
					bundle.putLong("tip", limitMs - useMs);
					Message msg = new Message();
					msg.what = 200;
					msg.setData(bundle);
					Tools.getApplication(pContext).getLashouService().handler.sendMessage(msg);
				}
				return 3;
			}
		}
	}
	
	
	public void showLockUI(Context pContext,LockCheckInfo lockCheckInfo,DaoBase dao) {
		
		Intent intent = new Intent();
		LockAppInfo appInfo = lockCheckInfo.getLockAppInfo();
		LockDao lockDao = new LockDao(pContext, dao);
		LockInfo  lockInfo = lockDao.getLockById(appInfo.getLockId());
		
//		
    	intent.putExtra("lockName", lockInfo.getLockName());
        intent.putExtra("password",lockInfo.getPassword());
//        
        if(lockInfo.getPasswordType().equals("image")){
        	intent.setClass(pContext, ImageLockActivity.class);
        } else if (lockInfo.getPasswordType().equals("figure")) {
        	intent.setClass(pContext, PasswordActivity.class); 
        }
//        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 启动密码输入界面
        pContext.startActivity(intent);
	}
	
	
	
	/**
	 * 检查网络
	 */
	public void checkNet(
			Context pContext,
			LockSettingInfo lockSettingInfo,
			DaoBase dao,
			ConnectivityManager mConnectivityManager,
		    WifiManager mWifiManager) {
		
		// 是否开wifi
		boolean isOpenWifi = true;
		// 是否开net
		boolean isOpenXg = true;
		
		// 获得wifi的不可用时段
		List<LockTimeSegmentBean> wifiLimitList = lockSettingInfo.getWifiLimitTimeList();
		if (null != wifiLimitList && wifiLimitList.size() > 0) {
			for (LockTimeSegmentBean ltsb : wifiLimitList) {
				int beginTime = StringUtils.stringToSecond3(ltsb.getStartTime(), ":");
				int endTime = StringUtils.stringToSecond3(ltsb.getEndTime(), ":");
				
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				int nowTime = StringUtils.stringToSecond3(sdf.format(new Date(System.currentTimeMillis())),":");
				
				Log.d(TAG, "时间比较："+beginTime +" " + nowTime + "  " + endTime );
				if (nowTime <= endTime && nowTime>=beginTime) {
					// wifi在限制期间内，啥也不管，关掉wifi
					if (mWifiManager.isWifiEnabled()) {
						mWifiManager.setWifiEnabled(false);
					}
					isOpenWifi = false;
					break;
				}
			}
		}
		
		if (isOpenWifi) {
			if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
			}
		}
		
		// 获得3G的网络不可用时段
		List<LockTimeSegmentBean> xgLimitList = lockSettingInfo.getNetLimitTimeList();
		if (null != xgLimitList && xgLimitList.size() > 0) {
			for (LockTimeSegmentBean ltsb : xgLimitList) {
				int beginTime = StringUtils.stringToSecond3(ltsb.getStartTime(), ":");
				int endTime = StringUtils.stringToSecond3(ltsb.getEndTime(), ":");
				
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				int nowTime = StringUtils.stringToSecond3(sdf.format(new Date(System.currentTimeMillis())),":");
				
				Log.d(TAG, "时间比较："+beginTime +" " + nowTime + "  " + endTime );
				if (nowTime <= endTime && nowTime>=beginTime) { // 不可用时段
					// wifi在限制期间内，啥也不管，关掉wifi
					int mobilenetstate= getAPNType(pContext,mConnectivityManager);
					if (mobilenetstate == 2 || mobilenetstate==3) {
						toggleMobileData(pContext,false);
					}
					isOpenXg = false;
					break;
				}
			}
		}
		
		if (isOpenXg) {
			int mobilenetstate= getAPNType(pContext,mConnectivityManager);
			if (mobilenetstate != 2 && mobilenetstate != 3) {
				toggleMobileData(pContext,true);
			}
		}
	}
	
	
	 /**
	  * 检查网络连接
	 * @param context
	 * @return -1：没有网络  1：WIFI网络2：wap网络3：net网络
	 */
	private int getAPNType(Context context,ConnectivityManager connMgr ) {

	        int netType = -1;
	        
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        
	        if(networkInfo==null){
	        	
	            return netType;
	            
	        } 

	        int nType = networkInfo.getType(); 

	        if(nType==ConnectivityManager.TYPE_MOBILE){ 

	           // Log.e("networkInfo.getExtraInfo()", "networkInfo.getExtraInfo() is "+networkInfo.getExtraInfo()); 

	            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){

	                netType = 3; 

	            } 

	            else{ 

	                netType = 2; 

	            } 

	        }
	        return netType; 

	    } 
	 /**
	  * �ƶ����翪��
	  */
	 private void toggleMobileData(Context context, boolean enabled) {
	  ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

	  Class<?> conMgrClass = null; // ConnectivityManager��
	  Field iConMgrField = null; // ConnectivityManager���е��ֶ�
	  Object iConMgr = null; // IConnectivityManager�������
	  Class<?> iConMgrClass = null; // IConnectivityManager��
	  Method setMobileDataEnabledMethod = null; // setMobileDataEnabled����

	  try {
	   // ȡ��ConnectivityManager��
	   conMgrClass = Class.forName(conMgr.getClass().getName());
	   // ȡ��ConnectivityManager���еĶ���mService
	   iConMgrField = conMgrClass.getDeclaredField("mService");
	   // ����mService�ɷ���
	   iConMgrField.setAccessible(true);
	   // ȡ��mService��ʵ����IConnectivityManager
	   iConMgr = iConMgrField.get(conMgr);
	   // ȡ��IConnectivityManager��
	   iConMgrClass = Class.forName(iConMgr.getClass().getName());
	   // ȡ��IConnectivityManager���е�setMobileDataEnabled(boolean)����
	   setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
	   // ����setMobileDataEnabled�����ɷ���
	   setMobileDataEnabledMethod.setAccessible(true);
	   // ����setMobileDataEnabled����
	   setMobileDataEnabledMethod.invoke(iConMgr, enabled);
	  } catch (ClassNotFoundException e) {
	   e.printStackTrace();
	  } catch (NoSuchFieldException e) {
	   e.printStackTrace();
	  } catch (SecurityException e) {
	   e.printStackTrace();
	  } catch (NoSuchMethodException e) {
	   e.printStackTrace();
	  } catch (IllegalArgumentException e) {
	   e.printStackTrace();
	  } catch (IllegalAccessException e) {
	   e.printStackTrace();
	  } catch (InvocationTargetException e) {
	   e.printStackTrace();
	  }
	 }
}
