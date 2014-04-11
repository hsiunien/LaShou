package cn.duocool.lashou.activity;

import cn.duocool.lashou.utils.Tools;
import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Tools.getApplication(this).setNowActivityName(this.getClass().getName());
	}
}
