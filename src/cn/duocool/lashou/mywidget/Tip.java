package cn.duocool.lashou.mywidget;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.duocool.lashou.R;

public class Tip {
	private Context context;
	AlertDialog mDialog;
	private ImageView loadImage;
	TextView contentTv,titleTv;
	private String defTitle,defContent;
	
	public Tip(Context context) {
		this.context=context;
		Builder build=new AlertDialog.Builder(context);
		LinearLayout layout=(LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialogview, null);
		build.setView(layout);
		this.mDialog= build.create();
		this.titleTv=(TextView) layout.findViewById(R.id.titleTv);
		this.contentTv=(TextView) layout.findViewById(R.id.contentTv);
		loadImage=(ImageView) layout.findViewById(R.id.img);
		titleTv.setText("提示");
		contentTv.setText("正在加载");
		
	}
	public void show(){
		if(!mDialog.isShowing()){
			Animation animation=AnimationUtils.loadAnimation(context, R.anim.loadinganimation);
//			loadImage.setAnimation(animation);
			loadImage.startAnimation(animation);
			mDialog.show();
		}
	}
	public void setTitle(String title){
		titleTv.setText(title);
	}
	public void setContent(String content){
		contentTv.setText(content);
	}
	public void dismiss(){
		if(mDialog.isShowing()){
			mDialog.dismiss();
		}
	}

}
