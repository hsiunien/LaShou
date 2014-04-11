package cn.duocool.lashou.net.client;

import android.graphics.Bitmap;

public final class  UploadData  {

	// 用户UserID			int
	private int userId;
	
	// 头像地址 			String
	private String headIconPath;
	
	// 邮箱   			String
	private Bitmap bitmap;

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

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
}
