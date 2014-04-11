package cn.duocool.lashou.model;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.service.LashouService;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class MyApplication extends Application {
	private final static String TAG  = MyApplication.class.getName();
	private boolean isLogin=false;
	public boolean isFirstRun=true;
	private UserInfo myInfo;
	
	/**
	 * 当前版本信息
	 */
	private int lashouEdition = CommDef.EDITION_CHILD;
	
	/**
	 * 上一个界面的是哪个
	 */
	private String preActivityName;
	
	/**
	 * 拉手服务
	 */
	private LashouService lashouService;
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		
		Log.d(TAG, "~~~~MyApplication onTerminate~~~~");
		if (null != lashouService) {
			lashouService.stopSelf();
			lashouService = null;
		}
	}
	
	public void setLoginState(boolean loginFlag){
		this.isLogin=loginFlag;
	}
	public void logout(){
		this.isLogin=false;
		if(myInfo.getHeadImg()!=null){
			myInfo.getHeadImg().recycle();
		}
		myInfo=null;
		SQLiteDatabase db=new DataBaseHelper(this).getWritableDatabase();
		db.delete("myInfo", null, null);
		db.close();
		final UMSocialService mController = UMServiceFactory.getUMSocialService(
				"com.umeng.login", RequestType.SOCIAL);
		//UMInfoAgent.removeOauth(this, SHARE_MEDIA.SINA);
		UMInfoAgent.removeOauth(this, SHARE_MEDIA.QZONE);
		mController.loginout(this,null);			
	}
	public boolean getLogin(){
		return isLogin;
	}
	public UserInfo getMyInfo() {
		if(myInfo==null){
			myInfo=new UserInfo();
		}
		return myInfo;
	}
	public void setMyInfo(UserInfo myInfo) {
		this.myInfo = myInfo;
	}

	/*以下为百度地图管理相关*/
	private static MyApplication mInstance = null;
	public boolean m_bKeyRight = true;
	public static BMapManager mBMapManager = null;

//	public static final String strKey = "E793e11645e14ee38b036f85bfe03df6";
	public static final String strKey = "D2e1452ce3d2bc1a11251fb3ebc50617";
	
	/*
    	注意：为了给用户提供更安全的服务，Android SDK自v2.1.3版本开始采用了全新的Key验证体系。
    	因此，当您选择使用v2.1.3及之后版本的SDK时，需要到新的Key申请页面进行全新Key的申请，
    	申请及配置流程请参考开发指南的对应章节
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "~~~~MyApplication onCreate~~~~");
		mInstance = this;
		initEngineManager(this);
	}

	public void initEngineManager(Context context) {
		if (mBMapManager == null) {
			mBMapManager = new BMapManager(context);
		}

		if (!mBMapManager.init(strKey,new MyGeneralListener())) {
			Toast.makeText(MyApplication.getInstance().getApplicationContext(), 
					"BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
		}
	}

	public static MyApplication getInstance() {
		return mInstance;
	}


	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	public static class MyGeneralListener implements MKGeneralListener {

		@Override
		public void onGetNetworkState(int iError) {
			if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
						Toast.LENGTH_LONG).show();
			}
			else if (iError == MKEvent.ERROR_NETWORK_DATA) {
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
						Toast.LENGTH_LONG).show();
			}
			// ...
		}

		@Override
		public void onGetPermissionState(int iError) {
			if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
				//授权Key错误：
				Toast.makeText(MyApplication.getInstance().getApplicationContext(), 
						"请输入正确的授权Key！", Toast.LENGTH_LONG).show();
				MyApplication.getInstance().m_bKeyRight = false;
			}
		}
	}
	
	// get 和 set 方法 ---------------------------------------------------------------------
	
	public LashouService getLashouService() {
		return lashouService;
	}

	public void setLashouService(LashouService lashouService) {
		this.lashouService = lashouService;
	}

	public int getLashouEdition() {
		SharedPreferences sp =  this.getSharedPreferences(CommDef.PREFERENCE_NAME, Context.MODE_PRIVATE);
		lashouEdition = sp.getInt(CommDef.EDITION_KEY, -1);
		return lashouEdition;
	}

	public void setLashouEdition(int lashouEdition) {
		SharedPreferences sp =  this.getSharedPreferences(CommDef.PREFERENCE_NAME, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.putInt(CommDef.EDITION_KEY, lashouEdition);
		ed.commit();
		this.lashouEdition = lashouEdition;
	}

	public String getPreActivityName() {
		return preActivityName;
	}

	public void setNowActivityName(String preActivityName) {
		this.preActivityName = preActivityName;
	}
}
