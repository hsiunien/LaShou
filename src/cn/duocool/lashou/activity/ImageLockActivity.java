package cn.duocool.lashou.activity;

//图形密码界面

import cn.duocool.lashou.broadcastreceiver.HomeWatcher;
import cn.duocool.lashou.broadcastreceiver.OnHomePressedListener;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.NinePointLineView;
import cn.duocool.lashou.utils.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

public class ImageLockActivity extends BaseActivity {
	public static String gotoActivity = null;// 输入完后去哪个Activity
	public static String lockName = null;
	public static String packageName = null;
	public static int screenHeight;
	public static int screenWidth;
	public static String netControl;
	public static Activity activity;
	public int fromHome;
	
	public String isBackClose = null;
	public static Activity closeActivity = null;
	public String isBackCloseDialog = null;
	public static MyDialog closeDialog = null;
	
	public static LockCallBack lockCallBack = null;
	public static boolean setFlag = false;

	private String question;
	private String answer;

	Intent intent;
	public static String password;
	
	private View v ;
	
	HomeWatcher mHomeWatcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		// Log.d("tag", "oncreate");
		// 监听home键
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				Log.e("tag", "onHomePressed");
				if (null != isBackClose && "close".equals(isBackClose)) {
					if (null != closeActivity) {
						closeActivity.finish();
					}
				}
				
				if (null != isBackCloseDialog && "close".equals(isBackCloseDialog)) {
					if (null != closeDialog) {
						closeDialog.close();
						if (null != closeDialog) {
							closeDialog = null;
						}
					}
				}

				if (null != lockCallBack) {
					lockCallBack.lockDone(setFlag,false);
					lockCallBack = null;
				}
				
				ImageLockActivity.this.finish();
			}

			@Override
			public void onHomeLongPressed() {
				Log.e("tag", "onHomeLongPressed");
			}
		});
		mHomeWatcher.startWatch();
		//
		gotoActivity = intent.getStringExtra("gotoActivity");
		password = intent.getStringExtra("password");
		netControl = intent.getStringExtra("netControl");
		packageName = intent.getStringExtra("packageName");
		lockName = intent.getStringExtra("lockName");
		fromHome = intent.getIntExtra("fromHome", 0);
		
		isBackClose = intent.getStringExtra("isBackClose");
		isBackCloseDialog = intent.getStringExtra("isBackCloseDialog");

		question = intent.getStringExtra("question");
		answer = intent.getStringExtra("answer");

		screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽（像素，如：480px）
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		Log.d("tag", "gotoActivity=" + gotoActivity);
		v = new NinePointLineView(this, ImageLockActivity.this,question,answer,lockCallBack);
		setContentView(v);

//		Log.d(TAG, "设定现在的Activity名字："+ImageLockActivity.class.getName());
//		Tools.getApplication(this).setNowActivityName(this.getClass().getName());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Log.d("tag", " onResume");

		super.onResume();
		Tools.getApplication(this).setNowActivityName(ImageLockActivity.class.getName());
		v.invalidate();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		// Log.d("tag", "onRestart()");

		super.onRestart();
		v.invalidate();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (gotoActivity == null) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				if (fromHome == 1) {
					Intent i = new Intent();
					i.setClass(this, ActivityHome.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.startActivity(i);
				}
				
				if (null != isBackClose && "close".equals(isBackClose)) {
					if (null != closeActivity) {
						closeActivity.finish();
						if (null != closeActivity) {
							closeActivity = null;
						}
					}
					this.finish();
				}

				if (null != isBackCloseDialog && "close".equals(isBackCloseDialog)) {
					if (null != closeDialog) {
						closeDialog.close();
						if (null != closeDialog) {
							closeDialog = null;
						}
					}
					this.finish();
				}
				
				if (null != lockCallBack) {
					lockCallBack.lockDone(setFlag,false);
					lockCallBack = null;
					this.finish();
				}
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {

		if (gotoActivity != null) {
			finish();
		}
		// gotoActivity=null;
		// Log.d("tag", "onPause");
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// Log.d("tag", "destroy");
		super.onDestroy();
		
		if (null != mHomeWatcher) {
			mHomeWatcher.stopWatch();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
}
