package cn.duocool.lashou.net.client;

import java.io.Serializable;


public class UserData implements Serializable   {
	
	private static final long serialVersionUID = 1L;

	// �û�UserID			int
	private int userId;
	
	// ͷ���ַ 			String
	private String headIconPath;
	
	// ����   			String
	private String email;
	
	// �ǳ�   			String
	private String nick;
	
	// �ֻ���� 	 		String
	private String phoneNumber;
	
	// ����      		String
	private String password;
	
	// �󶨵�����uid String
	private String sinaUid;
	
	// �󶨵�QQ uid		String
	private String qqUid;
	
	// �û�ͷ���ڷ������ϵ�λ�� ��ݿ�com1��Ӧ
	private String headImageServerPath;
	
	// ����tokon ��ݿ��Ӧcom2
	private String sinaToken;
	
	// qq tokon ��ݿ��Ӧcom3
	private String qqToken;
	
	@Override
	public String toString() {
		return "UserData [userId=" + userId + ", headIconPath=" + headIconPath
				+ ", email=" + email + ", nick=" + nick + ", phoneNumber="
				+ phoneNumber + ", password=" + password + ", sinaUid="
				+ sinaUid + ", qqUid=" + qqUid + ", headImageServerPath="
				+ headImageServerPath + ", sinaToken=" + sinaToken
				+ ", qqToken=" + qqToken + "]";
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getHeadIconPath() {
		return headIconPath;
	}

	public void setHeadIconPath(String headIconPath) {
		this.headIconPath = headIconPath;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSinaUid() {
		return sinaUid;
	}

	public void setSinaUid(String sinaUid) {
		this.sinaUid = sinaUid;
	}

	public String getQqUid() {
		return qqUid;
	}

	public void setQqUid(String qqUid) {
		this.qqUid = qqUid;
	}

	public String getHeadImageServerPath() {
		return headImageServerPath;
	}

	public void setHeadImageServerPath(String headImageServerPath) {
		this.headImageServerPath = headImageServerPath;
	}

	public String getSinaToken() {
		return sinaToken;
	}

	public void setSinaToken(String sinaToken) {
		this.sinaToken = sinaToken;
	}

	public String getQqToken() {
		return qqToken;
	}

	public void setQqToken(String qqToken) {
		this.qqToken = qqToken;
	}
}
