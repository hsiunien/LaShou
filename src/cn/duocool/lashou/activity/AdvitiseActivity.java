package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.R.layout;
import cn.duocool.lashou.R.menu;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AdvitiseActivity extends BaseActivity implements OnClickListener,NetTranListener {

	private TextView contectTv,contentTv;
	private Button button;
	private int userId=0;
	Tip tip;
	String name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advitise);
		contectTv=(TextView) findViewById(R.id.contectTv);
		contentTv=(TextView) findViewById(R.id.contentTv);
		button=(Button) findViewById(R.id.btnAdvSub);
		if(Tools.getApplication(this).getLogin()){
			contectTv.setEnabled(false);
			userId=Tools.getApplication(this).getMyInfo().getUserId();
			name=Tools.getApplication(this).getMyInfo().getUserName();
			contectTv.setText(name);
		}
		button.setOnClickListener(this);
		tip=new Tip(this);

 	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnAdvSub:
			if(!StringUtils.isEmpty(contentTv.getText().toString())){
			NetClient nc=new NetClient();
			nc.setOnNetTranListener(this);
			tip.setTitle("提示");
			tip.setContent("正在提交...");
			tip.show();
			nc.advsSubmit(800, ""+userId, name, contentTv.getText().toString());
			}else{
			Toast.makeText(this, "内容不能为空", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
	}
	@Override
	public void onTransmitted(int requestCode, ResponseData data) {

		switch (requestCode) {
		case 800:
			if(StringUtils.equleIgnoreCase("ok",  data.getResponseStatus())){
				Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
				contentTv.setText("");
			}
			
			tip.dismiss();
			break;

		default:
			break;
		}
	}

	 
}
