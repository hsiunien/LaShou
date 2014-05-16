package cn.duocool.lashou.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.model.MyLocation;
import cn.duocool.lashou.mywidget.TitleBar;
import cn.duocool.lashou.service.LashouService;
import cn.duocool.lashou.service.LocationService;
import cn.duocool.lashou.service.UploadLocationService;
import cn.duocool.lashou.utils.Tools;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * 设定电子围栏中心点，添加电子围栏的二级页面，电子围栏的三级页面
 * @author 杞桅
 *
 */
public class Set_Electronic_fence_PointActivity extends BaseActivity implements MKMapViewListener{
	private static final String TAG = "Set_Electronic_fence_PointActivity_";//TAG  
	private static ProgressDialog progressDialog = null;

	private TitleBar titleBar;//标题栏
	private ImageView mylocation;//我的位置按钮
	private LinearLayout tips;//位置选择提示
	private ImageButton tips_clean;//提示清除按钮
	private int r;//半径

	private MapView mMapView = null;//地图View  
	private MapController mMapController;//地图Controller

	private GeoPoint point;//地理坐标点
	private double Latitude;//用户当前选择的位置的纬度
	private double Longitude;//用户当前选择的位置的经度
	private float zoom;//缩放级别
	private OverlayItem item;

	private Button button = null;//位置图标点击弹出按钮
	private MapView.LayoutParams layoutParam = null;
	private MKSearch mMKSearch;
	private String drawablePaht;//图片存放路径


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//创建地图管理对象
        if (MyApplication.mBMapManager == null) {
            MyApplication.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            MyApplication.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
		//注意：请在使用setContentView前初始化BMapManager对象，否则会报错  
        
        
		//从前一个页面获取数据
		r = getIntent().getIntExtra("r", 500);
		switch (r) {
		case 100:
			zoom = 19;
			break;
		case 200:
			zoom = 18;
			break;
		case 500:
			zoom = 17;
			break;
		case 1000:
			zoom = 16;
			break;
		case 2000:
			zoom = 15;
			break;
		case 5000:
			zoom = 13.5f;
			break;
		default:
			zoom = 12.5f;
			break;
		}

		Latitude = getIntent().getDoubleExtra("Latitude",0);
		Longitude = getIntent().getDoubleExtra("Longitude",0);

		//		address = getIntent().getStringExtra("address");

		Log.d("onCreate", "La:"+Double.toString(Latitude)+"Lo"+Double.toString(Longitude));


		// 初始化MKSearch  
		mMKSearch = new MKSearch();  
		mMKSearch.init(MyApplication.mBMapManager, new MySearchListener());  



		setContentView(R.layout.activity_set_electronic_fence_point);

		mMapView = (MapView)findViewById(R.id.set_point_bmapsView);  
		mMapController=mMapView.getController();  
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
		point =new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6));  
		//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
		mMapController.enableClick(true);//响应点击事件
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(zoom);//设置地图zoom级别  
		initMarker();

		//初始化regMapViewListener
		mMapView.regMapViewListener(MyApplication.mBMapManager, this);
		//标题栏
		titleBar = (TitleBar) findViewById(R.id.activity_set_electronic_fence_point_titleBar);
		titleBar.setLeftButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Set_Electronic_fence_PointActivity.this.finish();
			}
		});
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(Latitude==0||Longitude==0){
					Toast.makeText(getApplicationContext(), "请选择中心！", Toast.LENGTH_SHORT).show();
					return;
				}else{
					double temp[] = new double[2];
					temp[0] = Latitude;
					temp[1] = Longitude;
					Handler handler = AddElectronicFenceActivity.handler;
					handler.sendMessage(handler.obtainMessage(1,temp));
					Boolean a = false;
					a=mMapView.getCurrentMap();
					if(!a){
						Toast.makeText(getApplicationContext(), "创建围栏截图失败，请稍后再试！", Toast.LENGTH_SHORT).show();
						return;
					}
					Log.d("111111111111", "1111");

					//显示正在加载
					progressDialog = ProgressDialog.show(Set_Electronic_fence_PointActivity.this, "请稍等...", "获取数据中...", true,false);
					Log.d("onRightButtonClick", "La:"+Double.toString(Latitude)+"Lo"+Double.toString(Longitude));
				}
			}
		});

		//位置选择提示
		tips = (LinearLayout) findViewById(R.id.activity_set_electronic_fence_point_tips);
		tips_clean = (ImageButton) findViewById(R.id.tips_clean);
		tips_clean.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				tips.setVisibility(View.GONE);				
			}
		});

		//我的位置图标
		mylocation = (ImageView) findViewById(R.id.activity_set_electronic_fence_point_mylocation);
		mylocation.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//获取用户位置信息
				int size =LashouService.locationList.size();
				MyLocation my_Location_temp;
				my_Location_temp = LashouService.locationList.get(size-1);
				Latitude = my_Location_temp.getLatitude();
				Longitude = my_Location_temp.getLongitude();
				point =new GeoPoint((int)(Latitude* 1E6),(int)(Longitude* 1E6));  
				mMapController.setZoom(zoom);//设置地图zoom级别  
				initMarker();
				mMapController.setCenter(point);//设置地图中心点  		

			}
		});

		//地图点击事件监听
		mMapView.regMapTouchListner(new MKMapTouchListener() {			
			@Override
			public void onMapLongClick(GeoPoint p) {
				Latitude = p.getLatitudeE6()*1E-6;
				Longitude = p.getLongitudeE6()*1E-6;
				point = p;  
				mMapController.setZoom(zoom);//设置地图zoom级别  
				initMarker();
				mMapController.setCenter(point);//设置地图中心点  		
				Log.d("onMapLongClick", "La:"+Double.toString(Latitude)+"Lo"+Double.toString(Longitude));

			}

			@Override
			public void onMapDoubleClick(GeoPoint arg0) {

			}

			@Override
			public void onMapClick(GeoPoint arg0) {

			}
		});
	}

	@Override  
	protected void onDestroy(){  
		mMapView.destroy();   
		super.onDestroy();  
	}  
	@Override  
	protected void onPause(){  
		mMapView.onPause();  
		super.onPause();  
	}  
	@Override  
	protected void onResume(){  
		mMapView.onResume();    
		super.onResume();  
	} 

	//画标记
	public void initMarker(){  	
		//准备要添加的Overlay   
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)   
		//准备overlay图像数据，根据实情情况修复   
		Drawable mark= getResources().getDrawable(R.drawable.selectpostion);   
		mark.setBounds(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight());
		//用OverlayItem准备Overlay数据    
		//使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置   
		item = new OverlayItem(point,"item","item");    
		item.setAnchor((float) 0.3,(float) 0.96);
		//创建IteminizedOverlay   
		OverlayTest itemOverlay = new OverlayTest(mark, mMapView);   
		//将IteminizedOverlay添加到MapView中   

		mMapView.removeView(button);
		mMapView.getOverlays().clear();   
		mMapView.getOverlays().add(itemOverlay);   

		//现在所有准备工作已准备好，使用以下方法管理overlay.   
		//添加overlay, 当批量添加Overlay时使用addItem(List<OverlayItem>)效率更高   
		itemOverlay.addItem(item);    

		//画圆
		GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
		mMapView.getOverlays().add(graphicsOverlay);
		//添加圆
		graphicsOverlay.setData(drawCircle());

		mMapView.refresh();   
		//删除overlay .   
		//itemOverlay.removeItem(itemOverlay.getItem(0));   
		//mMapView.refresh();   
		//清除overlay   
		// itemOverlay.removeAll();   
		// mMapView.refresh();   


		button = new Button(this);
		button.setText(AddElectronicFenceActivity.address);
		button.setBackgroundResource(R.drawable.frame_big3);
		button.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(button.isShown()){
					Set_Electronic_fence_PointActivity.this.mMapView.removeView(button);
				}
			}
		});



		HttpClient httpClient = new DefaultHttpClient();   
		HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);  

		mMKSearch.reverseGeocode(point); 
		Log.d("查询地址", "address");


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
				Set_Electronic_fence_PointActivity.this.mMapView.removeView(button);
				return true;
			}
			//在此处理item点击事件   
			if (index == 0){ 
				//				// 查询该经纬度值所对应的地址位置信息  
				//				mMKSearch.reverseGeocode(new GeoPoint ((int)(Latitude*1E6),(int)(Longitude*1E6))); 
				GeoPoint pt = new GeoPoint ((int)(Latitude*1E6),(int)(Longitude*1E6));
				//创建布局参数
				layoutParam  = new MapView.LayoutParams(
						//控件宽,继承自ViewGroup.LayoutParams
						MapView.LayoutParams.WRAP_CONTENT,
						//控件高,继承自ViewGroup.LayoutParams
						MapView.LayoutParams.WRAP_CONTENT,
						//使控件固定在某个地理位置
						pt,
						-2,
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
		// 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口   
		/*  
	        @Override  
	        protected OverlayItem createItem(int i) {  
	                return mGeoList.get(i);  
	        }  

	        @Override  
	        public int size() {  
	                return mGeoList.size();  
	        }  
		 */  
	}           


	/**
	 * 绘制圆，该圆随地图状态变化
	 * @return 圆对象
	 */
	public Graphic drawCircle() {
		//构建圆
		Geometry circleGeometry = new Geometry();

		//设置圆中心点坐标和半径
		circleGeometry.setCircle(item.getPoint(), r);
		Log.d("圆","La"+(Double.toString(point.getLatitudeE6()))+"Lo:"+(Double.toString(point.getLongitudeE6()))+"半径"+r);
		//设置样式
		Symbol circleSymbol = new Symbol();
		Symbol.Color circleColor = circleSymbol.new Color();
		circleColor.red = 255;
		circleColor.green = 0;
		circleColor.blue = 0;
		circleColor.alpha = 50;
		/*
		 * setSurface
		 * color - 颜色
		 * status - 填充状态，0表示不填充，1表示填充
		 * linewidth - 线宽,当填充状态为填充时线宽无意义
		 */
		circleSymbol.setSurface(circleColor,1,3);
		//生成Graphic对象
		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
		return circleGraphic;
	}

	/**
	 * 实现MKSearchListener接口,用于实现异步搜索服务，得到搜索结果
	 * 
	 * @author liufeng
	 */
	public class MySearchListener implements MKSearchListener {
		/**
		 * 根据经纬度搜索地址信息结果
		 * @param result 搜索结果
		 * @param iError 错误号（0表示正确返回）
		 */
		@Override
		public void onGetAddrResult(MKAddrInfo result, int iError) {
			if (result == null) {  
				return;  
			}  
			if(iError !=0){
				return;
			}
			AddElectronicFenceActivity.address = result.strAddr;
			button.setText(AddElectronicFenceActivity.address);
			mMapView.refresh();
		}

		/**
		 * 驾车路线搜索结果
		 * @param result 搜索结果
		 * @param iError 错误号（0表示正确返回）
		 */
		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
		}

		/**
		 * POI搜索结果（范围检索、城市POI检索、周边检索）
		 * @param result 搜索结果
		 * @param type 返回结果类型（11,12,21:poi列表 7:城市列表）
		 * @param iError 错误号（0表示正确返回）
		 */
		@Override
		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		}

		/**
		 * 公交换乘路线搜索结果
		 * @param result 搜索结果
		 * @param iError 错误号（0表示正确返回）
		 */
		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
		}

		/**
		 * 步行路线搜索结果
		 * @param result 搜索结果
		 * @param iError 错误号（0表示正确返回）
		 */
		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

		}
	}


	@Override
	public void onClickMapPoi(MapPoi arg0) {
	}

	@Override
	public void onGetCurrentMap(Bitmap arg0) {
		//取到系统当前时间作为图片名
		SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyyMMddHHmmss");       
		Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
		String    str    =    formatter.format(curDate); 

		Tools.savePic(arg0, str+".png");
		Log.d(TAG+"onGetCurrentMap", "创建截图成功");
		AddElectronicFenceActivity.is_delete = true;

		String path = null;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File sdPath = Environment.getExternalStorageDirectory();
			path = sdPath.getPath()+"/anxinbao/ElectronicFence/";
		}
		drawablePaht = path+str+".png";
		Log.d(TAG+"drawablePaht", drawablePaht);
		Handler handler = AddElectronicFenceActivity.handler;
		handler.sendMessage(handler.obtainMessage(2,drawablePaht));

		progressDialog.dismiss();		
		Set_Electronic_fence_PointActivity.this.finish();
	}

	@Override
	public void onMapAnimationFinish() {
	}

	@Override
	public void onMapLoadFinish() {
	}

	@Override
	public void onMapMoveFinish() {
	}
}