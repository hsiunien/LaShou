package cn.duocool.lashou.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.R;
import cn.duocool.lashou.adapter.SetTrackAdapter;
import cn.duocool.lashou.model.MyApplication;
import cn.duocool.lashou.net.client.LocationData;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.NetTranListener;
import cn.duocool.lashou.net.client.ResponseData;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 显示用户足迹的Activity 从服务器查1000条记录，筛选出用户设定的显示条数，显示出来
 * 
 * @author 杞桅
 * 
 */
public class ShowTrackActivity extends BaseActivity implements NetTranListener,
		OnItemClickListener {
	private int userID;// 被显示足迹的用户的ID
	private SharedPreferences settings;// 存放设置信息的SharedPreferences
	private int num;// 足迹显示条数
	private ProgressDialog progressDialog;

	private ListView listView;// 显示足迹时间的控件
	private List<LocationData> locationDatas;// 用户需要的足迹

	private MapView mapView;// 地图
	private MapController mapController;// 地图控制器
	private CustomItemizedOverlay overlay;// 标记物图层
	private ArrayList<OverlayItem> items;// 标记物
	private Drawable mark;// 标记物图片

	private ArrayList<GeoPoint> points;// 标记物所在点

	// 文字图层
	private Symbol textSymbol;
	private TextOverlay textOverlay;// 文字图层
	private ArrayList<TextItem> textItems;// 文字标记
	private Symbol.Color textColor;// 字体颜色
	private Symbol.Color bgClolor;// 背景颜色

	// 标记点击事件
	private Button button = null;// 位置图标点击弹出按钮
	private MapView.LayoutParams layoutParam = null;
	private ArrayList<GeoPoint> buttonPoints;// button弹出位置
	private ArrayList<String> buttonAddressStrings;// button显示地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 获取前一页面传过来的ID
		userID = getIntent().getIntExtra("userID", 0);
		if (userID == 0) {
			return;
		}

		// 获得系统时间
		Calendar sCalendar = Calendar.getInstance();
		sCalendar.setTime(new Date(System.currentTimeMillis()));
		sCalendar.add(Calendar.DATE, 1);
		Date dateMax = sCalendar.getTime();
		sCalendar.add(Calendar.DATE, -2);
		Date dateMin = sCalendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String maxTime = sdf.format(dateMax) + " 23:59:59";
		String minTime = sdf.format(dateMin) + " 00:00:01";

		// 发送请求，查询该用户的位置信息
		NetClient netClient = new NetClient();
		netClient.setOnNetTranListener(this);
		// netClient.getLocationList(888, Integer.toString(userID), "1000");
		netClient.getLocationByTime(888, Integer.toString(userID), minTime,
				maxTime);

		// 获取用户设置的足迹显示数量
		settings = getSharedPreferences("setting", 0);
		String temp_num = settings.getString("num", "30");
		num = Integer.valueOf(temp_num);

		progressDialog = ProgressDialog.show(this, "请稍等...", "正在获取家人信息...",
				true, false);

		// 初始化地图管理类
		if (MyApplication.mBMapManager == null) {
			MyApplication.mBMapManager = new BMapManager(this);
			/**
			 * 如果BMapManager没有初始化则初始化BMapManager
			 */
			MyApplication.mBMapManager.init(new MyApplication.MyGeneralListener());
		}

		// 加载视图
		setContentView(R.layout.activity_show_track);
		// 加载地图
		mapView = (MapView) findViewById(R.id.activity_show_track_bmapsView);
		mapController = mapView.getController();

		// 准备overlay图像数据，根据实情情况修复
		mark = getResources().getDrawable(R.drawable.printbg);
		mark.setBounds(0, 0, mark.getIntrinsicWidth(),
				mark.getIntrinsicHeight());

		listView = (ListView) findViewById(R.id.activity_show_track_lisview_01);
		listView.setOnItemClickListener(this);

		// 文字图层显示效果设置
		textSymbol = new Symbol();
		textColor = textSymbol.new Color();
		textColor.alpha = 255;
		textColor.red = 0;
		textColor.blue = 255;
		textColor.green = 0;

		bgClolor = textSymbol.new Color();
		bgClolor.alpha = 0;
		bgClolor.red = 255;
		bgClolor.blue = 255;
		bgClolor.green = 255;

		// button
		buttonPoints = new ArrayList<GeoPoint>();
		buttonAddressStrings = new ArrayList<String>();
	}

	@Override
	public void onTransmitted(int requestCode, ResponseData data) {
		progressDialog.dismiss();

		buttonPoints.clear();
		buttonAddressStrings.clear();

		if (requestCode == 888)// 获取好友足迹
		{
			if (data.getResponseStatus().equals("OK")) {
				if (data.getLocationDataList().size() > 0) {
					
					// 第一次筛选，如果距离太近了，就不显示了
					for (int i = data.getLocationDataList().size()-1; i >0 ; i--) {
						LocationData locationData1 = data.getLocationDataList().get(i);
						LocationData locationData2 = data.getLocationDataList().get(i-1);
						int Latitude1 = (int) (locationData1.getLatitude() * 1E6);
						int Longitude1 = (int) (locationData1.getLongitude() * 1E6);
						int Latitude2 = (int) (locationData2.getLatitude() * 1E6);
						int Longitude2 = (int) (locationData2.getLongitude() * 1E6);
						GeoPoint point1 = new GeoPoint(Latitude1,Longitude1);
						GeoPoint point2 = new GeoPoint(Latitude2,Longitude2);
						Double juli = DistanceUtil.getDistance(point1, point2);
						if (juli < 200) {
							data.getLocationDataList().remove(locationData1);
						}
					}
					
					
					locationDatas = new ArrayList<LocationData>();

					// 将获取到的好友位置信息进行筛选，留下用户需要的条数
					if (data.getLocationDataList().size() <= num) {
						// 条数不够或刚好这么多
						locationDatas = data.getLocationDataList();
					} else {
						// 做筛选，除去多余的
						for (int i = 0; i < num; i++) {
//							int k = data.getLocationDataList().size() / num;
//							LocationData locationData = data
//									.getLocationDataList().get(1 + i * k);
							LocationData locationData = data
									.getLocationDataList().get(i);
							locationDatas.add(locationData);
						}
					}
					
					// 第二次筛选  距离太近了 就不显示了
//					for (int i = locationDatas.size()-1; i >0 ; i--) {
//						int Latitude1 = (int) (locationDatas.get(i).getLatitude() * 1E6);
//						int Longitude1 = (int) (locationDatas.get(i).getLongitude() * 1E6);
//						int Latitude2 = (int) (locationDatas.get(i-1).getLatitude() * 1E6);
//						int Longitude2 = (int) (locationDatas.get(i-1).getLongitude() * 1E6);
//						GeoPoint point1 = new GeoPoint(Latitude1,Longitude1);
//						GeoPoint point2 = new GeoPoint(Latitude2,Longitude2);
//						Double juli = DistanceUtil.getDistance(point1, point2);
//						if (juli < 200) {
//							locationDatas.remove(i);
//						}
//					}
//					
//					
//					Double juli;
//					GeoPoint point1 = new GeoPoint(
//							(int) (location.getLatitude()),
//							(int) (location.getLongitude()));// 当前位置
//					GeoPoint point2 = new GeoPoint(
//							(int) (locationList.get(count - 1)
//									.getLatitude()),
//							(int) (locationList.get(count - 1)
//									.getLongitude()));// 上一个位置
//					juli = DistanceUtil.getDistance(point1, point2);
					
					SetTrackAdapter adapter = new SetTrackAdapter(this,
							locationDatas);
					listView.setAdapter(adapter);
//					
//					// 获得第一个点的坐标
//					int LatitudeOne = (int) (locationDatas.get(0).getLatitude() * 1E6);
//					int LongitudeOne = (int) (locationDatas.get(0).getLongitude() * 1E6);
//					GeoPoint GeoPointOne =  new GeoPoint(LatitudeOne, LongitudeOne);

					// 将足迹全部显示，并将地图中心移到第一个足迹处
					points = new ArrayList<GeoPoint>();
					items = new ArrayList<OverlayItem>();
					overlay = new CustomItemizedOverlay(mark, mapView);

					textOverlay = new TextOverlay(mapView);
					textItems = new ArrayList<TextItem>();
					for (int i = 0; i < locationDatas.size(); i++) {
						// 获得当前点的坐标
						String time = locationDatas.get(i).getTime().substring(5,locationDatas.get(i).getTime().length() - 5);
						int Latitude = (int) (locationDatas.get(i).getLatitude() * 1E6);
						int Longitude = (int) (locationDatas.get(i).getLongitude() * 1E6);
						points.add(new GeoPoint(Latitude, Longitude));
						items.add(new OverlayItem(points.get(i), i + "", i + ""));
						items.get(i).setAnchor((float) 0.5, (float) 0.5);
						// 文字标记
						TextItem textItem = new TextItem();
						textItem.fontColor = textColor;
						textItem.bgColor = bgClolor;
						textItem.fontSize = 18;
						textItem.text = time;
						textItem.pt = points.get(i);
						textItems.add(textItem);

						buttonPoints.add(points.get(i));
						buttonAddressStrings.add(locationDatas.get(i)
								.getAddress());
					}

					// 将IteminizedOverlay添加到MapView中
					mapView.removeView(button);
					mapView.getOverlays().clear();
					overlay.addItem(items);
					mapView.getOverlays().add(overlay);

					// 将文字Overlay加入到MapView中
					for (int i = 0; i < textItems.size(); i++) {
						textOverlay.addText(textItems.get(i));
					}
					mapView.getOverlays().add(textOverlay);

					// 设置地图中心
					mapController.setCenter(points.get(0));
					mapController.setZoom(19);

					mapView.refresh();

				} else {
					// size=0
					Toast.makeText(ShowTrackActivity.this, "您选择的家人暂无足迹，请稍后再试！",
							Toast.LENGTH_SHORT).show();
					return;
				}
			} else {
				// BAD
				Toast.makeText(ShowTrackActivity.this, "您选择的家人暂无足迹，请稍后再来！",
						Toast.LENGTH_SHORT).show();
				return;
			}

		}// 888
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// listview Item点击事件
		buttonPoints.clear();
		buttonAddressStrings.clear();

		mapView.removeView(button);
		mapView.getOverlays().clear();

		CustomItemizedOverlay itme_Overlay = new CustomItemizedOverlay(mark,
				mapView);// 标记物图层
		TextOverlay item_textOverlay = new TextOverlay(mapView);

		itme_Overlay.addItem(items.get(arg2));
		mapView.getOverlays().add(itme_Overlay);

		item_textOverlay.addText(textItems.get(arg2));
		mapView.getOverlays().add(item_textOverlay);

		mapController.setCenter(points.get(arg2));
		mapController.setZoom(16);

		buttonPoints.add(points.get(arg2));
		buttonAddressStrings.add(locationDatas.get(arg2).getAddress());

		mapView.refresh();
	}

	// 自定义Overlay，百度地图标记图层
	class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> {

		public CustomItemizedOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}

		// Itme点击事件
		protected boolean onTap(int index) {
			mapView.removeView(button);

			button = new Button(ShowTrackActivity.this);
			button.setText(buttonAddressStrings.get(index));
			button.setBackgroundResource(R.drawable.frame_big3);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (button.isShown()) {
						ShowTrackActivity.this.mapView.removeView(button);
					}
				}
			});

			// 创建布局参数
			layoutParam = new MapView.LayoutParams(
			// 控件宽,继承自ViewGroup.LayoutParams
					MapView.LayoutParams.WRAP_CONTENT,
					// 控件高,继承自ViewGroup.LayoutParams
					MapView.LayoutParams.WRAP_CONTENT,
					// 使控件固定在某个地理位置
					buttonPoints.get(index), -8, -20,
					// 控件对齐方式
					MapView.LayoutParams.BOTTOM_CENTER);
			// 添加View到MapView中
		    mapView.addView(button, layoutParam);
			return true;
		}

		public boolean onTap(GeoPoint pt, MapView mapView) {
			// 在此处理MapView的点击事件，当返回 true时
			super.onTap(pt, mapView);
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		mapView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

}
