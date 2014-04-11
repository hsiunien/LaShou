package cn.duocool.lashou.activity;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.R.layout;
import cn.duocool.lashou.R.menu;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFriendActivity extends BaseActivity implements OnClickListener,
NetTranListener{

	EditText searchEdit;
	private TitleBar titleBar;
	MyDialog dialog;
	private Button addButton;
	final static int ADDFRIEND=200;
	private Tip tip;
	
	private LashouService lashouService;
	private DaoBase dao;
	
//	@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		super.onRestart();
//		
//		lashouService = Tools.getApplication(this).getLashouService();
//		dao = lashouService.getLockService().getDao();
//		LockDao lockDao = new LockDao(this, dao);
//		LockInfo lockInfo = lockDao.getDefaultLock();
//		
//		String lockType = lockInfo.getPasswordType();
//		if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
//			// 图形锁
//			Intent intent = new Intent(this,ImageLockActivity.class);
//			intent.putExtra("lockName", lockInfo.getLockName());
//			intent.putExtra("password", lockInfo.getPassword());
//			intent.putExtra("question", lockInfo.getQuestion());
//			intent.putExtra("answer", lockInfo.getAnswer());
//			
//			intent.putExtra("isBackClose", "close");
//			ImageLockActivity.closeActivity  = this;
//			
//			startActivity(intent);
////			this.finish();
////			return;
//		} else {
//			// PIN锁
//			Intent intent = new Intent(this,PasswordActivity.class);
//			intent.putExtra("lockName", lockInfo.getLockName());
//			intent.putExtra("password", lockInfo.getPassword());
//			intent.putExtra("question", lockInfo.getQuestion());
//			intent.putExtra("answer", lockInfo.getAnswer());
//			
//			intent.putExtra("isBackClose", "close");
//			PasswordActivity.closeActivity  = this;
//			
//			startActivity(intent);
////			this.finish();
////			return;
//		}
//	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		
		lashouService = Tools.getApplication(this).getLashouService();
		dao = lashouService.getLockService().getDao();
		LockDao lockDao = new LockDao(this, dao);
		LockInfo lockInfo = lockDao.getDefaultLock();
		
		String lockType = lockInfo.getPasswordType();
		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_CHILD) {
			if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
				// 图形锁
				Intent intent = new Intent(this,ImageLockActivity.class);
				intent.putExtra("lockName", lockInfo.getLockName());
				intent.putExtra("password", lockInfo.getPassword());
				intent.putExtra("question", lockInfo.getQuestion());
				intent.putExtra("answer", lockInfo.getAnswer());
				
				intent.putExtra("isBackClose", "close");
				ImageLockActivity.closeActivity  = this;
				
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
				
				intent.putExtra("isBackClose", "close");
				PasswordActivity.closeActivity  = this;
				
				startActivity(intent);
//				this.finish();
//				return;
			}
		}
		
		searchEdit=(EditText) findViewById(R.id.searchEdit);
		titleBar=(TitleBar) findViewById(R.id.titleBar);
		titleBar.setRightButtonClick(this);
		tip=new Tip(this);
	}
 

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_btnRight:
			addButton=(Button) v;
			NetClient nc=new NetClient();
			String str=searchEdit.getText().toString();
			UserInfo myInfo=Tools.getApplication(this).getMyInfo();
			if(str.contains("@")&&!StringUtils.isEmail(str)){
				Toast.makeText(AddFriendActivity.this, "邮箱格式不正确", Toast.LENGTH_LONG).show();
				return ;
			}else if(StringUtils.isEmail(str)){
				nc.requestAddFriendByEmail(ADDFRIEND, myInfo.getUserId()+"", str);
			}else{
				nc.requestAddFriendByPhone(ADDFRIEND, myInfo.getUserId()+"", str);
			}
			v.setClickable(false);
			nc.setOnNetTranListener(this);	
			tip.setContent("正在查找家人");
			tip.show();
			break;
		case R.id.button1:
			if (dialog != null) {
				dialog.close();
			}
			break;
		case R.id.button2:
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		switch (requestCode) {
		case ADDFRIEND:
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
				tip.dismiss();
				dialog=new MyDialog(this);
				dialog.setTitle("添加家人");
				UserData userData=data.getPushData().getToUser();
				dialog.setContent("已经向 "+userData.getNick()+"发送了请求");
				dialog.setButton1("我知道了", this);
				dialog.show();
			}else{
				tip.dismiss();
				dialog=new MyDialog(this);
				dialog.setTitle("添加家人");
				String msg=data.getResponseMsg();
				dialog.setContent("错误:"+msg);
				dialog.setButton1("我知道了", this);
				dialog.show();
			}
			addButton.setClickable(true);
			break;

		default:
			break;
		}		
	}

}
