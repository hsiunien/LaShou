package cn.duocool.lashou.adapter;

import java.util.List;

import cn.duocool.lashou.R;
import cn.duocool.lashou.net.client.LocationData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SetTrackAdapter extends BaseAdapter {

	private Context context;
	private List<LocationData> locationDatas;


	
	public SetTrackAdapter(Context context, List<LocationData> locationDatas) {
		super();
		this.context = context;
		this.locationDatas = locationDatas;
	}

	@Override
	public int getCount() {
		return locationDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return locationDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.track__item, null);
		}
		
		TextView textView = (TextView) convertView.findViewById(R.id.track_item_textview_01);
		String string = locationDatas.get(position).getTime().toString();
		string = string.substring(5, string.length()-5);
		textView.setText(string);
		return convertView;
	}



}