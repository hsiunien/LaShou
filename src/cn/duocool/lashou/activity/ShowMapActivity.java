package cn.duocool.lashou.activity;

import java.util.ArrayList;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ShowTrackActivity.CustomItemizedOverlay;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.mywidget.TitleBar;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ShowMapActivity extends BaseActivity {
	private MapView mMapView = null; 
	private TitleBar titleBar;

	private String titlebarString;//标题栏文字
	private GeoPoint point;
	private double Latitude;//用户当前选择的位置的纬度
	private double Longitude;//用户当前选择的位置的经度
	private float zoom;//缩放级别
	private MapView.LayoutParams layoutParam = null;
	private int r;
	private static String address_showActivity;
	
	private Symbol.Color textColor;// 字体颜色
	private Symbol.Color bgClolor;// 背景颜色
	private Symbol textSymbol;

	private OverlayItem item;
	private OverlayItem item2;
	private Button button = null;//位置图标点击弹出按钮
	private String userName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		titlebarString = getIntent().getStringExtra("titlebarString")+"-地图预览";
		Latitude = getIntent().getDoubleExtra("Latitude", 0);
		Longitude = getIntent().getDoubleExtra("Longitude", 0);
		userName =  getIntent().getStringExtra("userName");
		if (null == userName) {
			userName = "";
		}
		point = new GeoPoint((int)(Latitude* 1E6), (int)(Longitude* 1E6));
		r = getIntent().getIntExtra("r", 0);
		address_showActivity = getIntent().getStringExtra("address");
		
		
		textSymbol = new Symbol();
		textColor = textSymbol.new Color();
		textColor.alpha = 255;
		textColor.red = 0;
		textColor.blue = 255;
		textColor.green = 0;

		bgClolor = textSymbol.new Color();
		bgClolor.alpha = 0;
		bgClolor.red = 0;
		bgClolor.blue = 0;
		bgClolor.green = 0;

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

		//创建地图管理对象
        if (MyApplication.mBMapManager == null) {
            MyApplication.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            MyApplication.mBMapManager.init(new MyApplication.MyGeneralListener());
        }
		//注意：请在使用setContentView前初始化BMapManager对象，否则会报错  
        
		setContentView(R.layout.activity_show_map);

		mMapView = (MapView)findViewById(R.id.activity_show_map_bmapsView);  
		MapController mMapController=mMapView.getController();  
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放  
		mMapController.setCenter(point);//设置地图中心点  
		mMapController.setZoom(zoom);//设置地图zoom级别  


		titleBar=(TitleBar) findViewById(R.id.activity_show_map_titleBar);
		titleBar.setTitle(titlebarString);
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowMapActivity.this.finish();
			}
		});

		initMarker();

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
		
		Drawable markText = getResources().getDrawable(R.drawable.printbg);
		markText.setBounds(0, 0, markText.getIntrinsicWidth(),
				markText.getIntrinsicHeight());
		
		
		//准备要添加的Overlay   
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)   
		//准备overlay图像数据，根据实情情况修复   
		Drawable mark= getResources().getDrawable(R.drawable.selectpostion);   
		mark.setBounds(0, 0, mark.getIntrinsicWidth(), mark.getIntrinsicHeight());
		//用OverlayItem准备Overlay数据    
		//使用setMarker()方法设置overlay图片,如果不设置则使用构建ItemizedOverlay时的默认设置   
		item = new OverlayItem(point,"item","item");    
		item.setAnchor((float) 0.3,(float) 0.96);
		
		// 文字标记
		TextItem textItem = new TextItem();
		textItem.fontColor = textColor;
		textItem.bgColor = bgClolor;
		textItem.fontSize = 18;
		textItem.text = "　　　　　　"+userName;
		GeoPoint pointText =  new GeoPoint((int)(Latitude* 1E6), (int)(Longitude* 1E6));
		textItem.pt = pointText;
		
		//创建IteminizedOverlay   
		OverlayTest itemOverlay = new OverlayTest(mark, mMapView);
		TextOverlay textOverlay = new TextOverlay(mMapView);
		textOverlay.addText(textItem);
		//将IteminizedOverlay添加到MapView中   

		mMapView.removeView(button);
		mMapView.getOverlays().clear();   
		mMapView.getOverlays().add(itemOverlay);
		mMapView.getOverlays().add(textOverlay);

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
		button.setText(ShowMapActivity.address_showActivity);
		button.setBackgroundResource(R.drawable.frame_big3);
		button.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(button.isShown()){
					ShowMapActivity.this.mMapView.removeView(button);
				}
			}
		});
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
				ShowMapActivity.this.mMapView.removeView(button);
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
						-10,
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
}
