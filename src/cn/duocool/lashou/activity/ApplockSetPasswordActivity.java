package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.service.LockService;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ApplockSetPasswordActivity extends BaseActivity implements OnClickListener
{
      String firstPwd;//输入的第一个密码
      Button okBt;//确定按钮
      EditText editText;//密码输入框
      TextView tip;//提示
      String lockName;//要更改密码的锁的名字
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		setContentView(R.layout.applocksetpassword);
		super.onCreate(savedInstanceState);
		firstPwd="";
		okBt=(Button) findViewById(R.id.ok);
		okBt.setOnClickListener(this);
		 okBt.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) 
				{
					
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						okBt.setBackgroundResource(R.drawable.btn_default_pressed_holo_light);
						break;
	                case MotionEvent.ACTION_UP:
	                	okBt.setBackgroundResource(R.drawable.btn_default_normal_holo_light);
						
						break;
					default:
						break;
					}
					return false;
				}
			});
		tip=(TextView) findViewById(R.id.tip);
		editText=(EditText) findViewById(R.id.password);
		Intent intent=getIntent();
		lockName=intent.getStringExtra("lockName");
	}
	@Override
	public void onClick(View v)
	{
		switch (v.getId()) {
		case R.id.ok:
			String pwd=editText.getText().toString();
			
			if(pwd.length()>=4)
			{
				if(firstPwd.equals(""))
				{
					firstPwd=pwd;
					tip.setText("请再输入一次");
					editText.setText("");
				}else
				{
					if(firstPwd.equals(pwd))
					{
				      DataBaseHelper dbHlper=new DataBaseHelper(this);
				      SQLiteDatabase db=dbHlper.getWritableDatabase();
				      ContentValues values=new ContentValues();
				      values.put("password", firstPwd);
				      values.put("passwordtype", "figure");
				      db.update("locks", values, "name=?", new String[]{lockName});
				      
				      if(db!=null)
      				{
      				db.close();
      				}
				      Toast.makeText(this, "密码设置成功！", Toast.LENGTH_SHORT).show();
//				     LockService.resetApplist();
//				     LockService.initLockInfo();
				      finish();
					}else
					{
						tip.setText("再次密码输入不匹配，请重新输入");
						firstPwd="";
					}
				}
			}else
			{
				tip.setText("密码长度须大于等于4");
			}
			break;

		default:
			break;
		}
		
	}

}
