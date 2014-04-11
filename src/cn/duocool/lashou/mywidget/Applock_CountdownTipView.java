package cn.duocool.lashou.mywidget;




import cn.duocool.lashou.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class Applock_CountdownTipView {
	private Context	mContext;
	public static WindowManager mWManager;		// WindowManager
	private WindowManager.LayoutParams mWMParams;	// WindowManager����
	public static  View mTableTip;
	private PopupWindow mPopuWin;
	private ServiceListener mSerLisrener;
	private View mShowView;
	private int mTag = 0;
	private int midX;
	private int midY;
	private int mOldOffsetX;
	private int mOldOffsetY;
public static	Handler handler;
	TextView showText;
	
	public Applock_CountdownTipView(Context context) {
		mContext = context;
	}
	
	public void sendMsg(String tip) {
		showText.setText(tip);
	}


	public void fun() {
		// ��������view WindowManager����
		mWManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		midX = mWManager.getDefaultDisplay().getWidth()/2-25;
		midY = mWManager.getDefaultDisplay().getHeight()/2-44;
		mTableTip = LayoutInflater.from(mContext).inflate(R.layout.ctrl_window, null);
		showText= (TextView) mTableTip.findViewById(R.id.showtext);
		mTableTip.setBackgroundColor(Color.TRANSPARENT);
		// ��������������һ����ļ�
		mTableTip.setOnTouchListener(mTouchListener);
		WindowManager wm = mWManager;
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
		mWMParams = wmParams;
		wmParams.type = 2003; // type�ǹؼ������2002��ʾϵͳ�����ڣ���Ҳ��������2003��
		wmParams.flags = 40;// �����������ɿ�
		wmParams.width = (int) mContext.getResources().getDimension(R.dimen.tip_width);
		wmParams.height =(int) mContext.getResources().getDimension(R.dimen.tip_height);
		wmParams.x=0;
		wmParams.y=-(int) mContext.getResources().getDimension(R.dimen.tip_y);
		wmParams.format = -3; // ͸��
		wm.addView(mTableTip, wmParams);// ������ص� ��WindowManager�ж���ղ����õ�ֵ
									// ֻ��addview�������ʾ��ҳ����ȥ��
		// ע�ᵽWindowManager win��Ҫ�ղ���������layout��wmParams�Ǹղ����õ�WindowManager����
		// Ч���ǽ�winע�ᵽWindowManager�в�����Ĳ�����wmParams�����ö�
//		params=new WindowManager.LayoutParams();
//		params.type = LayoutParams.TYPE_PHONE; // ����window type
//		params.format = PixelFormat.RGBA_8888; // ����ͼƬ��ʽ��Ч��Ϊ����͸��
//		params.gravity = Gravity.CENTER; // ����������Ҳ��м�
//         // ����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
//		params.x = 0;
//		params.y = 0;
//		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//// ������ڳ������
//		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//		params.height =WindowManager.LayoutParams.WRAP_CONTENT;
//		mShowView = LayoutInflater.from(mContext).inflate(R.layout.main, null);
//		Button button1 = (Button) mShowView.findViewById(R.id.button1);
//		Button button2 = (Button) mShowView.findViewById(R.id.button2);
//		Button button3 = (Button) mShowView.findViewById(R.id.button3);
//		Button button4 = (Button) mShowView.findViewById(R.id.button4);
//		button1.setOnClickListener(mClickListener);
//		button2.setOnClickListener(mClickListener);
//		button3.setOnClickListener(mClickListener);
//		button4.setOnClickListener(mClickListener);
	}
	
//	private OnClickListener mClickListener = new OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch(v.getId())
//			{
//				case R.id.button1:
//					disPopu();
//					Toast.makeText(mContext, "������1", Toast.LENGTH_SHORT).show();
//					break;
//				case R.id.button2:
//					disPopu();
//					Toast.makeText(mContext, "������2", Toast.LENGTH_SHORT).show();
//					break;
//				case R.id.button3:
//					disPopu();
//					Toast.makeText(mContext, "������3", Toast.LENGTH_SHORT).show();
//					break;
//				case R.id.button4:
//					mPopuWin.dismiss();
//					Toast.makeText(mContext, "������4", Toast.LENGTH_SHORT).show();
//					mWManager.removeView(mTableTip);
//					mSerLisrener.OnCloseService(true);
//					break;
//			}
//			
//		}
//	};
	private OnTouchListener mTouchListener = new OnTouchListener() {
		// ���|���
		float	lastX, lastY;

		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();

			float x = event.getX();
			float y = event.getY();
			
			if(mTag == 0){
			   mOldOffsetX= mWMParams.x; // ƫ��
			   mOldOffsetY = mWMParams.y; // ƫ��
			}
			
		    
			if (action == MotionEvent.ACTION_DOWN) {
				lastX = x;
				lastY = y;

			}
			else if (action == MotionEvent.ACTION_MOVE) {
				mWMParams.x += (int) (x - lastX); // ƫ��
				mWMParams.y += (int) (y - lastY); // ƫ��
				
				mTag = 1;
				mWManager.updateViewLayout(mTableTip, mWMParams);
			}

			else if (action ==  MotionEvent.ACTION_UP){
				int newOffsetX = mWMParams.x;
				int newOffsetY = mWMParams.y;					
				if(mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY){
//					mPopuWin = new PopupWindow(mShowView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//					mPopuWin.setTouchInterceptor(new OnTouchListener() {
//
//						public boolean onTouch(View v, MotionEvent event) {
//							// TODO Auto-generated method stub
//							if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//								disPopu();
//								return true;
//							}
//							return false;
//						}
//					});
//				mPopuWin.setBackgroundDrawable(new BitmapDrawable());
//				mPopuWin.setTouchable(true);
//				mPopuWin.setFocusable(true);
//				mPopuWin.setOutsideTouchable(true);
//				mPopuWin.setContentView(mShowView);
//				if(Math.abs(mOldOffsetX)>midX){
//					if(mOldOffsetX>0){
//						mOldOffsetX = midX;
//					}else{
//						mOldOffsetX = -midX;
//					}
//				}
//				if(Math.abs(mOldOffsetY)>midY){
//					if(mOldOffsetY>0){
//						mOldOffsetY = midY;
//					}else{
//						mOldOffsetY = -midY;
//					}
//				}
//				mPopuWin.setAnimationStyle(R.style.AnimationPreview);  
//				mPopuWin.setFocusable(true);  
//				mPopuWin.update();
//				mPopuWin.showAtLocation(mTableTip, Gravity.CENTER, -mOldOffsetX, -mOldOffsetY);
				}else {
					mTag = 0;
				}
			}
			return true;
		}
	};
	
	private void disPopu(){
		if(null!=mPopuWin){
			mPopuWin.dismiss();
		}
	}
	
	public interface ServiceListener{
		public void OnCloseService(boolean isClose);
	}
}
