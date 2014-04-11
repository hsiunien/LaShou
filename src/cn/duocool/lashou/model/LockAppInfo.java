package cn.duocool.lashou.model;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class LockAppInfo {
	
	private long appId;
	
	 // 程序App名字
    private String appName;
    
	// 程序包名
    private String packageName;
    
    // 程序包Activity名字
    private String activityName;
    
    // 程序的图标
    private Drawable appIcon;
    
    // 程序是否上锁
    private int islock;
    
    // 程序是否限制时长(-1:没有限制)
    private long limitTime;
    
    // 当前程序的剩余使用时间
    private long leftTime = 0;
    
    // 使用时间
    private long usedTime;
    
    // 设定的限定的日期
    private ArrayList<Integer> limitWeek=new ArrayList<Integer>();
 // 设定的限定的星期
    private ArrayList<Integer> limitDay=new ArrayList<Integer>();
 // 设定的限定的时间段
    private ArrayList<LockTimeSegmentBean> limitTimeSegment =new ArrayList<LockTimeSegmentBean>();
    
    // 解锁的密码
    private String password; 
    // 解锁的类型
    private String passwordtype;
    // 程序上的哪一把锁
    private int lockId;
    // 锁的名字
    private String lockName;
    
    
    private boolean isCheck;
    
	@Override
	public String toString() {
		return "LockAppInfo [appId=" + appId + ", appName=" + appName
				+ ", packageName=" + packageName + ", activityName="
				+ activityName + ", appIcon=" + appIcon + ", islock=" + islock
				+ ", limitTime=" + limitTime + ", limitWeek=" + limitWeek
				+ ", limitDay=" + limitDay + ", limitTimeSegment="
				+ limitTimeSegment + ", password=" + password
				+ ", passwordtype=" + passwordtype + ", lockId=" + lockId
				+ ", lockName=" + lockName + "]";
	}
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getIslock() {
		return islock;
	}
	public void setIslock(int islock) {
		this.islock = islock;
	}
	public long getLimitTime() {
		return limitTime;
	}
	public void setLimitTime(long limitTime) {
		this.limitTime = limitTime;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPasswordtype() {
		return passwordtype;
	}
	public void setPasswordtype(String passwordtype) {
		this.passwordtype = passwordtype;
	}
	public int getLockId() {
		return lockId;
	}
	public void setLockId(int lockId) {
		this.lockId = lockId;
	}
	public String getLockName() {
		return lockName;
	}
	public void setLockName(String lockName) {
		this.lockName = lockName;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public ArrayList<Integer> getLimitWeek() {
		return limitWeek;
	}
	public void setLimitWeek(ArrayList<Integer> limitWeek) {
		this.limitWeek = limitWeek;
	}
	public ArrayList<Integer> getLimitDay() {
		return limitDay;
	}
	public void setLimitDay(ArrayList<Integer> limitDay) {
		this.limitDay = limitDay;
	}
	public ArrayList<LockTimeSegmentBean> getLimitTimeSegment() {
		return limitTimeSegment;
	}
	public void setLimitTimeSegment(ArrayList<LockTimeSegmentBean> limitTimeSegment) {
		this.limitTimeSegment = limitTimeSegment;
	}
	public long getLeftTime() {
		return leftTime;
	}
	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}

	public long getUsedTime() {
		return usedTime;
	}

	public void setUsedTime(long usedTime) {
		this.usedTime = usedTime;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	
}
