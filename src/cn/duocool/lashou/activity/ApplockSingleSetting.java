package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockInfo;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LockService;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;

import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 设定应用程序的限制信息
 * 
 * @author xwood
 * 
 */
public class ApplockSingleSetting extends BaseActivity implements OnClickListener,ServiceConnection {
	private TextView appNameTV;// 应用名称

	private ImageView appicon;// 应用图标
	private TextView applockTV;// 被哪个锁锁了
	
	private TextView pickWeekTV;// 选择了星期几显示出来

	private TextView timeLimitTV;// 设置限制使用时间textView

	private String packageName;// 当前要设置的应用的包名

	private String[] locks;// 程序锁名字
	private SparseArray<LockInfo> lockInfoMap; // 程序锁信息 与 程序锁 的下标一一对应
	private int lockIndex;	// 程序锁下标
	private int lockIndexDef;	// 程序锁下标
	
//	String lockName;
	
	private String[] week;
	private String[] weekSimple;
	boolean[] pickedWeek = new boolean[] { 
			false, false, false, false, false, false, false };
	
	
//	int indexofappList;// 在appList里面的下标准
	
	// 拉手服务
	private LashouService lashouService; 
	
	private LockService lockService;
	private Map<String, LockAppInfo> allAppMap;
	private LockAppInfo lockAppInfo;
	private DaoBase dao;
	private LockDao lockDao;


	@Override
	protected void onDestroy() {
		unbindService(this);
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_single_app_setting);
		
		bindService(new Intent(this,LashouService.class), this, Context.BIND_AUTO_CREATE);
		
		appNameTV = (TextView) findViewById(R.id.appName);
		appicon = (ImageView) findViewById(R.id.app_icon);
		applockTV = (TextView) findViewById(R.id.appLock);
		Intent intent = getIntent(); // 获得来自主界面的intent
		appNameTV.setText(intent.getStringExtra("appName"));
		packageName = intent.getStringExtra("packageName");
		BitmapDrawable bd = new BitmapDrawable(
				(Bitmap) intent.getParcelableExtra("appicon"));
		appicon.setBackgroundDrawable(bd);

		
		
		timeLimitTV = (TextView) findViewById(R.id.timeLimitTV);

		pickWeekTV = (TextView) findViewById(R.id.pickWeekTV);
//		dbHelper = new DataBaseHelper(this);
		
		week = new String[] { this.getString(R.string.lockAppSettingWeek0),
				this.getString(R.string.lockAppSettingWeek1),
				this.getString(R.string.lockAppSettingWeek2),
				this.getString(R.string.lockAppSettingWeek3),
				this.getString(R.string.lockAppSettingWeek4),
				this.getString(R.string.lockAppSettingWeek5),
				this.getString(R.string.lockAppSettingWeek6) };
		
		weekSimple = new String[] { this.getString(R.string.lockAppSettingWeekS0),
				this.getString(R.string.lockAppSettingWeekS1),
				this.getString(R.string.lockAppSettingWeekS2),
				this.getString(R.string.lockAppSettingWeekS3),
				this.getString(R.string.lockAppSettingWeekS4),
				this.getString(R.string.lockAppSettingWeekS5),
				this.getString(R.string.lockAppSettingWeekS6) };
		
