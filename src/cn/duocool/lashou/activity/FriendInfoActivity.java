package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.mywidget.WiperSwitch.OnChangedListener;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.RelationData;
import cn.duocool.lashou.net.client.LocationData;
 
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;

public class FriendInfoActivity extends BaseActivity implements OnClickListener, NetTranListener, LockCallBack {
	TitleBar titleBar;
	ImageView headView;
	TextView nameTv,switchTv;
	private UserInfo myInfo;
	private WiperSwitch wiperSwitch_enter;//位置授权开关 
//	private int accessFlag=0;//授权状态
	RelationData userData;//当前页面用户数据
	private ImageLoader loader;
//	private  ProgressDialog progressDialog = null;
	
	private List<ProgressDialog> progressDialogList = new ArrayList<ProgressDialog>();
	//查看位置
	private RelativeLayout ckwz;
//	private MyDialog  dialog;
//	private Tip tip;
	//查看足迹
	private RelativeLayout ckzj;
	
	// 0 不能删 1 可以删
	private static int enableDel = 0;
	
	private LashouService lashouService;
	private DaoBase dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_info);
		wiperSwitch_enter = (WiperSwitch) findViewById(R.id.showSwitch);
		titleBar=(TitleBar) findViewById(R.id.titleBar);
		switchTv=(TextView) findViewById(R.id.switchstatus);
		nameTv=(TextView) findViewById(R.id.nameTv);
		headView=(ImageView) findViewById(R.id.headImg);
		loader=new ImageLoader(this);
		userData=(RelationData) getIntent().getSerializableExtra("user");
		myInfo=Tools.getApplication(this).getMyInfo();
		nameTv.setText(userData.getFriendData().getNick());
