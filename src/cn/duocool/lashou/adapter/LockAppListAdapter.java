package cn.duocool.lashou.adapter;

import java.util.List;
import cn.duocool.lashou.R;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.LockAppInfo;
import cn.duocool.lashou.mywidget.WiperSwitch;
import cn.duocool.lashou.mywidget.WiperSwitch.OnChangedListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LockAppListAdapter extends BaseAdapter {
	private final static String TAG  = LockAppListAdapter.class.getName();
	
	 private LayoutInflater mInflater;
	    private List<LockAppInfo> appList;
	    private Context context;
	    
	    public LockAppListAdapter(Context context, List<LockAppInfo> appList) {
	        this.mInflater = LayoutInflater.from(context);
	        this.appList = appList;
	        this.context=context;
	    }
	    @Override
	    public int getCount() {
	        return appList.size();
	    }
	    @Override
	    public Object getItem(int arg0) {
	        return appList.get(arg0);
	    }
	    @Override
	    public long getItemId(int arg0) {
	        return arg0;
	    }
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        
	    	convertView = mInflater.inflate(R.layout.applock_appitem, null);
	    	
	    	// new String[] {
            // "item1_imageivew", "item1_bigtv", "item1_smalltv" }, new int[] {
            // R.id.iv, R.id.bigtv, R.id.smalltv }
	    	ImageView iv = (ImageView) convertView.findViewById(R.id.iv);
	    	iv.setBackgroundDrawable(appList.get(position).getAppIcon());
	    	
	    	TextView tv = (TextView) convertView.findViewById(R.id.bigtv);
	    	tv.setText(appList.get(position).getAppName());

	        WiperSwitch wiperSwitch=(WiperSwitch) convertView.findViewById(R.id.wiperSwitch1);
	        
	       int islock= (Integer) appList.get(position).getIslock();
	       wiperSwitch.setisLock(islock==1? true:false);
	       addListener(convertView, position);
	       return convertView;
	    }
	    
	    private void addListener(View convertView,final int position) //锟斤拷蛹锟斤拷锟斤拷锟�
	    {
	    	Log.d(TAG, "addListener");
	    	final String nowPackageName = appList.get(position).getPackageName();
	    	WiperSwitch wiperSwitch=(WiperSwitch) convertView.findViewById(R.id.wiperSwitch1);
	    	wiperSwitch.setOnChangedListener(new OnChangedListener() {
					
				@Override
				public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
					DataBaseHelper dbHelper=new DataBaseHelper(context);
					SQLiteDatabase db=dbHelper.getWritableDatabase();
					
					if(checkState) {  // 由没上锁==》上锁
						appList.get(position).setIslock(1);
						
						ContentValues cv = new ContentValues(); 
					    cv.put("islock", 1);
					    db.update("appList", cv, "packageName = ?", new String[]{nowPackageName});  
					} else { // 上锁==》 没上锁
						appList.get(position).setIslock(0);
						
						ContentValues cv = new ContentValues(); 
					    cv.put("islock", 0);
					    db.update("appList", cv, "packageName = ?", new String[]{nowPackageName}); 
					}
					if(db!=null) {
						db.close();
       				}
				}
			});
             
	    }
}
