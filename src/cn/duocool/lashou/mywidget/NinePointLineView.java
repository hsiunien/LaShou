package cn.duocool.lashou.mywidget;



import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.AppLockMainActivity;

import cn.duocool.lashou.activity.ActivityHome;
import cn.duocool.lashou.activity.ImageLockActivity;
import cn.duocool.lashou.activity.LockCallBack;
import cn.duocool.lashou.activity.LockSettingActivity;
import cn.duocool.lashou.activity.PasswordActivity;
import cn.duocool.lashou.activity.SetImagePasswordActivity;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.thread.LockCheckThread;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Cap;
import android.graphics.Typeface;
import android.net.wifi.WifiManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NinePointLineView extends View {
      Context context;
      Activity activity;
    
      String question=null,answer=null;
	Paint linePaint = new Paint();
   
	Paint whiteLinePaint = new Paint();

	Paint textPaint = new Paint();
    Paint topTipPaint=new Paint();
    Paint topLine=new Paint();
	Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lockpoint);
	int defaultBitmapRadius = defaultBitmap.getWidth() / 2;

	Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.indicator_lock_area);
	int selectedBitmapDiameter = selectedBitmap.getWidth();
	int selectedBitmapRadius = selectedBitmapDiameter / 2;
	 Rect clickMeText;//找回密码的那个文字区域
	boolean clickMeTextFlag= false;
	 Boolean havepwdbackquestion;
	PointInfo[] points = new PointInfo[9];

	PointInfo startPoint = null;
    Paint forgetpwdpaint=new Paint();
	int width, height;
   boolean initpoint=false;
	int moveX, moveY;

	boolean isUp = false;

	StringBuffer lockString = new StringBuffer();

	public NinePointLineView(Context context) {
		super(context);
		this.context=context;
		this.setBackgroundColor(Color.WHITE);
		
		initPaint();
	}
	public NinePointLineView(Context context,Activity activity,String question,String answer,LockCallBack lockCallBack) {
		super(context);
		this.context=context;
		this.activity=activity;
	
		this.setBackgroundColor(Color.WHITE);
		
		initPaint();
		this.question = question;
		this.answer = answer;
//		 DataBaseHelper dbHlper=new DataBaseHelper(activity);
//	        SQLiteDatabase db=dbHlper.getReadableDatabase();
//	    
//	        Cursor c=db.rawQuery("select * from locks where name=?", new String[]{ImageLockActivity.lockName});
//	      if(c.moveToNext())
//	      {
//	    	  question=c.getString(c.getColumnIndex("question"));
//	    	  answer=c.getString(c.getColumnIndex("answer"));
//	      }
//	      if(question!=null)
//	      {
//	    	  havepwdbackquestion=true;
//	      }else
//	      {
//	    	  havepwdbackquestion=false;
//	      }
//	      if(db!=null)
//			{
//			db.close();
//			}
	}

	public NinePointLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		this.setBackgroundColor(Color.WHITE);
		initPaint();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		width = getWidth();
		height = getHeight();
		if (width != 0 && height != 0) {
			initPoints(points);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	private int startX = 0, startY = 0;

	@Override
	protected void onDraw(Canvas canvas) {

		//canvas.drawText("passwd:" + lockString, 0, 40, textPaint);

		if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) 
		{
 		drawLine(canvas, startX, startY, moveX, moveY);
		}
          
		 drawNinePoint(canvas);
         canvas.drawLine(0, ImageLockActivity.screenHeight/18*2,ImageLockActivity.screenWidth ,  ImageLockActivity.screenHeight/18*2, topLine);
        // topTipPaint.setUnderlineText(true);
         canvas.drawText("请输入"+ImageLockActivity.lockName+"的密码", ImageLockActivity.screenWidth/11, ImageLockActivity.screenHeight/17, topTipPaint);
         int temp= (int) getResources().getDimension(R.dimen.fpwd_size);
        
         canvas.drawText("忘记密码请点我", ImageLockActivity.screenWidth/2-(3*temp+temp/2), ImageLockActivity.screenHeight/18*4, forgetpwdpaint);
         
         // canvas.drawCircle(ImageLockActivity.screenWidth/2,  ImageLockActivity.screenHeight/18*4, getResources().getDimension(R.dimen.fpwd_size)*3, forgetpwdpaint);
      //  canvas.drawRect(clickMeText, forgetpwdpaint);
        
          super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		boolean flag = true;

		if (isUp) {
               
			finishDraw();

			flag = false;

		} else {
			handlingEvent(event);

			flag = true;

		}
		return flag;
	}

	private void handlingEvent(MotionEvent event) {
		if(!initpoint)
		{
			return;
		}
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(moveX, moveY) && temp.isSelected()==false) {
					temp.setSelected(true);
					if(startPoint ==null)//如果用户点击的不是第一个位置不是点的话，就要在move事件里设置startPoint
					{
						
						startPoint=temp;
					}
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					int len = lockString.length();
					if (len != 0) 
					{
						int preId = lockString.charAt(len - 1) - 48;
					//	Log.d("tag", "preId="+preId);
						
						points[preId].setNextId(temp.getId());
					//	Log.d("tag", "startPoint.getNextId()="+startPoint.getNextId());
					}
					lockString.append(temp.getId());
					break;
				}
			}
			invalidate();
			//invalidate(0, height - width, width, height);
			break;

		case MotionEvent.ACTION_DOWN:
			int downX = (int) event.getX();
			int downY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(downX, downY)) {
					temp.setSelected(true);
					
					startPoint = temp;
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					lockString.append(temp.getId());
					break;
				}
			}
			if(clickMeText.contains(downX, downY))
			{
				clickMeTextFlag = true;
				forgetpwdpaint.setUnderlineText(true);
				if(question==null||question.equals("无"))
				{
					   Toast.makeText(activity, "您还没有设置密码找回问题！", Toast.LENGTH_SHORT).show();
						  
					return;
				}
				 final	LinearLayout setquestionLayout=(LinearLayout)activity.getLayoutInflater().inflate(R.layout.applock_setquestiondialog, null);
			        final EditText questionEt=(EditText) setquestionLayout.findViewById(R.id.question);
			        final EditText answerET=(EditText) setquestionLayout.findViewById(R.id.answer);
			       
			        questionEt.setText(question);
			        questionEt.setFocusable(false);
			        answerET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				  new AlertDialog.Builder(activity)
				  .setTitle("请输入问题及答案")
				  .setView(setquestionLayout)
				  .setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						if(!questionEt.getText().toString().equals(""))
						{
							if(!answerET.getText().toString().equals(""))
							{
								
								
					        	 question=questionEt.getText().toString();
					        	if( answer.equals(answerET.getText().toString()))
					        	{
					        		Toast.makeText(activity, "请重新设置密码。", Toast.LENGTH_SHORT).show();
									Intent intent=new Intent();
									intent.putExtra("lockName", ImageLockActivity.lockName);
									intent.setClass(activity, LockSettingActivity.class);
									activity.startActivity(intent);
					        	}else
					        	{
					        		Toast.makeText(activity, "答案错误！", Toast.LENGTH_SHORT).show();
										
					        	}
					        	   
							}else
							{
								   Toast.makeText(activity, "答案不能为空", Toast.LENGTH_SHORT).show();
								    	
							}
						}else
						{
							   Toast.makeText(activity, "问题不能为空", Toast.LENGTH_SHORT).show();
						          
						}
						
					}
				})
				.setNegativeButton("取消", null)
				.show();
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			startX = startY = moveX = moveY = 0;
			isUp = true;
			forgetpwdpaint.setUnderlineText(false);
			String passwrod = ImageLockActivity.password;
			if(null !=passwrod && passwrod.trim().length() > 0 && passwrod.equals(lockString.toString()))
			{
				if(ImageLockActivity.gotoActivity!=null)
				{
				 
					if(ImageLockActivity.gotoActivity.equals("LockSettingActivity"))
			    	{
						
			    		Intent i=new Intent();
			    		i.setClass(activity,LockSettingActivity.class );
			    		i.putExtra("lockName",ImageLockActivity.lockName);
			    		context.startActivity(i);
			    		activity.finish();
			    	}else if(ImageLockActivity.gotoActivity.equals("ActivityHome"))
			    	{
			    		Intent i=new Intent();
			    		i.setClass(activity,ActivityHome.class );
			    		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			    		
			    	
			    		activity.startActivity(i);
			    		activity.finish();
			    	}
				}else
				{
					if(ImageLockActivity.packageName!=null)
					{
						Log.d("tag", "haveVerify true");
						LockService.appList.get(LockService.getApplistIndex(ImageLockActivity.packageName)).setHaveVerify(true);
					}
					if(ImageLockActivity.netControl!=null)
					{
						String netControl=ImageLockActivity.netControl;
						if(netControl.equals("wifion"))
						{
							 WifiManager wifiManager =(WifiManager) activity.getSystemService(Context.WIFI_SERVICE);// ��ȡWifi���� 
							 wifiManager.setWifiEnabled(true);
//							 LockTask.wifiState=true;
						}else if(netControl.equals("wifioff"))
						{
							WifiManager wifiManager =(WifiManager) activity.getSystemService(Context.WIFI_SERVICE);// ��ȡWifi���� 
							 wifiManager.setWifiEnabled(false);
//							 LockTask.wifiState=false;
						}else if(netControl.equals("netoff"))
						{
//							LockTask.toggleMobileData(activity, false);
//							LockTask.mobilenetstate=false;
						}else if(netControl.equals("neton"))
						{
//							LockTask.toggleMobileData(activity, true);
//							LockTask.mobilenetstate=true;
						}
					}
					
					
					if (ImageLockActivity.lockCallBack != null) {
						ImageLockActivity.lockCallBack.lockDone(ImageLockActivity.setFlag,true);
					}
					
			        activity.finish();
				}
			} else {
				// 密码错误
				Toast.makeText(activity, "密码错误", Toast.LENGTH_SHORT).show();
			}
			invalidate();
			finishDraw();
			break;
		default:
			break;
		}
	}

	private void finishDraw() {
		
		
		for (PointInfo temp : points) {
			temp.setSelected(false);
			temp.setNextId(temp.getId());
		}
		startPoint=null;
		lockString.delete(0, lockString.length());
		isUp = false;
		clickMeTextFlag = false;
		invalidate();
	}

	private void initPoints(PointInfo[] points) {

		int len = points.length;

		int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;

		int seletedX = seletedSpacing;
		int seletedY = height - width + seletedSpacing;

		int defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
		int defaultY = seletedY + selectedBitmapRadius - defaultBitmapRadius;

		for (int i = 0; i < len; i++) {
			if (i == 3 || i == 6) {
				seletedX = seletedSpacing;
				seletedY += selectedBitmapDiameter + seletedSpacing;

				defaultX = seletedX + selectedBitmapRadius
						- defaultBitmapRadius;
				defaultY += selectedBitmapDiameter + seletedSpacing;

			}
			points[i] = new PointInfo(i, defaultX, defaultY, seletedX, seletedY);

			seletedX += selectedBitmapDiameter + seletedSpacing;
			defaultX += selectedBitmapDiameter + seletedSpacing;
           initpoint=true;
		}
	}

	private void initPaint() {
		topTipPaint.setTextSize(getResources().getDimension(R.dimen.pt_size));
		topTipPaint.setARGB(244, 51, 181, 229);
		topTipPaint.setAntiAlias(true);
		
		topTipPaint.setTypeface(Typeface.MONOSPACE);
		
		forgetpwdpaint.setTextSize(getResources().getDimension(R.dimen.fpwd_size));
		forgetpwdpaint.setARGB(244, 51, 181, 229);
		//forgetpwdpaint.setTextAlign(Paint.Align.CENTER);
		forgetpwdpaint.setAntiAlias(true);
	
		forgetpwdpaint.setTypeface(Typeface.MONOSPACE);
		topLine.setARGB(244, 51, 181, 229);
		topLine.setStrokeWidth(getResources().getDimension(R.dimen.pwd_line_size));
		
		initLinePaint(linePaint);
		initTextPaint(textPaint);
		initWhiteLinePaint(whiteLinePaint);
		int temp= (int) getResources().getDimension(R.dimen.fpwd_size);
	
		clickMeText= new Rect(ImageLockActivity.screenWidth/2-temp*4, ImageLockActivity.screenHeight/18*4-temp*2,ImageLockActivity.screenWidth/2+temp*4,ImageLockActivity.screenHeight/18*4+temp);
		
	}

	/**
	 * @param paint
	 */
	private void initTextPaint(Paint paint) {
		textPaint.setTextSize(30);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.MONOSPACE);
	}

	/**
	 * @param paint
	 */
	private void initLinePaint(Paint paint) {
		paint.setColor(Color.GRAY);
		paint.setStrokeWidth(defaultBitmap.getWidth());
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);
	}

	/**
	 * @param paint
	 */
	private void initWhiteLinePaint(Paint paint) {
		paint.setARGB(105, 178, 172, 160);
		paint.setStrokeWidth(getResources().getDimension(R.dimen.pwd_line_size));
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);

	}

	/**
	 * 
	 * @param canvas
	 */
	private void drawNinePoint(Canvas canvas) {

		if (startPoint != null) {
			drawEachLine(canvas, startPoint);
		}

		for(PointInfo pointInfo : points) {
			if (pointInfo!=null) {
				
				if (pointInfo.isSelected()) {			
					canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(),pointInfo.getSeletedY(), null);
				}
				canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(),pointInfo.getDefaultY(), null);
			}
		}

	}

	/**
	 * @param canvas
	 * @param point
	 */
	private void drawEachLine(Canvas canvas, PointInfo point) {
		if (point.hasNextId()) {
			int n = point.getNextId();
			drawLine(canvas, point.getCenterX(), point.getCenterY(),
					points[n].getCenterX(), points[n].getCenterY());
			drawEachLine(canvas, points[n]);
		}
	}

	/**
	 * 
	 * @param canvas
	 * @param startX
	 * @param startY
	 * @param stopX
	 * @param stopY
	 */
	private void drawLine(Canvas canvas, float startX, float startY,
			float stopX, float stopY) {
		//canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		canvas.drawLine(startX, startY, stopX, stopY, whiteLinePaint);
	}

	/**
	 * @author zkwlx
	 * 
	 */
	private class PointInfo {

		private int id;

		private int nextId;

		private boolean selected;

		private int defaultX;

		private int defaultY;

		private int seletedX;

		private int seletedY;

		public PointInfo(int id, int defaultX, int defaultY, int seletedX,
				int seletedY) {
			this.id = id;
			this.nextId = id;
			this.defaultX = defaultX;
			this.defaultY = defaultY;
			this.seletedX = seletedX;
			this.seletedY = seletedY;
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public int getId() {
			return id;
		}

		public int getDefaultX() {
			return defaultX;
		}

		public int getDefaultY() {
			return defaultY;
		}

		public int getSeletedX() {
			return seletedX;
		}

		public int getSeletedY() {
			return seletedY;
		}

		public int getCenterX() {
			return seletedX + selectedBitmapRadius;
		}

		public int getCenterY() {
			return seletedY + selectedBitmapRadius;
		}

		public boolean hasNextId() {
			return nextId != id;
		}

		public int getNextId() {
			return nextId;
		}

		public void setNextId(int nextId) {
			this.nextId = nextId;
		}

		/**
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean isInMyPlace(int x, int y) {
			boolean inX = x > seletedX
					&& x < (seletedX + selectedBitmapDiameter);
			boolean inY = y > seletedY
					&& y < (seletedY + selectedBitmapDiameter);

			return (inX && inY);
		}

	}

}