//		tip=new Tip(this);
//		tip.setTitle("提示");
//		tip.show();
		synchronized (progressDialogList) {
			ProgressDialog progressDialog = ProgressDialog.show(FriendInfoActivity.this,
					"请稍等...", "正在获取信息...", true, false);
			progressDialogList.add(progressDialog);
		}
		
		 //获得好友关系
		final NetClient nc=new NetClient();
		Log.d("test", userData.getFriendData().getUserId()+"");
		Log.d("test2", Tools.getApplication(this).getMyInfo().getUserId()+"");
		nc.getRelation(666, userData.getFriendData().getUserId()+"", Tools.getApplication(this).getMyInfo().getUserId()+"");
		nc.setOnNetTranListener(this);
		wiperSwitch_enter.setImage(R.drawable.off, R.drawable.on);
		headView.setImageBitmap(loader.getImage(NetClient.getImgUrl(userData.getFriendId())));
		
		wiperSwitch_enter.setOnChangedListener(new OnChangedListener() {			
				@Override
				public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
					lashouService = Tools.getApplication(FriendInfoActivity.this).getLashouService();
					dao = lashouService.getLockService().getDao();
					LockDao lockDao = new LockDao(FriendInfoActivity.this, dao);
					LockInfo lockInfo = lockDao.getDefaultLock();
					
					String lockType = lockInfo.getPasswordType();
					if (Tools.getApplication(FriendInfoActivity.this).getLashouEdition() == CommDef.EDITION_CHILD) {
						if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
							// 图形锁
							Intent intent = new Intent(FriendInfoActivity.this,ImageLockActivity.class);
							intent.putExtra("lockName", lockInfo.getLockName());
							intent.putExtra("password", lockInfo.getPassword());
							intent.putExtra("question", lockInfo.getQuestion());
							intent.putExtra("answer", lockInfo.getAnswer());
							
							ImageLockActivity.lockCallBack = FriendInfoActivity.this;
							ImageLockActivity.setFlag = checkState;
							
							startActivity(intent);
//							this.finish();
//							return;
						} else {
							// PIN锁
							Intent intent = new Intent(FriendInfoActivity.this,PasswordActivity.class);
							intent.putExtra("lockName", lockInfo.getLockName());
							intent.putExtra("password", lockInfo.getPassword());
							intent.putExtra("question", lockInfo.getQuestion());
							intent.putExtra("answer", lockInfo.getAnswer());
							

							ImageLockActivity.lockCallBack = FriendInfoActivity.this;
							ImageLockActivity.setFlag = checkState;
							
							startActivity(intent);
//							this.finish();
//							return;
						}
					} else {
						//默认为false 波动一次就为true；
						if(checkState){
							switchTv.setText(getResources().getString(R.string.havePermission_toShow));
							nc.changViewRole(789, userData.getFriendData().getUserId()+"", myInfo.getUserId()+"", 3);
						}else{
							switchTv.setText(getResources().getString(R.string.noPermission_toShow));
							nc.changViewRole(789, userData.getFriendData().getUserId()+"", myInfo.getUserId()+"", 1);
						}
					}
					//tip.show(); 
				} 
			});
		
		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_PARENT) {
			 //删除按钮点击事件
			 titleBar.setRightButtonClick(new OnClickListener() {
				@Override
				public void onClick(View v) {
						final MyDialog dialog=new MyDialog(FriendInfoActivity.this);
						dialog.setButton1("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {
								//确定删除
								NetClient nc=new NetClient();
								nc.delRelation(444,
										Tools.getApplication(FriendInfoActivity.this)
												.getMyInfo().getUserId()+"", userData
												.getFriendData().getUserId()+"");
								nc.setOnNetTranListener(FriendInfoActivity.this);
								dialog.close();
							}
						});
						dialog.setButton2("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.close();
							}
						});
						dialog.setTitle("提示");
						dialog.setContent("您确定删除该家人吗?");
						dialog.show();
				}
			});
		} else {
			 //删除按钮点击事件
			 titleBar.setRightButtonClick(new OnClickListener() {
				@Override
				public void onClick(View v) {
						final MyDialog dialog=new MyDialog(FriendInfoActivity.this);
						dialog.setButton1("确定", new OnClickListener() {
							@Override
							public void onClick(View v) {
								
									//确定删除
									NetClient nc=new NetClient();
									nc.delRelation(444,
											Tools.getApplication(FriendInfoActivity.this)
													.getMyInfo().getUserId()+"", userData
													.getFriendData().getUserId()+"");
									nc.setOnNetTranListener(FriendInfoActivity.this);
									dialog.close();
							}
						});
						dialog.setButton2("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.close();
							}
						});
						dialog.setTitle("提示");
						dialog.setContent("您确定删除该家人吗?");
						dialog.show();
						
						lashouService = Tools.getApplication(FriendInfoActivity.this).getLashouService();
						dao = lashouService.getLockService().getDao();
						LockDao lockDao = new LockDao(FriendInfoActivity.this, dao);
						LockInfo lockInfo = lockDao.getDefaultLock();
						
						String lockType = lockInfo.getPasswordType();
						if (Tools.getApplication(FriendInfoActivity.this).getLashouEdition() == CommDef.EDITION_CHILD) {
							if (CommDef.LOCK_MODE_IMAGE.equals(lockType)) {
								// 图形锁
								Intent intent = new Intent(FriendInfoActivity.this,ImageLockActivity.class);
								intent.putExtra("lockName", lockInfo.getLockName());
								intent.putExtra("password", lockInfo.getPassword());
								intent.putExtra("question", lockInfo.getQuestion());
								intent.putExtra("answer", lockInfo.getAnswer());
								
								intent.putExtra("isBackCloseDialog", "close");
								ImageLockActivity.closeDialog  = dialog;
								
								startActivity(intent);
//								this.finish();
//								return;
							} else {
								// PIN锁
								Intent intent = new Intent(FriendInfoActivity.this,PasswordActivity.class);
								intent.putExtra("lockName", lockInfo.getLockName());
								intent.putExtra("password", lockInfo.getPassword());
								intent.putExtra("question", lockInfo.getQuestion());
								intent.putExtra("answer", lockInfo.getAnswer());
								

								intent.putExtra("isBackCloseDialog", "close");
								PasswordActivity.closeDialog  = dialog;
								
								startActivity(intent);
//								this.finish();
//								return;
							}
						}
				}
			});
		}
		

		// 查看位置点击事件
		ckwz = (RelativeLayout) findViewById(R.id.ckwz);
		ckwz.setOnClickListener(this);
		
		//查看足迹点击事件
		ckzj = (RelativeLayout) findViewById(R.id.ckzj);
		ckzj.setOnClickListener(this);
		
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		//查看位置请求
		if (requestCode == 888) {
			// 关闭窗口
			synchronized (progressDialogList) {
				if (null != progressDialogList && progressDialogList.size() > 0) {
					for (ProgressDialog progressDialogTemp : progressDialogList) {
						progressDialogTemp.dismiss();
						progressDialogTemp = null;
					}
					progressDialogList.clear();
				}
			}
			
			if(data.getResponseStatus().equals("OK") && null != data.getLocationDataList())
			{
				if(data.getLocationDataList().size()!=0 && null != data.getLocationDataList().get(0))
				{
					LocationData location=data.getLocationDataList().get(0);
					Intent intent = new Intent();
					intent.setClass(FriendInfoActivity.this, ShowMapActivity.class);
					intent.putExtra("titlebarString", userData.getFriendData().getNick()+"位置");
					intent.putExtra("Latitude", location.getLatitude());
					intent.putExtra("Longitude", location.getLongitude());
					intent.putExtra("address", location.getAddress());
					intent.putExtra("r", 0);
					FriendInfoActivity.this.startActivity(intent);	
				}else{
					//
					Toast.makeText(FriendInfoActivity.this, "暂时没有该家人位置，请稍后再试！", Toast.LENGTH_SHORT).show();
					return;
				}
			}else{
				Toast.makeText(FriendInfoActivity.this, "暂时没有该家人位置信息，请稍后再试！", Toast.LENGTH_SHORT).show();
				return;
			}

		}// 888 好友位置请求
		
		// 查看足迹请求
		if (requestCode == 777555) {
					// 关闭窗口
					synchronized (progressDialogList) {
						if (null != progressDialogList && progressDialogList.size() > 0) {
							for (ProgressDialog progressDialogTemp : progressDialogList) {
								progressDialogTemp.dismiss();
								progressDialogTemp = null;
							}
							progressDialogList.clear();
						}
					}
					
					if(data.getResponseStatus().equals("OK") && null != data.getLocationDataList())
					{
						if(data.getLocationDataList().size()!=0 && null != data.getLocationDataList().get(0))
						{
							// 将该用户ID发送给显示足迹的页面处理
							Intent intent = new Intent();
							intent.setClass(FriendInfoActivity.this,
									ShowTrackActivity.class);
							intent.putExtra("userID", userData.getFriendId());
							FriendInfoActivity.this.startActivity(intent);
						}else{
							//
							Toast.makeText(FriendInfoActivity.this, "暂时没有该家人足迹信息，请稍后再试！", Toast.LENGTH_SHORT).show();
							return;
						}
					}else{
						Toast.makeText(FriendInfoActivity.this, "暂时没有该家人足迹信息，请稍后再试！", Toast.LENGTH_SHORT).show();
						return;
					}

				}// 777555 查看足迹请求
		//删除好友
		if(requestCode == 444){
//			tip.dismiss();
//			tip = null;
			synchronized (progressDialogList) {
				if (null != progressDialogList && progressDialogList.size() > 0) {
					for (ProgressDialog progressDialogTemp : progressDialogList) {
						progressDialogTemp.dismiss();
						progressDialogTemp = null;
					}
					progressDialogList.clear();
				}
			}
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
				Toast.makeText(this, "删除家人成功", Toast.LENGTH_SHORT).show();
				setResult(777);
				finish();
			}else{
				Toast.makeText(this, "删除家人失败", Toast.LENGTH_SHORT).show();
			}
			//dialog.close();
		}
		if(requestCode==666){
			synchronized (progressDialogList) {
				if (null != progressDialogList && progressDialogList.size() > 0) {
					for (ProgressDialog progressDialogTemp : progressDialogList) {
						progressDialogTemp.dismiss();
						progressDialogTemp = null;
					}
					progressDialogList.clear();
				}
			}
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
			if(null==data.getRelationData().getAuth()||data.getRelationData().getAuth()==1){
//				 wiperSwitch_enter.setImage(R.drawable.off, R.drawable.on);
				 wiperSwitch_enter.setisLock(false);
				 switchTv.setText(getResources().getString(R.string.noPermission_toShow));
			}else if(null!=data.getRelationData().getAuth()&&data.getRelationData().getAuth()==3){
//				 wiperSwitch_enter.setImage(R.drawable.on, R.drawable.off);
				 switchTv.setText(getResources().getString(R.string.havePermission_toShow));
				 wiperSwitch_enter.setisLock(true);

			}
			
//			 accessFlag=data.getRelationData().getAuth();	
			}
		}
		//查看足迹
		if (requestCode == 777) {
//			synchronized (progressDialogList) {
//				if (null != progressDialogList && progressDialogList.size() > 0) {
//					for (ProgressDialog progressDialogTemp : progressDialogList) {
//						progressDialogTemp.dismiss();
//						progressDialogTemp = null;
//					}
//					progressDialogList.clear();
//				}
//			}
			if (StringUtils.equleIgnoreCase("ok", data.getResponseStatus())) {
				if (null == data.getRelationData().getAuth()
						|| data.getRelationData().getAuth() == 1) {
					
					synchronized (progressDialogList) {
						if (null != progressDialogList && progressDialogList.size() > 0) {
							for (ProgressDialog progressDialogTemp : progressDialogList) {
								progressDialogTemp.dismiss();
								progressDialogTemp = null;
							}
							progressDialogList.clear();
						}
					}
					
					final MyDialog dialog=new MyDialog(this);
					dialog.setTitle("提示");
					dialog.setContent("家人没有对您授权查看信息，您需要请求家人授权!");
					dialog.setButton1("请求授权", new OnClickListener() {
						@Override
						public void onClick(View v) {
							NetClient nc=new NetClient();
							nc.setOnNetTranListener(FriendInfoActivity.this);
							Log.d(getClass().toString(), myInfo.getUserId()+"|"+userData.getFriendId()+"");
							nc.requestSeeRole(788, myInfo.getUserId()+"", userData.getFriendId()+"");
							dialog.close();
						}
					});
					dialog.setButton2("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.close();
						}
					});
					dialog.show();
					
				} else if (null != data.getRelationData().getAuth()
						&& data.getRelationData().getAuth() == 3) {
					NetClient netClient = new NetClient();
					netClient.setOnNetTranListener(FriendInfoActivity.this);
					netClient.getLocationList(777555,Integer.toString(userData.getFriendId()), "1");
					
//					// 将该用户ID发送给显示足迹的页面处理
//					Intent intent = new Intent();
//					intent.setClass(FriendInfoActivity.this,
//							ShowTrackActivity.class);
//					intent.putExtra("userID", userData.getFriendId());
//					FriendInfoActivity.this.startActivity(intent);
				}
			}
		}
		//查看位置
		if(requestCode==778){
//			synchronized (progressDialogList) {
//				if (null != progressDialogList && progressDialogList.size() > 0) {
//					for (ProgressDialog progressDialogTemp : progressDialogList) {
//						progressDialogTemp.dismiss();
//						progressDialogTemp = null;
//					}
//					progressDialogList.clear();
//				}
//			}
			if (StringUtils.equleIgnoreCase("ok", data.getResponseStatus())) {
				if (null == data.getRelationData().getAuth()
						|| data.getRelationData().getAuth() == 1) {
					
					synchronized (progressDialogList) {
					if (null != progressDialogList && progressDialogList.size() > 0) {
							for (ProgressDialog progressDialogTemp : progressDialogList) {
								progressDialogTemp.dismiss();
								progressDialogTemp = null;
							}
							progressDialogList.clear();
						}
					}
					
					final MyDialog dialog=new MyDialog(this);
					dialog.setTitle("提示");
					dialog.setContent("家人没有对您授权查看信息，您需要请求家人授权!");
					dialog.setButton1("请求授权", new OnClickListener() {
						@Override
						public void onClick(View v) {
							NetClient nc=new NetClient();
							nc.setOnNetTranListener(FriendInfoActivity.this);
							Log.d(getClass().toString(), myInfo.getUserId()+"|"+userData.getFriendId()+"");
							nc.requestSeeRole(788, myInfo.getUserId()+"", userData.getFriendId()+"");
							dialog.close();
						}
					});
					dialog.setButton2("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.close();
						}
					});
					dialog.show();
					
				} else if (null != data.getRelationData().getAuth()
						&& data.getRelationData().getAuth() == 3) {
					NetClient netClient = new NetClient();
					netClient.setOnNetTranListener(FriendInfoActivity.this);
					netClient.getLocationList(888,Integer.toString(userData.getFriendId()), "1");
//					synchronized (progressDialogList) {
//						ProgressDialog progressDialog = ProgressDialog.show(FriendInfoActivity.this,
//								"请稍等...", "正在获取家人信息...", true, false);
//						progressDialogList.add(progressDialog);
//					}
				}
			}

		}
		if(requestCode==788){
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
				Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			}
		}
		if(requestCode==789){
			if(StringUtils.equleIgnoreCase("ok", data.getResponseStatus())){
				synchronized (progressDialogList) {
					if (null != progressDialogList && progressDialogList.size() > 0) {
						for (ProgressDialog progressDialogTemp : progressDialogList) {
							progressDialogTemp.dismiss();
							progressDialogTemp = null;
						}
						progressDialogList.clear();
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.button1:
//			//确定删除
//			NetClient nc=new NetClient();
//			nc.delRelation(444,
//					Tools.getApplication(FriendInfoActivity.this)
//							.getMyInfo().getUserId()+"", userData
//							.getFriendData().getUserId()+"");
//			nc.setOnNetTranListener(this);
//			dialog.close(); 
//			dialog = null;
//			tip=new Tip(this);
//			tip.setContent("请稍候...");
//			tip.setTitle("提示");
//			tip.show();
//			break;
//		case R.id.button2:
//			dialog.close();
//			dialog = null;
//			break;
		case R.id.ckwz:{ // 查看位置
//			tip=new Tip(this);
//			tip.show();
			synchronized (progressDialogList) {
				ProgressDialog progressDialog = ProgressDialog.show(FriendInfoActivity.this,
						"请稍等...", "正在获取位置信息...", true, false);
				progressDialogList.add(progressDialog);
			}
			NetClient checkwz=new NetClient();
			checkwz.getRelation(778, Tools.getApplication(this).getMyInfo().getUserId()+"",userData.getFriendData().getUserId()+"");
			checkwz.setOnNetTranListener(this);
 			break;
		}
		case R.id.ckzj: {
			synchronized (progressDialogList) {
				ProgressDialog progressDialog = ProgressDialog.show(FriendInfoActivity.this,
					"请稍等...", "正在获取足迹信息...", true, false);
				progressDialogList.add(progressDialog);
			}
			NetClient check=new NetClient();
			check.getRelation(777, Tools.getApplication(this).getMyInfo().getUserId()+"",userData.getFriendData().getUserId()+"");
			check.setOnNetTranListener(this);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public void lockDone(boolean setFlag,boolean flag) {
		if (flag) {
			final NetClient nc=new NetClient();
			if(setFlag){
				switchTv.setText(getResources().getString(R.string.havePermission_toShow));
				nc.changViewRole(789, userData.getFriendData().getUserId()+"", myInfo.getUserId()+"", 3);
			}else{
				switchTv.setText(getResources().getString(R.string.noPermission_toShow));
				nc.changViewRole(789, userData.getFriendData().getUserId()+"", myInfo.getUserId()+"", 1);
			}
		} else {
			if(setFlag){
				switchTv.setText(getResources().getString(R.string.noPermission_toShow));
				wiperSwitch_enter.setisLock(false);
			}else{
				switchTv.setText(getResources().getString(R.string.havePermission_toShow));
				wiperSwitch_enter.setisLock(true);
			}
		}
	}
	
//	//检查是否授权
//	private boolean checkPermission(){
//		if (3!=userData.getAuth()) {
//			MyDialog dialog=new MyDialog(this);
//			dialog.setTitle("提示");
//			dialog.setContent("家人没有对您授权查看信息，您需要请求家人授权!");
//			dialog.setButton1("请求授权", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					NetClient nc=new NetClient();
//					nc.setOnNetTranListener(FriendInfoActivity.this);
//					Log.d(getClass().toString(), myInfo.getUserId()+"|"+userData.getFriendId()+"");
//					nc.requestSeeRole(788, myInfo.getUserId()+"", userData.getFriendId()+"");
//				}
//			});
//			dialog.setButton2("取消", new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dialog.close();
//				}
//			});
//			dialog.show();
//			return false;
//		}
//		return true;
//	}

}
