package cn.duocool.lashou.net.client;

public class RelationData implements java.io.Serializable {

	private static final long serialVersionUID = 4228499415437117681L;
	private Integer userId;
	private UserData friendData;
	private Integer friendId;
	private Integer auth;
	private Integer reqestAuth;
	private Integer isFriend;
	private String authTime;
	private String requestAuthTime;
	private String com1;
	private String com2;
	private String com3;
	private String com4;
	private String com5;
	private String com6;
	private String com7;
	private String com8;
	private String com9;
	private String com10;
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public UserData getFriendData() {
		return friendData;
	}

	public void setFriendData(UserData friendData) {
		this.friendData = friendData;
	}

	public Integer getAuth() {
		return auth;
	}

	public void setAuth(Integer auth) {
		this.auth = auth;
	}

	public Integer getReqestAuth() {
		return reqestAuth;
	}

	public void setReqestAuth(Integer reqestAuth) {
		this.reqestAuth = reqestAuth;
	}

	public Integer getIsFriend() {
		return isFriend;
	}

	public void setIsFriend(Integer isFriend) {
		this.isFriend = isFriend;
	}

	public String getAuthTime() {
		return authTime;
	}

	public void setAuthTime(String authTime) {
		this.authTime = authTime;
	}

	public String getRequestAuthTime() {
		return requestAuthTime;
	}

	public void setRequestAuthTime(String requestAuthTime) {
		this.requestAuthTime = requestAuthTime;
	}
	
	public String getCom1() {
		return com1;
	}

	public void setCom1(String com1) {
		this.com1 = com1;
	}

	public String getCom2() {
		return com2;
	}

	public void setCom2(String com2) {
		this.com2 = com2;
	}

	public String getCom3() {
		return com3;
	}

	public void setCom3(String com3) {
		this.com3 = com3;
	}

	public String getCom4() {
		return com4;
	}

	public void setCom4(String com4) {
		this.com4 = com4;
	}

	public String getCom5() {
		return com5;
	}

	public void setCom5(String com5) {
		this.com5 = com5;
	}

	public String getCom6() {
		return com6;
	}

	public void setCom6(String com6) {
		this.com6 = com6;
	}

	public String getCom7() {
		return com7;
	}

	public void setCom7(String com7) {
		this.com7 = com7;
	}

	public String getCom8() {
		return com8;
	}



	public void setCom8(String com8) {
		this.com8 = com8;
	}



	public String getCom9() {
		return com9;
	}



	public void setCom9(String com9) {
		this.com9 = com9;
	}



	public String getCom10() {
		return com10;
	}



	public void setCom10(String com10) {
		this.com10 = com10;
	}

	public Integer getFriendId() {
		return friendId;
	}

	public void setFriendId(Integer friendId) {
		this.friendId = friendId;
	}
}