package cn.duocool.lashou.activity;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.utils.Tools;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 版本选择界面
 * 
 * @author xwood
 */
public class EditionSettingActivity extends BaseActivity implements OnClickListener {

	TextView editionName;
	
	SharedPreferences sp;
	RadioButton rbChild,rbParent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edition_setting);
	
		// 版本信息
		editionName = (TextView)findViewById(R.id.editionName);
		
		rbChild = (RadioButton)findViewById(R.id.rbChild);
		rbParent = (RadioButton)findViewById(R.id.rbParent);
		
//		rbChild.setOnCheckedChangeListener(this);
//		rbParent.setOnCheckedChangeListener(this);
		
//		findViewById(R.id.btnBack).setVisibility(View.GONE);
		
		
		sp = getSharedPreferences(CommDef.PREFERENCE_NAME, Context.MODE_PRIVATE);
		int ed = sp.getInt(CommDef.EDITION_KEY, -1);
		if (ed == CommDef.EDITION_CHILD) {
			rbChild.setChecked(true);
		} else if (ed == CommDef.EDITION_PARENT) {
			rbParent.setChecked(true);
		} else {
			rbChild.setChecked(true);
		}
		
		findViewById(R.id.btnOK).setOnClickListener(this);
		findViewById(R.id.btnNO).setOnClickListener(this);
		
	}
//	@Override
//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		if (rbChild.isChecked()) {
//			editionName.setText(R.string.EditionChildName);
//		}
//		if (rbParent.isChecked()) {
//			editionName.setText(R.string.EditionParentName);
//		}
//	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnOK) {
			final Editor ed =  sp.edit();
			if (rbChild.isChecked()) {
				
				final MyDialog myDialog = new MyDialog(EditionSettingActivity.this);
				myDialog.setTitle(EditionSettingActivity.this.getString(R.string.EditionTip));
				myDialog.setContent(EditionSettingActivity.this.getString(R.string.EditionTipContext));
				myDialog.setButton1(EditionSettingActivity.this.getString(R.string.EditionTipBtnOk), new OnClickListener() {
					@Override
					public void onClick(View v) { // 确定
						ed.putInt(CommDef.EDITION_KEY, CommDef.EDITION_CHILD);
						ed.commit();
						Tools.getApplication(EditionSettingActivity.this).setLashouEdition(CommDef.EDITION_CHILD);
						myDialog.close();
						
						Intent intent = new Intent(EditionSettingActivity.this,Main.class);
						startActivity(intent);
						EditionSettingActivity.this.finish();
					}
				});
				myDialog.show();
				return;
			} else {
				ed.putInt(CommDef.EDITION_KEY, CommDef.EDITION_PARENT);
				ed.commit();
				Tools.getApplication(this).setLashouEdition(CommDef.EDITION_PARENT);
								
				Intent intent = new Intent(this,Main.class);
				startActivity(intent);
				this.finish();
			}
			
		} else if (v.getId() == R.id.btnNO) {
			this.finish();
		}
	}
}
