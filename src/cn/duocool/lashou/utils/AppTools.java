package cn.duocool.lashou.utils;

import java.io.File;
import java.util.List;

import cn.duocool.lashou.R;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.net.client.AppData;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.NetTranProgressListener;
import cn.duocool.lashou.net.client.ResponseData;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class AppTools {
	private final static String TAG = AppTools.class.getName(); 
	
	// 后台检查
	public static int CHECK_MODE_BACK = 1;
	// 前台检查
	public static int CHECK_MODE_FORG = 2;
	
	public UpdateApk updateApk = new UpdateApk();
	public CheckUpdate checkUpdate = new CheckUpdate();
	
	public AppTools(Context context) {
		checkUpdate.setContext(context);
		updateApk.setContext(context);
	}

	/**
	 * 获得当前软件的版本号
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode  = 1;
		try {
			versionCode = context.getPackageManager()
				.getPackageInfo("cn.duocool.lashou", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
		return versionCode;
	}
	
	/**
	 * 获得当前软件的版本名字 （这个是给人看的）
	 * @return
	 */
	public static String getVersionName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager()
				.getPackageInfo("cn.duocool.lashou", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} 
		return verName;
	}
	
	public class CheckUpdate implements NetTranListener {
		
		private Context context;
		
		private int checkModel = CHECK_MODE_BACK;
		
		public int getCheckModel() {
			return checkModel;
		}

		public void setCheckModel(int checkModel) {
			this.checkModel = checkModel;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}

		public void checkUpdateStart() {
			// 获得服务器上的版本
			NetClient nc = new NetClient();
			nc.setOnNetTranListener(this);
			nc.getLastVersion(888);
		}

		@Override
		public void onTransmitted(int requestCode, ResponseData data) {
			if (requestCode == 888) {
				try{
				checkUpdateEnd(data.getAppData().getVersionCode(),data.getAppData().getVersionName(),data.getAppData());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public void checkUpdateEnd(String lastVerCode,String lastVerName,AppData data) {
			int verCode  = 1;
			String verName  = "";
			// 获得当前的版本
			try {
				verCode 
					= context.getPackageManager()
						.getPackageInfo("cn.duocool.lashou", 0).versionCode;
				verName 
					= context.getPackageManager()
						.getPackageInfo("cn.duocool.lashou", 0).versionName; 
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			
			int lastVerCodeI = Integer.valueOf(lastVerCode).intValue();
			
			if (lastVerCodeI > verCode) { // 开始升级
				if (CHECK_MODE_BACK == checkModel) { // 后台检查 出notifaction
					final MyDialog myDialog = new MyDialog(context);
					myDialog.setTitle(context.getString(R.string.apk_update_dialog_title));
					myDialog.setContent("有新版本了,\r\n 更新内容："+ data.getAppinfo() + "\r\n需要更新吗？");
					myDialog.setButton1(context.getString(R.string.apk_update_dialog_btn_update), new OnClickListener() {
						@Override
						public void onClick(View v) { // 升级
							updateApk.startUpdate();
							myDialog.close();
						}
					});
					myDialog.setButton2(context.getString(R.string.apk_update_dialog_btn_cancel), new OnClickListener() {
						@Override
						public void onClick(View v) { // 取消
							myDialog.close();
						}
					});
					myDialog.show();
				} else { // 前台检查 显示对话框
					final MyDialog myDialog = new MyDialog(context);
					myDialog.setTitle(context.getString(R.string.apk_update_dialog_title));
					myDialog.setContent("有新版本了！\r\n 更新内容："+ data.getAppinfo() + "\r\n需要更新吗？");
					myDialog.setButton1(context.getString(R.string.apk_update_dialog_btn_update), new OnClickListener() {
						@Override
						public void onClick(View v) { // 升级
							updateApk.startUpdate();
							myDialog.close();
						}
					});
					myDialog.setButton2(context.getString(R.string.apk_update_dialog_btn_cancel), new OnClickListener() {
						@Override
						public void onClick(View v) { // 取消
							myDialog.close();
						}
					});
					myDialog.show();
				}
			} else { // 不用升级
				if (CHECK_MODE_FORG == checkModel) { // 前台检查 显示对话框
					final MyDialog myDialog = new MyDialog(context);
					myDialog.setTitle(context.getString(R.string.apk_update_dialog_title));
					myDialog.setContent(context.getString(R.string.apk_update_no_update));
					myDialog.setButton1(context.getString(R.string.apk_update_dialog_btn_ok), new OnClickListener() {
						@Override
						public void onClick(View v) { // 升级
							myDialog.close();
						}
					});
					myDialog.show();
				}
			}
		}
	}
	
	public class UpdateApk  implements NetTranListener,NetTranProgressListener {
		
		Context context;
		ProgressDialog pd;
		
		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}
		
		public void startUpdate() {
			// 获得服务器上的版本
			NetClient nc = new NetClient();
			nc.setOnNetTranListener(this);
			nc.setOnNetTranProgressListener(this);
			nc.downloadApk(999);
			
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setTitle(context.getString(R.string.apk_update_download_title));
			pd.setMessage(context.getString(R.string.apk_update_download_message));
			pd.show();
		}

		@Override
		public void onTransmitted(int requestCode, ResponseData data) {
			if (requestCode == 999) { // 下载完毕
				pd.dismiss();
				// 下载完成 安装Apk
				installApk(data.getFilePath());
			}
		}

		@Override
		public void onTransmitting(int requestCode, long nowProgress,
				long maxProgress) { // 下载中
			pd.setMax((int)(maxProgress/1024));
			pd.setProgress((int)(nowProgress/1024));
		}
		
		public void installApk(String fileSavePath) {
			
			File file = new File(fileSavePath);  
			if(!file.exists()){  
				Log.i("", "找不到下载的软件"); 
				return;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}
}
