package cn.duocool.lashou.mywidget;





import cn.duocool.lashou.R;
import cn.duocool.lashou.activity.ActivityHome;
import cn.duocool.lashou.activity.AppLockMainActivity;

import cn.duocool.lashou.activity.LockSettingActivity;
import cn.duocool.lashou.activity.SetImagePasswordActivity;
import cn.duocool.lashou.activity.ApplockSingleSetting;
import cn.duocool.lashou.dao.DataBaseHelper;
import cn.duocool.lashou.model.AppModel;
import cn.duocool.lashou.service.LockService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.graphics.Paint.Cap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class setImagePasswordView extends View {
      
   String topTip;//顶部提示
   String leftButtonText,rightButtonText;
   Boolean rightBtClikable=false;
   String firstPassword="";
   String secondPassword="";
   Boolean leftBtpress=false,rightBtpress=false;
   Rect leftRect,rightRect;
   Boolean drawable=true;//设置屏幕可不可以画
	Context context;
	 Dialog d=null;
      Activity activity;
      String password;
      Boolean clickFlag=false;//是否点中了那9个点
	Paint linePaint = new Paint();
     Boolean error=false;
	Paint whiteLinePaint = new Paint();
    String lockName;//要改密码的程序锁的名字
	Paint textPaint = new Paint();
   
	public setImagePasswordView(Context context,Activity activity,String lockName) 
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.activity=activity;
		this.setBackgroundColor(Color.WHITE);
		this.lockName=lockName;
		leftButtonText="取消";
		rightButtonText="继续";
		topTip="绘制您的图案密码";
		 leftRect=new Rect(0, SetImagePasswordActivity.screenHeight/10*8,SetImagePasswordActivity.screenWidth/2,SetImagePasswordActivity.screenHeight);
		   
		 rightRect=new Rect(SetImagePasswordActivity.screenWidth/2,SetImagePasswordActivity.screenHeight/10*8,SetImagePasswordActivity.screenWidth,SetImagePasswordActivity.screenHeight);
		  
		initPaint();
	}

	public setImagePasswordView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	public setImagePasswordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		this.setBackgroundColor(Color.WHITE);
		initPaint();
		// TODO Auto-generated constructor stub
	}
	Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.lockpoint);
	int defaultBitmapRadius = defaultBitmap.getWidth() / 2;

	Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.indicator_lock_area);
	Bitmap errorBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.indicator_lock_areaerror);
	int selectedBitmapDiameter = selectedBitmap.getWidth();
	int selectedBitmapRadius = selectedBitmapDiameter / 2;

	PointInfo[] points = new PointInfo[9];

	PointInfo startPoint = null;

	int width, height;

	int moveX, moveY;

	boolean isUp = false;
	boolean initpoint=false;
	StringBuffer lockString = new StringBuffer();



	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		width = getWidth();
		height = getHeight();
		
		//Log.d("tag", "height="+width+"height="+height+"widthMeasureSpec="+widthMeasureSpec+"heightMeasureSpec="+heightMeasureSpec);
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
		//画顶部提示
		canvas.drawText(topTip, SetImagePasswordActivity.screenWidth/2, SetImagePasswordActivity.screenHeight/8, textPaint);

		if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {
			drawLine(canvas, startX, startY, moveX, moveY);
		}

		drawNinePoint(canvas);
		drawBottomButton(canvas);
         
		super.onDraw(canvas);
	}
 /**
 * 画下面的两个按钮
 */
