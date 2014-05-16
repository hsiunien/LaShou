package cn.duocool.lashou.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.duocool.lashou.R;
import cn.duocool.lashou.model.MyElectronicFence;
import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.model.MyLocationRemind;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.mywidget.WiperSwitch.OnChangedListener;
import cn.duocool.lashou.service.ElectronicFenceService;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.utils.Log;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.adapter.SetpersonAdapter;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.RelationData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 添加电子围栏Acitivity,电子围栏二级页面
 * @author 杞桅
 */
public class AddElectronicFenceActivity extends BaseActivity implements NetTranListener, ServiceConnection{
	
	private static final String TAG = AddElectronicFenceActivity.class.getName();//TAG  

	// 电子围栏服务
	ElectronicFenceService electronicFenceService = null;
	
	//全局通用变量
	private MyElectronicFence myElectronicFence = null;//电子围栏
	private int isAddEF_Flag = 0;//是否是添加电子围栏的标志位。0表示添加，1表示修改
	private String titlebarString = null;//标题栏文字
	private Intent intent = null;//Intent
	public static Handler handler = null;//Handler
	private static ProgressDialog progressDialog = null;

	//标题栏
	private TitleBar titleBar = null;//标题栏

	//设置提醒标题
	private RelativeLayout setTitle = null;//设置标题
	private TextView title_String = null;//标题文字TextView
	private String title = null;//标题文字

	//选择关心对象
	private RelativeLayout setPerson = null;//设置关心对象 
	private TextView person_String = null;//关心对象TextView 
	private String person = null;//控件上显示的关心对象名字
	private ArrayList<Integer> choose_id = null;//用户已选择关心对象ID
	private ArrayList<String> choose_name = null;//用户已选择关心对象名字
	private int choose=0;//用户已选择关心对象数量
	private String userID;
	private ResponseData rd = null;//ResponseData，返回数据
	private boolean is_First = true;//是否第一次编辑弹出框
	private String temp_choose_name[] = null;//用于记录用户选择的好友
	private int tmep_Echoose_id[] = null;//保存用户在做选择操作前的好友ID，用于退出时使用
	private AlertDialog dialog = null;//AlertDialog，弹出选择框
	private ListView listView = null;//ListView
	private SetpersonAdapter myAdapter = null;//适配器
	private ArrayList<HashMap<String, String>> listItem = null;


	private boolean is_set_person = false;

	//设置中心点
	private boolean is_set_map = false;//用户是否选择中心点，若未选择中心点，则没有围栏截图
	private RelativeLayout setPoint;//设置中心点
	private TextView point_String;//中心点文字TextView
	private double Latitude;//用户选择位置的纬度
	private double Longitude;//用户选择位置的经度
	static String address;//地址
	private int flag = 0;//用户未设置中心点的标记
	private String drawablePaht;//图片存放路径
	public static boolean is_delete = false;

	//设置半径
	private RelativeLayout setR = null;//设置半径
	private TextView r_String = null;//半径文字TextView
	private int r = 0;//半径
	private int i = -1;//默认选择项

