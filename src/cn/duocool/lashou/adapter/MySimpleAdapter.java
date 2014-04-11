package cn.duocool.lashou.adapter;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ApplockSingleSetting;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.AppModel;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.mywidget.WiperSwitch.OnChangedListener;
import cn.duocool.lashou.service.LockService;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleAdapter extends BaseAdapter
{

	 private LayoutInflater mInflater;
	    private List<Map<String, Object>> list;
	    private int layoutID;
	    private String flag[];
	    private int ItemIDs[];
	    private Context context;
	    
	    public MySimpleAdapter(Context context, List<Map<String, Object>> list,
	            int layoutID, String flag[], int ItemIDs[]) {
	        this.mInflater = LayoutInflater.from(context);
	        this.list = list;
	        this.layoutID = layoutID;
	        this.flag = flag;
	        this.ItemIDs = ItemIDs;
	        this.context=context;
	    }
	    @Override
	    public int getCount() {
	        // TODO Auto-generated method stub
	        return list.size();
	    }
	    @Override
	    public Object getItem(int arg0) {
	        // TODO Auto-generated method stub
	        return 0;
	    }
	    @Override
	    public long getItemId(int arg0) {
	        // TODO Auto-generated method stub
	        return 0;
	    }
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        convertView = mInflater.inflate(layoutID, null);
	        for (int i = 0; i < flag.length; i++) 
	        {//
	            if (convertView.findViewById(ItemIDs[i]) instanceof ImageView) //锟斤拷锟矫筹拷锟斤拷图锟斤拷
	            {
	                ImageView iv = (ImageView) convertView.findViewById(ItemIDs[i]);
//	                iv.setBackgroundResource((Integer) list.get(position).get(
//	                        flag[i]));
	                iv.setBackgroundDrawable((Drawable)list.get(position).get(
	                        flag[i]));
	            } else if (convertView.findViewById(ItemIDs[i]) instanceof TextView) {
	                TextView tv = (TextView) convertView.findViewById(ItemIDs[i]);
	                tv.setText((String) list.get(position).get(flag[i]));
	            }else{
	                
	            }
	        }
	        WiperSwitch wiperSwitch=(WiperSwitch) convertView.findViewById(R.id.wiperSwitch1);
	       int islock= (Integer) list.get(position).get("islock");//锟斤拷锟絣ist锟斤拷锟斤拷锟絠slock为1锟斤拷么锟斤拷示锟斤拷锟斤拷为锟斤拷为0锟斤拷锟斤拷
	        wiperSwitch.setisLock(islock==1? true:false);
	        addListener(convertView, position);
	        return convertView;
	    }
	/**
	 * 
	 * 
	 */
	    
	    public void addListener(View convertView,int position) //锟斤拷蛹锟斤拷锟斤拷锟�
	    {
	    	//Log.d("tag", "addListener");
	    	  final int  p=position;
	    	WiperSwitch wiperSwitch=(WiperSwitch) convertView.findViewById(R.id.wiperSwitch1);//锟斤拷锟斤拷状态锟斤拷锟斤拷
	    	       wiperSwitch.setOnChangedListener(new OnChangedListener() 
	    	       {
					
					@Override
					public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) 
					{
						DataBaseHelper dbHelper=new DataBaseHelper(context);
						 SQLiteDatabase db=dbHelper.getWritableDatabase();
						 Cursor c = db.rawQuery("SELECT * FROM appList WHERE packageName = ?", new String[]{(String)list.get(p).get("packname")});
						
					   if(checkState)
					   {
						   
						   list.get(p).put("islock", 1);  
						   if(!c.moveToNext())//锟叫断帮拷锟斤拷锟角凤拷锟窖撅拷锟斤拷锟斤拷
						   {
							  
							   
						   ContentValues values=new ContentValues();
						   values.put("packageName",(String)list.get(p).get("packname"));//锟斤拷锟斤拷菘锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
						   values.put("islock", 1);
						
						   values.put("week", "请选择星期几可用");
						   values.put("limitTime", "0"); //0代表没有限制
						   values.put("lockId", 1);
						   db.insert("appList", null, values);
						   //int lockid=cursor.getInt(cursor.getColumnIndex("lockId"));
							  //把密码和密码类型搞出来，存到applist
						    int lockid=1;
							  Cursor lockCursor=db.query("locks", new String[]{"id","name","password","passwordtype"}, "id=?", new String[]{lockid+""}, null, null, null);
							  String password = null;
							  String passwordtype= null;
							  String lockName=null;
							  if(lockCursor.moveToNext())
							 {
							  password=lockCursor.getString(lockCursor.getColumnIndex("password"));
							  passwordtype=lockCursor.getString(lockCursor.getColumnIndex("passwordtype"));
							  lockName=lockCursor.getString(lockCursor.getColumnIndex("name"));
							 }
						   LockService.appList.add(new AppModel((String)list.get(p).get("packname"),new ArrayList<Integer>(),"0",password,passwordtype,lockName)); 
						  
						   }else if(c.getInt(c.getColumnIndex("islock"))==0)//锟叫断此筹拷锟斤拷锟角凤拷锟斤拷
						   {
							  int lockid=c.getInt(c.getColumnIndex("lockId"));
							  Cursor lockCursor=db.query("locks", new String[]{"id","name","password","passwordtype"}, "id=?", new String[]{lockid+""}, null, null, null);
							  String password = null;
							  String passwordtype= null;
							  String lockName=null;
							  if(lockCursor.moveToNext())
							 {
							  password=lockCursor.getString(lockCursor.getColumnIndex("password"));
							  passwordtype=lockCursor.getString(lockCursor.getColumnIndex("passwordtype"));
							  lockName=lockCursor.getString(lockCursor.getColumnIndex("name"));
							 }
							   ContentValues cv = new ContentValues(); 
						        cv.put("islock", 1);
						      
						        db.update("appList", cv, "packageName = ?", new String[]{(String)list.get(p).get("packname")});  
						       
//						        LockService.appList.add(new AppModel((String)list.get(p).get("packname"),ApplockSingleSetting.stringToInt(c.getString(c.getColumnIndex("week"))),c.getString(c.getColumnIndex("limitTime")),password,passwordtype,lockName));
						   
						   }
						 //LockService.appList.add(new AppModel((String)list.get(p).get("packname")));
						   //Log.e("tag", applicationName);
						    
					   }else
					   {
						   list.get(p).put("islock", 0);  
						   if(c.moveToNext())//锟叫断帮拷锟斤拷锟角凤拷锟窖撅拷锟斤拷锟斤拷
						{
						   if(c.getInt(c.getColumnIndex("islock"))==1)//锟窖此筹拷锟斤拷锟轿拷锟斤拷锟�
						   {
							   //Log.d("tag", "锟斤拷锟斤拷锟斤拷锟角�);
							   ContentValues cv = new ContentValues(); 
						        cv.put("islock", 0);
						        //锟斤拷锟斤拷锟斤拷锟� 
						        db.update("appList", cv, "packageName = ?", new String[]{(String)list.get(p).get("packname")});
						     int index=LockService.getApplistIndex((String)list.get(p).get("packname"));
						        if(index!=-1)
						        {
						        LockService.appList.remove(index);
						        }
						        //Log.d("tag", "锟斤拷锟斤拷锟斤拷莺锟�");
//						        for(int i=0;i<LockService.appList.size();i++)
//						 {
//							 Log.d("tag", LockService.appList.get(i).getPackageName());
//						 }
						   }
						  
					   }
					   }
					   if(db!=null)
       				{
       				db.close();
       				}
					}
				});
             
	    }

}
