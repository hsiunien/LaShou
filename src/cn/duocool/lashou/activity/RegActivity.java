package cn.duocool.lashou.activity;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.NetTranProgressListener;
import cn.duocool.lashou.net.client.RegData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UploadData;

public class RegActivity extends BaseActivity implements OnClickListener,
		NetTranListener,NetTranProgressListener {

	private ImageView headImg;
	private EditText et_email, et_phone, et_pwd, et_name;
	private UserInfo mUserInfo;
	private final int REGCODE=888,CHOICEPICREQUEST=998,CROPREQUEST=99802,
			UPLOADPIC=666,CHECKEMAILEXIST=667,CHECKPHONEEXIST=668;
	private CheckBox cb_pwd;
	private ProgressDialog   dialog;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);
		//DatabaseHelper db = new DatabaseHelper(this);
		headImg = (ImageView) findViewById(R.id.imgv_head);
		headImg.setOnClickListener(this);
		et_email = (EditText) findViewById(R.id.et_email);
		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		cb_pwd=(CheckBox) findViewById(R.id.cb_showPwd);
		cb_pwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					et_pwd.setInputType(InputType.TYPE_CLASS_TEXT);
				} else {
					et_pwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}
			}
		});
		//若是 用社交账号注册 需要填写相关信息 
		mUserInfo=Tools.getApplication(this).getMyInfo();
		if(null!=mUserInfo.getHeadImg()){
			headImg.setImageBitmap(Tools.getApplication(this).getMyInfo().getHeadImg());
		}
		if(null!=mUserInfo.getUserName()){
			et_name.setText(Tools.getApplication(this).getMyInfo().getUserName());
		}
		dialog= new ProgressDialog(this);
		dialog.setMessage("注册中...");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				mUserInfo.setHeadImg(bm);
				//保存图片
				Tools.bitm2png(bm, CommDef.headImg);
				 
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_head:
			// 选择并剪裁头像
			// 启动图库 是一个应用程序的activity
			// Intnet 启动的方式：
			// 1 显式的启动 明确告诉OS 我启动的是哪个类（包名+类名）
			// 2 隐式的启动 ：不是明确的。暧昧的告诉OS，让OS猜。
			// Aciton：Intent.ACTION_GET_CONTENT // 可以是MP3 BMP PNG 。。。
			// 启动图库
			Intent i = new Intent();
			i.setAction(Intent.ACTION_GET_CONTENT);
			i.setType("image/*"); // 资源的MIME类型 image/jpeg
			startActivityForResult(i, CHOICEPICREQUEST);

			break;
		case R.id.btnLogin:
 
			// 检查注册信息合法
			if(!checkInfo()){
				return;
			}
			NetClient nc = new NetClient();
			nc.checkExistEmail(CHECKEMAILEXIST, et_email.getText().toString());
			nc.setOnNetTranListener(this);
			dialog.show();
			break;
		default:
			break;
		}
	}

	private boolean checkInfo() {

		if(StringUtils.isEmpty(et_email.getText().toString())&&StringUtils.isEmpty(et_phone.getText().toString())){
			  Toast.makeText(this, "手机号码或者邮箱号至少填写一项 ", Toast.LENGTH_SHORT).show();
				return false;	
		}
		if(!StringUtils.isEmpty(et_email.getText().toString())&&!Tools.isEmail(et_email.getText().toString())){
			  Toast.makeText(this, "邮箱 格式不正确 ", Toast.LENGTH_SHORT).show();
				return false;	
		}

		if(!StringUtils.isEmpty(et_phone.getText().toString())&&!StringUtils.isMobileNO(et_phone.getText().toString())){
			  Toast.makeText(this, "手机号码不正确 ", Toast.LENGTH_SHORT).show();
				return false;	
		}
		if(et_name.getText().toString()==null||"".equals(et_name.getText().toString())){
		  Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
		return false;
		}
		if(mUserInfo.getHeadImg()==null){
			  Toast.makeText(this, "请选择您的头像 ", Toast.LENGTH_SHORT).show();
				return false;	
		}
		if(StringUtils.isEmpty(et_pwd.getText().toString())){
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		switch (requestCode) {
		case REGCODE:
			if("ok".equals(StringUtils.toLowerCase(data.getResponseStatus()))){
				//Tools.getApplication(this).setLoginState(true);
				mUserInfo.setUserId(Integer.valueOf(data.getUserId()));
				UploadData uploadData=new UploadData();
				uploadData.setUserId(mUserInfo.getUserId());
				uploadData.setBitmap(mUserInfo.getHeadImg());
				dialog.setMessage("正在上传头像");
				NetClient nc = new NetClient();
				nc.uploadHeadIcon(UPLOADPIC, uploadData);
				nc.setOnNetTranListener(this);	 
 			}else{
				Toast.makeText(this, "888:"+data.getResponseMsg()+" " + data.getUserId(), Toast.LENGTH_SHORT).show();
			}			
			break;
			//检查邮件是否被注册
		case CHECKEMAILEXIST:
			if("ok".equals(StringUtils.toLowerCase(data.getResponseStatus()))){
				Log.d("CheckMAILE", data.getCheckResult());
				if(StringUtils.equleIgnoreCase("1",data.getCheckResult())){
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "该邮箱已经注册", Toast.LENGTH_SHORT).show();
				}else{
					NetClient nc = new NetClient();
					nc.checkExistPhone(CHECKPHONEEXIST, et_phone.getText().toString());
					nc.setOnNetTranListener(this);		
				}
			}else{
			}
			break;
			//检查电话是否被注册
		case CHECKPHONEEXIST:
			if("ok".equals(StringUtils.toLowerCase(data.getResponseStatus()))){
				Log.d("CheckPHONE", data.getCheckResult());
				if(StringUtils.equleIgnoreCase("1",data.getCheckResult())){
					Toast.makeText(getApplicationContext(), "该号码已经注册", Toast.LENGTH_SHORT).show();
					if(dialog.isShowing()){
						dialog.dismiss();
					}
				}else{
					NetClient nc = new NetClient();
					RegData regData = new RegData();
					regData.setUserId(0); // 不用的字段必须填写，int 用 0 字符串用 "" float 用0.0f
					regData.setNick(et_name.getText().toString());
					regData.setEmail(et_email.getText().toString());
					regData.setHeadIconPath("e:\\wwx.ex");
					regData.setPassword(et_pwd.getText().toString());
					regData.setPhoneNumber(et_phone.getText().toString());
					regData.setQqUid(mUserInfo.getQQUid());
					regData.setQqToken(mUserInfo.getQqToken());
					regData.setSinaUid(mUserInfo.getSinaUid());
					regData.setSinaToken(mUserInfo.getSinaToken());
					nc.userRegister(REGCODE, regData);
					nc.setOnNetTranListener(this);		
				}
			}
			break;
		case UPLOADPIC:
			if(StringUtils.equleIgnoreCase("ok", data.getResponseMsg())||StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				Toast.makeText(getApplicationContext(), "注册成功，请登录", Toast.LENGTH_SHORT).show();
				this.finish();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onTransmitting(int requestCode, long nowProgress,
			long maxProgress) {
		switch (requestCode) {
		case UPLOADPIC:
			Log.d("tag", nowProgress+"/"+maxProgress);
			break;

		default:
			break;
		}
	}

}
