package cn.duocool.lashou.model;

import android.graphics.Bitmap;

import cn.duocool.lashou.net.client.RegData;

/**
 * 用户实体类
 * @author hsiunien
 *
 */
public class UserInfo {
	
	private String userName;
	private int userId;
	private Bitmap headImg;
	private String headSrc="anxinbao/head.png";//default
	private String tel;
	private String password;
	private String email;
	private String sinaUid;
	private String QQUid;
	private String qqToken;
	private String sinaToken;
	public UserInfo() {
	}
	public UserInfo(RegData data) {
		this.setUserId(data.getUserId());
		this.setTel(data.getPhoneNumber());
		this.setPassword(data.getPassword());
		this.setUserName(data.getNick());
	}
	public RegData toRegData(){
		RegData data=new RegData();
		data.setUserId(userId);
		data.setPhoneNumber(tel);
		data.setPassword(password);
		data.setNick(userName);
		data.setQqUid(QQUid);
		data.setQqToken(qqToken);
		data.setSinaUid(sinaUid);
		data.setSinaToken(sinaToken);
		return data;
	}
	public String getUserName() {
		return userName;
	}

	public int getUserId() {
		return userId;
	}

	public Bitmap getHeadImg() {
		return headImg;
	}

	public String getTel() {
		return tel;
	}

	public String getPassword() {
		return password;
	}

	public String getSinaUid() {
		return sinaUid;
	}

	public String getQQUid() {
		return QQUid;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setHeadImg(Bitmap headImg) {
		this.headImg = headImg;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSinaUid(String sinaUid) {
		this.sinaUid = sinaUid;
	}

	public void setQQUid(String qQUid) {
		QQUid = qQUid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getHeadSrc() {
		return headSrc;
	}
	public void setHeadSrc(String headSrc) {
		this.headSrc = headSrc;
	}
	public String getQqToken() {
		return qqToken;
	}
	public String getSinaToken() {
		return sinaToken;
	}
	public void setQqToken(String qqToken) {
		this.qqToken = qqToken;
	}
	public void setSinaToken(String sinaToken) {
		this.sinaToken = sinaToken;
	} 
}
