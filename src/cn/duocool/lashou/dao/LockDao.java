package cn.duocool.lashou.dao;

import java.util.ArrayList;
import java.util.List;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.LockSystemInfo;
import cn.duocool.lashou.model.LockTimeSegmentBean;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LockDao {
	
	private Context mContext;
	private DaoBase dao;

	public LockDao(Context mContext,DaoBase dao) {
		this.mContext = mContext;
		this.dao = dao;
	}
	
	/**
	 * 初期化每个锁
	 */
	public void initLocks() {
		
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();
			
			cursor = db.query("locks", null, null, null, null, null, null);
			if (null == cursor || cursor.getCount()<=0) { // 数据库里没有锁的信息
				for (int i=0;i<CommDef.LOCK_SIZE;i++) {
					ContentValues values=new ContentValues();
					values.put("name", mContext.getString(R.string.lockShowName) + i + "");
					values.put("password", "");
					values.put("passwordtype", "image");
					values.put("question", mContext.getString(R.string.lockQuestionDefValue));
					if (0 == i) {
						values.put("isDefault", 1); // 1 默认 0 非默认
					} else {
						values.put("isDefault", 0); // 1 默认 0 非默认
					}
					db.insert("locks", null, values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	/**
	 * 获得系统里面的所有的锁的信息
	 * @return
	 */
	public List<LockInfo> getAllLocks() {
		List<LockInfo> lockInfoList = new ArrayList<LockInfo>();
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursor = db.query("locks", null, null, null, null, null, "_id  asc");
			if (null != cursor && cursor.getCount() > 0) {
				cursor.moveToFirst();
				do {
					int lockId = cursor.getInt(cursor.getColumnIndex("_id"));
					String lockName = cursor.getString(cursor.getColumnIndex("name"));
					String password = cursor.getString(cursor.getColumnIndex("password"));
					String passwordtype = cursor.getString(cursor.getColumnIndex("passwordtype"));
					String question = cursor.getString(cursor.getColumnIndex("question"));
					String answer = cursor.getString(cursor.getColumnIndex("answer"));
					int isDefault = cursor.getInt(cursor.getColumnIndex("isDefault"));
					
					LockInfo lockInfo = new LockInfo();
					lockInfo.setLockId(lockId);
					lockInfo.setLockName(lockName);
					lockInfo.setLockType(passwordtype);
					lockInfo.setPassword(password);
					lockInfo.setPasswordType(passwordtype);
					lockInfo.setQuestion(question);
					lockInfo.setAnswer(answer);
					lockInfo.setIsDefault(isDefault);
					lockInfoList.add(lockInfo);
				} while(cursor.moveToNext());
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		return lockInfoList;
	}
	
	/**
	 * 获得默认锁信息
	 */
	public LockInfo getDefaultLock() {
		
		LockInfo lockInfo = new LockInfo();
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursor = db.query("locks", null, "isDefault=?", new String[]{"1"}, null, null, null);
			if (null != cursor && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int lockId = cursor.getInt(cursor.getColumnIndex("_id"));
				String lockName = cursor.getString(cursor.getColumnIndex("name"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				String passwordtype = cursor.getString(cursor.getColumnIndex("passwordtype"));
				String question = cursor.getString(cursor.getColumnIndex("question"));
				String answer = cursor.getString(cursor.getColumnIndex("answer"));
				int isDefault = cursor.getInt(cursor.getColumnIndex("isDefault"));
				
				lockInfo.setLockId(lockId);
				lockInfo.setLockName(lockName);
				lockInfo.setLockType(passwordtype);
				lockInfo.setPassword(password);
				lockInfo.setPasswordType(passwordtype);
				lockInfo.setQuestion(question);
				lockInfo.setAnswer(answer);
				lockInfo.setIsDefault(isDefault);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		return lockInfo;
	}
	
	/**
	* 获得锁的整体信息
	 */
	public LockSystemInfo getLockAllInfo() {
		
		// create table lockall(_id INTEGER PRIMARY KEY AUTOINCREMENT,lockallstate int,isautostart int,DeviceAdminAdd int)
		LockSystemInfo lockInfo = new LockSystemInfo();
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursor = db.query("lockall", null, null,null, null, null, null);
			if (null != cursor && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex("_id"));
				int lockallstate = cursor.getInt(cursor.getColumnIndex("lockallstate"));
				int isautostart = cursor.getInt(cursor.getColumnIndex("isautostart"));
				int deviceAdminAdd = cursor.getInt(cursor.getColumnIndex("DeviceAdminAdd"));
				
				lockInfo.setId(id);
				lockInfo.setIsautostart(isautostart);
				lockInfo.setLockallstate(lockallstate);
				lockInfo.setDeviceAdminAdd(deviceAdminAdd);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		return lockInfo;
	}
	
	/**
	* 更新锁的整体信息
	 */
	public void updateLockAllAutostart(int state) {
		
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("isautostart",String.valueOf(state)); // -1 就是不要限制
			
			db.update("lockall", value, null, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	* 更新锁
	 */
	public void updateAppIsLockByPackage(int isLock,String packageName) {
		
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("islock",String.valueOf(isLock)); // -1 就是不要限制
			
			db.update("appList", value, "packageName = ?", new String[]{packageName});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* 更新锁
	 */
	public void updateAppIsLockByAppId(int isLock,long appId) {
		
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("islock",String.valueOf(isLock)); // -1 就是不要限制
			
			db.update("appList", value, "_id = ?", new String[]{String.valueOf(appId)});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* 更新锁的整体信息
	 */
	public void updateLockAllDeviceAdminAdd(int state) {
		
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("DeviceAdminAdd",String.valueOf(state)); // -1 就是不要限制
			
			db.update("lockall", value, null, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	* 更新锁的整体信息
	 */
	public void updateLockAllState(int state) {
		
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("lockallstate",String.valueOf(state)); // -1 就是不要限制
			
			db.update("lockall", value, null, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 使用ID获得锁信息
	 */
	public LockInfo getLockById(int pLockId) {
		
		LockInfo lockInfo = new LockInfo();
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursor = db.query("locks", null, "_id=?", new String[]{String.valueOf(pLockId)}, null, null, null);
			if (null != cursor && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int lockId = cursor.getInt(cursor.getColumnIndex("_id"));
				String lockName = cursor.getString(cursor.getColumnIndex("name"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				String passwordtype = cursor.getString(cursor.getColumnIndex("passwordtype"));
				String question = cursor.getString(cursor.getColumnIndex("question"));
				String answer = cursor.getString(cursor.getColumnIndex("answer"));
				int isDefault = cursor.getInt(cursor.getColumnIndex("isDefault"));
				
				lockInfo.setLockId(lockId);
				lockInfo.setLockName(lockName);
				lockInfo.setLockType(passwordtype);
				lockInfo.setPassword(password);
				lockInfo.setPasswordType(passwordtype);
				lockInfo.setQuestion(question);
				lockInfo.setAnswer(answer);
				lockInfo.setIsDefault(isDefault);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		return lockInfo;
	}
	
	/**
	 * 初期化提醒表
	 */
	public void intiRemind() {
		
		// 初期化单个锁的信息
		Cursor cursorRemind = null;
		try{
			SQLiteDatabase db = dao.getDB();

			// 提前提醒信息
			cursorRemind = db.query("remind", null, null, null, null, null, null);
			if  (null == cursorRemind || cursorRemind.getCount() <= 0) {
				// 没有提醒时间
				ContentValues value=new ContentValues();
				value.put("time",60000);
				db.insert("remind", null, value);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursorRemind) {
				cursorRemind.close();
				cursorRemind = null;
			}
		}
	}
	
	/**
	 * 获得提醒时间
	 */
	public long getRemind() {
		
		long remind = 0;
		// 初期化单个锁的信息
		Cursor cursorRemind = null;
		try{
			SQLiteDatabase db = dao.getDB();

			// 提前提醒信息
			cursorRemind = db.query("remind", null, null, null, null, null, null);
			if  (null == cursorRemind || cursorRemind.getCount() <= 0) {
				// 没有提醒时间
				ContentValues value=new ContentValues();
				value.put("time",60000);
				db.insert("remind", null, value);
				remind = 60000;
			} else {
				cursorRemind.moveToFirst();
				remind = cursorRemind.getLong(cursorRemind.getColumnIndex("time"));
			}
			return remind;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursorRemind) {
				cursorRemind.close();
				cursorRemind = null;
			}
		}
		return remind;
	}
	
	/**
	 * 初期化锁的整体信息
	 */
	public void initLockall() {
		
		// 初期化单个锁的信息
		Cursor cursorLockall = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursorLockall = db.query("lockall", null, null, null, null, null, null);
			if (null == cursorLockall || cursorLockall.getCount() <= 0) {
				ContentValues value=new ContentValues();
				value.put("lockallstate",0); // 是否点过全上了锁的钮  0 没有点过
				value.put("isautostart", 1); // 是否手机启动就自动上锁  1 上锁
				value.put("DeviceAdminAdd", 0); // 是否添加到设备管理器  0 没有
				db.insert("lockall", null, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursorLockall) {
				cursorLockall.close();
				cursorLockall = null;
			}
		}
	}
	
	
	/**
	 * 获得wifi的可用时段
	 * 
	 * @param dataList
	 */
	public void getWifiTimeSegment(List<LockTimeSegmentBean> dataList) {
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();

			// 提前提醒信息
			cursor = db.query("wifiusable", null, null, null, null, null, null);
			if  (null != cursor && cursor.getCount() > 0) {				
				cursor.moveToFirst();
				do {
					LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
					lockTimeSegmentBean.setTimeId(cursor.getInt(cursor.getColumnIndex("_id")));
					lockTimeSegmentBean.setStartTime(cursor.getString(cursor.getColumnIndex("beginTime")));
					lockTimeSegmentBean.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
//					lockTimeSegmentBean.setAppId(cursor.getInt(cursor.getColumnIndex("appid")));
					dataList.add(lockTimeSegmentBean);
				} while(cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	/**
	 * 获得3G的可用时段
	 * 
	 * @param dataList
	 */
	public void get3GTimeSegment(List<LockTimeSegmentBean> dataList) {
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();

			// 提前提醒信息
			cursor = db.query("netusable", null, null, null, null, null, null);
			if  (null != cursor && cursor.getCount() > 0) {				
				cursor.moveToFirst();
				do {
					LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
					lockTimeSegmentBean.setTimeId(cursor.getInt(cursor.getColumnIndex("_id")));
					lockTimeSegmentBean.setStartTime(cursor.getString(cursor.getColumnIndex("beginTime")));
					lockTimeSegmentBean.setEndTime(cursor.getString(cursor.getColumnIndex("endTime")));
//					lockTimeSegmentBean.setAppId(cursor.getInt(cursor.getColumnIndex("appid")));
					dataList.add(lockTimeSegmentBean);
				} while(cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
	}

	
	/**
	 * 使用包名获得应用的信息
	 */
	public LockAppInfo getAppByPackageName(String pPackageName) {
		
		LockAppInfo lockAppInfo = new LockAppInfo();
		Cursor cursor = null;
		Cursor cursorDay = null;
		Cursor cursorWeek = null;
		Cursor cursorTime = null;
		try{
			SQLiteDatabase db = dao.getDB();
		
			// 初始化锁的整体性信息的表
			cursor = db.query("appList", null, "packageName=?", new String[] {pPackageName}, null, null, null);
			if (null != cursor && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int appId = cursor.getInt(cursor.getColumnIndex("_id"));
				String appName = cursor.getString(cursor.getColumnIndex("appName"));
				String packageName = cursor.getString(cursor.getColumnIndex("packageName"));
				int islock = cursor.getInt(cursor.getColumnIndex("islock"));
//				String week = cursor.getString(cursor.getColumnIndex("week"));
				String limitTime = cursor.getString(cursor.getColumnIndex("limitTime"));
				int lockId = cursor.getInt(cursor.getColumnIndex("lockId"));
				
				lockAppInfo.setAppId(appId);
				lockAppInfo.setAppName(appName);
				lockAppInfo.setIslock(islock);
				lockAppInfo.setLimitTime(Long.valueOf(limitTime));
				lockAppInfo.setPackageName(packageName);
				lockAppInfo.setLockId(lockId);
				
				
				// 获得应用的日期限制 (日期升序)
				cursorDay = db.query("appusableday", null, "appid=?", new String[] {String.valueOf(appId)}, null, null, "day asc");
				if (null != cursorDay && cursorDay.getCount() > 0) {
					lockAppInfo.getLimitDay().clear();
					
					cursorDay.moveToFirst();
					do {
						lockAppInfo.getLimitDay().add(cursorDay.getInt(cursorDay.getColumnIndex("day")));
					} while(cursorDay.moveToNext());
				}
				
				// 获得应用的周限制 (周升序)
				cursorWeek = db.query("appusableweek", null, "appid=?", new String[] {String.valueOf(appId)}, null, null, "week asc");
				if (null != cursorWeek && cursorWeek.getCount() > 0) {
					lockAppInfo.getLimitWeek().clear();
					
					cursorWeek.moveToFirst();
					do {
						lockAppInfo.getLimitWeek().add(cursorWeek.getInt(cursorWeek.getColumnIndex("week")));
					} while(cursorWeek.moveToNext());
				}
				
				// 获得应用的周限制 (设置时间升序)
				cursorTime = db.query("appusabletime", null, "appid=?", new String[] {String.valueOf(appId)}, null, null, "beginTime asc");
				if (null != cursorTime && cursorTime.getCount() > 0) {
					lockAppInfo.getLimitTimeSegment().clear();
					
					cursorTime.moveToFirst();
					do {
						LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
						lockTimeSegmentBean.setTimeId(cursorTime.getInt(cursorTime.getColumnIndex("_id")));
						lockTimeSegmentBean.setStartTime(cursorTime.getString(cursorTime.getColumnIndex("beginTime")));
						lockTimeSegmentBean.setEndTime(cursorTime.getString(cursorTime.getColumnIndex("endTime")));
						lockTimeSegmentBean.setAppId(cursorTime.getInt(cursorTime.getColumnIndex("appid")));
						lockAppInfo.getLimitTimeSegment().add(lockTimeSegmentBean);
					} while(cursorTime.moveToNext());
				}
				
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
			if (null != cursorDay) {
				cursorDay.close();
				cursorDay = null;
			}
			if (null != cursorWeek) {
				cursorWeek.close();
				cursorWeek = null;
			}
			if (null != cursorTime) {
				cursorTime.close();
				cursorTime = null;
			}
		}
		return lockAppInfo;
	}
	
	
	/**
	 * 加一个应用的信息到数据库里（添加基础数据用）
	 */
	public long addBaseAppInfo(LockAppInfo lockAppInfo) {
		long appId = -1;
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// appName varchar(256),packageName varchar(300),islock int,week varchar(30),limitTime varchar(30),lockId int)");
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("appName",lockAppInfo.getAppName()); // 应用程序名
			value.put("packageName",lockAppInfo.getPackageName()); // 包名
			value.put("islock",lockAppInfo.getIslock()); // 是否上锁
			value.put("week",""); // 这个字段不要了。。。
			value.put("limitTime",String.valueOf(lockAppInfo.getLimitTime())); // -1 就是不要限制
			value.put("lockId",lockAppInfo.getLockId()); // 锁的Id
			
			appId = db.insert("appList", null, value);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return appId;
	}
	
	/**
	 * 更新锁定可用星期
	 */
	public void updateAppLimitWeek(int appId,List<Integer> weeks) {
		try{
			SQLiteDatabase db = dao.getDB();
			
			db.delete("appusableweek", "appid=?", new String[]{String.valueOf(appId)});
			
			if (null != weeks && weeks.size() > 0) {
				for (int week : weeks) {
					// 添加应用信息
					ContentValues value=new ContentValues();
					value.put("week",week);
					value.put("appid",appId);
					db.insert("appusableweek", null, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新可用时长
	 */
	public void updateAppInfoForLimitTime(long appId, int limitTime,int leftUseTime) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// appName varchar(256),packageName varchar(300),islock int,week varchar(30),limitTime varchar(30),lockId int)");
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("limitTime",String.valueOf(limitTime)); // -1 就是不要限制
			value.put("leftUseTime",String.valueOf(leftUseTime)); // -1 剩余使用时间
			
			db.update("appList", value, "_id=?", new String[]{String.valueOf(appId)});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 更新应用使用的锁
	 */
	public void updateAppInfoForUseLock(long appId, int lockId) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// appName varchar(256),packageName varchar(300),islock int,week varchar(30),limitTime varchar(30),lockId int)");
			// 添加应用信息
			ContentValues value=new ContentValues();
			value.put("lockId",lockId); 
			
			db.update("appList", value, "_id=?", new String[]{String.valueOf(appId)});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加一个时间段
	 */
	public long addAppUseTimeSegment(LockTimeSegmentBean lockTimeSegmentBean) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 添加信息
			ContentValues values=new ContentValues();
			values.put("beginTime",lockTimeSegmentBean.getStartTime());
			values.put("endTime",lockTimeSegmentBean.getEndTime()); 
			values.put("appid",lockTimeSegmentBean.getAppId()); 

			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			return db.insert("appusabletime", null, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 添加WIFI的可用时间段
	 */
	public long addWifiUseTimeSegment(LockTimeSegmentBean lockTimeSegmentBean) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 添加信息
			ContentValues values=new ContentValues();
			values.put("beginTime",lockTimeSegmentBean.getStartTime());
			values.put("endTime",lockTimeSegmentBean.getEndTime());
//			values.put("appid",lockTimeSegmentBean.getAppId());

			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			return db.insert("wifiusable", null, values); // netusable
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 删除一个WIFI的可用时间段
	 */
	public void delWifiUseTimeSegment(int id) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			db.delete("wifiusable", "_id=?",new String[]{String.valueOf(id)});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除一个3G的可用时间段
	 */
	public void del3GUseTimeSegment(int id) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			db.delete("netusable", "_id=?",new String[]{String.valueOf(id)});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加3G的可用时间段
	 */
	public long add3GUseTimeSegment(LockTimeSegmentBean lockTimeSegmentBean) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 添加信息
			ContentValues values=new ContentValues();
			values.put("beginTime",lockTimeSegmentBean.getStartTime());
			values.put("endTime",lockTimeSegmentBean.getEndTime());
//			values.put("appid",lockTimeSegmentBean.getAppId());

			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			return db.insert("netusable", null, values); // 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 删除一个时间段
	 */
	public void delAppUseTimeSegment(long appId, int timeId) {
		try{
			SQLiteDatabase db = dao.getDB();
			// 字段
			// "create table appusabletime(_id INTEGER PRIMARY KEY AUTOINCREMENT,beginTime varchar(30),endTime varchar(30),appid int) "
			db.delete("appusabletime", "_id=? and appid=?",new String[]{String.valueOf(timeId),String.valueOf(timeId)});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获得现有的删除 包是否 已经设置了 拦截功能
	 * @return
	 */
	public LockAppInfo getUnInstallPackage() {
		
		LockAppInfo lockAppInfo = null;
		// 初期化单个锁的信息
		Cursor cursor = null;
		try{
			SQLiteDatabase db = dao.getDB();

			// 提前提醒信息
			cursor = db.query("appList", null, "packageName=?", new String[] { "com.android.packageinstaller" }, null, null, null);
			if  (null != cursor && cursor.getCount() > 0) {	
				lockAppInfo = new LockAppInfo();
				cursor.moveToFirst();
				int appId = cursor.getInt(cursor.getColumnIndex("_id"));
				String appName = cursor.getString(cursor.getColumnIndex("appName"));
				String packageName = cursor.getString(cursor.getColumnIndex("packageName"));
				int islock = cursor.getInt(cursor.getColumnIndex("islock"));
	//					String week = cursor.getString(cursor.getColumnIndex("week"));
				String limitTime = cursor.getString(cursor.getColumnIndex("limitTime"));
				int lockId = cursor.getInt(cursor.getColumnIndex("lockId"));
				
				lockAppInfo.setAppId(appId);
				lockAppInfo.setAppName(appName);
				lockAppInfo.setIslock(islock);
				lockAppInfo.setLimitTime(Long.valueOf(limitTime));
				lockAppInfo.setPackageName(packageName);
				lockAppInfo.setLockId(lockId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != cursor) {
				cursor.close();
				cursor = null;
			}
		}
		
		return lockAppInfo;
	}
	
	/**
	 * 使能 安装和卸载
	 * @param isLock
	 */
	public void enableUnInstallPackage(int isLock) {
		
		SQLiteDatabase db = dao.getDB();
		// 字段
		// appName varchar(256),packageName varchar(300),islock int,week varchar(30),limitTime varchar(30),lockId int)");
		// 添加应用信息
		ContentValues value=new ContentValues();
		value.put("islock",isLock); 
		
		db.update("appList", value, "packageName=?", new String[] { "com.android.packageinstaller" });
	}
	
	
//	/**
//	 * 获得一个应用的可用时长
//	 */
//	public void getAppLimitTimeSegment(int appId) {
//		
//		// 初期化单个锁的信息
//		Cursor cursor = null;
//		try{
//			SQLiteDatabase db = dao.getDB();
//			
//			// 获得应用的周限制 (设置时间升序)
//			cursor = db.query("appusabletime", null, "appid=?", new String[] {String.valueOf(appId)}, null, null, "beginTime asc");
//			if (null != cursor && cursor.getCount() > 0) {
//				cursorTime.moveToFirst();
//				do {
//					LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
//					lockTimeSegmentBean.setStartTime(cursorTime.getString(cursorTime.getColumnIndex("beginTime")));
//					lockTimeSegmentBean.setEndTime(cursorTime.getString(cursorTime.getColumnIndex("endTime")));
//					lockAppInfo.getLimitTimeSegment().add(lockTimeSegmentBean);
//				} while(cursorTime.moveToNext());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != cursorLockall) {
//				cursorLockall.close();
//				cursorLockall = null;
//			}
//		}
//	}
}
