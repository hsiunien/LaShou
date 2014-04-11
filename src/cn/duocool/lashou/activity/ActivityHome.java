package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.List;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.setImagePasswordView;
import cn.duocool.lashou.service.ElectronicFenceService;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LocationService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.service.PushService;
import cn.duocool.lashou.service.PushService.LocalService;
import cn.duocool.lashou.thread.SentLocationThread;
import cn.duocool.lashou.utils.AppTools;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;
import cn.duocool.lashou.view.box.BoxGridView;
import cn.duocool.lashou.view.box.BoxItem;
import cn.duocool.lashou.view.box.OnBoxClick;
import cn.duocool.lashou.view.box.OnBoxViewChanged;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.PushData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityHome extends BaseActivity  implements ServiceConnection{
	private final static String TAG = ActivityHome.class.getName();
	
	ImageView myInfoImagv,bgImagV;//我的头像 背景图片
	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);
	
	// 图标
	private int[] icons = { 
			R.drawable.lock_icon, 
			R.drawable.hywl_icon,
			R.drawable.location_icon, 
			R.drawable.setting_icon,
			R.drawable.share_icon, 
			R.drawable.exit_icon, // 退出系统
			R.drawable.theme_icon, // 主题
			R.drawable.mail_us_icon, 
			R.drawable.about_us_icon };

	private TextView goLoginTv;
	private ImageLoader imgLoader;
	private int [] colors = {
//		R.color.color_01,	R.color.color_02,	R.color.color_03,	R.color.color_04,	R.color.color_05,	R.color.color_06,	R.color.color_07,	R.color.color_08
		R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background,R.color.box_background	
	};
