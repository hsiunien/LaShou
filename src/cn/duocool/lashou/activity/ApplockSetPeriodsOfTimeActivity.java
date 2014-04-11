package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DaoBase;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.dao.LockDao;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.model.LockTimeSegmentBean;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * 本界面用于设置时间段
 */
public class ApplockSetPeriodsOfTimeActivity extends BaseActivity implements
		android.view.View.OnClickListener {

//	private final static String TAG = ApplockSetPeriodsOfTimeActivity.class.getName();
	
	RelativeLayout beginTimeRL, endTimeRL, addperiod;
	TextView endTimeTV, beginTimeTV;
	DataBaseHelper dbHelper;
	ListView listView;
	
	SimpleAdapter simpleAdapter;
	
	int appid;
	
	String packageName = null;
	
	// 拉手服务
	private LashouService lashouService;	
	private LockService lockService;
	private Map<String, LockAppInfo> allAppMap;
	private LockAppInfo lockAppInfo;
	private DaoBase dao;
	private LockDao lockDao;
	
	public List<Map<String, Object>> listData;	// 声明列表容器
	
	// 时间对话框
	Dialog dialog = null;
	
	// 临时用保存设定的时间
	private String beginTimeH = "0";
	private String beginTimeM = "0";
	private String endTimeH= "0";
	private String endTimeM= "0";

	// 本界面的设置类型
	public final static int SET_TYPE_USETIME_APP = 0;
	public final static int SET_TYPE_USETIME_XG = 1;
	public final static int SET_TYPE_USETIME_WIFI = 2;

	
//	public TitleBar titlebar;
	
	// 传递的设置类型
	private int setType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_setperiodsoftime);
		
		Intent intent = getIntent();
		setType = intent.getIntExtra("SetType", 0);
		packageName = intent.getStringExtra("packageName");
		
		// 获得后台数据
		lashouService = Tools.getApplication(this).getLashouService();
		lockService = lashouService.getLockService();
		allAppMap = lockService.getAllAppMap();
		lockAppInfo = allAppMap.get(packageName);
		dao = lockService.getDao();
		lockDao = new LockDao(this, dao);
		
		findViewById(R.id.beginTimeRL).setOnClickListener(this);
		findViewById(R.id.endTimeRL).setOnClickListener(this);
		findViewById(R.id.addperiod).setOnClickListener(this);
		
		beginTimeTV = (TextView) findViewById(R.id.beginTimeTV);
		beginTimeTV.setText("00"+ getString(R.string.lockAppSettingHour2) + "00"+ getString(R.string.lockAppSettingMinute2));
		endTimeTV = (TextView) findViewById(R.id.endTimeTV);
		endTimeTV.setText("00"+ getString(R.string.lockAppSettingHour2) + "00"+ getString(R.string.lockAppSettingMinute2));
		listView = (ListView) findViewById(R.id.listview);
		
		// 适配器用数据
		listData = new ArrayList<Map<String, Object>>();
		
		// 更具类型 初期化界面
		
		switch (setType) {
			case SET_TYPE_USETIME_APP: {
				TitleBar titlebar = (TitleBar) findViewById(R.id.titleBar1);
				TextView tv = (TextView) titlebar.findViewById(R.id.titleBar_title);
				tv.setText(R.string.lockAppSettingTimeSegmentTitleBarTtitle1); // "可用时间段"
				
				// 获得当前程序的可以使用的时间长度
				List<LockTimeSegmentBean> lockTimeSegmentList =  lockAppInfo.getLimitTimeSegment();
				for (int i=0;i<lockTimeSegmentList.size();i++) {
					LockTimeSegmentBean lockTimeSegmentBean = lockTimeSegmentList.get(i);
					HashMap<String,Object> mapData = new HashMap<String, Object>();
					
					// 里面存放的格式：HH:mm:ss
					String beginTime  = lockTimeSegmentBean.getStartTime();
					String endTime = lockTimeSegmentBean.getEndTime();
					// 23点15分
					mapData.put("timeId", Integer.valueOf(lockTimeSegmentBean.getTimeId()));
					mapData.put("begin", beginTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + beginTime.split(":")[1] + getString(R.string.lockAppSettingMinute2)); 
					mapData.put("end", endTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + endTime.split(":")[1] + getString(R.string.lockAppSettingMinute2));
					
					listData.add(mapData);
				}
				
				// 设定适配器
				simpleAdapter = new SimpleAdapter(this, listData,
						R.layout.applock_periodoftimeitem, 
						new String[] { "begin","end" }, 
						new int[] { R.id.beginTV, R.id.endTV });
				listView.setAdapter(simpleAdapter);
				
				// 长点删除
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						final int nowPostion = position;
						new AlertDialog.Builder(ApplockSetPeriodsOfTimeActivity.this)
						.setTitle(R.string.lockAppSettingTimeSegmentDelConfirmTip) // 您确定要删除？
						.setPositiveButton(R.string.lockAppSettingbtnOK, // 确定
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											
											// 获得要删除的ID
											int timeID = ((Integer)listData.get(nowPostion).get("timeId")).intValue();
											
											// 删除数据库
											lockDao.delAppUseTimeSegment(lockAppInfo.getAppId(), timeID);
											int delIndex = -1;
											for (int i=0;i<lockAppInfo.getLimitTimeSegment().size();i++) {
												if (timeID == lockAppInfo.getLimitTimeSegment().get(i).getTimeId()){
													delIndex = i;
													break;
												}
											}
											if (delIndex >= 0 && lockAppInfo.getLimitTimeSegment().size() > 0) {
												lockAppInfo.getLimitTimeSegment().remove(delIndex);
											}
											
											// 删除显示用数据
											listData.remove(nowPostion);
											
											simpleAdapter.notifyDataSetChanged();
										}
									})
						.setNegativeButton(R.string.lockAppSettingbtnCancel, null) // 取消
						.show();
						
						return true;
					}
				});
				
				break;
			}
			case SET_TYPE_USETIME_XG: {
				TitleBar titlebar = (TitleBar) findViewById(R.id.titleBar1);
				TextView tv = (TextView) titlebar.findViewById(R.id.titleBar_title);
				tv.setText(R.string.lockAppSettingTimeSegmentTitleBarTtitle2); // "可用时间段"
				
				// 获得当前程序的可以使用的时间长度
				List<LockTimeSegmentBean> xgTimeSegmentList=  lockService.getLockSettingInfo().getNetLimitTimeList();
				for (int i=0;i<xgTimeSegmentList.size();i++) {
					LockTimeSegmentBean lockTimeSegmentBean = xgTimeSegmentList.get(i);
					HashMap<String,Object> mapData = new HashMap<String, Object>();
					
					// 里面存放的格式：HH:mm:ss
					String beginTime  = lockTimeSegmentBean.getStartTime();
					String endTime = lockTimeSegmentBean.getEndTime();
					// 23点15分
					mapData.put("timeId", Integer.valueOf(lockTimeSegmentBean.getTimeId()));
					mapData.put("begin", beginTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + beginTime.split(":")[1] + getString(R.string.lockAppSettingMinute2)); 
					mapData.put("end", endTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + endTime.split(":")[1] + getString(R.string.lockAppSettingMinute2));
					
					listData.add(mapData);
				}
				
				// 设定适配器
				simpleAdapter = new SimpleAdapter(this, listData,
						R.layout.applock_periodoftimeitem, 
						new String[] { "begin","end" }, 
						new int[] { R.id.beginTV, R.id.endTV });
				listView.setAdapter(simpleAdapter);
				
				// 长点删除
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						final int nowPostion = position;
						new AlertDialog.Builder(ApplockSetPeriodsOfTimeActivity.this)
						.setTitle(R.string.lockAppSettingTimeSegmentDelConfirmTip) // 您确定要删除？
						.setPositiveButton(R.string.lockAppSettingbtnOK, // 确定
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											
											// 获得要删除的ID
											int timeID = ((Integer)listData.get(nowPostion).get("timeId")).intValue();
											
											// 删除数据库
											lockDao.del3GUseTimeSegment(timeID);
											int delIndex = -1;
											for (int i=0;i<lockService.getLockSettingInfo().getNetLimitTimeList().size();i++) {
												if (timeID == lockService.getLockSettingInfo().getNetLimitTimeList().get(i).getTimeId()){
													delIndex = i;
													break;
												}
											}
											if (delIndex >= 0 && lockService.getLockSettingInfo().getNetLimitTimeList().size() > 0) {
												lockService.getLockSettingInfo().getNetLimitTimeList().remove(delIndex);
											}
											
											// 删除显示用数据
											listData.remove(nowPostion);
											
											simpleAdapter.notifyDataSetChanged();
										}
									})
						.setNegativeButton(R.string.lockAppSettingbtnCancel, null) // 取消
						.show();
						
						return true;
					}
				});
				
				
				break;
			}
			case SET_TYPE_USETIME_WIFI: {
				TitleBar titlebar = (TitleBar) findViewById(R.id.titleBar1);
				TextView tv = (TextView) titlebar.findViewById(R.id.titleBar_title);
				tv.setText(R.string.lockAppSettingTimeSegmentTitleBarTtitle2); // "可用时间段"
				
				// 获得当前程序的可以使用的时间长度
				List<LockTimeSegmentBean> wifiTimeSegmentList = lockService.getLockSettingInfo().getWifiLimitTimeList();
				for (int i=0;i<wifiTimeSegmentList.size();i++) {
					LockTimeSegmentBean lockTimeSegmentBean = wifiTimeSegmentList.get(i);
					HashMap<String,Object> mapData = new HashMap<String, Object>();
					
					// 里面存放的格式：HH:mm:ss
					String beginTime  = lockTimeSegmentBean.getStartTime();
					String endTime = lockTimeSegmentBean.getEndTime();
					// 23点15分
					mapData.put("timeId", Integer.valueOf(lockTimeSegmentBean.getTimeId()));
					mapData.put("begin", beginTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + beginTime.split(":")[1] + getString(R.string.lockAppSettingMinute2)); 
					mapData.put("end", endTime.split(":")[0] + getString(R.string.lockAppSettingHour2) + endTime.split(":")[1] + getString(R.string.lockAppSettingMinute2));
					
					listData.add(mapData);
				}
				
				// 设定适配器
				simpleAdapter = new SimpleAdapter(this, listData,
						R.layout.applock_periodoftimeitem, 
						new String[] { "begin","end" }, 
						new int[] { R.id.beginTV, R.id.endTV });
				listView.setAdapter(simpleAdapter);
				
				// 长点删除
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
						final int nowPostion = position;
						new AlertDialog.Builder(ApplockSetPeriodsOfTimeActivity.this)
						.setTitle(R.string.lockAppSettingTimeSegmentDelConfirmTip) // 您确定要删除？
						.setPositiveButton(R.string.lockAppSettingbtnOK, // 确定
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											
											// 获得要删除的ID
											int timeID = ((Integer)listData.get(nowPostion).get("timeId")).intValue();
											
											// 删除数据库
											lockDao.delWifiUseTimeSegment(timeID);
											int delIndex = -1;
											for (int i=0;i<lockService.getLockSettingInfo().getWifiLimitTimeList().size();i++) {
												if (timeID == lockService.getLockSettingInfo().getWifiLimitTimeList().get(i).getTimeId()){
													delIndex = i;
													break;
												}
											}
											if (delIndex >= 0 && lockService.getLockSettingInfo().getWifiLimitTimeList().size() > 0) {
												lockService.getLockSettingInfo().getWifiLimitTimeList().remove(delIndex);
											}
											
											// 删除显示用数据
											listData.remove(nowPostion);
											
											simpleAdapter.notifyDataSetChanged();
										}
									})
						.setNegativeButton(R.string.lockAppSettingbtnCancel, null) // 取消
						.show();
						
						return true;
					}
				});
				break;
			}
			default:
				break;
		}
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.beginTimeRL: {
			
			if (null !=dialog && dialog.isShowing()) { // 窗口已经打开
				return;
			}
			
			dialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							
							String beginTimeHour = hourOfDay > 9 ? ""+hourOfDay : "0"+hourOfDay;
							String beginTimeMinute = minute > 9 ? ""+minute : "0"+minute;
							beginTimeH = beginTimeHour;
							beginTimeM = beginTimeMinute;
							
							beginTimeTV.setText(beginTimeHour + getString(R.string.lockAppSettingHour2) + beginTimeMinute + getString(R.string.lockAppSettingMinute2));
							
						}
					}, Integer.parseInt(beginTimeH),Integer.parseInt(beginTimeM), true);
			dialog.show();
			break;
		}
		case R.id.endTimeRL:{
			
			if (null !=dialog && dialog.isShowing()) { // 窗口已经打开
				return;
			}
			
			dialog = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {
						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							
							String endTimeHour = hourOfDay > 9 ? ""+hourOfDay : "0"+hourOfDay;
							String endTimeMinute = minute > 9 ? ""+minute : "0"+minute;
							endTimeH = endTimeHour;
							endTimeM = endTimeMinute;
							
							endTimeTV.setText(endTimeHour + getString(R.string.lockAppSettingHour2) + endTimeMinute + getString(R.string.lockAppSettingMinute2));
							
							dialog.dismiss();
							dialog = null;
						}
					}, Integer.parseInt(endTimeH),Integer.parseInt(endTimeM), true);
			dialog.show();
			break;
		}
		case R.id.addperiod: { // 添加时间段
			switch (setType) {
				case SET_TYPE_USETIME_APP: {
					
					int beginMinute = StringUtils.stringToSecond2(beginTimeH+":"+beginTimeM,":");
					int endMinute = StringUtils.stringToSecond2(endTimeH+":"+endTimeM,":");
					
					if (beginMinute > endMinute) { // 开始时间 > 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError01Tip, Toast.LENGTH_SHORT).show();
					} else if (beginMinute == endMinute) {  // 开始时间 == 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError02Tip, Toast.LENGTH_SHORT).show();
					} else  { // 开始时间 < 结束时间
						// 获得当前程序的可以使用的时间长度
						List<LockTimeSegmentBean> lockTimeSegmentList =  lockAppInfo.getLimitTimeSegment();
						for (int i=0;i<lockTimeSegmentList.size();i++) {
							LockTimeSegmentBean lockTimeSegmentBean = lockTimeSegmentList.get(i);
							int dbStartTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getStartTime(),":"); // xx:xx:xx
							int dbendTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getEndTime(),":"); // xx:xx:xx
							if ((beginMinute >dbStartTime && beginMinute < dbendTime) ||
									(endMinute >dbStartTime && endMinute < dbendTime	)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError04Tip, Toast.LENGTH_SHORT).show();
								return;
							}
							if ((beginMinute == dbStartTime && endMinute == dbendTime)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError03Tip, Toast.LENGTH_SHORT).show();
								return;
							}
						}
		
						LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
						lockTimeSegmentBean.setAppId(Long.valueOf(lockAppInfo.getAppId()).intValue());
						lockTimeSegmentBean.setStartTime(beginTimeH+":"+beginTimeM+":00");
						lockTimeSegmentBean.setEndTime(endTimeH+":"+endTimeM+":00");
						
						// 添加数据库
						long timeID = lockDao.addAppUseTimeSegment(lockTimeSegmentBean);
						lockTimeSegmentBean.setTimeId(Long.valueOf(timeID).intValue());
						
						lockTimeSegmentList.add(lockTimeSegmentBean);
						
					
						HashMap<String,Object> mapData = new HashMap<String, Object>();
						
						// 里面存放的格式：HH:mm:ss
						mapData.put("timeId", Integer.valueOf(Long.valueOf(timeID).intValue()));
						mapData.put("begin", beginTimeH + getString(R.string.lockAppSettingHour2) + beginTimeM + getString(R.string.lockAppSettingMinute2)); 
						mapData.put("end", endTimeH + getString(R.string.lockAppSettingHour2) + endTimeM + getString(R.string.lockAppSettingMinute2));
						listData.add(mapData);
						simpleAdapter.notifyDataSetChanged();
					}
					break;
				
				}
				case SET_TYPE_USETIME_WIFI: {
					int beginMinute = StringUtils.stringToSecond2(beginTimeH+":"+beginTimeM,":");
					int endMinute = StringUtils.stringToSecond2(endTimeH+":"+endTimeM,":");
					
					if (beginMinute > endMinute) { // 开始时间 > 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError01Tip, Toast.LENGTH_SHORT).show();
					} else if (beginMinute == endMinute) {  // 开始时间 == 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError02Tip, Toast.LENGTH_SHORT).show();
					} else  { // 开始时间 < 结束时间
						// 获得当前程序的可以使用的时间长度
						List<LockTimeSegmentBean> wifiTimeSegmentList =  lockService.getLockSettingInfo().getWifiLimitTimeList();
						for (int i=0;i<wifiTimeSegmentList.size();i++) {
							LockTimeSegmentBean lockTimeSegmentBean = wifiTimeSegmentList.get(i);
							int dbStartTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getStartTime(),":"); // xx:xx:xx
							int dbendTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getEndTime(),":"); // xx:xx:xx
							if ((beginMinute >dbStartTime && beginMinute < dbendTime) ||
									(endMinute >dbStartTime && endMinute < dbendTime	)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError04Tip, Toast.LENGTH_SHORT).show();
								return;
							}
							if ((beginMinute == dbStartTime && endMinute == dbendTime)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError03Tip, Toast.LENGTH_SHORT).show();
								return;
							}
						}
		
						LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
						lockTimeSegmentBean.setStartTime(beginTimeH+":"+beginTimeM+":00");
						lockTimeSegmentBean.setEndTime(endTimeH+":"+endTimeM+":00");
						
						// 添加数据库
						long timeID = lockDao.addWifiUseTimeSegment(lockTimeSegmentBean);
						lockTimeSegmentBean.setTimeId(Long.valueOf(timeID).intValue());
						
						wifiTimeSegmentList.add(lockTimeSegmentBean);
						
					
						HashMap<String,Object> mapData = new HashMap<String, Object>();
						
						// 里面存放的格式：HH:mm:ss
						mapData.put("timeId", Integer.valueOf(Long.valueOf(timeID).intValue()));
						mapData.put("begin", beginTimeH + getString(R.string.lockAppSettingHour2) + beginTimeM + getString(R.string.lockAppSettingMinute2)); 
						mapData.put("end", endTimeH + getString(R.string.lockAppSettingHour2) + endTimeM + getString(R.string.lockAppSettingMinute2));
						listData.add(mapData);
						simpleAdapter.notifyDataSetChanged();
					}
					break;
				}
				case SET_TYPE_USETIME_XG: {
					int beginMinute = StringUtils.stringToSecond2(beginTimeH+":"+beginTimeM,":");
					int endMinute = StringUtils.stringToSecond2(endTimeH+":"+endTimeM,":");
					
					if (beginMinute > endMinute) { // 开始时间 > 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError01Tip, Toast.LENGTH_SHORT).show();
					} else if (beginMinute == endMinute) {  // 开始时间 == 结束时间
						Toast.makeText(this, R.string.lockAppSettingTimeSegmentError02Tip, Toast.LENGTH_SHORT).show();
					} else  { // 开始时间 < 结束时间
						// 获得当前程序的可以使用的时间长度
						List<LockTimeSegmentBean> xgTimeSegmentList =  lockService.getLockSettingInfo().getNetLimitTimeList();
						for (int i=0;i<xgTimeSegmentList.size();i++) {
							LockTimeSegmentBean lockTimeSegmentBean = xgTimeSegmentList.get(i);
							int dbStartTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getStartTime(),":"); // xx:xx:xx
							int dbendTime = StringUtils.stringToSecond3(lockTimeSegmentBean.getEndTime(),":"); // xx:xx:xx
							if ((beginMinute >dbStartTime && beginMinute < dbendTime) ||
									(endMinute >dbStartTime && endMinute < dbendTime	)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError04Tip, Toast.LENGTH_SHORT).show();
								return;
							}
							if ((beginMinute == dbStartTime && endMinute == dbendTime)) {
								Toast.makeText(this, R.string.lockAppSettingTimeSegmentError03Tip, Toast.LENGTH_SHORT).show();
								return;
							}
						}
		
						LockTimeSegmentBean lockTimeSegmentBean = new LockTimeSegmentBean();
						lockTimeSegmentBean.setStartTime(beginTimeH+":"+beginTimeM+":00");
						lockTimeSegmentBean.setEndTime(endTimeH+":"+endTimeM+":00");
						
						// 添加数据库
						long timeID = lockDao.add3GUseTimeSegment(lockTimeSegmentBean);
						lockTimeSegmentBean.setTimeId(Long.valueOf(timeID).intValue());
						
						xgTimeSegmentList.add(lockTimeSegmentBean);
						
					
						HashMap<String,Object> mapData = new HashMap<String, Object>();
						
						// 里面存放的格式：HH:mm:ss
						mapData.put("timeId", Integer.valueOf(Long.valueOf(timeID).intValue()));
						mapData.put("begin", beginTimeH + getString(R.string.lockAppSettingHour2) + beginTimeM + getString(R.string.lockAppSettingMinute2)); 
						mapData.put("end", endTimeH + getString(R.string.lockAppSettingHour2) + endTimeM + getString(R.string.lockAppSettingMinute2));
						listData.add(mapData);
						simpleAdapter.notifyDataSetChanged();
					}
					break;
				}
				default:
					break;
			}
			break;
		}
		default:
			break;
		}
	}
}
