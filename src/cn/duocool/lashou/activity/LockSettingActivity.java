package cn.duocool.lashou.activity;


import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.mywidget.WiperSwitch;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockSettingActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
         RelativeLayout setName,setPassWord,setQuestion,passwordLock,imageLock;
         TitleBar titleBar;
         TextView lockNameTextView,setPasswordTextView;
         String lockName;
         SQLiteDatabase db;
         DataBaseHelper dbHelper;
          CheckBox passwordbox;
          CheckBox imageBox;
          String passwordType;//密码形式
          String question,answer;
         
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_setting);
		titleBar=(TitleBar) findViewById(R.id.titleBar1);
		lockNameTextView=(TextView) titleBar.findViewById(R.id.titleBar_title);
		Intent intent=getIntent();
		lockName=intent.getStringExtra("lockName");
		lockNameTextView.setText(lockName);
		dbHelper=new DataBaseHelper(this);
		setName=(RelativeLayout) findViewById(R.id.setName);
		passwordbox=(CheckBox) findViewById(R.id.pbox);
		imageBox=(CheckBox) findViewById(R.id.ibox);
		passwordbox.setOnCheckedChangeListener(this);
		imageBox.setOnCheckedChangeListener(this);
		
		setPassWord=(RelativeLayout) findViewById(R.id.setPassword);
		setQuestion=(RelativeLayout) findViewById(R.id.setQuestion);
		passwordLock=(RelativeLayout) findViewById(R.id.passwordLock);
		imageLock=(RelativeLayout) findViewById(R.id.imageLock);
		setPasswordTextView=(TextView) findViewById(R.id.setPasswordTextView);
		passwordLock.setOnClickListener(this);
		imageLock.setOnClickListener(this);
		setName.setOnClickListener(this);
		setPassWord.setOnClickListener(this);
		setQuestion.setOnClickListener(this);
		
		setDefaultData();
		
	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.setName:
			
			final View setNameLayout=getLayoutInflater().inflate(R.layout.applock_setlocknamedialog, null);
			final EditText et=(EditText) setNameLayout.findViewById(R.id.setlocknameeditText);
			
			et.setText(lockName);
			Dialog d=new AlertDialog.Builder(this)
		  
			.setTitle("请输入名字")
			.setView(setNameLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{ 
					
			           if(!et.getText().toString().equals(""))
			           {
			        	  db=dbHelper.getWritableDatabase();
			        	 ContentValues values=new ContentValues();
			        	 values.put("name", et.getText().toString());
			        	 db.update("locks", values, "name=?", new String[]{lockName});
			      	   //Toast.makeText(LockSettingActivity.this, et.getText().toString(), Toast.LENGTH_SHORT).show();
			      	   lockNameTextView.setText(et.getText().toString());
			      	   Toast.makeText(LockSettingActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
						
			        	 db.close();
			           }else
			           {
			        	   Toast.makeText(LockSettingActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
			           }
				}
			})
			.setNegativeButton("取消", null)
			.show();
		 
			break;
        case R.id.setPassword:
        	
			if(imageBox.isChecked())
			{
				Intent intent=new Intent();
				intent.setClass(this, SetImagePasswordActivity.class);
			intent.putExtra("lockName", lockName);
			intent.putExtra("backable", "1");
			startActivity(intent);
				
			}else if(passwordbox.isChecked())
			{
				Intent intent=new Intent();
				intent.setClass(this, ApplockSetPasswordActivity.class);
				intent.putExtra("lockName", lockName);
				intent.putExtra("backable", "1");
				startActivity(intent);
			}
			break;
        case R.id.setQuestion:
        final	LinearLayout setquestionLayout=(LinearLayout) getLayoutInflater().inflate(R.layout.applock_setquestiondialog, null);
        final EditText questionEt=(EditText) setquestionLayout.findViewById(R.id.question);
        final EditText answerET=(EditText) setquestionLayout.findViewById(R.id.answer);
        questionEt.setText(question);
        answerET.setText(answer);
        	new AlertDialog.Builder(this)
			.setTitle("请输入问题及答案")
			.setView(setquestionLayout)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					if(!questionEt.getText().toString().equals(""))
					{
						if(!answerET.getText().toString().equals(""))
						{
							
							db=dbHelper.getWritableDatabase();
							 ContentValues values=new ContentValues();
							 values.put("question", questionEt.getText().toString());
				        	 values.put("answer", answerET.getText().toString());
				        	 db.update("locks", values, "name=?", new String[]{lockName});
				        	 db.close();
				        	 question=questionEt.getText().toString();
				        	 answer=answerET.getText().toString();
				        	   Toast.makeText(LockSettingActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
							   
						}else
						{
							   Toast.makeText(LockSettingActivity.this, "答案不能为空", Toast.LENGTH_SHORT).show();
							    	
						}
					}else
					{
						   Toast.makeText(LockSettingActivity.this, "问题不能为空", Toast.LENGTH_SHORT).show();
					          
					}
					
				}
			})
			.setNegativeButton("取消", null)
			.show();
	    break;
      case R.id.passwordLock:
    	  Log.d("tag", "passwordlock");
			if(!passwordbox.isChecked())
			{
				passwordbox.setChecked(true);
				imageBox.setChecked(false);
				setPasswordTextView.setText("设置密码锁");
				   Toast.makeText(LockSettingActivity.this, "请点击下面设置密码锁，重新设置密码后生效！", Toast.LENGTH_SHORT).show();
					
			}
			break;
      case R.id.imageLock:
    	  if(!imageBox.isChecked())
			{
				passwordbox.setChecked(false);
				imageBox.setChecked(true);
				setPasswordTextView.setText("设置图形锁");
				  Toast.makeText(LockSettingActivity.this, "请点击下面设置图形锁，重新设置密码后生效！", Toast.LENGTH_SHORT).show();
					
			}
    	    break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		switch (buttonView.getId()) {
       case R.id.pbox:
			if(isChecked)
			{
				
			}
			else
			{
				
			}
			break;
        case R.id.ibox:
			
			break;

		default:
			break;
		}
	}
	/**
	 * 设置初始数据
	 */
	public void setDefaultData()
	{
		db=dbHelper.getReadableDatabase();
	Cursor c=	db.rawQuery("select * from locks where name=?", new String[]{lockName});
		if(c.moveToNext())
		{
			
			String passwordType=c.getString(c.getColumnIndex("passwordtype"));
			if(passwordType.equals("image"))
			{
				imageBox.setChecked(true);
				setPasswordTextView.setText("设置图形锁");
			}
			else if(passwordType.equals("figure"))
			{
				passwordbox.setChecked(true);
			}
			question=c.getString(c.getColumnIndex("question"));
			if(!question.equals("无"))
			{
				
				answer=c.getString(c.getColumnIndex("answer"));
			}else
			{
				question="";
			}
			
		}
	}

}
