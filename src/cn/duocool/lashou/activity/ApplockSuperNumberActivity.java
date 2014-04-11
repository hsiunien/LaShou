package cn.duocool.lashou.activity;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.mywidget.WiperSwitch;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Administrator
 *设置号码，可以加锁解锁程序
 */
public class ApplockSuperNumberActivity  extends BaseActivity
{
	 WiperSwitch wiperSwitch;//
	 ListView listView;
	 public  List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();// 声明列表容器
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applock_supernumber);
		listView=(ListView) findViewById(R.id.listView1);
		setListData();
		Toast.makeText(this, "发送'unlock'+'程序名'解锁程序，发送'lock'+'程序名'锁定程序", Toast.LENGTH_LONG).show();
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3)
			{

           	 AlertDialog dialog = new AlertDialog.Builder(ApplockSuperNumberActivity.this)
					.setTitle("您确定要删除？")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							String number=(String) list.get(arg2).get("number");
							if(number!=null)
							{
								DataBaseHelper databaseHelper = new DataBaseHelper(ApplockSuperNumberActivity.this);
						    	SQLiteDatabase	db = databaseHelper.getWritableDatabase();
						    	db.delete("numbers", "number=?", new String[]{number});
						    	if(db!=null)
		        				{
		        				db.close();
		        				}
						    	setListData();
							}
						}
					})
					.setNegativeButton("取消", null)
					.show();
				
			
				return false;
			}
		});
		
		
		 wiperSwitch=(WiperSwitch) findViewById(R.id.wiperSwitch1);
		wiperSwitch.setImage(R.drawable.addnumber1, R.drawable.addnumber2);
		
		
		wiperSwitch.setOnTouchListener(new OnTouchListener() 
		{
			
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					wiperSwitch.setisLock(true);
					wiperSwitch.reDraw();
					break;
                 case MotionEvent.ACTION_UP:
                	 wiperSwitch.setisLock(false);
                	 wiperSwitch.reDraw();
                	
                	 AlertDialog dialog = new AlertDialog.Builder(ApplockSuperNumberActivity.this)
						.setTitle("选择方式")
						.setItems(new String[] { "手动输入", "选择联系人" },
								new DialogInterface.OnClickListener() {

									@SuppressWarnings("deprecation")
									@Override
									public void onClick(DialogInterface dialog,int which) 
									{
								
								if (which == 0) 
								{
									final View layout = getLayoutInflater().inflate(R.layout.applock_setquestiondialog,(ViewGroup) findViewById(R.id.question));
									TextView name=(TextView) layout.findViewById(R.id.textView1);
									name.setText("姓名：");
									TextView answer=(TextView) layout.findViewById(R.id.textView2);
									answer.setText("号码：");
									new AlertDialog.Builder(ApplockSuperNumberActivity.this)
											.setTitle("请输入")
											.setView(layout)
											.setPositiveButton(
													"确定",
													new DialogInterface.OnClickListener() 
													{

														@Override
														public void onClick(DialogInterface dialog,int which) 
														{
															EditText nameET=(EditText) layout.findViewById(R.id.question);
															EditText numberET=(EditText) layout.findViewById(R.id.answer);
															
															DataBaseHelper databaseHelper = new DataBaseHelper(ApplockSuperNumberActivity.this);
													    	SQLiteDatabase	db = databaseHelper.getWritableDatabase();
															String name=nameET.getText().toString().trim();
															String number=numberET.getText().toString().trim();
															if(!name.equals("")&&!number.equals(""))
															{
																
																Cursor cursor = db.rawQuery(
																		"select * from numbers where number=?",
																		new String[] {number });
																  if(cursor.moveToNext())
																  {
																	  Toast.makeText(ApplockSuperNumberActivity.this, number+ "已经存在",
																				Toast.LENGTH_SHORT).show();
																  }else
																  {
																	  ContentValues values = new ContentValues();
																	  values.put("number", number);
																		values.put("name",name);
																		db.insert("numbers", null, values);
																		
																		Toast.makeText(ApplockSuperNumberActivity.this,
																				"号码增加成功！", Toast.LENGTH_SHORT)
																				.show();
																		setListData();
																  }
															}else
															{
																Toast.makeText(ApplockSuperNumberActivity.this, "请输填写完整！",
																	Toast.LENGTH_SHORT).show();
																return;
															}
													    	
														
															
															if(db!=null)
									        				{
									        				db.close();
									        				}
														}
													})
													.setNegativeButton("取消", null)
													.show();
									         
								}
								if(which==1)
								{
									
									Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); 
									 
									  ApplockSuperNumberActivity.this.startActivityForResult(intent, 1);
									 
								
								}

									}
								})

						.show();
					break;
				default:
					break;
				}
				
				return true;
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		 
		switch (requestCode) {
		case 1:
			 if (data == null) 
			 {
				 Log.e("tag", "data == null");
              return;
             }
			 Log.e("tag", "data 不为 null");
			 ContentResolver reContentResolverol = getContentResolver();
          Uri uri = data.getData();
          Cursor cursor = getContentResolver().query(uri, null, null, null, null);
          cursor.moveToFirst();
        String  username = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String usernumber = null;

		  
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
         
         Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
                  null,   
                  ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);  

         while (phone.moveToNext()) {  
         	 
        usernumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  

			 
          } 
         DataBaseHelper databaseHelper = new DataBaseHelper(this);
		SQLiteDatabase	db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", usernumber);
		values.put("name", username);
		Cursor cursor2 = db.rawQuery(
				"select * from numbers where number=?",
				new String[] { usernumber });
		if (usernumber!=null) 
		{
			if (cursor2.moveToFirst()) 
			{
				Toast.makeText(this, username + "已经存在",
						Toast.LENGTH_SHORT).show();
			} else 
			{
				db.insert("numbers", null, values);
				
				Toast.makeText(this,
						"号码增加成功！", Toast.LENGTH_SHORT)
						.show();
			    setListData();
			}
		} 
		if(db!=null)
		{
		db.close();
		}
			break;
		default:
			break;
		}
	       
	}

	/**
	 * 设置listView的初始数据
	 */
	public void setListData()
	{
		list.clear();
		DataBaseHelper dbHlper=new DataBaseHelper(this);
		SQLiteDatabase db=dbHlper.getReadableDatabase();
		 Cursor cursor = db.rawQuery("select * from numbers ",null);
		 while(cursor.moveToNext())
	     {
	    	HashMap<String, Object> map=new HashMap<String, Object>();
	    	map.put("name",cursor.getString(cursor.getColumnIndex("name")) );
	    	map.put("number",cursor.getString(cursor.getColumnIndex("number")) );
	   	    list.add(map);
	   	 }
		 if(db!=null)
			{
			db.close();
			}
		 SimpleAdapter simpleAdapter=new SimpleAdapter(this, list, R.layout.applick_shownumberitem, new String[]{"name","number"}, new int[]{R.id.name,R.id.number});
		 listView.setAdapter(simpleAdapter);
	}

}