//	/**
//	 * 关闭锁的服务
//	 */
//	public void stopLocking()
//	{
//		Intent intent=new Intent();
//		intent.setClass(ActivityHome.this, LockService.class);
//		stopService(intent);
//		DataBaseHelper dbhlper=new DataBaseHelper(this);
//		SQLiteDatabase db=dbhlper.getWritableDatabase();
//		ContentValues value=new ContentValues();
//		value.put("isautostart",0);
//		 db.update("lockall", value, "id=1", null);
//		 if(db!=null)
//		 {
//			 db.close();
//		 }
//	}
	
	/**
	 * 停掉位置服务，包括围栏和定位
	 */
	protected void stopLocationService() {
		
		//Intent intent=new Intent();
		//intent.setClass(ActivityHome.this, LocationService.class);
		//stopService(intent);
		
		Intent intent2=new Intent();
		intent2.setClass(ActivityHome.this, ElectronicFenceService.class);
		stopService(intent2);
		
		//SentLocationThread.sentLocationThread_is_exit = true;
		
//		ElectronicFenceService.is_Exit = true;
	}
	
	

	/**
	 * 停掉推送服务
	 */
	protected void stopPushService() {
		
		Intent intent=new Intent();
		intent.setClass(ActivityHome.this, PushService.class);
		stopService(intent);
	}
	
	private OnBoxClick[] activitys = { 
			new OnBoxClick() {
				@Override
				public void boxClick() 
				{
					Intent intent=new Intent(ActivityHome.this,AppLockMainActivity.class);
					startActivity(intent);
				}
			},
			new OnBoxClick() {
				@Override
				public void boxClick() {					
					if(!Tools.getApplication(ActivityHome.this).getLogin()) {
						final MyDialog myDialog = new MyDialog(ActivityHome.this);
						myDialog.setTitle(ActivityHome.this.getString(R.string.eletronic_fence_dialog_title));
						myDialog.setContent(ActivityHome.this.getString(R.string.eletronic_fence_dialog_msg));
						myDialog.setButton1(ActivityHome.this.getString(R.string.apk_update_dialog_btn_ok), new OnClickListener() {
							@Override
							public void onClick(View v) { 
								myDialog.close();
							}
						});
						myDialog.setcloseBtonClickListener(new OnClickListener() {				
							@Override
							public void onClick(View v) {
								myDialog.close();					
							}
						});
						myDialog.show();
					} else {
						Intent intent = new Intent(ActivityHome.this,Electronic_fenceActivity.class);
						startActivity(intent);
					}
				}
			},
			new OnBoxClick() {  // 好友位置
				@Override
				public void boxClick() { // 好友位置
					
					if(!Tools.getApplication(ActivityHome.this).getLogin()){
						final MyDialog myDialog = new MyDialog(ActivityHome.this);
						myDialog.setTitle(ActivityHome.this.getString(R.string.eletronic_fence_dialog_title));
						myDialog.setContent(ActivityHome.this.getString(R.string.eletronic_fence_dialog_msg));
						myDialog.setButton1(ActivityHome.this.getString(R.string.apk_update_dialog_btn_ok), new OnClickListener() {
							@Override
							public void onClick(View v) { 
								myDialog.close();
							}
						});
						myDialog.setcloseBtonClickListener(new OnClickListener() {				
							@Override
							public void onClick(View v) {
								myDialog.close();					
							}
						});
						myDialog.show();
					} else {
						Intent intent = new Intent(ActivityHome.this,FriendLocationActivity.class);
						startActivity(intent);
					}
				}
			},
			new OnBoxClick() {
				@Override
				public void boxClick() {
					Intent intent = new Intent(ActivityHome.this,SettingActivity.class);
					startActivity(intent);
				}
			},
			new OnBoxClick() {
				@Override
				public void boxClick() {
					mController.setShareContent(getResources().getString(R.string.share_conent));
					mController.setShareMedia(new UMImage(ActivityHome.this,R.drawable.lashou_share));
					mController.openShare(ActivityHome.this, false);
				}
			},
			new OnBoxClick() { // 退出系统功能实现
				@Override
				public void boxClick() {					
					final MyDialog myDialog = new MyDialog(ActivityHome.this);
					myDialog.setTitle(ActivityHome.this.getString(R.string.exit_warn_title));
					myDialog.setContent(ActivityHome.this.getString(R.string.exit_warn_msg));
					myDialog.setButton1(ActivityHome.this.getString(R.string.exit_warn_btn_ok), new OnClickListener() {
						@Override
						public void onClick(View v) { // 确定
							myDialog.close();
							// 退出系统
							// 函数系统调用
//							stopLocking();
							stopLocationService();//停掉位置服务，包括围栏和定位
							stopPushService();
							Tools.getApplication(ActivityHome.this).isFirstRun=true;
//							LashouService lashouService = Tools.getApplication(ActivityHome.this).getLashouService();
							Intent intent=new Intent();
							intent.setClass(ActivityHome.this, LashouService.class);
							stopService(intent);
							ActivityHome.this.finish();
						}
					});
					myDialog.setButton2(ActivityHome.this.getString(R.string.exit_warn_btn_cancel), new OnClickListener() {
						@Override
						public void onClick(View v) { // 取消
							myDialog.close();
						}
					});
					myDialog.show();
					
					
					
				}
			},
			new OnBoxClick() { // 主题
				@Override
				public void boxClick() {
					Intent intent = new Intent(ActivityHome.this,ThemeActivity.class);
					startActivity(intent);
				}
			},
			new OnBoxClick() {
				@Override
				public void boxClick() {
					Intent intent = new Intent(ActivityHome.this,AdvitiseActivity.class);
					startActivity(intent);
				}
			},
			new OnBoxClick() {
				@Override
				public void boxClick() {
					Intent intent = new Intent(ActivityHome.this,AboutAndHelperActivity.class);
					startActivity(intent);
				}
			}
			
		};
	// 删除不需要的分享平台 增加微信平台
	String appId = "wx04be9dfd58afe096";//wx644ed164029da71e wxb023e4d25209ce09
	String contentUrl ;	
	private String[] titles;
