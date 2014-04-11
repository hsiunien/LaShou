package cn.duocool.lashou.model;

/**
 * 限制时长保存数据用Bean
 * @author xwood
 */
public class LockTimeSegmentBean {
	
	private int timeId;
	private String startTime;
	private String endTime;
	private int appId;
	
	public int getTimeId() {
		return timeId;
	}
	public void setTimeId(int timeId) {
		this.timeId = timeId;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
}
