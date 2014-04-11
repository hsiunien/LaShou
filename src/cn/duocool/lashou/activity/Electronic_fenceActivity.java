package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.duocool.lashou.R;
import cn.duocool.lashou.adapter.SetElectronicFencesAdapter;
import cn.duocool.lashou.model.MyElectronicFence;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.utils.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 电子围栏Acitivity
 * @author 杞桅
 */
public class Electronic_fenceActivity extends BaseActivity {
	private TitleBar titleBar;//标题栏
	private Intent intent;//Intent

	private ListView listview;//用于显示电子围栏的ListView
	private TextView textview;//顶部提示条,当没有电子围栏时显示，有电子围栏则不显示

	private ArrayList<MyElectronicFence> MyElectronicFences;//电子围栏List


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_electronic_fence);

		titleBar = (TitleBar) findViewById(R.id.titleBar);
		//titleBar左按钮点击事件（返回）
		titleBar.setLeftButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Electronic_fenceActivity.this.finish();
			}
		});

		//titleBar右按钮点击事件（跳转到添加电子围栏页面）
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(Electronic_fenceActivity.this,AddElectronicFenceActivity.class);
				startActivity(intent);				
			}
		});



		//用于显示电子围栏的ListView	
		listview = (ListView) findViewById(R.id.activity_electronic_fence_list_01);




		//ListView Item 点击事件
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//如果没有进出记录，跳转到电子围栏设置界面
				if(MyElectronicFences.get(arg2).getK() == 0){
					Intent intent = new Intent();
					intent.setClass(Electronic_fenceActivity.this, AddElectronicFenceActivity.class);
					Bundle mBundle = new Bundle();  
					mBundle.putSerializable("myElectronicFence",MyElectronicFences.get(arg2));  
					intent.putExtras(mBundle);  
					Electronic_fenceActivity.this.startActivity(intent);	
				}else{
					Intent intent = new Intent();
					intent.setClass(Electronic_fenceActivity.this, ShowMyLocationRemindActivity.class);
					Bundle mBundle = new Bundle();  
					mBundle.putSerializable("myElectronicFence",MyElectronicFences.get(arg2));  
					intent.putExtras(mBundle);  
					Electronic_fenceActivity.this.startActivity(intent);	
				}

			}
		});

		//ListView Item 长按
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				AlertDialog alertDialog =	new AlertDialog.Builder(Electronic_fenceActivity.this).setTitle("操作选项").setItems(
						new String[] { Electronic_fenceActivity.this.getString(R.string.eletronic_fence_delete_title) },new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								boolean temp =MyElectronicFence.DeleteElectronicFence(MyElectronicFences.get(arg2), Electronic_fenceActivity.this);
								if(temp)
								{
									Toast.makeText(Electronic_fenceActivity.this, Electronic_fenceActivity.this.getString(R.string.eletronic_fence_delete_success), Toast.LENGTH_SHORT).show();
									onResume();
								}else{
									Toast.makeText(Electronic_fenceActivity.this, Electronic_fenceActivity.this.getString(R.string.eletronic_fence_delete_bad), Toast.LENGTH_SHORT).show();
								}
							}
						}).create();
				alertDialog.show();
				return true;
			}
		});




		//顶部提示条
		textview = (TextView) findViewById(R.id.activity_electronic_fence_tips_clean);

	}//onCreate（）


	@Override
	protected void onResume() {
		super.onResume();

		//从数据库中读取已经存在的电子围栏
		MyElectronicFences = new ArrayList<MyElectronicFence>();
		MyElectronicFences = MyElectronicFence.GetElectronicFence(this);
		//生成动态数组，加入数据  
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
		for(int i=0;i<MyElectronicFences.size();i++)  
		{  

			HashMap<String, Object> map = new HashMap<String, Object>();  
			map.put("img_pre",MyElectronicFences.get(i).getDrawablePaht());  
			map.put("title",MyElectronicFences.get(i).getTitle());
			String temp = "";
			if(MyElectronicFences.get(i).getK() == 0){
				temp ="暂时还没有提醒消息";
			}else{
				temp = MyElectronicFences.get(i).getK()+"条提醒";
			}
			map.put("msg",temp);           
			map.put("address",MyElectronicFences.get(i).getAddress());  

			listItem.add(map);  
		}  
		//生成适配器的Item和动态数组对应的元素  
		SetElectronicFencesAdapter setEFAdapter = new SetElectronicFencesAdapter(this,listItem,MyElectronicFences);	
		listview.setAdapter(setEFAdapter);

		if(MyElectronicFences == null || MyElectronicFences.size() == 0){
			textview.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}else{
			listview.setVisibility(View.VISIBLE);
			textview.setVisibility(View.GONE);
		}

	}
}
