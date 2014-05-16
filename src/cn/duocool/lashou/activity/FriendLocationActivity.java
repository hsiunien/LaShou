package cn.duocool.lashou.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LocationService;
import cn.duocool.lashou.service.UploadLocationService;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;
import cn.duocool.lashou.net.client.LocationData;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.RelationData;
import cn.duocool.lashou.net.client.ResponseData;
import cn.duocool.lashou.net.client.UserData;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * 朋友位置Activity
 * @author 杞桅
 *
 */
public class FriendLocationActivity extends BaseActivity implements OnClickListener,NetTranListener{
	private TitleBar titleBar;//标题栏
	private MapView mMapView = null; 

	LinearLayout linearLayoutBottom;
	LinearLayout imageitem;
	private String userID;
	private ResponseData rd = null;//ResponseData，返回数据
	private static ProgressDialog progressDialog = null;
	private ArrayList<HashMap<String, String>> listItem = null;
	public NetClient netClient;


	MapController mMapController;
	BMapManager bMapManager;
	private Button button = null;//位置图标点击弹出按钮
	private double latitude;//用户当前选择的位置的纬度
	private double longitude;//用户当前选择的位置的经度
	private float zoom;//缩放级别
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem item;
	private String address = null;
	private String friendID;
	private ArrayList<Bitmap> bitmaps;//用户头像
	private BitmapDrawable  user_mark;

	private SharedPreferences location;//存放位置信息的SharedPreferences
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//创建地图管理对象
		if (MyApplication.mBMapManager == null) {
			MyApplication.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			MyApplication.mBMapManager.init(new MyApplication.MyGeneralListener());
		}

		//加载视图
		setContentView(R.layout.activity_friendlocation);

		//加载地图
		mMapView=(MapView)findViewById(R.id.bmapsView);  

		MyLocation myLocation = null;
		//获取我的位置
		if (LashouService.locationList.size() >0) {
			myLocation = LashouService.locationList.get(LashouService.locationList.size()-1);
		}
		if(myLocation ==null){
			//从本地取到用户的最后一个位置记录
			myLocation = new MyLocation();
			location = getSharedPreferences("location", 0);
			String temp= location.getString("location","116.403838,39.915181");
			String[] temp_point = temp.split(",");
			myLocation.setLatitude(Double.valueOf(temp_point[0]));
			myLocation.setLongitude(Double.valueOf(temp_point[1]));
		}


		mMapController=mMapView.getController();  
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
		GeoPoint point =new GeoPoint((int)(myLocation.getLatitude()* 1E6),(int)(myLocation.getLongitude()* 1E6));  
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
		mMapController.enableClick(true);//响应点击事件
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(16);//设置地图zoom级别 

