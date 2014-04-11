package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.R.layout;
import cn.duocool.lashou.R.menu;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateUserInfoActivity extends BaseActivity implements NetTranListener{
	EditText editText;
	public final static int CHANGENICK=1,CHANGEPWD=2,CHANGEPHONE=3;
	private int type;
	private  TitleBar titleBar;
	private UserInfo mUser;
	private Tip tip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_user_info);
		titleBar=(TitleBar) findViewById(R.id.titleBar);
		titleBar.setRightButtonClick(saveOnclick);
		type=getIntent().getExtras().getInt("type");
		showEdit(type);
		mUser=Tools.getApplication(this).getMyInfo();
	}
	private void showEdit(int type){
		switch (type) {
		case CHANGENICK:
			editText=(EditText) findViewById(R.id.changeNickName);
			break;
		case CHANGEPWD:
			editText=(EditText) findViewById(R.id.changePwd);
			break;
		case CHANGEPHONE:
			editText=(EditText) findViewById(R.id.changePhone);
				break;
		default:
			editText=(EditText) findViewById(R.id.changeNickName);			break;
		}
		editText.setVisibility(View.VISIBLE);
	}
	 
	private OnClickListener saveOnclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//监测是否合法
			if(!checkInfo()){
				return;
			}
			if(null==tip){
				tip=new Tip(UpdateUserInfoActivity.this);
				
			}
			tip.show();
			NetClient client=new NetClient(); 
			switch (type) {
			case CHANGENICK:
				mUser.setUserName(editText.getText().toString());
				client.updateUserInfo(type, mUser.toRegData());
				client.setOnNetTranListener(UpdateUserInfoActivity.this);
				break;
			case CHANGEPWD:
				mUser.setPassword(editText.getText().toString());
				client.updateUserInfo(type, mUser.toRegData());
				client.setOnNetTranListener(UpdateUserInfoActivity.this);
				break;
			case CHANGEPHONE:
				client.checkExistPhone(777, editText.getText().toString());
				client.setOnNetTranListener(UpdateUserInfoActivity.this);
				break;
			}
		}
	};
	
	private boolean checkInfo() {
		switch (type) {
		case CHANGENICK:
			if(editText.getText().toString()==null||"".equals(editText.getText().toString())){
				  Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
				return false;
				}
			break;
		case CHANGEPWD:
			if(StringUtils.isEmpty(editText.getText().toString())){
				Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
				return false;
			}
			break;
		case CHANGEPHONE:
			if(!StringUtils.isMobileNO(editText.getText().toString())){
				  Toast.makeText(this, "手机号码不正确 ", Toast.LENGTH_SHORT).show();
					return false;	
			}
			break;
		}
		return true;
	}	
	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		String msg="";
		switch (requestCode) {
		case CHANGENICK:
			msg="修改昵称成功";
			break;
		case CHANGEPWD:
			msg="修密码成功";
			break;
		case CHANGEPHONE:
			msg="修改手机号码成功";
			break;
		case 777:
			msg="该手机号已存在";
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())&&StringUtils.equleIgnoreCase("0",data.getCheckResult())){
			mUser.setTel(editText.getText().toString());
			NetClient nc=new NetClient();
			nc.updateUserInfo(CHANGEPHONE, mUser.toRegData());
			nc.setOnNetTranListener(UpdateUserInfoActivity.this);
			}else{
				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				tip.dismiss();
			}
			return;//不需要继续执行
		}
		tip.dismiss();
		if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus()) ){
			Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

}