//	private Handler handler=null;
	MyDialog dialog;
	PushService pushService;
	private boolean connectedService=false;
	SharedPreferences sharedPreferences;
	String defaultUrl="theme/theme1.png";
	String bgUrl;
	private DaoBase dao;
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if (Tools.getApplication(this).getPreActivityName().equals(ActivityHome.class.getName()) ||
				Tools.getApplication(this).getPreActivityName().equals(Main.class.getName())) {
			
			LockDao lockDao = new LockDao(this, dao);
			LockInfo lockInfo = lockDao.getDefaultLock();
			
			String lockType = lockInfo.getPasswordType();
			if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
				// 图形锁
				Intent intent = new Intent(this,ImageLockActivity.class);
				intent.putExtra("lockName", lockInfo.getLockName());
				intent.putExtra("password", lockInfo.getPassword());
				intent.putExtra("question", lockInfo.getQuestion());
				intent.putExtra("answer", lockInfo.getAnswer());
				
				startActivity(intent);
//				this.finish();
//				return;
			} else {
				// PIN锁
				Intent intent = new Intent(this,PasswordActivity.class);
				intent.putExtra("lockName", lockInfo.getLockName());
				intent.putExtra("password", lockInfo.getPassword());
				intent.putExtra("question", lockInfo.getQuestion());
				intent.putExtra("answer", lockInfo.getAnswer());
				startActivity(intent);
//				this.finish();
//				return;
			}
		}
		
//		Log.d(TAG, "设定现在的Activity名字："+ActivityHome.class.getName());
//		Tools.getApplication(this).setNowActivityName(ActivityHome.class.getName());
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		

	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		dao = Tools.getApplication(this).getLashouService().getLockService().getDao();
		LockDao lockDao = new LockDao(this, dao);
		LockInfo lockInfo = lockDao.getDefaultLock();
		
		if (StringUtils.isNull(lockDao.getDefaultLock().getPassword())) {
			// 密码设置界面
			Intent intent = new Intent(this,SetImagePasswordActivity.class);
			intent.putExtra("lockName", lockInfo.getLockName());
			intent.putExtra("gotoActivity", "ActivityHome");
			startActivity(intent);
			this.finish();
			return;
		}
		
		if (Tools.getApplication(this).getPreActivityName().equals(Main.class.getName()) 
				|| Tools.getApplication(this).getPreActivityName().equals(ActivityHome.class.getName())) {
			
			String lockType = lockInfo.getPasswordType();
			if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
				// 图形锁
				Intent intent = new Intent(this,ImageLockActivity.class);
				intent.putExtra("lockName", lockInfo.getLockName());
				intent.putExtra("password", lockInfo.getPassword());
				intent.putExtra("question", lockInfo.getQuestion());
				intent.putExtra("answer", lockInfo.getAnswer());

			
				
				startActivity(intent);
//				this.finish();
//				return;
			} else {
				// PIN锁
				Intent intent = new Intent(this,PasswordActivity.class);
				intent.putExtra("lockName", lockInfo.getLockName());
				intent.putExtra("password", lockInfo.getPassword());
				intent.putExtra("question", lockInfo.getQuestion());
				intent.putExtra("answer", lockInfo.getAnswer());
				
				
				
				startActivity(intent);
//				this.finish();
//				return;
			}
		}

		
		initialView();
		bgImagV=(ImageView) findViewById(R.id.homeBg);
		sharedPreferences=getSharedPreferences("config", 0);
		// 
//		contentUrl=getResources().getString(R.string.share_link);
		// 删除不需要的分享平台 增加微信平台
		String appId = "wx04be9dfd58afe096";
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL);
		mController.getConfig().supportWXPlatform(this, appId, getString(R.string.share_link));
		mController.getConfig()
				.supportWXCirclePlatform(this, appId, getString(R.string.share_link));
		// mController.getConfig().setPlatforms(SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN);
		myInfoImagv = (ImageView) findViewById(R.id.myInfo);
