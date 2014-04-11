package cn.duocool.lashou.net.client;

public final class  RegData  {

	
	// 用户UserID			int
	private int userId;
	
	// 头像地址 			String
	private String headIconPath;
	
	// 邮箱   			String
	private String email;
	
	// 昵称   			String
	private String nick;
	
	// 手机号码 	 		String
	private String phoneNumber;
	
	// 密码      		String
	private String password;
	
	// 绑定的新浪uid String
	private String sinaUid;
	
	// 绑定的QQ uid		String
	private String qqUid;
	
	// 用户头像在服务器上的位置 数据库com1对应
	private String headImageServerPath;
	
	// 新浪tokon 数据库对应com2
	private String sinaToken;
	
	// qq tokon 数据库对应com3
	private String qqToken;
	

	@Override
	public String toString() {
		return "RegData [userId=" + userId + ", headIconPath=" + headIconPath
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
