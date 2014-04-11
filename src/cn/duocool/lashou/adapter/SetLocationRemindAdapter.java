package cn.duocool.lashou.adapter;

import java.util.ArrayList;
import cn.duocool.lashou.R;
import cn.duocool.lashou.model.MyElectronicFence;
import cn.duocool.lashou.model.MyLocationRemind;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetLocationRemindAdapter extends BaseAdapter {
	private Context context;
	private MyElectronicFence myElectronicFence;
	private ArrayList<MyLocationRemind> myLocationReminds = null;//存放围栏进出记录

	public SetLocationRemindAdapter(Context context,
			MyElectronicFence myElectronicFence,
			ArrayList<MyLocationRemind> myLocationReminds) {
		super();
		this.context = context;
		this.myElectronicFence = myElectronicFence;
		this.myLocationReminds = myLocationReminds;
	}

	@Override
	public int getCount() {
		return myLocationReminds.size();
	}

	@Override
	public Object getItem(int position) {
		return myLocationReminds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.location_remind_item, null);
		}

		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.location_remind_imageview_01);
		//isIn,是进入还是离开，0则表示是进入，1则表示是离开
		String temp  = "";
		if(myLocationReminds.get(position).getIsIn() == 0){
			imageView1.setImageResource(R.drawable.icon_in);
			temp ="进入";
		}else{
			imageView1.setImageResource(R.drawable.icon_out);
			temp ="离开";
		}
		TextView textView_name = (TextView) convertView.findViewById(R.id.location_remind_textview_01);
//		int positionId=myLocationReminds.get(position).getMonitoredPerson_ID();
		String name = myLocationReminds.get(position).getMonitoredPerson_Name();
//		int ps=myElectronicFence.getMonitoredPersonIDs().indexOf(positionId);
		if(null != name){
//		String text=myElectronicFence.getMonitoredPersonNames().get(ps);
		textView_name.setText(name);
		}
		TextView textView_msg = (TextView) convertView.findViewById(R.id.location_remind_textview_02);

		textView_msg.setText("于" + myLocationReminds.get(position).getTime()+temp+"该区域");

		return convertView;
	}

}