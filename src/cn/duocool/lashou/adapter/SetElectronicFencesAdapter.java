package cn.duocool.lashou.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ShowMapActivity;
import cn.duocool.lashou.model.MyElectronicFence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetElectronicFencesAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, Object>> listItem;
	private MyElectronicFence myElectronicFence = null;
	private ArrayList<MyElectronicFence> MyElectronicFences;


	public SetElectronicFencesAdapter(Context context,
			ArrayList<HashMap<String, Object>> listItem,ArrayList<MyElectronicFence> MyElectronicFences) {
		super();
		this.context = context;
		this.listItem = listItem;
		this.MyElectronicFences = MyElectronicFences;
	}

	@Override
	public int getCount() {
		return listItem.size();
	}

	@Override
	public Object getItem(int position) {
		return listItem.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.electronic_fence_item, null);
		}
		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.electronic_fence_imageview_01);
		Bitmap bmp =null;
		if (null == listItem && null == listItem.get(position) && null == listItem.get(position).get("img_pre")) {
			bmp = BitmapFactory.decodeFile(listItem.get(position).get("img_pre").toString());
		}
		if(bmp == null){
			imageView1.setImageResource(R.drawable.map);
		}else{
			imageView1.setImageBitmap(bmp);
		}
		

		imageView1.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//地图截图点击事件
				myElectronicFence = new MyElectronicFence();
				myElectronicFence = MyElectronicFences.get(position);
				Intent intent = new Intent();
				intent.setClass(context, ShowMapActivity.class);
				intent.putExtra("titlebarString", myElectronicFence.getTitle());
				intent.putExtra("Latitude", myElectronicFence.getLatitude());
				intent.putExtra("Longitude", myElectronicFence.getLongitude());
				intent.putExtra("address", myElectronicFence.getAddress());
				intent.putExtra("r", myElectronicFence.getR());
				context.startActivity(intent);	
				
			}
		});

		ImageView imageView2 = (ImageView) convertView.findViewById(R.id.electronic_fence_imageview_02);
		imageView2.setBackgroundResource(R.drawable.go);

		TextView textView1 = (TextView) convertView.findViewById(R.id.electronic_fence_textview_01);
		textView1.setText(listItem.get(position).get("title").toString());

		TextView textView2 = (TextView) convertView.findViewById(R.id.electronic_fence_textview_02);
		textView2.setText(listItem.get(position).get("msg").toString());

		TextView textView3 = (TextView) convertView.findViewById(R.id.electronic_fence_textview_03);
		textView3.setText(listItem.get(position).get("address").toString());

		return convertView;
	}

//	@Override
//	public void onClick(View v) {
//	
//	}

}
