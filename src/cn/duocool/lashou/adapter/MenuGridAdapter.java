package cn.duocool.lashou.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.duocool.lashou.R;
import cn.duocool.lashou.model.MenuGridItem;

public class MenuGridAdapter extends BaseAdapter {

	ImageView imageView;
	TextView textView;
	private Context context;
	private List<MenuGridItem> gridList;
	public MenuGridAdapter(Context context, List<MenuGridItem> gridList) {
		 this.context=context;
		 this.gridList=gridList; 
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gridList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return gridList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuGridItem holder;
		if (convertView == null) {
			holder = gridList.get(position);
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.menu_item, null);
			convertView.setTag(holder);
			//convertView.setPadding(15, 15, 15, 15); // ÿ��ļ��
		} else {
			holder = (MenuGridItem) convertView.getTag();
		}
		 imageView =   (ImageView) convertView.findViewById(R.id.imageIcon);
		 imageView.setBackgroundResource(holder.getImageId());
		 textView = (TextView) convertView.findViewById(R.id.title);
		 textView.setText(holder.getTitle());
		// textView.setTextColor(Color.WHITE);
	/*	 if(convertView.getLayoutParams()!=null){
			 convertView.getLayoutParams().height=convertView.getLayoutParams().width;
		 System.out.println("------- "+convertView.getLayoutParams().height);
		 }*/
		 
		return convertView;
	}

}
