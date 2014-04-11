package cn.duocool.lashou.activity;

import cn.duocool.lashou.mywidget.setImagePasswordView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class SetImagePasswordActivity extends BaseActivity {
    public static int screenHeight;
    public static  int screenWidth;
    public static String backable;//从设置密码界面跳过来的时候不屏蔽返回键
    public static String gotoActivity=null;//设置完后去哪个Activity
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）  
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）  
		Intent intent=getIntent();
		String lockName=intent.getStringExtra("lockName");
		gotoActivity=intent.getStringExtra("gotoActivity");
		backable=intent.getStringExtra("backable");
		setImagePasswordView myView=new setImagePasswordView(this, this,lockName);
		setContentView(myView);
		
		  
		//Log.e("tag", "screenWidth=" + screenWidth + "; screenHeight=" + screenHeight);  
		  
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(gotoActivity==null)
		{
			  if(backable!=null&&backable.equals("1"))
			  {
				  return super.onKeyDown(keyCode, event);
			  }
			 if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
			 {  
				   
			        return true;  
			 }
			  
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onPause() {
	     finish();
		super.onPause();
	}
   
}
