package cn.duocool.lashou.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

public class MenuAdapter extends PagerAdapter {

	
	private List<View> pagers;
	private Context context;
	public MenuAdapter(Context context,List<View> pagers) {
		this.pagers=pagers;
		this.context=context;
	}
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager)container).addView(pagers.get(position));
		return pagers.get(position);
	};
	@Override
	public int getCount() {
		 
		return pagers.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		 
		return arg0==arg1;
	}

	
	
 

}
