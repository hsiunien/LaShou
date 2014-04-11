package cn.duocool.lashou.model;

public class LockCheckInfo {
	
	private boolean isShowLockUI = false;
	
	private LockAppInfo lockAppInfo;

	public boolean isShowLockUI() {
		return isShowLockUI;
	}

	public void setShowLockUI(boolean isShowLockUI) {
		this.isShowLockUI = isShowLockUI;
	}

	public LockAppInfo getLockAppInfo() {
		return lockAppInfo;
	}

	public void setLockAppInfo(LockAppInfo lockAppInfo) {
		this.lockAppInfo = lockAppInfo;
	}
}