public void drawBottomButton(Canvas canvas)
 {
	Paint paint=new Paint();
	paint.setTextAlign(Paint.Align.CENTER);
	paint.setTextSize(getResources().getDimension(R.dimen.pt_size));
	paint.setAntiAlias(true);
	paint.setTypeface(Typeface.MONOSPACE);
	//paint.setARGB(255, 189, 189, 189);
	paint.setColor(Color.GRAY);
	Paint p2=new Paint();
	p2.setTextAlign(Paint.Align.CENTER);
	p2.setTextSize(getResources().getDimension(R.dimen.pt_size));
	p2.setAntiAlias(true);
	p2.setTypeface(Typeface.MONOSPACE);
	p2.setColor(Color.BLACK);
	canvas.drawLine(0, SetImagePasswordActivity.screenHeight/10*8, SetImagePasswordActivity.screenWidth, SetImagePasswordActivity.screenHeight/10*8, paint);
    canvas.drawLine(SetImagePasswordActivity.screenWidth/2, SetImagePasswordActivity.screenHeight/10*8, SetImagePasswordActivity.screenWidth/2, SetImagePasswordActivity.screenHeight, paint);
    if(leftBtpress)
    {
    	Log.e("tag", "drawleftBtbg");
    	canvas.drawRect(leftRect, paint);
    //	canvas.drawRect(0,  SetImagePasswordActivity.screenHeight-90, SetImagePasswordActivity.screenWidth/2, SetImagePasswordActivity.screenHeight, paint);
    }
    if(rightBtpress&&rightBtClikable)
    {Log.e("tag", "drawrightBtbg");
    	canvas.drawRect(rightRect, paint);
    }
    canvas.drawText(leftButtonText, SetImagePasswordActivity.screenWidth/4, SetImagePasswordActivity.screenHeight/10*9, p2);
    if(rightBtClikable)
    {
    	
    	canvas.drawText(rightButtonText, SetImagePasswordActivity.screenWidth/4*3, SetImagePasswordActivity.screenHeight/10*9, p2);
    }else
    {
    	canvas.drawText(rightButtonText, SetImagePasswordActivity.screenWidth/4*3, SetImagePasswordActivity.screenHeight/10*9, paint);
         
    }
    
 }
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!initpoint)
		{
			return true;
		}
		int x = (int) event.getX();
		int y= (int) event.getY();
		boolean flag = true;
		if(!leftRect.contains(x, y)&&!rightRect.contains(x, y)&&!drawable)
		{
			return false;
		}
		if(drawable==false)
		{
			
		}
               
		//	finishDraw();

		//	flag = false;

		
		handlingEvent(event);

		
		return flag;
	}

	/**
	 * 处理触摸事件
	 * @param event
	 */
	private void handlingEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(moveX, moveY) && temp.isSelected()==false) {
					clickFlag=true;
					temp.setSelected(true);
					if(startPoint ==null)//如果用户点击的不是第一个位置不是点的话，就要在move事件里设置startPoint
					{
						
						startPoint=temp;
					}
					topTip="完成后请松开手指";
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					int len = lockString.length();
					if (len != 0) 
					{
						int preId = lockString.charAt(len - 1) - 48;
						points[preId].setNextId(temp.getId());
						
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
			if(leftRect.contains(downX, downY))
			{
				Log.e("tag", "leftRect");
				leftBtpress=true;
			
				if(leftButtonText.equals("取消"))
				{
					if(SetImagePasswordActivity.gotoActivity==null&&SetImagePasswordActivity.backable==null)
					{
						    
						     Log.e("tag", "gotoActivity=null");
						     Intent intent=new Intent(Intent.ACTION_MAIN);
						     intent.addCategory(Intent.CATEGORY_HOME);
						   
				    		activity.startActivity(intent); 
					}
					else
					{
					activity.finish();
					}
				}
				if(error)
				{
					finishDraw();
					error=false;
					leftButtonText="取消";
					
				}
				drawable=true;
				
			}
			if(rightRect.contains(downX, downY))
			{
				rightBtpress=true;
				if(rightBtClikable)
				{
					if(rightButtonText.equals("确定"))
					{
						DataBaseHelper dbHelper=new DataBaseHelper(context);
						SQLiteDatabase db=dbHelper.getWritableDatabase();
						ContentValues values=new ContentValues();
						values.put("password", firstPassword);
						values.put("passwordtype", "image");
					    db.update("locks", values, "name=?", new String[]{lockName} );
					    Toast.makeText(context, "密码设置成功！", Toast.LENGTH_SHORT).show();
					   //重新初始化锁信息，
//					    LockService.initLockInfo();
					    //重新初始化applist里面的数据
//					    LockService.resetApplist();
					    Cursor c=db.rawQuery("select * from locks where _id=1", null);
					    if(c.moveToNext())
					    {
					    String	question=c.getString(c.getColumnIndex("question"));
					    if(question.equals("无"))
						{
					    	 final	LinearLayout setquestionLayout=(LinearLayout) activity.getLayoutInflater().inflate(R.layout.applock_setquestiondialog, null);
					         final EditText questionEt=(EditText) setquestionLayout.findViewById(R.id.question);
					         final EditText answerET=(EditText) setquestionLayout.findViewById(R.id.answer);
					         questionEt.setText("");
					         answerET.setText("");
					          d=new AlertDialog.Builder(context)
					 			.setTitle("请输入密码找回用的问题及答案")
					 			.setView(setquestionLayout)
					 			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					 				
					 				@Override
					 				public void onClick(DialogInterface dialog, int which) 
					 				{
					 					if(!questionEt.getText().toString().equals(""))
					 					{
					 						if(!answerET.getText().toString().equals(""))
					 						{
					 							DataBaseHelper dbHelper=new DataBaseHelper(context);
					 							SQLiteDatabase db=dbHelper.getWritableDatabase();
					 							db=dbHelper.getWritableDatabase();
					 							 ContentValues values=new ContentValues();
					 							 values.put("question", questionEt.getText().toString());
					 				        	 values.put("answer", answerET.getText().toString());
					 				        	 db.update("locks", values, "name=?", new String[]{lockName});
					 				        
					 				        	
					 				        	   Toast.makeText(context, "设置成功！", Toast.LENGTH_SHORT).show();
					 				        	  if(SetImagePasswordActivity.gotoActivity!=null)
					 							    {
					 							    	if(SetImagePasswordActivity.gotoActivity.equals("LockSettingActivity"))
					 							    	{
					 							    		Intent i=new Intent();
					 							    		i.setClass(activity,LockSettingActivity.class );
					 							    		i.putExtra("lockName",lockName);
					 							    		activity.startActivity(i);
					 							    	}else if(SetImagePasswordActivity.gotoActivity.equals("ActivityHome"))
					 							    	{
					 							    		Intent i=new Intent();
					 							    		i.setClass(activity,ActivityHome.class );
					 							    		
//					 							    		 LockService.lock1Pwd=firstPassword;
					 							    	
					 							    		activity.startActivity(i);
					 							    	}
					 							    }
					 							    if(db!=null)
					 		        				{
					 		        				db.close();
					 		        				}
					 							   
					 							   
					 								activity.finish();
					 				        	   
					 						}else
					 						{
					 							   Toast.makeText(context, "答案不能为空", Toast.LENGTH_SHORT).show();
					 							Handler handler=new Handler();
					 								handler.postDelayed(new Runnable() {
					 									@Override
					 									public void run() {
					 											d.show();
					 									}
					 								}, 300); 
					 						}
					 					}else
					 					{
					 						   Toast.makeText(context, "问题不能为空", Toast.LENGTH_SHORT).show();
					 						  Handler handler=new Handler();
				 								handler.postDelayed(new Runnable() {
				 									@Override
				 									public void run() {
				 											d.show();
				 									}
				 								}, 300); 
					 					}
					 					
					 				}
					 			})
					 			.show();
					         d.setCancelable(false);
							
						}else
						{
							if(SetImagePasswordActivity.gotoActivity!=null)
							    {
							    	if(SetImagePasswordActivity.gotoActivity.equals("LockSettingActivity"))
							    	{
							    		Intent i=new Intent();
							    		i.setClass(activity,LockSettingActivity.class );
							    		i.putExtra("lockName",lockName);
							    		activity.startActivity(i);
							    	}else if(SetImagePasswordActivity.gotoActivity.equals("ActivityHome"))
							    	{
							    		Intent i=new Intent();
							    		i.setClass(activity,ActivityHome.class );
							    		
//							    		 LockService.lock1Pwd=firstPassword;
							    	
							    		activity.startActivity(i);
							    	}
							    }
							    if(db!=null)
		        				{
		        				db.close();
		        				}
							   
							   
								activity.finish();
						}
					    }
					    
					}else if(rightButtonText.equals("继续"))
					{
						firstPassword=lockString.toString();
						finishDraw();
						topTip="请再次绘制您的图案进行确认";
						drawable=true;
						
						
						rightButtonText="确定";
						rightBtClikable=false;
					}
					
					
				}
			}
			for (PointInfo temp : points) {
				if (temp.isInMyPlace(downX, downY)) {
					temp.setSelected(true);
					clickFlag=true;
					startPoint = temp;
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					lockString.append(temp.getId());
					break;
				}
			}
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
		
			startX = startY = moveX = moveY = 0;
			isUp = true;
			leftBtpress=false;
			rightBtpress=false;
			
			if(clickFlag&&lockString.toString().length()>=4)
			{
				if(rightButtonText.equals("继续"))
				{
				rightBtClikable=true;
				topTip="图案已记录";
				drawable=false;
				
				}
				if(rightButtonText.equals("确定"))
				{
					if(firstPassword.equals(lockString.toString()))
					{
						rightBtClikable=true;
						topTip="解锁图案已确认，请按确定";
						drawable=false;
					}else
					{
						error=true;
						topTip="很抱歉，两次绘制的图案不一致！";
						drawable=false;
						leftButtonText="重试";
					}
					
				}
			}
			if(clickFlag&&lockString.toString().length()<4)
			{
				error=true;
				leftButtonText="重试";
				drawable=false;
			}
			
			invalidate();
			//finishDraw();
			
			break;
		default:
			break;
		}
	}

	private void finishDraw() {
		
//		if(password.equals(lockString.toString()))
//		{
//			
//		activity.finish();
//		}
		for (PointInfo temp : points) {
			temp.setSelected(false);
			temp.setNextId(temp.getId());
		}
		clickFlag=false;
		startPoint=null;
		lockString.delete(0, lockString.length());
		isUp = false;
		invalidate();
	}

	private void initPoints(PointInfo[] points) {

		int len = points.length;

		int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;

		int seletedX = seletedSpacing;
		//int seletedY = height - width + seletedSpacing;
		int seletedY =height/7;
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

		}
		initpoint=true;
	}

	private void initPaint() {
		textPaint.setTextAlign(Paint.Align.CENTER);
		initLinePaint(linePaint);
		initTextPaint(textPaint);
		initWhiteLinePaint(whiteLinePaint);
	}

	/**
	 * @param paint
	 */
	private void initTextPaint(Paint paint) 
	{
		textPaint.setTextSize(getResources().getDimension(R.dimen.pt_size));
		Log.e("tag","pt_height="+ getResources().getDimension(R.dimen.pt_size));
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.MONOSPACE);
	}

	/**
	 * @param paint
	 */
	private void initLinePaint(Paint paint) 
	{
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
					if(!error)
					{
					canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(),pointInfo.getSeletedY(), null);
					}else
					{
						canvas.drawBitmap(errorBitmap, pointInfo.getSeletedX(),pointInfo.getSeletedY(), null);
						
					}
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
	//	canvas.drawLine(startX, startY, stopX, stopY, linePaint);
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
