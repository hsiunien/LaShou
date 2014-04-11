package cn.duocool.lashou.net.client;

import java.io.Serializable;

public final class  PushData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String pushDataId;
	
	// 0:加好友 1:响应加好友信息 同意  2:响应加好友信息 不同意  3:请求查看位置权限  4:响应请求查看权限 同意   5:响应请求查看权限 不同意    6:匿名消息   7:广告推送
	private int pushType;
	
	// 推送的时候，添加的用户信息
	private UserData attachmentUser;
	
	// 推送给的那个用户
	private UserData toUser;
	
	// 推送时间
	private String pushTime;
	
	@Override
	public String toString() {
		return "PushData [pushDataId=" + pushDataId + ", pushType=" + pushType
				+ ", attachmentUser=" + attachmentUser + ", toUser=" + toUser
				+ ", pushTime=" + pushTime + "]";
	}

	public int getPushType() {
		return pushType;
	}

	public void setPushType(int pushType) {
		this.pushType = pushType;
	}

	public UserData getAttachmentUser() {
		return attachmentUser;
	}

	public void setAttachmentUser(UserData attachmentUser) {
		this.attachmentUser = attachmentUser;
	}

	public UserData getToUser() {
		return toUser;
	}

	public void setToUser(UserData toUser) {
		this.toUser = toUser;
	}

	public String getPushDataId() {
		return pushDataId;
	}

	public void setPushDataId(String pushDataId) {
		this.pushDataId = pushDataId;
	}

	public String getPushTime() {
		return pushTime;
	}

	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}
}
