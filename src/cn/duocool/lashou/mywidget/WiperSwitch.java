package cn.duocool.lashou.mywidget;





import cn.duocool.lashou.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * 
 * 
 *
 */
public class WiperSwitch extends View implements OnTouchListener{
	private Bitmap bg_on, bg_off, slipper_btn;
	private Bitmap locked,unlocked;
	/**
	 * ����ʱ��x�͵�ǰ��x
	 */
	private float downX, nowX=0;
	
	/**
	 * ��¼�û��Ƿ��ڻ���
	 */
	private boolean onSlip = false;
	
	/**
	 * ��ǰ��״̬
	 */
	private boolean islock = false;
	
	/**
	 * ����ӿ�
	 */
	private OnChangedListener listener;
	
	
	public WiperSwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public WiperSwitch(Context context) {
		super(context);
		init();
		
	}

	public WiperSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public void init(){
		//����ͼƬ��Դ
//		
//		bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.bg_on);
//		bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.bg_off);
//		slipper_btn = BitmapFactory.decodeResource(getResources(), R.drawable.slipper_btn);
//		
		
		
		locked= BitmapFactory.decodeResource(getResources(), R.drawable.ic_locked);
		unlocked= BitmapFactory.decodeResource(getResources(), R.drawable.ic_unlocked);
		setOnTouchListener(this);
		 
	}
    public void setImage(int offImage,int onImage)
    {
    	locked= BitmapFactory.decodeResource(getResources(), onImage);
		unlocked= BitmapFactory.decodeResource(getResources(), offImage);
	
    }
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		float x = 0;
		if(islock)
		{
			canvas.drawBitmap(locked, matrix, paint);
		}else
		{
			canvas.drawBitmap(unlocked, matrix, paint);
		}
		//���nowX���ñ����������߹�״̬
//		if (nowX >= (bg_on.getWidth()/2)){
//			canvas.drawBitmap(bg_off, matrix, paint);//����ر�ʱ�ı���
//		}else{
//			canvas.drawBitmap(bg_on, matrix, paint);//�����ʱ�ı��� 
//		}
//		
//		if (onSlip) {//�Ƿ����ڻ���״̬,  
//			if(nowX >= bg_on.getWidth())//�Ƿ񻮳�ָ����Χ,�����û����ܵ���ͷ,����������ж�
//				x = bg_on.getWidth() - slipper_btn.getWidth()/2;//��ȥ����1/2�ĳ���
//			else
//				x = nowX - slipper_btn.getWidth()/2;
//		}else {
//			if(islock){//��ݵ�ǰ��״̬���û����xֵ
//				x = bg_on.getWidth() - slipper_btn.getWidth();
//			}else{
//				x = 0;
//			}
//		}
//		
//		//�Ի��黬�������쳣���?�����û�����
//		if (x < 0 ){
//			x = 0;
//		}
//		else if(x > bg_on.getWidth() - slipper_btn.getWidth()){
//			x = bg_on.getWidth() - slipper_btn.getWidth();
//		}
//		
//		//�����
//		canvas.drawBitmap(slipper_btn, x , 1, paint); 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:{
//			if (event.getX() > bg_off.getWidth() || event.getY() > bg_off.getHeight()){
//				return false;
//			}else{
//				onSlip = true;
//				downX = event.getX();
//				nowX = downX;
//			}
			break;
		}
		case MotionEvent.ACTION_MOVE:{
			//nowX = event.getX();
			break;
		}
		case MotionEvent.ACTION_UP:{
//			onSlip = false;
//			if(event.getX() >= (bg_on.getWidth()/2)){
//				islock = true;
//				nowX = bg_on.getWidth() - slipper_btn.getWidth();
//			}else{
//				islock = false;
//				nowX = 0;
//			}
//			
			if(islock)
			{
				islock=false;
			}else
			{
				islock=true;
			}
			if(listener != null){
				listener.OnChanged(WiperSwitch.this, islock);
			}
			
			Log.d("tag", "ACTION_UP"+islock);
			break;
		}
		}
		//ˢ�½���
		invalidate();
		return true;
	}
	
	
	
	/**
	 * ΪWiperSwitch����һ������ⲿ���õķ���
	 * @param listener
	 */
	public void setOnChangedListener(OnChangedListener listener){
		this.listener = listener;
	}
	
	public void reDraw()
	{
		invalidate();
	}
	/**
	 * ���û������صĳ�ʼ״̬�����ⲿ����
	 * @param checked
	 */
	public void setisLock(boolean checked){
//		if(checked){
//			nowX = bg_off.getWidth()-slipper_btn.getWidth();;
//		}else{
//			nowX = 0;
//		}
		islock = checked;
		invalidate();
	}                                                                                       
    
	
    public boolean isIslock() {
		return islock;
	}


	/**
     * �ص�ӿ�
     * @author len
     *
     */
	public interface OnChangedListener 
	{
		public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);
	}


}

