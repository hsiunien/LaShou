package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.mywidget.MyDialog;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.thread.LockCheckThread;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.broadcastreceiver.HomeWatcher;
import cn.duocool.lashou.broadcastreceiver.OnHomePressedListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends BaseActivity
{

	private static final String TAG = PasswordActivity.class.getName();  
    Button okButton;  
    EditText passwordEditText;  
    TextView topTip;//横线上面的字
    private boolean mFinish = false;
    String password;
    String gotoActivity;//密码输入完后跳到哪个activity
    String lockName;
    public static int screenHeight;
    public static  int screenWidth;
    String packageName;
    Button findPwd;
    public static String netControl;
    String question=null,answer=null;
    public int fromHome;
    
    public String isBackClose = null;
	public static Activity closeActivity = null;
	public String isBackCloseDialog = null;
	public static MyDialog closeDialog = null;
	
	public static LockCallBack lockCallBack = null;
	public static boolean setFlag = false;
	
	private HomeWatcher mHomeWatcher;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_passwordactivity);
		//监听home键
		mHomeWatcher = new HomeWatcher(this);  
	        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {  
	            @Override  
	            public void onHomePressed() {  
	            	if (null != isBackClose && "close".equals(isBackClose)) {
						if (null != closeActivity) {
							closeActivity.finish();
						}
					}
	            	
	            	if (null != isBackCloseDialog && "close".equals(isBackCloseDialog)) {
						if (null != closeDialog) {
							closeDialog.close();
							if (null != closeDialog) {
								closeDialog = null;
							}
						}
					}
	            	
	            	 if (lockCallBack != null) {
	  				   lockCallBack.lockDone(setFlag,false);
	  				}
	            	
	            	PasswordActivity.this.finish();  
	            }  
	            @Override  
	            public void onHomeLongPressed() {  
	                Log.e("tag", "onHomeLongPressed");  
	            }  
	        });  
	        mHomeWatcher.startWatch();  
	        //
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）  
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）  
		topTip=(TextView) findViewById(R.id.topTitle);
		passwordEditText = (EditText) findViewById(R.id.password);  
		Intent intent=getIntent();
		fromHome=intent.getIntExtra("fromHome", 0);
		  packageName=intent.getStringExtra("packageName");
		password=intent.getStringExtra("password");
		   netControl=intent.getStringExtra("netControl");
		gotoActivity=intent.getStringExtra("gotoActivity");
		lockName=intent.getStringExtra("lockName");
		
		question = intent.getStringExtra("question");
		answer = intent.getStringExtra("answer");
		
		topTip.setText("请输入"+lockName+"的密码");