		//indexofappList = LockService.getApplistIndex(packageName);// 返回这个应用在applist中的下标，-1表示没有
		//setDefaultData();// 从数据库中读出数据显示出来
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.pickWeek: { // 选择一周哪天可以用
				new AlertDialog.Builder(this)
					.setTitle(getString(R.string.lockAppSettingWeekTitle))
					// 请选择可用的星期
					.setMultiChoiceItems(week, pickedWeek,
							new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which, boolean isChecked) {
									pickedWeek[which] = isChecked;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									
									// 获得程序信息
									Map<String, LockAppInfo> allAppMap = lockService.getAllAppMap();
									LockAppInfo lockAppInfo = allAppMap.get(packageName);
									lockAppInfo.getLimitWeek().clear();
									List<Integer> weeks = new ArrayList<Integer>();
									StringBuilder sb  = new StringBuilder();
									for (int i =0;i<pickedWeek.length;i++) {
										if (pickedWeek[i]) {
											weeks.add(i);
											lockAppInfo.getLimitWeek().add(i);
											sb.append(weekSimple[i]).append(",");
										}
									}
									
									// 更新
									DaoBase dao = lockService.getDao();
									LockDao lockDao = new LockDao(ApplockSingleSetting.this,dao);
									if (weeks.size() > 0) { // 周限制 不为空
										int appId = (int)lockAppInfo.getAppId();
										lockDao.updateAppLimitWeek(appId, weeks);	
										
										String showText = sb.toString();
										showText = showText.substring(0, showText.length()-1);
										pickWeekTV.setText(showText);
									} else { // 没有周限制
										// 清空数据库的周限制
										int appId = (int)lockAppInfo.getAppId();
										lockDao.updateAppLimitWeek(appId, null);	
										
										pickWeekTV.setText(getString(R.string.lockAppSettingWeekSelectTip));
									}
								}
							}).setNegativeButton("取消", new  DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 获得程序信息
									Map<String, LockAppInfo> allAppMap = lockService.getAllAppMap();
									LockAppInfo lockAppInfo = allAppMap.get(packageName);
									// 恢复周信息
									List<Integer> weeks = lockAppInfo.getLimitWeek();
									if (weeks.size() <=0) {
										for (int i=0;i<pickedWeek.length;i++) {
											pickedWeek[i] = false;
										}
									} else {
										for (int i=0;i<pickedWeek.length;i++) {
											pickedWeek[i] = false;
										}
										for (int i =0;i<weeks.size();i++) {
											pickedWeek[weeks.get(i)] = true;
										}
									}
								}
								
							}).create().show();
			break;
		}
		case R.id.timeLimitRl: {// 选择使用限制时长
			
			long limitTime = lockAppInfo.getLimitTime();
			
			TimePickerDialog timePickerDialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {

							// 更新显示
							timeLimitTV.setText(hourOfDay + getString(R.string.lockAppSettingHour) + minute + getString(R.string.lockAppSettingMinute));
							
							// 更新保存的信息
							if (0==hourOfDay && 0==minute) {
								lockAppInfo.setLimitTime(-1);
								lockAppInfo.setLeftTime(-1);
								lockDao.updateAppInfoForLimitTime(lockAppInfo.getAppId(),-1,-1);
							} else {
								lockAppInfo.setLimitTime((hourOfDay*60*60*1000 + minute * 60 * 1000));
								lockAppInfo.setLeftTime(-1);
								lockDao.updateAppInfoForLimitTime(
										lockAppInfo.getAppId(),
										(hourOfDay*60*60*1000 + minute * 60 * 1000),
										(hourOfDay*60*60*1000 + minute * 60 * 1000));
							}							
						}
					}, (limitTime <= 0) ? 0 : (int)(limitTime/1000/60/60), (limitTime <= 0) ? 0 : (int)((limitTime % (1000 * 60 * 60)) / (1000 * 60)), true);
			timePickerDialog.setTitle("");
			timePickerDialog.show();
			break;
		}
		case R.id.lockSelect: { // 选择锁
			lockIndex = lockIndexDef;
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle(R.string.lockAppSettingSelectLockTitle); // 请选择程序y锁

			builder2.setSingleChoiceItems(locks, lockIndex,
				new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) { 
//								// OK，点了 设定了，变更
//								applockTV.setText(lockInfoMap.get(which).getLockName());
								lockIndex = which;
				}
			});
			builder2.setPositiveButton(R.string.lockAppSettingbtnOK, // 确定
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// OK，点了 设定了
							lockIndexDef = lockIndex;
							
							// 更新界面
							applockTV.setText(lockInfoMap.get(lockIndex).getLockName());
							
							// 变更后台数据
							lockAppInfo.setLockId(lockInfoMap.get(lockIndex).getLockId());
							
							// 变更数据库
							lockDao.updateAppInfoForUseLock(lockAppInfo.getAppId(), lockAppInfo.getLockId());
						}
					});

			builder2.setNegativeButton(R.string.lockAppSettingbtnCancel, null);
			builder2.show();
			break;
		}
		case R.id.setusabletime: { // 设定可用时长
			Intent intent = new Intent();
			intent.setClass(this, ApplockSetPeriodsOfTimeActivity.class);
			intent.putExtra("SetType", ApplockSetPeriodsOfTimeActivity.SET_TYPE_USETIME_APP);
			intent.putExtra("packageName", packageName);
			startActivity(intent);
			break;
		}
		default:
			break;
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		lashouService = ((LashouService.LocalServer)service).getService();
		lashouService.getLockService();
		// 初期化界面 的显示数据
		initUI(lashouService.getLockService(),this);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		lashouService = null;
	}
	
	/**
	 * 初期化界面
	 */
	private void initUI(LockService lockService,Context context) {
		
		this.lockService = lockService;
		
		// 锁选择
		findViewById(R.id.lockSelect).setOnClickListener(this);
		// 可用星期
		findViewById(R.id.pickWeek).setOnClickListener(this);
		// 可用时间
		findViewById(R.id.setusabletime).setOnClickListener(this);
		// 使用时长
		findViewById(R.id.timeLimitRl).setOnClickListener(this);
		
		// 获得程序信息
		this.allAppMap = lockService.getAllAppMap();
		this.lockAppInfo = allAppMap.get(packageName);
		this.dao = lockService.getDao();
		this.lockDao = new LockDao(this, dao);
		
		// 初期化周信息
		List<Integer> weeks = lockAppInfo.getLimitWeek();
		if (weeks.size() <=0) {
			pickWeekTV.setText(getString(R.string.lockAppSettingWeekSelectTip));
		} else {
			StringBuilder sb  = new StringBuilder();
			for (int i =0;i<weeks.size();i++) {
				sb.append(weekSimple[weeks.get(i)]).append(",");
				pickedWeek[weeks.get(i)] = true;
			}
			String showText = sb.toString();
			showText = showText.substring(0, showText.length()-1);
			pickWeekTV.setText(showText);
		}
		
		// 初期化 可用时长
		long limitTime = lockAppInfo.getLimitTime();
		if (0 >= limitTime) { // 默认值 没有设定
			timeLimitTV.setText(getString(R.string.lockAppSettingUseTimeTip));
		} else { // 设定了时间
			int hour = (int) (limitTime/1000/60/60);
			int min = (int) ((limitTime % (1000 * 60 * 60)) / (1000 * 60));
			String h = hour > 9 ? String.valueOf(hour) : "0"+String.valueOf(hour);
			String m = min > 9 ? String.valueOf(min) : "0"+String.valueOf(min);
			
			timeLimitTV.setText(h + getString(R.string.lockAppSettingHour) + m + getString(R.string.lockAppSettingMinute));
		}
		
		// 初期化 锁选择
		LockInfo lockInfo = lockDao.getLockById(lockAppInfo.getLockId());
		applockTV.setText(lockInfo.getLockName());	
		
		// 获得所有的锁
		List<LockInfo> lockList =  lockDao.getAllLocks();
		locks = new String[lockList.size()];
		lockInfoMap = new SparseArray<LockInfo>();
		for (int i=0;i<lockList.size();i++) {
			if (lockAppInfo.getLockId()== lockList.get(i).getLockId()) {
				this.lockIndex = i;
				this.lockIndexDef = i;
			}
			locks[i] = lockList.get(i).getLockName();
			lockInfoMap.put(i,  lockList.get(i));
		}
	}
}
