package cn.duocool.lashou.activity;

import java.io.File;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.duocool.lashou.R;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UploadData;
import cn.duocool.lashou.thread.SentLocationThread;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;

public class UserCenterActivity extends Activity implements OnClickListener ,NetTranListener{
	TitleBar titleBar;
	UserInfo mUser;
	MyDialog dialog;
	Tip tip;
	ImageView headImg;
	UMSocialService mController=UMServiceFactory.getUMSocialService("com.umeng.login", RequestType.SOCIAL);
	
	private final int REGCODE=888,CHOICEPICREQUEST=998,CROPREQUEST=99802,
			UPLOADPIC=666,CHECKEMAILEXIST=667,CHECKPHONEEXIST=668,UPDATEUSERINFO=669;	
	TextView userNameTv,emailTv,phoneTv,sinaTv,qqTv;
	View setHead,setNicName,setpassword,setPhone,setEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
		mUser=Tools.getApplication(this).getMyInfo();
		titleBar = (TitleBar) findViewById(R.id.titleBar);
		titleBar.setRightButtonClick(this);
		dialog=new MyDialog(this);
		userNameTv=(TextView) findViewById(R.id.userName);
		emailTv=(TextView) findViewById(R.id.email);
		phoneTv=(TextView) findViewById(R.id.mobile);
		qqTv=(TextView) findViewById(R.id.qqUid);
		sinaTv=(TextView) findViewById(R.id.sinaUid);		
		findViewById(R.id.setHead).setOnClickListener(this);//设置头像
		findViewById(R.id.setNicName).setOnClickListener(this);//设置昵称
		findViewById(R.id.setpassword).setOnClickListener(this);//设置密码
		findViewById(R.id.setEmail).setOnClickListener(this);//设置邮箱
		findViewById(R.id.setPhone).setOnClickListener(this);//设置手机
		findViewById(R.id.shareToSina).setOnClickListener(this);//设置新浪
		findViewById(R.id.shareToQQ).setOnClickListener(this);//设置QQ
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().setSsoHandler(new QZoneSsoHandler(this));
	}
	private boolean notBind(String str){
	return 	StringUtils.isBlank(str)||str.equals("-");
 	}
	@Override
	protected void onResume() {
		super.onResume();
		//设置个人信息
		userNameTv.setText(mUser.getUserName());
		emailTv.setText(mUser.getEmail());
		phoneTv.setText(mUser.getTel());
		qqTv.setText(notBind(mUser.getQQUid())?"未绑定":"已绑定");
		sinaTv.setText(notBind(mUser.getSinaUid())?"未绑定":"已绑定");
		headImg=(ImageView) findViewById(R.id.myHead);
		if(mUser.getHeadImg()!=null){
			headImg.setImageBitmap(mUser.getHeadImg());
		}		
		
	}
	@Override
	public void onClick(View v) {
		 
		Intent i;
		switch (v.getId()) {
		//titlebar上的注销按钮
		case R.id.title_btnRight:
			dialog.setButton1("确定", this);
			dialog.setTitle("提示");
			dialog.setContent("您确定注销账号吗?");
			dialog.setButton2("取消", this);
			dialog.show();
			break;
			//	确定注销
		case R.id.button1:
			//用户注销，关闭发送用户位置的线程
			//SentLocationThread.sentLocationThread_is_exit = true;
			
			Tools.getApplication(this).logout();
			dialog.close();
			this.finish();
			break;
		case R.id.button2:
			dialog.close();
			break;			
		case R.id.setHead:
			  i = new Intent();
			i.setAction(Intent.ACTION_GET_CONTENT);
			i.setType("image/*"); // 资源的MIME类型 image/jpeg
			startActivityForResult(i, CHOICEPICREQUEST);
			break;
		case R.id.setNicName:
			i=new Intent(this,UpdateUserInfoActivity.class);
			i.putExtra("type", UpdateUserInfoActivity.CHANGENICK);
			startActivity(i);
			break;		
		case R.id.setEmail:			

			break;
		case R.id.setpassword:
			i=new Intent(this,UpdateUserInfoActivity.class);
			i.putExtra("type", UpdateUserInfoActivity.CHANGEPWD);
			startActivity(i);
			break;	
		case R.id.setPhone:
			i=new Intent(this,UpdateUserInfoActivity.class);
			i.putExtra("type", UpdateUserInfoActivity.CHANGEPHONE);
			startActivity(i);
			break;
		case R.id.shareToQQ:
			if(!notBind(mUser.getQQUid())){
				dialog=new MyDialog(this);
				dialog.setTitle("提示");
				dialog.setContent("确定取消绑定吗?");
				dialog.setButton1("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
						mUser.setQQUid("-");
						mUser.setQqToken("-");
						NetClient nc=new NetClient();
						nc.setOnNetTranListener(UserCenterActivity.this);
						nc.updateUserInfo(UPDATEUSERINFO,mUser.toRegData());
						mController.deleteOauth(getApplicationContext(), SHARE_MEDIA.QZONE, null);
					}
				});
				dialog.setButton2("取消", null);
				dialog.show();
			}else{
				 mController.login(UserCenterActivity.this, SHARE_MEDIA.QZONE,new SocializeClientListener() {
						@Override
						public void onStart() {
						}
						
						@Override
						public void onComplete(int status, SocializeEntity arg1) {
							if(status==200){
							mController.getPlatformInfo(UserCenterActivity.this,
									SHARE_MEDIA.QZONE, new UMDataListener() {
								@Override
								public void onStart() {
								}

								// 获得授权信息  并且更新token
								
								@Override
								public void onComplete(int status,
										Map<String, Object> info) {
									// 相关平台的授权信息都以K-V的形式封装在info中
									String uid = info.get(
											"uid").toString();
									String token = info
											.get("access_token")
											.toString();
									mUser.setQQUid(uid);
									mUser.setQqToken(token);
									NetClient nc=new NetClient();
									nc.setOnNetTranListener(UserCenterActivity.this);
									nc.updateUserInfo(UPDATEUSERINFO,mUser.toRegData());
									}
							});
							}
						}
					});
			}
			break;
		case R.id.shareToSina:
			if(!notBind(mUser.getSinaUid())){
				dialog=new MyDialog(this);
				dialog.setTitle("提示");
				dialog.setContent("确定取消绑定吗?");
				dialog.setButton1("确定", new OnClickListener() {
					@Override
					public void onClick(View v) {
					    mController.deleteOauth(getApplicationContext(), SHARE_MEDIA.SINA, null);
						mUser.setSinaUid("-");
						mUser.setSinaToken("-");
						NetClient nc=new NetClient();
						nc.setOnNetTranListener(UserCenterActivity.this);
						nc.updateUserInfo(UPDATEUSERINFO,mUser.toRegData());
					}
				});
				dialog.setButton2("取消", null);
				dialog.show();
			}else{
				 mController.login(UserCenterActivity.this, SHARE_MEDIA.SINA,new SocializeClientListener() {
					@Override
					public void onStart() {
					}
					
					@Override
					public void onComplete(int status, SocializeEntity arg1) {
						if(status==200){
						mController.getPlatformInfo(UserCenterActivity.this,
								SHARE_MEDIA.SINA, new UMDataListener() {
							@Override
							public void onStart() {
							}

							// 获得授权信息  并且更新token
							
							@Override
							public void onComplete(int status,
									Map<String, Object> info) {
								// 相关平台的授权信息都以K-V的形式封装在info中
								String sinaUid = info.get(
										"uid").toString();
								String sinaToken = info
										.get("access_token")
										.toString();
								mUser.setSinaUid(sinaUid);
								mUser.setSinaToken(sinaToken);
								NetClient nc=new NetClient();
								nc.setOnNetTranListener(UserCenterActivity.this);
								nc.updateUserInfo(UPDATEUSERINFO,mUser.toRegData());
								}
						});
						}
					}
				});
			}
			break;
		default:

			break;
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOICEPICREQUEST) { // 选择图片得到的响应
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Log.i("AAA", uri.toString());
				// 启动缩放工具Activity(URI 显示出来)
				Intent is = new Intent();
				is.setAction("com.android.camera.action.CROP");
				is.setDataAndType(uri, "image/*");
				is.putExtra("crop", "true");
				is.putExtra("aspectX", 1);
				is.putExtra("aspectY", 1);
				is.putExtra("outputX", 128);
				is.putExtra("outputY", 128);
				is.putExtra("return-data", true);
				startActivityForResult(is, CROPREQUEST);
			}
		}
		//剪裁结束后执行
		if (requestCode == CROPREQUEST) {
			if (resultCode == RESULT_OK) {
			// 取的方式：
				Bitmap bm = data.getParcelableExtra("data");
				headImg.setImageBitmap(bm);
				mUser.setHeadImg(bm);
			//	mUser.setHeadSrc(NetClient.getImgUrl(mUser.getUserId()));
				//保存图片
			//	Tools.bitm2png(bm,  Iader.);
				File f = new File(Environment.getExternalStorageDirectory()
							+ File.separator + ImageLoader.downloadSrc 
							+ ImageLoader.getFileName(NetClient.getImgUrl(mUser.getUserId())));
				if(f!=null){
					f.delete();
				}
				Tools.bitm2png(bm,ImageLoader.downloadSrc+ ImageLoader.getFileName(NetClient.getImgUrl(mUser.getUserId())));
				UploadData uploadData=new UploadData();
				
				uploadData.setUserId(mUser.getUserId());
				uploadData.setBitmap(mUser.getHeadImg());
			    tip=new  Tip(this);
				tip.setContent("正在上传头像");
				tip.show();
				NetClient nc = new NetClient();
				nc.uploadHeadIcon(UPLOADPIC, uploadData);
				nc.setOnNetTranListener(this);	 
			}

		}
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		switch (requestCode) {
	case UPLOADPIC:
		if(StringUtils.equleIgnoreCase("ok", data.getResponseMsg())||StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
			tip.dismiss();
			Toast.makeText(getApplicationContext(), "头像修改成功", Toast.LENGTH_SHORT).show();
		}else{
			tip.dismiss();
			Toast.makeText(getApplicationContext(), "头像修改失败", Toast.LENGTH_SHORT).show();
		}
		break;
	case UPDATEUSERINFO:
		dialog.close();
		if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
			qqTv.setText(notBind(mUser.getQQUid())?"未绑定":"已绑定");
			sinaTv.setText(notBind(mUser.getSinaUid())?"未绑定":"已绑定");
		}
		break;
	}
 }
}
