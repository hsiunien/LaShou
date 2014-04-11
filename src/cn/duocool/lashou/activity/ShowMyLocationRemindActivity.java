package cn.duocool.lashou.activity;

import java.util.ArrayList;
import cn.duocool.lashou.R;
import cn.duocool.lashou.adapter.SetLocationRemindAdapter;
import cn.duocool.lashou.model.MyElectronicFence;
import cn.duocool.lashou.model.MyLocationRemind;
import cn.duocool.lashou.mywidget.TitleBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * 显示围栏进出记录的Activity
 * @author 杞桅
 *
 */
public class ShowMyLocationRemindActivity extends BaseActivity {
	private static final String TAG = "ShowMyLocationRemindActivity";

	private MyElectronicFence myElectronicFence = null;//电子围栏

	private ArrayList<MyLocationRemind> myLocationReminds = null;//存放围栏进出记录
	private TitleBar titleBar;//标题栏
	private Intent intent;//Intent

	private ListView listview;//ListView
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_remind);

		myElectronicFence = (MyElectronicFence) getIntent().getSerializableExtra("myElectronicFence");

		titleBar = (TitleBar) findViewById(R.id.activity_location_remind_titleBar);
		//titleBar左按钮点击事件（返回）
		titleBar.setLeftButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowMyLocationRemindActivity.this.finish();
			}
		});

		//titleBar右按钮点击事件（跳转到添加电子围栏页面）
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ShowMyLocationRemindActivity.this, AddElectronicFenceActivity.class);
				Bundle mBundle = new Bundle();  
				mBundle.putSerializable("myElectronicFence",myElectronicFence);  
				intent.putExtras(mBundle);  
				ShowMyLocationRemindActivity.this.startActivity(intent);				
			}
		});

		listview = (ListView) findViewById(R.id.activity_location_remind_list_01);

		//根据围栏ID，查找该围栏的所有进出记录
		myLocationReminds = MyLocationRemind.QueryRemind(myElectronicFence.getID(), this);

		//生成适配器
		SetLocationRemindAdapter setLocationRemindAdapter = new SetLocationRemindAdapter(this, myElectronicFence, myLocationReminds);	
		listview.setAdapter(setLocationRemindAdapter);

		//Item点击事件
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				MyLocationRemind myLocationRemind = (MyLocationRemind)listview.getItemAtPosition(arg2);
				
				intent = new Intent();
				intent.setClass(ShowMyLocationRemindActivity.this, ShowMapActivity.class);
				intent.putExtra("titlebarString", myElectronicFence.getTitle());
				intent.putExtra("Latitude", myElectronicFence.getLatitude());
				intent.putExtra("userName", myLocationRemind.getMonitoredPerson_Name());
				intent.putExtra("Longitude", myElectronicFence.getLongitude());
				intent.putExtra("address", myElectronicFence.getAddress());
				intent.putExtra("r", myElectronicFence.getR());
				ShowMyLocationRemindActivity.this.startActivity(intent);	

			}
		});







	}
}
