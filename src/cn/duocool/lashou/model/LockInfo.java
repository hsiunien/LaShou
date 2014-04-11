package cn.duocool.lashou.model;

public class LockInfo {
	
    // 解锁的密码
    private String password; 
    // 密码类型
    private String passwordType;
    // 解锁的类型
    private String lockType;
    // 程序上的哪一把锁
    private int lockId;
    // 锁的名字
    private String lockName;
    // 锁的问题
    private String question;
    // 锁的回答问题
    private String answer;
    // 是否为默认锁
    private int isDefault;
    
    // 程序使用剩余时间
    private String warnTime;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getLockType() {
		return lockType;
	}

	public void setLockType(String lockType) {
		this.lockType = lockType;
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

	public String getWarnTime() {
		return warnTime;
	}

	public void setWarnTime(String warnTime) {
		this.warnTime = warnTime;
	}

	public String getPasswordType() {
		return passwordType;
	}

	public void setPasswordType(String passwordType) {
		this.passwordType = passwordType;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}
}
