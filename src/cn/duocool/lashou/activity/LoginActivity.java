package cn.duocool.lashou.activity;

import java.io.File;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.duocool.lashou.R;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.thread.SentLocationThread;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.DownLoadFile;
import cn.duocool.lashou.utils.download.ImageLoader;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;

public class LoginActivity extends BaseActivity implements OnClickListener,
UMAuthListener, NetTranListener {
	private String tag = LoginActivity.class.toString();
	ProgressDialog dialog;
	TitleBar titleBar;
	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.login", RequestType.SOCIAL);
	Button loginButton;
	RelativeLayout shareSinaButton, shareQQbutton;
	CheckBox checkBox;
	EditText pwdEdit,accountEdit;
	private UserInfo user;
	private final int CHECKBIND = 888, CHECKSINABIND = 889,
			GETLOGININFOBYTOKEN = 890,LOGINBYEMAIL=891,LOGINBYPHONE=892;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		checkBox = (CheckBox) findViewById(R.id.checkBoxShowPwd);
		pwdEdit = (EditText) findViewById(R.id.pwdEdit);
		accountEdit=(EditText) findViewById(R.id.editText1);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					pwdEdit.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
				} else {
					pwdEdit.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}
			}
		});

		titleBar.setRightButtonClick(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,
						RegActivity.class);
				startActivity(intent);
			}
		});
		shareSinaButton = (RelativeLayout) findViewById(R.id.shareToSina);
		shareSinaButton.setOnClickListener(this);
		shareQQbutton = (RelativeLayout) findViewById(R.id.shareToQQ);
		shareQQbutton.setOnClickListener(this);
		loginButton = (Button) findViewById(R.id.btnOK);
		loginButton.setOnClickListener(this);
		//mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new QZoneSsoHandler(this));
		// 注销用户的登录状态
		mController.loginout(this, null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("test------", "" + requestCode);
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		Log.d("test------", "" + (ssoHandler == null));
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	@Override
	public void onClick(View v) {

		final NetClient nc = new NetClient();
		nc.setOnNetTranListener(LoginActivity.this);

		switch (v.getId()) {
		case R.id.shareToSina:
			dialog = ProgressDialog.show(LoginActivity.this,
					"提示", "正在连接新浪账号..");
			dialog.setCancelable(true);
			// System.out.println(oau.isAuthenticated(this,SHARE_MEDIA.SINA));
			mController.login(LoginActivity.this, SHARE_MEDIA.SINA,
					new SocializeClientListener() {
				@Override
				public void onStart() {
				}
				@Override
				public void onComplete(int code, SocializeEntity entity) {
					if (code == 200) {
						dialog.setMessage("授权完成,正在跳转");
						// dialog.dismiss();
						mController.getPlatformInfo(LoginActivity.this,
								SHARE_MEDIA.SINA, new UMDataListener() {
							@Override
							public void onStart() {
							}

							// 获得授权信息 如果用户没有绑定，则跳转到绑定界面
							@Override
							public void onComplete(int status,
									Map<String, Object> info) {
								// 相关平台的授权信息都以K-V的形式封装在info中
								if (status == 200
										&& info != null) {
									user = Tools
											.getApplication(
													LoginActivity.this)
													.getMyInfo();
									String sinaUid = info.get(
											"uid").toString();
									String sinaToken = info
											.get("access_token")
											.toString();
									String headSrc = info
											.get("profile_image_url")
											.toString();
									String name = info.get(
											"screen_name")
											.toString();

									user.setSinaUid(sinaUid);
									user.setSinaToken(sinaToken);
									user.setHeadSrc(headSrc);
									user.setUserName(name);
									// 检查是否绑定
									nc.checkExistUID(CHECKBIND,
											user.getSinaUid(),
											"1");

								} else
									Log.d("Log", "发生错误："
											+ status);
								mController
								.loginout(
										getApplicationContext(),
										null);
							}
						});
					} else {
						showConnectError(code);
					}
				}
			});

			break;
		case R.id.shareToQQ:
			dialog = ProgressDialog.show(LoginActivity.this,
					"提示", "正在连接QQ账号..");
			dialog.setCancelable(true);

			mController.login(LoginActivity.this, SHARE_MEDIA.QZONE,
					new SocializeClientListener() {
				@Override
				public void onStart() {

				}

				@Override
				public void onComplete(int errorCode,
						SocializeEntity entity) {
					if (errorCode == 200) {
						dialog.setMessage("正在获取用户信息...");
						// dialog.dismiss();

						mController.getPlatformInfo(LoginActivity.this,
								SHARE_MEDIA.QZONE,
								new UMDataListener() {
							@Override
							public void onStart() {
							}

							@Override
							public void onComplete(int status,
									Map<String, Object> info) {
								// 相关平台的授权信息都以K-V的形式封装在info中
								if (status == 200
										&& info != null) {
									user = Tools
											.getApplication(
													LoginActivity.this)
													.getMyInfo();
									String qqUid = info.get(
											"uid").toString();
									String qqToken = info.get(
											"access_token")
											.toString();
									String headSrc = info
											.get("profile_image_url")
											.toString();
									String name = info.get(
											"screen_name")
											.toString();
									user.setQQUid(qqUid);
									user.setQqToken(qqToken);
									user.setHeadSrc(headSrc);
									user.setUserName(name);
									// 检查是否绑定
									nc.checkExistUID(CHECKBIND,
											user.getQQUid(),
											"2");

									StringBuilder sb = new StringBuilder();
									Set<String> keys = info
											.keySet();

									for (String kStr : keys) {
										sb.append(kStr
												+ "="
												+ info.get(kStr)
												.toString()
												+ "\r\n");
									}
									Log.d("TestData",
											sb.toString());

								} else
									Log.d("TestData", "发生错误："
											+ status);
								mController
								.loginout(
										getApplicationContext(),
										null);
							}
						});
					} else {
						showConnectError(errorCode);
					}
				}
			});
			break;
		case R.id.btnOK:
			if(!checkLoginInfo()){
				return; 
			}
			dialog=ProgressDialog.show(this, null, "正在登录...");
			//			dialog.setMessage("");
			String account=accountEdit.getText().toString();
			user = Tools.getApplication(LoginActivity.this).getMyInfo();
			if(account.contains("@")){
				nc.getUserInfoByEmailPwd(LOGINBYEMAIL,account , pwdEdit.getText().toString());
			}else{
				nc.getUserInfoByPhonePwd(LOGINBYPHONE,account , pwdEdit.getText().toString());
			}


			break;
		}
	}
	/**
	 * 检查输入合法性
	 * @return true 合法
	 */
	private  boolean checkLoginInfo(){
		if(StringUtils.isBlank(accountEdit.getText().toString())){
			Toast.makeText(this, "请输入邮箱或者手机号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(StringUtils.isBlank(pwdEdit.getText().toString())){
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return false;

		}
		return true;

	}
	private void showConnectError(int code) {
		if (code == -104 || code == -102) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					getString(R.string.connectError), Toast.LENGTH_SHORT)
					.show();
		} else {
			dialog.setMessage(getString(R.string.errCode)+code);
			dialog.setCancelable(true);
		}
	}

	@Override
	public void onCancel(SHARE_MEDIA arg0) {
		Log.d("log------------", "onCancel");
	}

	@Override
	public void onComplete(Bundle bundle, SHARE_MEDIA mediaEmu) {
		Log.d("log------------", "onComplete" + mediaEmu.name());
		if (bundle != null && !TextUtils.isEmpty(bundle.getString("uid"))) {
			Toast.makeText(getApplicationContext(), "授权成功!正在登陆...",
					Toast.LENGTH_SHORT).show();
			mController.login(LoginActivity.this, mediaEmu,
					new SocializeClientListener() {

				@Override
				public void onStart() {
					Log.d("axb-----", " 登陆开始started");
				}

				@Override
				public void onComplete(int arg0, SocializeEntity entity) {
					Log.d("axb-----", " onComplete" + arg0);
					Toast.makeText(getApplicationContext(),
							"登陆成功 id=" + entity.getNickName(),
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT)
			.show();
		}
		Set<String> s = bundle.keySet();
		for (String str : s) {
			Log.d("log------------", s + "=" + bundle.getString(str));
		}
	}

	@Override
	public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
		Log.d("log------------", "onError");
	}

	@Override
	public void onStart(SHARE_MEDIA arg0) {
		Log.d("log------------", "onStart");

	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		switch (requestCode) {
		case CHECKBIND:
			if ("OK".equals(data.getResponseStatus())) {
				// 没有绑定 下载头像 并跳入注册界面
				if ("0".equals(data.getCheckResult())) {
					dialog.setMessage("您没有绑定账号,正在为您跳转到注册界面");
					DownLoadFile download = new DownLoadFile(user.getHeadSrc());
					download.startDownLoad(new DownLoadFile.DownloadListener() {
						@Override
						public void onDownloading(int nowSize, int readSize) {
						}

						@Override
						public void onComplete(File file) {
							Bitmap bmp = BitmapFactory.decodeFile(file
									.getPath());
							user.setHeadImg(bmp);
							Intent intent = new Intent(LoginActivity.this,
									RegActivity.class);
							dialog.dismiss();
							startActivity(intent);
							

						}
					});
				} else {
					// 已经绑定 成功  跳转到主界面
					dialog.setMessage("登录成功");
					NetClient loginClient = new NetClient();
					loginClient.setOnNetTranListener(LoginActivity.this);
					if (user.getQQUid() != null) {
						loginClient.getUserInfoByToken(GETLOGININFOBYTOKEN, user.getQQUid(),
								user.getQqToken(), "2");
					}
					if (user.getSinaUid() != null) {
						loginClient.getUserInfoByToken(GETLOGININFOBYTOKEN, user.getSinaUid(),
								user.getSinaToken(), "1");
					}
				}
			} else {
				dialog.setMessage("出错了");
				dialog.setCancelable(true);
			}
			break;
		case LOGINBYPHONE:
		case LOGINBYEMAIL:
		case GETLOGININFOBYTOKEN:
			//获取头像
			if("OK".equals(data.getResponseStatus())){
				Tools.getApplication(getApplicationContext()).setLoginState(true);
				user.setUserId(data.getRegData().getUserId());
				user.setUserName(data.getRegData().getNick());
				user.setEmail(data.getRegData().getEmail());
				user.setTel(data.getRegData().getPhoneNumber());
				user.setQQUid(data.getRegData().getQqUid());
				user.setQqToken(data.getRegData().getQqToken());
				user.setSinaUid(data.getRegData().getSinaUid());
				user.setSinaToken(data.getRegData().getSinaToken());
				ImageLoader imageLoader=new ImageLoader(getApplicationContext());
				NetClient nc=new NetClient();
				String url=nc.getImgUrl(user.getUserId());
				user.setHeadSrc(url);
				if (null == imageLoader.getImage(url)) {
					// 传一个user对象 让下载类去加载
					imageLoader.downloadHead(user);
				} else {
					user.setHeadImg(imageLoader.getImage(url));
				}
				Intent intent=new Intent(this, UserCenterActivity.class);
				startActivity(intent); 
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				this.finish();
			}else{
				Toast.makeText(this, data.getResponseMsg(), Toast.LENGTH_SHORT).show();
				if(dialog.isShowing()){
					dialog.dismiss();
				}
			}
			break;
		default:
			break;
		}
	}
}
