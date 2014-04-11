package cn.duocool.lashou.mywidget;


import cn.duocool.lashou.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyDialog {
	private AlertDialog mDialog;
	private Context context;
	private ImageButton closeBt;
	private TextView titleTv,contentTv;
	private Button button1,button2;
	private Object tag;
	public MyDialog(Context context){
		this.context=context;
		LayoutInflater li=LayoutInflater.from(context);
		LinearLayout layout=   (LinearLayout) li.inflate(R.layout.widget_myalertdialog, null);
		closeBt=(ImageButton) layout.findViewById(R.id.btnClose);
		titleTv=(TextView) layout.findViewById(R.id.dialogTitle);
		contentTv=(TextView) layout.findViewById(R.id.dialogContent);
		button1=(Button) layout.findViewById(R.id.button1);
		button1.setVisibility(View.GONE);
		button2=(Button) layout.findViewById(R.id.button2);
		button2.setVisibility(View.GONE);
		mDialog=new AlertDialog.Builder(context).setView(layout).create();
		mDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
		mDialog.setCanceledOnTouchOutside(false);
		closeBt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDialog.isShowing()){
					mDialog.dismiss();
				}
			}
		});
	}
	public void setTag(Object tag){
		this.tag=tag;
	}
	public Object getTag(){
		return  tag;
	}
	public void show(){
		mDialog.show();
	}
	public void close(){
		if(mDialog.isShowing()){
			mDialog.dismiss();
		}
	}
	public  void setTitle(String title){
		titleTv.setText(title);
	}
	public void setContent(String content){
		contentTv.setText(content);
	}

	public void setcloseBtonClickListener( OnClickListener listener){
		closeBt.setOnClickListener(listener);
	}
	/**
	 * @param button1Name
	 * @param listener id为button1
	 */
	public void setButton1(String button1Name,OnClickListener listener){
		button1.setVisibility(View.VISIBLE);
		button1.setText(button1Name);
		if(listener!=null){
			button1.setOnClickListener(listener);
		}

	}
	/**
	 * @param button2Name
	 * @param listener ID	为button2
	 */
	public void setButton2(String button2Name,OnClickListener listener){
		button2.setVisibility(View.VISIBLE);
		button2.setText(button2Name);
		if(listener!=null){
			button2.setOnClickListener(listener);
		}else{
			button2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					close();
				}
			});
		}
	}


}
