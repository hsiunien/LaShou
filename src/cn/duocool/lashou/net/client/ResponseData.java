package cn.duocool.lashou.net.client;

import java.io.InputStream;
import java.util.List;

import android.graphics.Bitmap;

public class ResponseData {
	// 每个响应都会用到
	private String userId;
	
	// 响应方法
	private String responseModtd;
	
	// 响应状态
	private String responseStatus;
	// 响应消息
	private String responseMsg;
	
	// 推送响应消息
	private PushData pushData;
	
	// 应用程序消息
	private AppData  appData;
	
	// 下载的时候会用到
	private Bitmap bitmap;
	private String filePath;
	
	// 如果获得用户信息，这个里面会放返回的用户信息
	private RegData regData;

	// 获得用户位置的时候会用到
	private LocationData  locationData;
	
	// 获得用户位置的时候会用到(指定条数的位置)
	private List<LocationData>  locationDataList;
	
	// 获得用户的好友信息是会得到
	private RelationData relationData;
	
	// 检查系统里面是否已经存在  1 存在     0 不存在
	private String checkResult;
	
	// 获得用户的好友列表时会用到
	private List<RelationData> relationDataList;
	
	// 下载文件得到的文件流
	private InputStream inputStream;
	
	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public RegData getRegData() {
		return regData;
	}

	public void setRegData(RegData regData) {
		this.regData = regData;
	}

	public LocationData getLocationData() {
		return locationData;
	}

	public void setLocationData(LocationData locationData) {
		this.locationData = locationData;
	}

	public List<LocationData> getLocationDataList() {
		return locationDataList;
	}

	public void setLocationDataList(List<LocationData> locationDataList) {
		this.locationDataList = locationDataList;
	}

	public RelationData getRelationData() {
		return relationData;
	}

	public void setRelationData(RelationData relationData) {
		this.relationData = relationData;
	}

	public List<RelationData> getRelationDataList() {
		return relationDataList;
	}

	public void setRelationDataList(List<RelationData> relationDataList) {
		this.relationDataList = relationDataList;
	}

	public String getResponseModtd() {
		return responseModtd;
	}

	public void setResponseModtd(String responseModtd) {
		this.responseModtd = responseModtd;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public PushData getPushData() {
		return pushData;
	}

	public void setPushData(PushData pushData) {
		this.pushData = pushData;
	}

	public AppData getAppData() {
		return appData;
	}

	public void setAppData(AppData appData) {
		this.appData = appData;
	}	
}
