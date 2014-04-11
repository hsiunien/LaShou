package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.R.layout;
import cn.duocool.lashou.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class HelpActivity extends  BaseActivity{

	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		webView=(WebView) findViewById(R.id.webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/help/index.html");
	}

 
}
