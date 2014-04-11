package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.R.layout;
import cn.duocool.lashou.R.menu;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.utils.AppTools;
import cn.duocool.lashou.utils.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AboutAndHelperActivity extends BaseActivity {
	private final static String TAG = AboutAndHelperActivity.class.getName();
	TextView tvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_and_helper);
		
		Log.d(TAG, "设定现在的Activity名字："+AboutAndHelperActivity.class.getName());
		Tools.getApplication(this).setNowActivityName(AboutAndHelperActivity.class.getName());
	
		// 版本信息
		tvVersion = (TextView)findViewById(R.id.editionName);
		tvVersion.setText(this.getString(R.string.apk_now_version_title) + ":" + AppTools.getVersionName(this));
		// 检查更新
		findViewById(R.id.apk_update).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (Tools.checkNetWorkIsConnect(AboutAndHelperActivity.this) == 0) {
					Toast.makeText(AboutAndHelperActivity.this ,getString(R.string.apk_update_network_msg_bad), Toast.LENGTH_SHORT).show();
				} else {
					AppTools appTools  = new AppTools(AboutAndHelperActivity.this);
					// 有弹出窗口的检查
					appTools.checkUpdate.setCheckModel(AppTools.CHECK_MODE_FORG);
					appTools.checkUpdate.checkUpdateStart();
				}
			}
		});
		//帮助
		
		findViewById(R.id.softhelp).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 startActivity(new Intent(AboutAndHelperActivity.this, HelpActivity.class));
			}
		});
		 
		// 联系我们
		findViewById(R.id.contact_usAAA).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final MyDialog myDialog = new MyDialog(AboutAndHelperActivity.this);
				myDialog.setTitle(AboutAndHelperActivity.this.getString(R.string.company_title));
				myDialog.setContent(AboutAndHelperActivity.this.getString(R.string.company_name));
				myDialog.setButton1(AboutAndHelperActivity.this.getString(R.string.company_btn), new OnClickListener() {
					@Override
					public void onClick(View v) { // 确定
						myDialog.close();
					}
				});
				myDialog.show();
			}
		});
	}
 

}
