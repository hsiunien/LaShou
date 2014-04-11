package cn.duocool.lashou.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.duocool.lashou.R;

public class StartActivity extends FragmentActivity {

	private ViewPager mViewPage;
	private CollectionPagerAdapter collection;
	private static int[] pics={R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		mViewPage = (ViewPager) findViewById(R.id.pager);
		collection=new CollectionPagerAdapter(getSupportFragmentManager());
		mViewPage.setAdapter(collection);
		//设置最后一张图片的点击事件
		 
 
		mViewPage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				curcleChange(position);
				Log.d("tesst",""+((MyFragment)collection.getItem(position)) .getClass().toString());

				if(position==pics.length){
								}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
			 
			}
		});
	}

	class CollectionPagerAdapter extends FragmentPagerAdapter {

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Object instantiateItem(View container, int position) {
			Log.d("view ", position+"  "+container.getClass().toString());
			if(position==pics.length-1){
				
			}
			return super.instantiateItem(container, position);
		}

		@Override
		public Fragment getItem(int position) {
			OnClickListener listener = null;
			 
			if(position==pics.length-1){
				listener=startHomeClick;
				
			}
			Fragment fragment = new MyFragment(position,listener);
	        Bundle args = new Bundle();
	        fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return pics.length;
		}
	}

	  OnClickListener startHomeClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			startActivity(new Intent(getApplicationContext(), ActivityHome.class));
			finish();
//			Log.d("被点击",v.getClass().toString());
		}
	};
	public static class MyFragment extends Fragment {
		public static final String ARG_OBJECT = "object";
		private int mPosition;
		private  OnClickListener clickListener;
		public MyFragment(int position,OnClickListener listener) {
			mPosition=position;
			 this.clickListener=listener;
		}	
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView=inflater.inflate(R.layout.image_layout, null);
			ImageView v = (ImageView) rootView.findViewById(android.R.id.background);
			v.setImageResource(pics[mPosition]);
			v.setTag(mPosition);
			if(clickListener!=null){
				rootView.setOnClickListener(clickListener);
				

			}
			return rootView;
		}
	}
	
 	//滑动时两个小圈跟着变化
	private void curcleChange(int currentPage){
		LinearLayout circleChangeContainer=(LinearLayout) findViewById(R.id.circleChange);
		for (int i = 0; i < circleChangeContainer.getChildCount(); i++) {
			TextView imgv=(TextView) circleChangeContainer.getChildAt(i);
			if(i==currentPage){
				imgv.setBackgroundResource(R.drawable.circle_1);
			}else{
				imgv.setBackgroundResource(R.drawable.circle_0);				
			}
		}
	}
}
