package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import cn.duocool.lashou.R;
import cn.duocool.lashou.adapter.FriendLisgtAdapter;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.RelationData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;

/**
 * 
 * 家人列表
 * @author hsiunien
 *
 */
public class FriendListActivity extends BaseActivity implements NetTranListener,OnItemClickListener {

	private ListView listView;
	Tip tip;
	private final int  FRIENDLIST=201;
	private List<RelationData> relationList;
	// private ArrayAdapter<String> autoCompleteSource;
	//AutoCompleteTextView autoCompleteView;
	//private List<String> source;
	private FriendLisgtAdapter fla;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_list);
		//autoCompleteView=(AutoCompleteTextView) findViewById(R.id.searchEdit);
		listView=(ListView) findViewById(R.id.friendLv);
		TitleBar titleBar=(TitleBar) findViewById(R.id.titleBar);
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(getApplicationContext(), AddFriendActivity.class);
				startActivity(intent);
			}
		});
		tip=new Tip(this);
		//source=new ArrayList<String>();
		
		//autoCompleteSource=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, source);
		//autoCompleteView.setAdapter(autoCompleteSource);
		
		reflushFriend();

	}

	private void reflushFriend(){
		if(Tools.getApplication(this).getLogin()){
		NetClient nc=new NetClient();
		nc.getRelationList(FRIENDLIST, Tools.getApplication(this).getMyInfo().getUserId()+"");
		nc.setOnNetTranListener(this);
		fla=new FriendLisgtAdapter(this);
		
		listView.setAdapter(fla);
		listView.setOnItemClickListener(this);
		tip.show();
		}
	}
	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
	switch (requestCode) {
	case FRIENDLIST:
		if(StringUtils.equleIgnoreCase("ok",data.getResponseStatus())){
			//Log.d(getClass().toString(),""+data.getResponseMsg());
			relationList=data.getRelationDataList();
			fla.setFriendList(relationList);
			//source.clear();
			/*for (int i = 0; i < fla.getCount(); i++) {
				UserData userData=(UserData) fla.getItem(i);
				source.add(userData.getNick());
			}*/
			//autoCompleteSource.notifyDataSetChanged();
			fla.notifyDataSetChanged();
			tip.dismiss();
		}
		break;

	default:
		break;
	}	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 789:
			//删除好友刷新list
			 if(resultCode==777){
				 reflushFriend();
			 }
			break;

		default:
			break;
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		RelationData user=(RelationData) fla.getItem(position);
		 Intent intent=new Intent(this, FriendInfoActivity.class);
		 intent.putExtra("user", user);
		 startActivityForResult(intent, 789);
	}
	 
	 
 

}
