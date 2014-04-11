package cn.duocool.lashou.activity;

import java.util.ArrayList;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.broadcastreceiver.LashouDeviceAdminReceiver;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.AppModel;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.utils.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
	RelativeLayout lock1, lock2, lock3; // 第1，2，3行

	TextView lock1Name, lock2Name, lock3Name;// 锁一1，2，3的名字

	Boolean lock1havepwd, lock2havepwd, lock3havepwd;// 是否设置了密码
	String lock1Pwd, lock2Pwd, lock3Pwd;// 锁一1，2，3的密码
	String lock1Type, lock2Type, lock3Type;// 密码类型

	SQLiteDatabase db;
	DataBaseHelper dbHelper;
	RelativeLayout setNumber;// 设置手机号码那一行
	RelativeLayout setRemind;// 设置提前提醒时间
	RelativeLayout setwifi;// 设置wifi
	RelativeLayout set3g;
	RelativeLayout preventUnstall;
	TextView remindTextView;// 设置提醒时间那行的时间
	RelativeLayout addtoDevice;
	CheckBox checkBox1;// 防止被卸载的checkbox
	CheckBox checkBox2;
	// 以下为位置服务设置
	private RelativeLayout updatetime;// 设置上传时间点击区域
	private TextView updatetiem_string;// 显示时间TextView
	private String time;// 显示时间
	private int time_i;// 选择项

	private RelativeLayout showtrack_num;// 设置显示足迹条数的点击区域
	private TextView showtrack_num_string;// 显示足迹数量的TextView
	private String num;// 显示数量
	private int num_i;// 选择项

	private SharedPreferences settings;// 存放设置信息的SharedPreferences

	// 防卸载用的（设备管理器）
	private DevicePolicyManager mDPM;
	private ComponentName mDeviceAdminReveiver;
	
	private LashouService lashouService;
	private LockDao lockDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_main);
		
		lashouService = Tools.getApplication(this).getLashouService();
		lockDao = new LockDao(this,lashouService.getLockService().getDao());
		
		
		lock1 = (RelativeLayout) findViewById(R.id.lock1);
		lock1.setOnClickListener(this);
		lock2 = (RelativeLayout) findViewById(R.id.lock2);
		lock3 = (RelativeLayout) findViewById(R.id.lock3);
		lock1Name = (TextView) findViewById(R.id.lock1Name);
		lock2Name = (TextView) findViewById(R.id.lock2Name);
		lock3Name = (TextView) findViewById(R.id.lock3Name);
		setNumber = (RelativeLayout) findViewById(R.id.setPhoneNumber);
		lock2.setOnClickListener(this);
		lock3.setOnClickListener(this);
		setNumber.setOnClickListener(this);

		dbHelper = new DataBaseHelper(this);
		
		setRemind = (RelativeLayout) findViewById(R.id.setremind);
		setRemind.setOnClickListener(this);
		setwifi = (RelativeLayout) findViewById(R.id.setWifi);
		setwifi.setOnClickListener(this);
		set3g = (RelativeLayout) findViewById(R.id.set3g);
		set3g.setOnClickListener(this);
		remindTextView = (TextView) findViewById(R.id.remind);
		// dbHelper=new DataBaseHelper(this);
		preventUnstall = (RelativeLayout) findViewById(R.id.preventuninstall);
		preventUnstall.setOnClickListener(this);
		
		
		addtoDevice = (RelativeLayout) findViewById(R.id.addtoDevice);
		addtoDevice.setOnClickListener(this);
		
		// remindTextView.setText(LockService.remindTime/60000+"分"+LockService.remindTime%60000/1000+"秒");
		checkBox1 = (CheckBox) findViewById(R.id.checkbox1);
		checkBox1.setClickable(false);
		checkBox1.setFocusable(false);
		
		checkBox2 = (CheckBox) findViewById(R.id.checkbox2);
		checkBox2.setClickable(false);
		checkBox2.setFocusable(false);
		
		LockAppInfo lockAppInfo = lockDao.getUnInstallPackage();
		if (null != lockAppInfo && lockAppInfo.getIslock() == 1) {
			checkBox1.setChecked(true);
		} else {
			checkBox1.setChecked(false);
		}
		

		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_PARENT) {
			lock2.setVisibility(View.GONE);
			lock3.setVisibility(View.GONE);
			setNumber.setVisibility(View.GONE);
			setRemind.setVisibility(View.GONE);
			setwifi.setVisibility(View.GONE);
			set3g.setVisibility(View.GONE);
			preventUnstall.setVisibility(View.GONE);
			addtoDevice.setVisibility(View.GONE);
			
			findViewById(R.id.my_divide_line_1).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_2).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_3).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_4).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_5).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_6).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_7).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_8).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_9).setVisibility(View.GONE);
			findViewById(R.id.my_divide_line_10).setVisibility(View.GONE);
		}

		// 位置服务设置
		updatetime = (RelativeLayout) findViewById(R.id.updatetime);
		updatetime.setOnClickListener(this);
		updatetiem_string = (TextView) findViewById(R.id.updatetiem_string);
		time = new String();
		showtrack_num = (RelativeLayout) findViewById(R.id.showtrack_num);
		showtrack_num.setOnClickListener(this);
		showtrack_num_string = (TextView) findViewById(R.id.showtrack_num_string);
		num = new String();

		if (Tools.getApplication(this).getLashouEdition() == CommDef.EDITION_CHILD) {
			// 得到设备管理器（防卸载）
			mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
			mDeviceAdminReveiver = new ComponentName(this,
					LashouDeviceAdminReceiver.class);
			
			
			
			
			
			if (mDPM.isAdminActive(mDeviceAdminReveiver)) {
				checkBox2.setChecked(true);
			} else {
				checkBox2.setChecked(false);
			}
		}

	}// onCreate

	@Override
	protected void onResume() {
		setDefaultData();// 设置页面初始数据
		super.onResume();

		// 位置服务设置获取初始数据
		settings = getSharedPreferences("setting", 0);
		String temp_time = settings.getString("time", "2");
		String temp_num = settings.getString("num", "30");

		time_i = Integer.valueOf(temp_time);
		num_i = Integer.valueOf(temp_num);

		time = temp_time + "分钟";
		num = temp_num + "条";

		updatetiem_string.setText(time);
		showtrack_num_string.setText(num);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lock1:{// 第一个选项
			Intent intent = new Intent();
			intent.putExtra("lockName", lock1Name.getText().toString());
			if (!lock1havepwd) {
				intent.putExtra("gotoActivity", "LockSettingActivity");
				intent.setClass(this, SetImagePasswordActivity.class);
				startActivity(intent);
			} else {
				intent.putExtra("gotoActivity", "LockSettingActivity");
				if (lock1Type.equals("image")) {
					intent.setClass(this, ImageLockActivity.class);
				} else {
					intent.setClass(this, PasswordActivity.class);
				}
				intent.putExtra("password", lock1Pwd);
				startActivity(intent);

			}
			break;
		}
		case R.id.lock2:{// 第二个选项
			Intent intent2 = new Intent();
			intent2.putExtra("lockName", lock2Name.getText().toString());
			if (!lock2havepwd) {
				intent2.putExtra("gotoActivity", "LockSettingActivity");
				intent2.setClass(this, SetImagePasswordActivity.class);
				startActivity(intent2);
			} else {
				intent2.putExtra("gotoActivity", "LockSettingActivity");
				if (lock2Type.equals("image")) {
					intent2.setClass(this, ImageLockActivity.class);
				} else {
					intent2.setClass(this, PasswordActivity.class);
				}
				intent2.putExtra("password", lock2Pwd);
				startActivity(intent2);
			}
			break;
		}
		case R.id.lock3:{// 第三个选项
			Intent intent3 = new Intent();
			intent3.putExtra("lockName", lock3Name.getText().toString());
			if (!lock3havepwd) {
				intent3.putExtra("gotoActivity", "LockSettingActivity");
				intent3.setClass(this, SetImagePasswordActivity.class);
				startActivity(intent3);
			} else {
				intent3.putExtra("gotoActivity", "LockSettingActivity");
				if (lock3Type.equals("image")) {
					intent3.setClass(this, ImageLockActivity.class);
				} else {
					intent3.setClass(this, PasswordActivity.class);
				}
				intent3.putExtra("password", lock3Pwd);
				startActivity(intent3);
			}
			break;
		}
		case R.id.setPhoneNumber: {// 设置号码行
			Intent intent4 = new Intent();
			intent4.setClass(this, ApplockSuperNumberActivity.class);
			startActivity(intent4);
			break;
		}
		case R.id.setremind:{// 设置提前提醒时间
			
			long remindTime = lockDao.getRemind();
			 TimePickerDialog dialog=new TimePickerDialog(this, new
			 TimePickerDialog.OnTimeSetListener() {
			
			 @Override
			 public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			 {
			 remindTextView.setText(hourOfDay+"分"+minute+"秒");
			 ContentValues value=new ContentValues();
			 value.put("time", hourOfDay*60000+minute*1000);
			 // LockService.remindTime=hourOfDay*60000+minute*1000;
			 db=dbHelper.getWritableDatabase();
			 db.update("remind", value, null,null);
			
			 if(db!=null)
			 {
			 db.close();
			 }
			 
			 lashouService.getLockService().getLockSettingInfo().setRemindTime(hourOfDay*60000+minute*1000);
			 
			 }
			 //
			 },
			 (int)(remindTime/60000),
			 (int)(remindTime%60000/1000),
			 true);
			 dialog.setTitle("");
			 dialog.show();

			break;
		}
		case R.id.setWifi: {
			Intent intent = new Intent();
			intent.setClass(this, ApplockSetPeriodsOfTimeActivity.class);
			intent.putExtra("SetType", ApplockSetPeriodsOfTimeActivity.SET_TYPE_USETIME_WIFI);
			startActivity(intent);
			break;
		}
		case R.id.set3g: {
			Intent intent = new Intent();
			intent.setClass(this, ApplockSetPeriodsOfTimeActivity.class);
			intent.putExtra("SetType", ApplockSetPeriodsOfTimeActivity.SET_TYPE_USETIME_XG);
			startActivity(intent);
			break;
		}
		case R.id.preventuninstall: {
//			DataBaseHelper dbHlper = new DataBaseHelper(this);
//			SQLiteDatabase db = dbHlper.getWritableDatabase();
//			ContentValues values = new ContentValues();
//			int index = LockService
//					.getApplistIndex("com.android.packageinstaller");
			LockAppInfo lockAppInfo =  lockDao.getUnInstallPackage();
			
			if (checkBox1.isChecked()) {
				Log.d("tag", "islock" + 0);
				checkBox1.setChecked(false);
				
				// 不拦截
				if (null != lockAppInfo) {
					lockDao.enableUnInstallPackage(0);
					LockAppInfo lockAppInfoMem =  lashouService.getLockService().getAllAppMap().get("com.android.packageinstaller");
					lockAppInfoMem.setIslock(0);
				}
				
			} else {

				checkBox1.setChecked(true);
				// 拦截
				if (null != lockAppInfo) {
					lockDao.enableUnInstallPackage(1);
					LockAppInfo lockAppInfoMem =  lashouService.getLockService().getAllAppMap().get("com.android.packageinstaller");
					lockAppInfoMem.setIslock(1);
				}

			}
			break;
		}
		case R.id.addtoDevice: {

			if (mDPM.isAdminActive(mDeviceAdminReveiver)) {
				// 从设备管理器中删除（防卸载功能）
				
				mDPM.removeActiveAdmin(mDeviceAdminReveiver);
				checkBox2.setChecked(false);
				DataBaseHelper dbh = new DataBaseHelper(this);
				SQLiteDatabase db2 = dbh.getWritableDatabase();
				ContentValues value = new ContentValues();
				value.put("DeviceAdminAdd", 0);
				db2.update("lockall", value, null, null);
				if (db2 != null) {
					db2.close();
				}
			} else {
				checkBox2.setChecked(true);
				// 加入到设备管理器中（防卸载功能）
				Intent intentDeviceAdmin = new Intent(
						DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intentDeviceAdmin.putExtra(
						DevicePolicyManager.EXTRA_DEVICE_ADMIN,
						mDeviceAdminReveiver);
				intentDeviceAdmin.putExtra(
						DevicePolicyManager.EXTRA_ADD_EXPLANATION,
						getString(R.string.deviceAdmin1));
				startActivityForResult(intentDeviceAdmin, 1);

			}

			break;
		}
		case R.id.updatetime: {
			// 设置足迹上传时间
			int i = -1;
			switch (time_i) { // 3.另外把上传的时间，除了半个小时之外，还可以增加到三个时间段，一小时，三小时，五小时。
			case 1:
				i = 0;
				break;
			case 2:
				i = 1;
				break;
			case 3:
				i = 2;
				break;
			case 5:
				i = 3;
				break;
			case 10:
				i = 4;
			case 15:
				i = 5;
				break;
			case 20:
				i = 6;
				break;
			case 30:
				i = 7;
				break;
			case 60:
				i = 8;
				break;
			case 180:
				i = 9;
				break;
			case 300:
				i = 10;
				break;
			default:
				break;
			}
			Dialog time_dialog = new AlertDialog.Builder(SettingActivity.this)
					.setSingleChoiceItems(
							new String[] { "1分钟", "2分钟", "3分钟", "5分钟", "10分钟",
									"15分钟", "20分钟", "30分钟","1小时","3小时","5小时" }, i,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										time = "1分钟";
										time_i = 1;
										break;
									case 1:
										time = "2分钟";
										time_i = 2;
										break;
									case 2:
										time = "3分钟";
										time_i = 3;
										break;
									case 3:
										time = "5分钟";
										time_i = 5;
										break;
									case 4:
										time = "10分钟";
										time_i = 10;
										break;
									case 5:
										time = "15分钟";
										time_i = 15;
										break;
									case 6:
										time = "20分钟";
										time_i = 20;
										break;
									case 7:
										time = "30分钟";
										time_i = 30;
										break;
									case 8:
										time = "1小时";
										time_i = 60;
										break;
									case 9:
										time = "3小时";
										time_i = 180;
										break;
									default:
										time = "5小时";
										time_i = 300;
										break;

									}
									dialog.dismiss();
									updatetiem_string.setText(time);
									settings = getSharedPreferences("setting",
											0);
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putString("time", time_i + "");
									editor.commit();
									
									LashouService.sendAlarmSetting();
								}
							}).create();
			time_dialog.show();
			break;
		}
		case R.id.showtrack_num: {
			// 设置足迹显示数量
			int j = -1;
			switch (num_i) {
			case 10:
				j = 0;
				break;
			case 20:
				j = 1;
				break;
			case 30:
				j = 2;
				break;
			case 50:
				j = 3;
				break;
			case 100:
				j = 4;
			default:
				break;
			}
			Dialog num_dialog = new AlertDialog.Builder(SettingActivity.this)
					.setSingleChoiceItems(
							new String[] { "10条", "20条", "30条", "50条", "100条" },
							j, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:
										num = "10条";
										num_i = 10;
										break;
									case 1:
										num = "20条";
										num_i = 20;
										break;
									case 2:
										num = "30条";
										num_i = 30;
										break;
									case 3:
										num = "50条";
										num_i = 50;
										break;
									default:
										num = "100条";
										num_i = 100;
										break;
									}
									dialog.dismiss();
									showtrack_num_string.setText(num);
									settings = getSharedPreferences("setting",
											0);
									SharedPreferences.Editor editor = settings
											.edit();
									editor.putString("num", num_i + "");
									editor.commit();
								}
							}).create();
			num_dialog.show();
			break;
		}
		default:
			break;
		}

	}

	/**
	 * 设置页面初始数据
	 */
	public void setDefaultData() {
		db = dbHelper.getReadableDatabase();

		Cursor c = db.rawQuery("select * from locks", null);

		String lockName = "";
		ArrayList<String> lockNames = new ArrayList<String>();
		while (c.moveToNext()) {
			lockName = c.getString(c.getColumnIndex("name"));
			lockNames.add(lockName);
			int id = c.getInt(c.getColumnIndex("_id"));
			switch (id) {
			case 1:
				if (c.getString(c.getColumnIndex("password")).equals("")) {
					lock1havepwd = false;
				} else {
					lock1havepwd = true;
					lock1Pwd = c.getString(c.getColumnIndex("password"));
					lock1Type = c.getString(c.getColumnIndex("passwordtype"));
				}
				break;
			case 2:
				if (c.getString(c.getColumnIndex("password")).equals("")) {
					lock2havepwd = false;
				} else {
					lock2havepwd = true;
					lock2Pwd = c.getString(c.getColumnIndex("password"));
					lock2Type = c.getString(c.getColumnIndex("passwordtype"));
				}
				break;
			case 3:
				if (c.getString(c.getColumnIndex("password")).equals("")) {
					lock3havepwd = false;
				} else {
					lock3havepwd = true;
					lock3Pwd = c.getString(c.getColumnIndex("password"));
					lock3Type = c.getString(c.getColumnIndex("passwordtype"));
				}
				break;

			default:
				break;
			}
		}
		lock1Name.setText(lockNames.get(0));

		lock2Name.setText(lockNames.get(1));

		lock3Name.setText(lockNames.get(2));

		Cursor cc = db.rawQuery("select * from lockall", null);
		int a = 0;
		if (cc.moveToNext()) {
			a = cc.getInt(cc.getColumnIndex("DeviceAdminAdd"));
		}
		if (a == 1) {
			checkBox2.setChecked(true);
		} else {
			checkBox2.setChecked(false);
		}
		if (db != null) {
			db.close();
		}
	}

}
