package cn.duocool.lashou.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import cn.duocool.lashou.R;
import cn.duocool.lashou.utils.download.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetpersonAdapter extends BaseAdapter {
	// 填充数据的list
	ArrayList<HashMap<String, String>> list;
	// 用来控制CheckBox的选中状况
	private static HashMap<Integer, Boolean> isSelected;
	// 上下文
	private Context context;

	// 构造器
	public SetpersonAdapter(ArrayList<HashMap<String, String>> list, Context context) {
		this.context = context;
		this.list = list;
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initDate();
	}

	// 初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < list.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
		{
			convertView = LayoutInflater.from(context).inflate(R.layout.electronic_fence_setperson_item, null);
		}

		ImageView imageView1 = (ImageView) convertView.findViewById(R.id.setperson_imageview_01);

		ImageLoader loader=new ImageLoader(context);
		Bitmap bmp=loader.getImage(list.get(position).get("img_url").toString());
		if(bmp==null){
			loader.downloadImage(imageView1);
		}else{
			imageView1.setImageBitmap(bmp);
		}

		ImageView imageView2 = (ImageView) convertView.findViewById(R.id.setperson_imageview_02);
		if (getIsSelected().get(position))
		{
			imageView2.setImageResource(R.drawable.sele);
		} else
		{
			imageView2.setImageResource(R.drawable.seleunable);
		}

		TextView textView = (TextView) convertView.findViewById(R.id.setperson_textview);
		textView.setText(list.get(position).get("name").toString());
		return convertView;
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		SetpersonAdapter.isSelected = isSelected;
	}

}