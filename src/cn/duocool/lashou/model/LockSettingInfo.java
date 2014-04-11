package cn.duocool.lashou.model;

import java.util.List;

public class LockSettingInfo {
	
	// 时间提醒
    private long remindTime;
    
    // wifi 可以使用时间
    private List<LockTimeSegmentBean> wifiLimitTimeList;
    
    // 2G 3G 网络 可以使用时间
    private List<LockTimeSegmentBean> netLimitTimeList;


	public long getRemindTime() {
		return remindTime;
	}


	public void setRemindTime(long remindTime) {
		this.remindTime = remindTime;
	}


	public List<LockTimeSegmentBean> getWifiLimitTimeList() {
		return wifiLimitTimeList;
	}


	public void setWifiLimitTimeList(List<LockTimeSegmentBean> wifiLimitTimeList) {
		this.wifiLimitTimeList = wifiLimitTimeList;
	}


	public List<LockTimeSegmentBean> getNetLimitTimeList() {
		return netLimitTimeList;
	}


	public void setNetLimitTimeList(List<LockTimeSegmentBean> netLimitTimeList) {
		this.netLimitTimeList = netLimitTimeList;
	}
}