	private WiperSwitch wiperSwitch_enter;//进入提醒开关 
	private boolean is_In = true;//进入提醒
	private WiperSwitch wiperSwitch_leave;//离开提醒开关 
	private boolean is_Out = true;//离开提醒

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_electronic_fence);
		
		// 绑定 电子围栏服务
		Intent electronicFenceServiceIntent = new Intent(this,ElectronicFenceService.class);
		bindService(electronicFenceServiceIntent, this, Context.BIND_AUTO_CREATE);

		//获取myElectronicFence，若为空，则为新增操作
		myElectronicFence = (MyElectronicFence) getIntent().getSerializableExtra("myElectronicFence");

		if(myElectronicFence==null){
			//为新增电子围栏操作
			Log.d(TAG+"onCreate","添加电子围栏");
			isAddEF_Flag = 0;
			titlebarString = "添加电子围栏";
		}else{
			//为修改电子围栏操作
			Log.d(TAG+"onCreate","修改电子围栏");
			isAddEF_Flag = 1;
			titlebarString = "修改电子围栏";
		}

		//标题栏
		titleBar = (TitleBar) findViewById(R.id.activity_add_electronic_titleBar);
		titleBar.setTitle(titlebarString);
		titleBar.setLeftButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//返回按键
				AddElectronicFenceActivity.this.finish();
			}
		});
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//保存按键，先判断
				//满足条件，在做操作
				if(isAddEF_Flag == 0){
					//新增操作
					if(choose == 0 || is_set_person == false){
						Toast.makeText(AddElectronicFenceActivity.this, "请选择关心对象！", Toast.LENGTH_SHORT).show();
						return;
					}
					if(is_set_map == false){
						Toast.makeText(AddElectronicFenceActivity.this, "请选择中心点！", Toast.LENGTH_SHORT).show();
						return;
					}
					if(is_In == false && is_Out ==false){
						Toast.makeText(AddElectronicFenceActivity.this, "请选择提醒类型！", Toast.LENGTH_SHORT).show();
						return;
					}
					//新增操作
					Log.d(TAG+"添加电子围栏", "添加电子围栏操作");
					myElectronicFence = new MyElectronicFence(drawablePaht, title, Integer.valueOf(userID), choose_id, choose_name, address, Latitude, Longitude, r, 0, is_In, is_Out);
					myElectronicFence.setInoutFlag(0);
					boolean tmep =myElectronicFence.SavaMyElectronicFence(myElectronicFence, AddElectronicFenceActivity.this);
					if(tmep){
						Toast.makeText(AddElectronicFenceActivity.this, "添加电子围栏成功！", Toast.LENGTH_SHORT).show();
						// 通知服务 更新 检查内容
						if(null != electronicFenceService) {
							electronicFenceService.updateData();
						}
						Log.d(TAG+"添加电子围栏", "添加电子围栏成功");
						AddElectronicFenceActivity.this.finish();
					}else{
						Toast.makeText(AddElectronicFenceActivity.this, "添加电子围栏失败，请重新再试！", Toast.LENGTH_SHORT).show();
					}

				}else{
					//修改操作
					choose = choose_id.size();
					if(choose == 0 ){
						Toast.makeText(AddElectronicFenceActivity.this, "请选择关心对象！", Toast.LENGTH_SHORT).show();
						return;
					}
					if(is_In == false && is_Out ==false){
						Toast.makeText(AddElectronicFenceActivity.this, "请选择提醒类型！", Toast.LENGTH_SHORT).show();
						return;
					}
					//获取该围栏的参数
					int id = myElectronicFence.getID();
					if(is_delete){
						//删除围栏截图
						File file = new File(myElectronicFence.getDrawablePaht());
						file.delete();
					}else{
						drawablePaht = myElectronicFence.getDrawablePaht();
					}
					userID = Integer.toString(Tools.getApplication(AddElectronicFenceActivity.this).getMyInfo().getUserId());	
					int r = myElectronicFence.getR();
					int k = MyLocationRemind.QueryRemind(myElectronicFence.getID(), AddElectronicFenceActivity.this).size();	

					myElectronicFence = new MyElectronicFence(id,drawablePaht, title, Integer.valueOf(userID), choose_id, choose_name, address, Latitude, Longitude, r, k, is_In, is_Out);
					myElectronicFence.setInoutFlag(0);
					boolean tmep =MyElectronicFence.UpdateElectronicFence(myElectronicFence, AddElectronicFenceActivity.this);
					
					if(tmep){
						Toast.makeText(AddElectronicFenceActivity.this, "修改电子围栏成功！", Toast.LENGTH_SHORT).show();
						// 通知服务 更新 检查内容
						if(null != electronicFenceService) {
							electronicFenceService.updateData();
						}
						Log.d(TAG+"修改电子围栏", "修改电子围栏成功");
						AddElectronicFenceActivity.this.finish();
					}else{
						Toast.makeText(AddElectronicFenceActivity.this, "修改电子围栏失败，请重新再试！", Toast.LENGTH_SHORT).show();
					}

				}				
			}
		});

		//设置提醒标题
		setTitle = (RelativeLayout) findViewById(R.id.setTitle);//绑定点击控件
		title_String = (TextView) findViewById(R.id.title_String);//绑定显示控件
		if(isAddEF_Flag == 1){
			//修改操作
			title = myElectronicFence.getTitle();
		}else{
			//新增操作，默认提醒标题为“我的位置提醒”
			title = "我的位置提醒";
		}
		title_String.setText(title);
		setTitle.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {		
				title = title_String.getText().toString();
				intent = new Intent();
				intent.setClass(AddElectronicFenceActivity.this,Set_Electronic_fence_TitleActivity.class);
				intent.putExtra("title", title);
				startActivity(intent);		
			}
		});

		//选择关心对象
		setPerson = (RelativeLayout) findViewById(R.id.setPerson);//绑定点击控件
		person_String = (TextView) findViewById(R.id.person_String);//绑定显示控件
		if(isAddEF_Flag  == 1){
			//修改操作
			choose_id = myElectronicFence.getMonitoredPersonIDs();
			choose_name = myElectronicFence.getMonitoredPersonNames();
			StringBuffer temp_monitoredPersonNames =new StringBuffer();
			for(int i=0;i<myElectronicFence.getMonitoredPersonNames().size();i++){
				temp_monitoredPersonNames.append(myElectronicFence.getMonitoredPersonNames().get(i).toString()).append(",");
			}
			person = temp_monitoredPersonNames.substring(0, temp_monitoredPersonNames.length()-1);
		}else{
			//新增操作
			choose_id = null;
			choose_name = null;
			person = "请选择关心对象";
		}
		person_String.setText(person);
		setPerson.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				/*
				 * 获取好友列表，异步返回结果
				 */
				userID = Integer.toString(Tools.getApplication(AddElectronicFenceActivity.this).getMyInfo().getUserId());
				NetClient nc = new NetClient();
				nc.setOnNetTranListener(AddElectronicFenceActivity.this);
				nc.getRelationList(888,userID);
				//显示正在加载
				progressDialog = ProgressDialog.show(AddElectronicFenceActivity.this, "请稍等...", "获取数据中...", true,false);
			}
		});

		//设置中心点
		setPoint = (RelativeLayout) findViewById(R.id.setPoint);//绑定点击控件
		point_String =(TextView) findViewById(R.id.point_String);//绑定显示控件
		if(isAddEF_Flag == 1){
			//修改操作
			Latitude = myElectronicFence.getLatitude();
			Longitude = myElectronicFence.getLongitude();
			address =  myElectronicFence.getAddress();
			point_String.setText(address);
			flag=1;
			is_delete = false;
		}else{
			if(flag==0){
				//获取用户位置信息
				int size =LashouService.locationList.size();
				if(size==0){
					Log.e(this, "我的位置列表信息是");
				}else {
                    MyLocation my_Location_temp;
                    my_Location_temp = LashouService.locationList.get(size - 1);
                    Latitude = my_Location_temp.getLatitude();
                    Longitude = my_Location_temp.getLongitude();
                    address = my_Location_temp.getAddress();
                    flag = 1;
                }
			}
		}
		setPoint.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Log.d("flag", Integer.toString(flag));
				intent = new Intent();
				intent.putExtra("r", r);
				intent.putExtra("Latitude", Latitude);
				intent.putExtra("Longitude", Longitude);
				Log.d("setPoint", "La:"+Double.toString(Latitude)+"Lo"+Double.toString(Longitude));
				intent.setClass(AddElectronicFenceActivity.this,Set_Electronic_fence_PointActivity.class);
				startActivity(intent);		
				is_set_map = true;
			}
		});

		//设置半径
		setR = (RelativeLayout) findViewById(R.id.setR);
		r_String = (TextView) findViewById(R.id.r_String);
		if(isAddEF_Flag == 1){
			//修改操作
			int temp_r = myElectronicFence.getR();
			if(temp_r == 100){
				r_String.setText("100米");
				r=100;
				i=0;
			}else if(temp_r == 200){
				r_String.setText("200米");
				r=200;
				i=1;
			}else if(temp_r == 500){
				r_String.setText("500米");
				r=500;
				i=2;
			}else if(temp_r == 1000){
				r_String.setText("一公里");
				r=1000;
				i=3;
			}else if(temp_r == 2000){
				r_String.setText("2公里");
				r=2000;
				i=4;
			}else if(temp_r == 5000){
				r_String.setText("5公里");
				r=5000;
				i=5;
			}else if(temp_r == 10000){
				r_String.setText("10公里");
				r=10000;
				i=6;
			}
		}else{
			r = 500;//默认半径500M
			i = 2;//默认选项2
		}
		setR.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Dialog dialog = new  AlertDialog.Builder(AddElectronicFenceActivity.this)
				.setSingleChoiceItems(
						new String[] { "100米", "200米","500米","1公里","2公里","5公里","10公里" }, i,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
								case 0:
									r_String.setText("100米");
									r=100;
									i=0;
									dialog.dismiss();
									break;
								case 1:
									r_String.setText("200米");
									r=200;
									i=1;
									dialog.dismiss();
									break;
								case 2:
									r_String.setText("500米");
									r=500;
									i=2;
									dialog.dismiss();
									break;
								case 3:
									r_String.setText("1公里");
									r=1000;
									i=3;
									dialog.dismiss();
									break;
								case 4:
									r_String.setText("2公里");
									r=2000;
									i=4;
									dialog.dismiss();
									break;
								case 5:
									r_String.setText("5公里");
									r=5000;
									i=5;
									dialog.dismiss();
									break;
								default:
									r_String.setText("10公里");
									r=10000;
									i=6;
									dialog.dismiss();
									break;
								}
							}
						})
						.create();
				dialog.show();
			}
		});



		//进入提醒开关
		wiperSwitch_enter = (WiperSwitch) findViewById(R.id.activity_add_electronic_linearLayout_03_wiperSwitch_enter);

		if(isAddEF_Flag == 1){
			//修改操作
			if(myElectronicFence.getIs_In()){
				wiperSwitch_enter.setImage(R.drawable.on, R.drawable.off);
			}else{
				wiperSwitch_enter.setImage(R.drawable.off, R.drawable.on);
			}
			is_In = myElectronicFence.getIs_In();
		}else{
			wiperSwitch_enter.setImage(R.drawable.on, R.drawable.off);
		}
		wiperSwitch_enter.setOnChangedListener(new OnChangedListener() {			
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				if(checkState == true){
					is_In = false;
				}
				if(checkState == false){
					is_In = true;
				}

			}
		});


		//离开提醒开关
		wiperSwitch_leave = (WiperSwitch) findViewById(R.id.activity_add_electronic_linearLayout_03_wiperSwitch_leave);
		if(isAddEF_Flag == 1){
			//修改操作
			if(myElectronicFence.getIs_Out()){
				wiperSwitch_leave.setImage(R.drawable.on, R.drawable.off);
			}else{
				wiperSwitch_leave.setImage(R.drawable.off, R.drawable.on);
			}
			is_Out = myElectronicFence.getIs_Out();
		}else{
			wiperSwitch_leave.setImage(R.drawable.on, R.drawable.off);
		}
		wiperSwitch_leave.setOnChangedListener(new OnChangedListener() {			
			@Override
			public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
				if(checkState == true){
					is_Out = false;
				}
				if(checkState == false){
					is_Out = true;
				}

			}
		});


		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 0){
					title_String.setText(msg.obj.toString());
					title = title_String.getText().toString();
				}
				if(msg.what == 1){
					double temp[] = new double[2];
					temp = (double[]) msg.obj;
					Latitude = temp[0];
					Longitude = temp[1];
					Log.d("Handler", "La:"+Double.toString(Latitude)+"Lo"+Double.toString(Longitude));
					point_String.setText(address);
				}
				if(msg.what == 2){
					drawablePaht = msg.obj.toString();
					Log.d(TAG+"drawablePaht", drawablePaht);
				}

				super.handleMessage(msg);
			}
		};
	}//onCreate()


	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		progressDialog.dismiss();
		if(requestCode == 888){
			//返回好友数据
			if(data.getResponseStatus().equals("OK")){
				//有返回结果
				if(data.getRelationDataList().size()!=0){
					//结果有效
					rd = data;
					if(isAddEF_Flag != 1){
						//新增操作，默认选择第一个好友为关心对象
						if(is_First){
							choose_id = new ArrayList<Integer>();
							choose_id.add(rd.getRelationDataList().get(0).getFriendId().intValue());
							is_First = false;
						}
					}
					//用于记录用户选择好友的名字
					temp_choose_name = new String[rd.getRelationDataList().size()];

					//保存choose_id，用于用户点击取消时使用
					tmep_Echoose_id = new int[choose_id.size()]; 
					for(int i=0;i<choose_id.size();i++){
						tmep_Echoose_id[i] = choose_id.get(i);
					}

					//获取好友信息
					List<RelationData> relationDataList =  rd.getRelationDataList();

					//生成动态数组，加入数据  
					listItem = new ArrayList<HashMap<String, String>>();  

					for(int i=0;i<rd.getRelationDataList().size();i++)  
					{  
						temp_choose_name[i] = new String("");
						RelationData relationData = relationDataList.get(i);
						UserData userData = relationData.getFriendData();
						if(rd.getRelationDataList().get(i).getAuth().intValue() == 3){//3为已向我公开位置的好友，1为false
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("img_url","http://115.29.172.16/lashouserver/dl0101A.do?d="+Integer.toString(userData.getUserId()));  
							map.put("name",userData.getNick());  
							map.put("ID",Integer.toString(userData.getUserId()));
							listItem.add(map);  
						}
					}  
					if(listItem.size() == 0){
						//没有对我公开位置的好友
						Toast.makeText(AddElectronicFenceActivity.this, "没有对我公开位置的家人！", Toast.LENGTH_SHORT).show();
						return;
					}
					//生成适配器
					myAdapter = new SetpersonAdapter(listItem,AddElectronicFenceActivity.this);

					listView= new ListView(AddElectronicFenceActivity.this);
					listView.setAdapter(myAdapter);

					//初始化弹出窗口
					choose = 0;
					person = "请选择关心对象";
					for(int i=0;i<listItem.size();i++){					
						//将用户已选择的ID标记出来
						String temp = listItem.get(i).get("ID").toString();
						for(int j=0;j<choose_id.size();j++){
							if(temp.equals(choose_id.get(j).toString())){
								SetpersonAdapter.getIsSelected().put(i, true);
								choose++;	
//								temp_choose_name[i] =  listItem.get(i).get("name")+",";
								temp_choose_name[i] =  listItem.get(i).get("name");
							}
						}

					}
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							if(SetpersonAdapter.getIsSelected().get(arg2)){
								SetpersonAdapter.getIsSelected().put(arg2, false);
								//在choose_id中删除ID								
								choose_id.remove(choose_id.indexOf(Integer.valueOf(listItem.get(arg2).get("ID"))));								
								//person字符串中删除name
								temp_choose_name[arg2] = "";
								choose--;
							}
							else if(!SetpersonAdapter.getIsSelected().get(arg2)){
								SetpersonAdapter.getIsSelected().put(arg2, true);
								//在choose_id中增加ID
								choose_id.add(Integer.valueOf(listItem.get(arg2).get("ID")));
								//person字符串中增加name
//								temp_choose_name[arg2] = listItem.get(arg2).get("name") + ",";
								temp_choose_name[arg2] = listItem.get(arg2).get("name");
								choose++;
							}
							myAdapter.notifyDataSetChanged();
							dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText("确定"+"("+choose+")");
						}
					});


					dialog = new  AlertDialog.Builder(AddElectronicFenceActivity.this)
					.setTitle("请选择需要关心的家人")
					.setView(listView)
					.setPositiveButton("确定"+"("+choose+")", new DialogInterface.OnClickListener() {				
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(choose == 0){
								person = "请选择关心对象";
								person_String.setText(person);
								Toast.makeText(AddElectronicFenceActivity.this, "请至少选择一位家人！", Toast.LENGTH_SHORT).show();
								return;
							}else{
								choose_name = new ArrayList<String>();
								person = "";
								for(int i=0;i<temp_choose_name.length;i++){
									if (null != temp_choose_name[i] && temp_choose_name[i].trim().length() >0) {
									person = person+temp_choose_name[i] + ",";									
									choose_name.add(temp_choose_name[i]);
									}
								}

								if(!person.equals("")){
									person = person.substring(0, person.length()-1);
								}
								person_String.setText(person);
								is_set_person = true;
							}


						}
					})
					.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							choose_id.clear();
							for(int i=0;i<tmep_Echoose_id.length;i++){	
								choose_id.add(i, tmep_Echoose_id[i]);
								Log.d(TAG+"choose_id", Integer.toString(choose_id.get(i)));
							}
						}
					})
					.setCancelable(false)
					.create();
					dialog.show();
				}else{
					//结果无效
					Toast.makeText(AddElectronicFenceActivity.this, "没有家人信息，请先添加家人！", Toast.LENGTH_SHORT).show();
					return;
				}
			}else{
				//BAD
				Toast.makeText(AddElectronicFenceActivity.this, "获取家人列表失败，请稍后重试！", Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(this);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		electronicFenceService = ((ElectronicFenceService.LocalServer)service).getService();
	}


	@Override
	public void onServiceDisconnected(ComponentName name) {
		electronicFenceService = null;
	}
}