//		handler=new Handler(){
//			public void handleMessage(android.os.Message msg) {
//				/*PushData  data=(PushData) msg.obj;
//				final UserData userdata=data.getAttachmentUser();
////				 0:加好友 1:响应加好友信息 同意  2:响应加好友信息 不同意  3:请求查看位置权限  4:响应请求查看权限 同意   5:响应请求查看权限 不同意    6:匿名消息   7:广告推送
//				if(data.getPushType()==0){
//					final MyDialog dialog = new MyDialog(ActivityHome.this);
//					dialog.setTitle("好友申请");
//					dialog.setContent(userdata.getNick() + "申请加您为好友");
//					dialog.setTag(userdata);
//					dialog.show();
//					dialog.setButton1("同意", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							dialog.close();
//							NetClient nc = new NetClient();
//							nc.setOnNetTranListener(ActivityHome.this);
//							nc.responseAddFriend(888,
//									Tools.getApplication(ActivityHome.this)
//											.getMyInfo().getUserId()
//											+ "", ((UserData)dialog.getTag()).getUserId()  + "",
//									"Y");
//						}
//					});
//					dialog.setButton2("拒绝", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							dialog.close();
//							NetClient nc = new NetClient();
//							nc.setOnNetTranListener(ActivityHome.this);
//							nc.responseAddFriend(888,
//									Tools.getApplication(ActivityHome.this)
//											.getMyInfo().getUserId()
//											+ "", ((UserData)dialog.getTag()).getUserId() + "",
//									"N");
//						}
//					});
//				}else if(data.getPushType()==1){
//					final MyDialog dialog=new MyDialog(ActivityHome.this);
//					dialog.setTitle("好友申请");
//					dialog.setContent(userdata.getNick()+"已经同意了您的申请");
//					dialog.show();
//					dialog.setButton1("我知道了", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							dialog.close();
//						}
//					});
//				}else if(data.getPushType()==2){
//					final MyDialog dialog=new MyDialog(ActivityHome.this);
//					dialog.setTitle("好友申请");
//					dialog.setContent(userdata.getNick()+" 拒绝 了您的申请");
//					dialog.show();
//					dialog.setButton1("我知道了", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							dialog.close();
//						}
//					});
//				}else if(data.getPushType()==3){
//					final MyDialog dialog=new MyDialog(ActivityHome.this);
//					dialog.setTitle("权限申请");
//					dialog.setContent(userdata.getNick()+" 向您申请位置查看");
//					dialog.show();
//					dialog.setButton1("同意", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							NetClient nc=new NetClient();
//							nc.changViewRole(789, userdata.getUserId()+"",  Tools.getApplication(ActivityHome.this).getMyInfo().getUserId()+"", 3);
//							dialog.close();
//						}
//					});
//					dialog.setButton2("拒绝", new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							NetClient nc=new NetClient();
//							nc.changViewRole(789, userdata.getUserId()+"",  Tools.getApplication(ActivityHome.this).getMyInfo().getUserId()+"", 1);
//							dialog.close();
//						}
//					});
//							
//				}	
//			*/	
//			};
//		};
		//如果已经登录则创建服务push
		if(Tools.getApplication(this).getLogin()){
			Intent service=new Intent(this, PushService.class);
			bindService(service, this, Context.BIND_AUTO_CREATE);
		} 
		//背景图片
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "设定现在的Activity名字："+ActivityHome.class.getName());
		Tools.getApplication(this).setNowActivityName(ActivityHome.class.getName());
		
		UserInfo user = Tools.getApplication(this).getMyInfo();
		if (Tools.getApplication(this).getLogin()
				&& Tools.getApplication(this).getMyInfo().getHeadImg() != null) {
			// System.out.println(Tools.getApplication(this)==null);
			myInfoImagv.setImageBitmap(user.getHeadImg());
			goLoginTv.setVisibility(View.GONE);
		}else{
			myInfoImagv.setImageResource(R.drawable.umeng_socialize_default_avatar);
			goLoginTv.setVisibility(View.VISIBLE);
			
		}
		//设置背景 不是每次都要设置吧？？
		if(imgLoader==null){
			imgLoader=new ImageLoader(this);
		}
		bgUrl=sharedPreferences.getString("bgImg", "theme/theme1.jpg");
		if(bgUrl.startsWith("content")){
			Uri uri=Uri.parse(bgUrl);
			bgImagV.setImageURI(uri);
		}else if(bgUrl.startsWith("theme")){
			bgImagV.setImageBitmap(imgLoader.loadFromAssests(bgUrl)); 
		}else{
			//这里的文件url没有加密 tools 的加载方法可用
			bgImagV.setImageBitmap(Tools.loadBitmapFromSdCard(bgUrl));
		}
		
		
		
	}
	

	// 初始化视图组建
	private void initialView() {
		titles = new String[] { 
				getString(R.string.appLock),
				getString(R.string.digitalFence),
				getString(R.string.friendLocation),
				getString(R.string.setting), 
				getString(R.string.share),
				getString(R.string.exitSystem), // 退出系统
				getString(R.string.theme), 
				getString(R.string.suggest),
				getString(R.string.aboutandhelp) };
		// 测试数据-----------------------------------------------------------------
		// BoxViewPager bgv = new BoxViewPager(this);
		// setContentView(bgv);
		// 测试数据-----------------------------------------------------------------
		List<BoxItem> boxItemList = new ArrayList<BoxItem>();
		for (int i = 0; i < icons.length; i++) {
			if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_PARENT) {
				if (icons[i] == R.drawable.lock_icon) {
					continue;
				}
			}
			BoxItem bi = new BoxItem();
			bi.setBackGroundResId(colors[i]);
			bi.setIconResId(icons[i]);
			String title = titles[i];
			bi.setText(title);
			bi.setOnBoxClick(activitys[i]);
			boxItemList.add(bi);
		} 
		BoxGridView bgv = (BoxGridView) findViewById(R.id.boxGridView1);
		bgv.setInitData(boxItemList);
		// 在这里实现 现在页面数的回调方法
		 bgv.setOnBoxViewChanged(new OnBoxViewChanged() {
			@Override
			public void pagerChanged(int nowPage, int maxPage) {
				LinearLayout circleChangeContainer=(LinearLayout) findViewById(R.id.circleChange);
				for (int i = 0; i < circleChangeContainer.getChildCount(); i++) {
					TextView imgv=(TextView) circleChangeContainer.getChildAt(i);
					if(i!=nowPage){
						imgv.setBackgroundResource(R.drawable.circle_0);
					}else{
						imgv.setBackgroundResource(R.drawable.circle_1);				
					}
				}				
			}
		});
		 goLoginTv=(TextView) findViewById(R.id.toLogin);
		myInfoImagv = (ImageView) findViewById(R.id.myInfo);
		myInfoImagv.setOnClickListener(clickListener);
	}

	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.myInfo:
				Intent intent = new Intent();
				if (Tools.getApplication(getApplicationContext()).getLogin()) {
					intent.setClass(getApplicationContext(),
							UserCenterActivity.class);
				} else {
					intent.setClass(getApplicationContext(),
							LoginActivity.class);
				}
				startActivity(intent);
				break;
			case R.id.boxGridView1:
				Log.d(getClass().toString(), "test");
				break;
			default:
				break;
			}
		}
	};
	
	protected void onDestroy() {
		Log.d(getClass().toString(), "destroy");
		if(Tools.getApplication(this).getLogin()){
		DataBaseHelper dbHelper=new DataBaseHelper(this);
		dbHelper.saveUser(Tools.getApplication(this).getMyInfo());
		}
		if(connectedService){
			unbindService(this);
		}
		super.onDestroy();
	}

 

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		pushService=((LocalService) service).getService();
		connectedService=true;
		//pushService.setHandler(handler);
		if(Tools.getApplication(this).getLogin()){
		pushService.setUserId(Tools.getApplication(this).getMyInfo().getUserId()+"");
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		connectedService=false;		
	};
}