		titleBar=(TitleBar) findViewById(R.id.activity_friendlocation_titleBar);
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new  Intent(FriendLocationActivity.this,FriendListActivity.class);
				startActivity(intent);
			}
		});
		linearLayoutBottom=(LinearLayout) findViewById(R.id.mainLinearLayout);
		userID = Integer.toString(Tools.getApplication(this).getMyInfo().getUserId());
		NetClient nc = new NetClient();
		nc.setOnNetTranListener(this);
		nc.getRelationList(888,userID);
		
		if (null == progressDialog) {
			progressDialog = ProgressDialog.show(this, "请稍等...", "正在获取家人信息...", true,false);
		}
	}

	@Override
	public void onClick(View v) 
	{
		mMapView.removeView(button);
		mMapView.getOverlays().clear();   
		int id=v.getId();
		if(id>=0&&id<listItem.size()) //获取地址
		{
			friendID = new String();
			NetClient nc = new NetClient();
			nc.setOnNetTranListener(this);
			nc.getLocationList(456, listItem.get(id).get("ID"), 1+"");
			if (null == progressDialog) {
				progressDialog = ProgressDialog.show(this, "请稍等...", "正在获取家人信息...", true,false);
			}
			friendID  =listItem.get(id).get("ID");
			user_mark= new BitmapDrawable(bitmaps.get(v.getId()));
		}
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) 
	{
		if (null != progressDialog) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if(requestCode == 888)//获取好友信息
		{
			Log.d("tag", "返回数据");
			if(data.getResponseStatus().equals("OK"))
			{
				if(data.getRelationDataList().size()!=0)
				{
					rd = data;
					//获取好友信息
					List<RelationData> relationDataList =  rd.getRelationDataList();

					//生成动态数组，加入数据  
					listItem = new ArrayList<HashMap<String, String>>();  

					for(int i=0;i<relationDataList.size();i++)  
					{

						RelationData relationData = relationDataList.get(i);
						UserData userData = relationData.getFriendData();
						if(relationDataList.get(i).getAuth().intValue() == 3){//3为已向我公开位置的好友，1为false
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("img_url", NetClient.baseEndPoint + NetClient.methods[2] + "?d="+Integer.toString(userData.getUserId()));
							//							map.put("img_url","http://115.29.172.16/lashouserver/dl0101A.do?d="+Integer.toString(userData.getUserId()));  
							map.put("name",userData.getNick());  
							map.put("ID",Integer.toString(userData.getUserId()));
							listItem.add(map);
						}
					}

					if (null == listItem || listItem.size() <= 0) {
						Toast.makeText(FriendLocationActivity.this, "没有对我公开位置的家人！", Toast.LENGTH_SHORT).show();
						return;
					}

					bitmaps = new ArrayList<Bitmap>();
					for(int i=0;i<listItem.size();i++)
					{
						ImageLoader loader=new ImageLoader(this);						
						bitmaps.add(loader.getImage(listItem.get(i).get("img_url").toString()));

						imageitem= (LinearLayout)this.getLayoutInflater().inflate(R.layout.imageitem,null);
						imageitem.setId(i);
						ImageView iv=(ImageView) imageitem.findViewById(R.id.imageView1) ;
						if(bitmaps.get(i)==null){
							loader.downloadImage(iv);
						}else{
							iv.setImageBitmap(bitmaps.get(i));
						}
						TextView tv=(TextView) imageitem.findViewById(R.id.textView1);
						tv.setText(listItem.get(i).get("name"));
						//  tv.setText("i"+i);
						linearLayoutBottom.addView(imageitem);
						imageitem.setOnClickListener(this);

					}

				}//size()!=0
				else{
					Toast.makeText(FriendLocationActivity.this, "没有家人信息，请先添加好友！", Toast.LENGTH_SHORT).show();
					return;
				}
			}//OK
			else{
				Toast.makeText(FriendLocationActivity.this, "获取家人列表失败，请稍后重试！", Toast.LENGTH_SHORT).show();
				return;
			}
		}// 888
		if(requestCode == 456)
		{
			if(data.getResponseStatus().equals("OK"))
			{

				if(data.getLocationDataList().size()!=0)
				{
					LocationData location=data.getLocationDataList().get(0);
					latitude=location.getLatitude();//纬度
					longitude=location.getLongitude();//经度
					Log.d("tag", "latitude="+latitude+"longitude="+longitude);
					GeoPoint point = new GeoPoint((int)(latitude* 1E6), (int)(longitude* 1E6));
					address = location.getAddress();
					//画标记
					initMarker(point);
					zoom = 12;
					mMapController.setZoom(zoom);
					mMapController.setCenter(point);
					mMapView.refresh();
				}else{
					//
					Toast.makeText(FriendLocationActivity.this, "暂时没有该家人位置，请稍后再试！", Toast.LENGTH_SHORT).show();
					return;
				}
			}else{
				Toast.makeText(FriendLocationActivity.this, "暂时没有该家人位置，请稍后再来吧！", Toast.LENGTH_SHORT).show();
				return;
			}

		}


	} 

	//画标记
	public void initMarker(GeoPoint point){  	
		//准备要添加的Overlay   
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)   
		//准备overlay图像数据，根据实情情况修复   
		Drawable mark= getResources().getDrawable(R.drawable.frame_small2);   
		mark.setBounds(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight());
		//用OverlayItem准备Overlay数据    
		//使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置   
		item = new OverlayItem(point,"item","item");    
		item.setAnchor((float) 0.5,(float) 0.7);
		//创建IteminizedOverlay   
		OverlayTest itemOverlay = new OverlayTest(mark, mMapView);   
		//将IteminizedOverlay添加到MapView中   


		//画好友头像

		user_mark.setBounds(0, 0, user_mark.getIntrinsicWidth(), user_mark.getIntrinsicHeight());
		OverlayTest user_Overlay = new OverlayTest(user_mark, mMapView);  
		OverlayItem user_item = new OverlayItem(point,"user_item","user_item");    
		user_item.setAnchor((float) 0.5,(float) 1);	
		user_Overlay.addItem(user_item);


		mMapView.removeView(button);
		mMapView.getOverlays().clear();   
		mMapView.getOverlays().add(itemOverlay);   
		//Log.d("test", (user_Overlay==null)+"");
		mMapView.getOverlays().add(user_Overlay);   

		//现在所有准备工作已准备好，使用以下方法管理overlay.   
		//添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高   
		itemOverlay.addItem(item);    

		mMapView.refresh();   
		//删除overlay .   
		//itemOverlay.removeItem(itemOverlay.getItem(0));   
		//mMapView.refresh();   
		//清除overlay   
		// itemOverlay.removeAll();   
		// mMapView.refresh();   


		button = new Button(this);
		button.setText(address);
		button.setBackgroundResource(R.drawable.frame_big3);
		button.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(button.isShown()){
					//将该用户ID发送给显示足迹的页面处理
					Intent intent = new Intent();
					intent.setClass(FriendLocationActivity.this, ShowTrackActivity.class);
					intent.putExtra("userID", Integer.valueOf(friendID));
					FriendLocationActivity.this.startActivity(intent);	
				}
			}
		});

	}

	/*  
	 * 要处理overlay点击事件时需要继承ItemizedOverlay  
	 * 不处理点击事件时可直接生成ItemizedOverlay.  
	 */  
	class OverlayTest extends ItemizedOverlay<OverlayItem> {   
		//用MapView构造ItemizedOverlay   
		public OverlayTest(Drawable mark,MapView mapView){   
			super(mark,mapView);   
		}   
		protected boolean onTap(int index) {   
			if(button.isShown()){
				FriendLocationActivity.this.mMapView.removeView(button);
				return true;
			}
			//在此处理item点击事件   
			if (index == 0){ 
				//				// 查询该经纬度值所对应的地址位置信息  
				//				mMKSearch.reverseGeocode(new GeoPoint ((int)(Latitude*1E6),(int)(Longitude*1E6))); 
				GeoPoint pt = new GeoPoint ((int)(latitude*1E6),(int)(longitude*1E6));
				//创建布局参数
				layoutParam  = new MapView.LayoutParams(
						//控件宽,继承自ViewGroup.LayoutParams
						MapView.LayoutParams.WRAP_CONTENT,
						//控件高,继承自ViewGroup.LayoutParams
						MapView.LayoutParams.WRAP_CONTENT,
						//使控件固定在某个地理位置
						pt,
						0,
						-100,
						//控件对齐方式
						MapView.LayoutParams.BOTTOM_CENTER);
				//添加View到MapView中
				mMapView.addView(button,layoutParam);
			}
			return true;   
		}   
		public boolean onTap(GeoPoint pt, MapView mapView){   
			//在此处理MapView的点击事件，当返回 true时   
			super.onTap(pt,mapView);   
			return false;   
		}   

	}


    @Override
    protected void onPause() {
        /**
         *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
         */
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        /**
         *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
         */
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        /**
         *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
         */
        mMapView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMapView.onRestoreInstanceState(savedInstanceState);
    }

}