//		 DataBaseHelper dbHlper=new DataBaseHelper(this);
//	        SQLiteDatabase db=dbHlper.getReadableDatabase();
//	    
//	        Cursor c=db.rawQuery("select * from locks where name=?", new String[]{lockName});
//	      if(c.moveToNext())
//	      {
//	    	  question=c.getString(c.getColumnIndex("question"));
//	    	  answer=c.getString(c.getColumnIndex("answer"));
//	      }
//	      if(db!=null)
//			{
//	    	
//			db.close();
//			}
		findPwd= (Button) findViewById(R.id.find);
		findPwd.setBackgroundColor(Color.WHITE);
		findPwd.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		findPwd.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
			
				switch (	event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					findPwd.setBackgroundResource(R.drawable.btn_default_pressed_holo_light);
					
					break;
                case MotionEvent.ACTION_UP:
                	findPwd.setBackgroundColor(Color.WHITE);
    				
                	//  Toast.makeText(PasswordActivity.this, "up", Toast.LENGTH_SHORT).show();
  					
    				if(question==null||question.equals("无"))
    				{
    					   Toast.makeText(PasswordActivity.this, "您还没有设置密码找回问题！", Toast.LENGTH_SHORT).show();
    					break;
    				}
    				
    				 final	LinearLayout setquestionLayout=(LinearLayout)PasswordActivity.this.getLayoutInflater().inflate(R.layout.applock_setquestiondialog, null);
    			        final EditText questionEt=(EditText) setquestionLayout.findViewById(R.id.question);
    			        final EditText answerET=(EditText) setquestionLayout.findViewById(R.id.answer);
    			       
    			        questionEt.setText(question);
    			        questionEt.setFocusable(false);
    			        answerET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    				  new AlertDialog.Builder(PasswordActivity.this)
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
    								
    								
    					        	 question=questionEt.getText().toString();
    					        	if( answer.equals(answerET.getText().toString()))
    					        	{
    					        		Toast.makeText(PasswordActivity.this, "请重新设置密码。", Toast.LENGTH_SHORT).show();
    									Intent intent=new Intent();
    									intent.putExtra("lockName", lockName);
    									intent.setClass(PasswordActivity.this, LockSettingActivity.class);
    									PasswordActivity.this.startActivity(intent);
    					        	}else
    					        	{
    					        		Toast.makeText(PasswordActivity.this, "答案错误！", Toast.LENGTH_SHORT).show();
    										
    					        	}
    					        	   
    							}else
    							{
    								   Toast.makeText(PasswordActivity.this, "答案不能为空", Toast.LENGTH_SHORT).show();
    								    	
    							}
    						}else
    						{
    							   Toast.makeText(PasswordActivity.this, "问题不能为空", Toast.LENGTH_SHORT).show();
    						          
    						}
    						
    					}
    				})
    				.setNegativeButton("取消", null)
    				.show();
					break;

				default:
					break;
				}
				return false;
			}
		});
		
        okButton = (Button) findViewById(R.id.ok); 
        
        okButton.setOnTouchListener(new OnTouchListener() {

		
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
		       
				switch (event.getAction()) 
				{
				case MotionEvent.ACTION_DOWN:
					okButton.setBackgroundResource(R.drawable.btn_default_pressed_holo_light);
					break;
                case MotionEvent.ACTION_UP:
                	okButton.setBackgroundResource(R.drawable.btn_default_normal_holo_light);
					
					break;
				default:
					break;
				}
				return false;
			}
		});
        okButton.setOnClickListener(new View.OnClickListener() {  
           public void onClick(View v) 
           {  
        	   Log.e("tag", "input="+passwordEditText.getText().toString());
        	   Log.e("tag", "password="+ password );
        	   if(passwordEditText.getText().toString().equals(password))
        	   {
        		   if(gotoActivity!=null)
        		   {
        			   Intent i=new Intent();
        			   if(gotoActivity.equals("LockSettingActivity"))
        			   {
        			   i.setClass(PasswordActivity.this,LockSettingActivity.class);
        			   i.putExtra("lockName", lockName);
        			   startActivity(i);
        			   }else if(gotoActivity.equals("AppLockMainActivity"))
        			   {
        				   i.setClass(PasswordActivity.this,AppLockMainActivity.class);
            			   i.putExtra("lockName", lockName);
            			   startActivity(i);
        			   }
        		   }else
        		   {
        			   if(packageName!=null)
   					{
   						Log.d("tag", "haveVerify true");
   						LockService.appList.get(LockService.getApplistIndex(packageName)).setHaveVerify(true);
   					
   					
   					
   					}
        			   if(netControl!=null)
   					{
   						
   						if(netControl.equals("wifion"))
   						{
   							 WifiManager wifiManager =(WifiManager) getSystemService(Context.WIFI_SERVICE);// ��ȡWifi���� 
   							 wifiManager.setWifiEnabled(true);
//   							 LockTask.wifiState=true;
   						}else if(netControl.equals("wifioff"))
   						{
   							WifiManager wifiManager =(WifiManager)getSystemService(Context.WIFI_SERVICE);// ��ȡWifi���� 
   							 wifiManager.setWifiEnabled(false);
//   							 LockTask.wifiState=false;
   						}else if(netControl.equals("netoff"))
   						{
//   							LockTask.toggleMobileData(PasswordActivity.this, false);
//   							LockTask.mobilenetstate=false;
   						}else if(netControl.equals("neton"))
   						{
//   							LockTask.toggleMobileData(PasswordActivity.this, true);
//   							LockTask.mobilenetstate=true;
   						}
   					} 
        			   
        			   if (lockCallBack != null) {
        				   lockCallBack.lockDone(setFlag,true);
   					}
        			   finish();
        		   }
        	   } else {
        		   Toast.makeText(PasswordActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
        	   }
        	 
           
           }  
       });
        
        Log.d(TAG, "设定现在的Activity名字："+PasswordActivity.class.getName());
		Tools.getApplication(this).setNowActivityName(PasswordActivity.class.getName());
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(gotoActivity==null)
		{
		 if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) 
		 {
			 if(fromHome==1) 
		    	{
			    Intent i=new Intent();
	    		i.setClass(this,ActivityHome.class );
	    		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK	);
	    		this.startActivity(i); 
		    	}
			 
			 if (null != isBackClose && "close".equals(isBackClose)) {
					if (null != closeActivity) {
						closeActivity.finish();
					}
					this.finish();
				}
			 
			 if (null != isBackCloseDialog && "close".equals(isBackCloseDialog)) {
					if (null != closeDialog) {
						closeDialog.close();
						if (null != closeDialog) {
							closeDialog = null;
						}
					}
					this.finish();
				}
			 
			   if (lockCallBack != null) {
				   lockCallBack.lockDone(setFlag,false);
				   this.finish();
				}
			 
		        return true;  
		 }
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onPause() 
	{
		
		if(gotoActivity!=null)
		{
    	finish();
		}
		gotoActivity=null;
		super.onPause();
	} 
	
	@Override
	protected void onResume() {
		super.onResume();
		Tools.getApplication(this).setNowActivityName(PasswordActivity.class.getName());
	}
	
}
