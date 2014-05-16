package cn.duocool.lashou.view.box;

import java.util.ArrayList;
import java.util.List;
import cn.duocool.lashou.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

public class BoxGridView 
extends ViewGroup 
implements OnTouchListener,OnClickListener,OnLongClickListener {
	
	private final String TAG = BoxGridView.class.getName();
	private Context context;
	private LayoutInflater layoutInflater;
	
	
	// 每行放几个方块
	int sizeOfColumn = 2;
	// 方块有几行
	int sizeOfRow = 3;
	int sizeOfRowOnePage = 3;
	// 长按的时间设定（多长时间算长按）
	int  pressTime = 2000;
	// 屏幕页数
	int screenSize = 0;
	
	private OnBoxViewChanged onBoxViewChanged;

	public void setOnBoxViewChanged(OnBoxViewChanged onBoxViewChanged) {
		this.onBoxViewChanged = onBoxViewChanged;
	}
	
	
	// 保存每个方块的位置与区域
	Rect[] boxRect;
	List<BoxItem> dataList = new ArrayList<BoxItem>();
	
	// 用于移动方块时，是否移动屏幕的界限
	SparseIntArray moveDieLine = new SparseIntArray();
	
	int colors[] = {R.color.color_01,R.color.color_02,R.color.color_03,R.color.color_04,R.color.color_05,R.color.color_06};
	int ids[] = {R.id.box00,R.id.box01,R.id.box02,R.id.box03,R.id.box04,R.id.box05,R.id.box06,R.id.box07,R.id.box08,R.id.box09,
			R.id.box10,R.id.box11,R.id.box12,R.id.box13,R.id.box14,R.id.box15,R.id.box16,R.id.box17,R.id.box18,R.id.box19,
			R.id.box20,R.id.box21,R.id.box22,R.id.box23,R.id.box24,R.id.box25,R.id.box26,R.id.box27,R.id.box28,R.id.box29,
			R.id.box30};
	
	/**
	 * 设定有几个格子及格子里面的内容
	 * @param dataList 格子数据列表
	 */
	public void setInitData(List<BoxItem> dataList) {
		// 当前类存一份
		this.dataList = dataList;
		// 获得尺寸
		int dataSize = dataList.size();
		// 计算总共有几行 sizeOfColumn:一行有几个图标
		sizeOfRow = dataSize/sizeOfColumn;
		// 如果多一个就多加一行
		if (dataSize%sizeOfColumn > 0) {
			sizeOfRow = sizeOfRow + 1;
		}
		// 计算页面个数 总有有几屏
		screenSize = sizeOfRow/sizeOfRowOnePage;
		// 如果多一行 就多加一屏
		if (sizeOfRow%sizeOfRowOnePage>0) {
			screenSize = screenSize + 1;
		}
		// 为每个方格准备好
		boxRect = new Rect[sizeOfColumn*sizeOfRow];
		
		
		initViews();
	}
	
	int boxW = 0;
	int boxH = 0;
	int leftMargin = 0;
	int topMargin = 0;
	int boxSpace = 0;
	int screenWidth; //得到宽度
	int screenHeight;  //得到高度
	
	int pressCount = 0; // true down false up
	boolean downPressFlag = false;
	
	Animation zoomAnim;
	
	public BoxGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		
		zoomAnim = AnimationUtils.loadAnimation(context, R.anim.box_action_down);
	}

	public BoxGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		zoomAnim = AnimationUtils.loadAnimation(context, R.anim.box_action_down);
	}

	public BoxGridView(Context context) {
		super(context);
		this.context = context;
		
		zoomAnim = AnimationUtils.loadAnimation(context, R.anim.box_action_down);
		//initViews(context);
	}
	
	/**
	 * 初期化界面
	 * @param
	 */
	private void initViews() {
		
		mScroller = new Scroller(context);
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
	    screenWidth = dm.widthPixels;    //得到宽度
	    screenHeight = dm.heightPixels;  //得到高度
	    
	    // 计算屏幕边界
	    for (int i=0;i<=screenSize;i++) {
	    	moveDieLine.put(i, screenWidth*i);
	    	Log.d(TAG, "screenSize:"+screenSize+" i:"+i +" line:"+moveDieLine.get(i));
	    }
	    
		// 最小滑动距离，用于分辨什么是误操作，什么是真的滑动
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		boxW = (int)context.getResources().getDimension(R.dimen.box_size_w);
		boxH = (int)context.getResources().getDimension(R.dimen.box_size_h);
		leftMargin = (int)context.getResources().getDimension(R.dimen.box_left_margin);
		topMargin = (int)context.getResources().getDimension(R.dimen.box_top_margin);
		boxSpace = (int)context.getResources().getDimension(R.dimen.box_size_space);
		
		layoutInflater = LayoutInflater.from(context);
		
		for (int i=0;i<dataList.size();i++) {
			View childView = layoutInflater.inflate(R.layout.view_box_item, null);
			BoxItem boxData  = dataList.get(i);
			childView.setId(ids[i]);
			childView.setOnTouchListener(this);
			childView.setOnLongClickListener(this);
			childView.setOnClickListener(this);
			boxData.setLayoutView(childView);
			boxData.setId(ids[i]);
			boxData.setOrder(i);
			
			// 初期化
			boxRect[i] = new Rect();
			boxData.setRect(new Rect());
			
			ImageView ivBG = (ImageView)childView.findViewById(R.id.boxBackGround_iv);
			ivBG.setBackgroundResource(boxData.getBackGroundResId());
			ImageView ivIcon = (ImageView)childView.findViewById(R.id.boxIcon_iv);
			ivIcon.setImageResource(boxData.getIconResId());
			TextView tvTitle = (TextView)childView.findViewById(R.id.boxText_tv);
			tvTitle.setText(boxData.getText());
			boxData.setIvBG(ivBG);
			boxData.setTvTitle(tvTitle);
			boxData.setIvIcon(ivIcon);
			
			dataList.set(i, boxData);
			childView.setTag(boxData);
			this.addView(childView);
		}
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		int nowChildNo = 0;
		if (changed) {
			for (int row=0; row<sizeOfRow; row++) {
				for (int column=0;column < sizeOfColumn;column++) {
					if (nowChildNo >= dataList.size()) {
						break;
					}
					// 取到当前的方块 
					View child = getChildAt(nowChildNo);
					
					child.measure(boxW, boxH);
					
					int nowH = 0;
					int nowW = 0;
					
					if (column > 0) { // 不是列开始方格加间距
						nowW = (boxW + boxSpace) * column + boxSpace + leftMargin+((int)(row / sizeOfRowOnePage)) *screenWidth;
					} else {
						nowW = leftMargin+((int)(row / sizeOfRowOnePage)) *screenWidth;
					}
					
					if ((row % sizeOfRowOnePage) > 0) { // 不是行开始方格加间距
						nowH = (boxH + boxSpace) * (row % sizeOfRowOnePage)  + boxSpace + topMargin;
					} else {
						nowH = topMargin;
					}
					
					child.layout(
							nowW, 
							nowH, 
							nowW+boxW, 
							nowH+boxH);
					
//					Log.d("AAA", "nowChildNo left top righy bottom"+ nowChildNo+ " "+nowW +"  "+nowH+"  "+(nowW+boxW) +" "+(nowH+boxH));
					boxRect[nowChildNo].set(nowW, nowH, nowW+boxW, nowH+boxH);
					dataList.get(nowChildNo).getRect().set(nowW, nowH, nowW+boxW, nowH+boxH);
					nowChildNo++;
				}
			}
		}
	}
	
	
	private int mTouchSlop = 0;
	private float mLastionMotionX = 0 ;       // 记住上次触摸屏的位置
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker = null;
	
	//两种状态: 是否处于滑屏状态  
    private static final int TOUCH_STATE_REST = 0;  //什么都没做的状态  
    private static final int TOUCH_STATE_SCROLLING = 1;  //开始滑屏的状态  
    private int mTouchState = TOUCH_STATE_REST; //默认是什么都没做的状态  
    
    public static int  SNAP_VELOCITY = 600 ;  //最小的滑动速率
    public int curScreen = 0;
	
    // 是否长按一个地方
    private boolean mLongPress = false; 
    
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		
		float x = ev.getX();
		float y = ev.getY();
		
		float rx = ev.getRawX();
		float ry = ev.getRawY();
		
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_MOVE :
				final int xDiff = (int) Math.abs(mLastionMotionX - x);  
				Log.d(TAG, "onInterceptTouchEvent ACTION_MOVE xDiff>mTouchSlop"+(xDiff>mTouchSlop));
				// 超过了最小滑动距离，就可以认为开始滑动了
				// 并且没有长按其中的任何一个方块
				if (!mLongPress && xDiff>mTouchSlop) {
					 mTouchState = TOUCH_STATE_SCROLLING;
					 // OK 将事件交给自己的onTouchEvent处理
					 // 自己的TouchEvent 主要还是处理整个界面的划屏处理
					 return true;
				}
				
				break;
			case MotionEvent.ACTION_DOWN :
				Log.d(TAG, "onInterceptTouchEvent ACTION_DOWN");  
				// 得到按下的开始的位置
				mLastionMotionX = x;

				Log.d(TAG, "x y rawX rawY"+x + " " + y + " " + rx + " " + ry);

				Log.d(TAG, "mTouchState != TOUCH_STATE_REST"+mScroller.isFinished() + "");  
		        mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING; 
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP :
				Log.d(TAG, "onInterceptTouchEvent ACTION_UP");
				if (mLongPress) { // 将长按恢复
					mLongPress =  false;
				}
		        mTouchState = TOUCH_STATE_REST;  
				break;
	
			default:
				break;
		}
		// false 的意思是 不交给自己的onTouchEvent处理，交给子控件的TouchEvent处理
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.d(TAG, "onTouchEvent mLongPress"+mLongPress);
		
		// 如果长按就将事件交给子view的
		if (mLongPress)  {
			return false;
		}
		if (null == mVelocityTracker) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		
		float x = event.getX();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
//				Log.d("AAA", "onTouchEvent ACTION_DOWN");
				if (null !=  mScroller) {
					if (!mScroller.isFinished()) {
						mScroller.abortAnimation();
					}
				}
				mLastionMotionX = x;
				break;
			case MotionEvent.ACTION_MOVE:
//				Log.d("AAA", "onTouchEvent ACTION_MOVE mLongPress"+mLongPress);
			
				int detaX = (int)(mLastionMotionX - x ); //每次滑动屏幕，屏幕应该移动的距离  
				scrollBy(detaX, 0);//开始缓慢滑屏咯。 detaX > 0 向右滑动 ， detaX < 0 向左滑动 ，
//				Log.d("AAA", "--- MotionEvent.ACTION_MOVE--> detaX is " + detaX );
				mLastionMotionX = x ;
				
				break;
			case MotionEvent.ACTION_UP:
//				Log.d("AAA", "onTouchEvent ACTION_UP");
				
				 final VelocityTracker velocityTracker = mVelocityTracker  ;  
		         velocityTracker.computeCurrentVelocity(1000);  
		         
		         //计算速率  
				int velocityX = (int) velocityTracker.getXVelocity();
//				Log.d("AAA" , "---velocityX---" + velocityX);
				
				// 滑动速率达到了一个标准(快速向left滑屏，返回上一个屏幕) 马上进行切屏处理  
	            if (velocityX > SNAP_VELOCITY && curScreen > 0) {  
	                // Fling enough to move left  
//	                Log.d("AAA", "snap left");
	                snapToScreen(curScreen - 1);
	            }   //快速向right滑屏，返回下一个屏幕)  
	            else if(velocityX < -SNAP_VELOCITY && curScreen < (getChildCount()-1)){  
//	                Log.d("AAA", "snap right");
	                snapToScreen(curScreen + 1);  
	            }  
	            //以上为快速移动的 ，强制切换屏幕  
	            else{  
	                //我们是缓慢移动的，因此先判断是保留在本屏幕还是到下一屏幕  
	                snapToDestination();  
	            } 
	          //回收VelocityTracker对象  
	            if (mVelocityTracker != null) {  
	                mVelocityTracker.recycle();  
	                mVelocityTracker = null;  
	            }  
	            //修正mTouchState值  
	            mTouchState = TOUCH_STATE_REST;
//	            Log.d("AAA", "--- MMotionEvent.ACTION_UP" );
				break;
			case MotionEvent.ACTION_CANCEL:  
//				Log.d("AAA", "--- MotionEvent.ACTION_CANCEL" );
	            mTouchState = TOUCH_STATE_REST ;  
	            break;
	
			default:
				break;
		}
		
		return true;
	}
	
	int fromBoxId = -1;
	boolean isMoveNextScreen = false;
	View moveNextBoxView;
	int nextLeft;
	int nextTop;
	int nextRight;
	int nextBottom;
	int moveNextScreenCount = 0;
	
	public void moveBox() {
		for (int i=0;i<dataList.size();i++) {
			final BoxItem data  = dataList.get(i);
			int order = data.getOrder();
			final Rect toRect = boxRect[order];
			// 没动的方块就不要动了
			if (toRect.left-data.getRect().left ==0 && toRect.top-data.getRect().top==0) {
				continue;
			}
			TranslateAnimation moveAnim = new  TranslateAnimation(0,(toRect.left-data.getRect().left),0,(toRect.top-data.getRect().top));
//			Log.d("AAA", " moveAnim"+(toRect.left-data.getRect().left) + " " + (toRect.top-data.getRect().top));
			data.getRect().set(toRect.left, toRect.top, toRect.right, toRect.bottom);
			moveAnim.setDuration(150);

			moveAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					// 清除闪烁
					TranslateAnimation anim = new TranslateAnimation(0,0,0,0);
					data.getLayoutView().setAnimation(anim);

					data.getLayoutView().layout(toRect.left, toRect.top, toRect.right, toRect.bottom);
				}
			});
			
			data.getLayoutView().startAnimation(moveAnim);
		}
	}
	
	//真正的实现跳转屏幕的方法  
    private void snapToScreen(int whichScreen,int startX,boolean isLeft){   
        //简单的移到目标屏幕，可能是当前屏或者下一屏幕  
        //直接跳转过去，不太友好  
        //scrollTo(mLastScreen * MultiScreenActivity.screenWidth, 0);  
        //为了友好性，我们在增加一个动画效果  
        //需要再次滑动的距离 屏或者下一屏幕的继续滑动距离  
          
        curScreen = whichScreen ;  
        //防止屏幕越界，即超过屏幕数  
        if(curScreen > screenSize - 1)  {
            curScreen = screenSize - 1 ;  
            return;
        }
        
        if (curScreen<0) {
        	curScreen = 0;
        	return ;
        }
        //为了达到下一屏幕或者当前屏幕，我们需要继续滑动的距离.根据dx值，可能想左滑动，也可能像又滑动  
        int dx = curScreen * getWidth() - getScrollX();
          
        Log.d(TAG, "### snapToScreen  ### dx is " + dx + " getScrollX()" + getScrollX());  
          
        mScroller.startScroll(getScrollX(), 0, dx, 0,Math.abs(dx) * 2);
        
        Log.d(TAG, "onBoxViewChanged" + curScreen + " "  +screenSize);
        if (null != onBoxViewChanged) {
			onBoxViewChanged.pagerChanged(curScreen, screenSize-1);
		}
          
        //由于触摸事件不会重新绘制View，所以此时需要手动刷新View 否则没效果  
        invalidate();  
    }  
	
	//真正的实现跳转屏幕的方法  
    private void snapToScreen(int whichScreen){   
        //简单的移到目标屏幕，可能是当前屏或者下一屏幕  
        //直接跳转过去，不太友好  
        //scrollTo(mLastScreen * MultiScreenActivity.screenWidth, 0);  
        //为了友好性，我们在增加一个动画效果  
        //需要再次滑动的距离 屏或者下一屏幕的继续滑动距离  
          
        curScreen = whichScreen ;  
        //防止屏幕越界，即超过屏幕数  
        if(curScreen > screenSize - 1) 
            curScreen = screenSize - 1 ;  
        //为了达到下一屏幕或者当前屏幕，我们需要继续滑动的距离.根据dx值，可能想左滑动，也可能像又滑动  
        int dx = curScreen * getWidth() - getScrollX();  
          
        Log.d("", "### onTouchEvent  ACTION_UP### dx is " + dx);  
          
        mScroller.startScroll(getScrollX(), 0, dx, 0,Math.abs(dx) * 2);  
        
        Log.d(TAG, "onBoxViewChanged" + curScreen + " "  +screenSize);
        if (null != onBoxViewChanged) {
			onBoxViewChanged.pagerChanged(curScreen, screenSize-1);
		}
          
        //由于触摸事件不会重新绘制View，所以此时需要手动刷新View 否则没效果  
        invalidate();  
    }  
	
	////我们是缓慢移动的，因此需要根据偏移值判断目标屏是哪个？  
    private void snapToDestination(){  
        //当前的偏移位置  
        int scrollX = getScrollX() ;  
          
//        Log.d("AAA", "### onTouchEvent snapToDestination ### scrollX is " + scrollX);
        //判断是否超过下一屏的中间位置，如果达到就抵达下一屏，否则保持在原屏幕      
        //直接使用这个公式判断是哪一个屏幕 前后或者自己  
        //判断是否超过下一屏的中间位置，如果达到就抵达下一屏，否则保持在原屏幕  
        // 这样的一个简单公式意思是：假设当前滑屏偏移值即 scrollCurX 加上每个屏幕一半的宽度，除以每个屏幕的宽度就是  
        //  我们目标屏所在位置了。 假如每个屏幕宽度为320dip, 我们滑到了500dip处，很显然我们应该到达第二屏  
        int destScreen = (getScrollX() + screenWidth / 2 ) / screenWidth ;  
          
       // Log.d("AAA", "### onTouchEvent  ACTION_UP### dx destScreen " + destScreen);
          
        snapToScreen(destScreen);  
    }
    
	
	@Override
	public void computeScroll() {
		
		if (!mLongPress){
		///	Log.d("AAA", "computeScroll1 mHasPerformedLongPress");
			if (mScroller.computeScrollOffset()) {  
				//Log.d("AAA", "mScroller.getCurrX()111"+mScroller.getCurrX());
				scrollTo(mScroller.getCurrX(), 0);
				postInvalidate();
			} 
		}
		
		if (isMoveNextScreen) {
		//	Log.d("AAA", "computeScroll2 isMoveNextScreen");
			if (mScroller.computeScrollOffset()) {  
		//		Log.d("AAA", "mScroller.getCurrX()222:"+mScroller.getCurrX());
				scrollTo(mScroller.getCurrX(), 0);
				postInvalidate();
			} 
			if (mScroller.isFinished()) {
		//		Log.d("AAA", "mScroller.isFinished()222"+mScroller.isFinished());
				isMoveNextScreen = false;
			}
		}
	}

	@Override
	public boolean onLongClick(View v) {
		Log.d(TAG, "onLongClick~~~~");
		mLongPress = true;
		
		v.startAnimation(zoomAnim);
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick1~~~~");
		int nowId = v.getId();
		for (int i=0;i<dataList.size();i++) {
			BoxItem boxItem = dataList.get(i);
			if (nowId == boxItem.getId()) {
				OnBoxClick onBoxClick = boxItem.getOnBoxClick();
				if (null != onBoxClick) {
					onBoxClick.boxClick();
				}
				break;
			}
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Log.d(TAG, "onTouch~~~~");
		
		Log.d(TAG, " BoxGridView onTouch mLongPress:"+mLongPress);
		final View v = view;
		// 当长按下后并且是移动中的场合，移动方块
		if (mLongPress && event.getAction() == MotionEvent.ACTION_MOVE) {
			
			int rx  = (int) event.getRawX();
			int ry = (int) event.getRawY();
			int vLeft = rx-boxW/2 + screenWidth * curScreen;
			int vTop = ry-topMargin-boxH/2;
			int vRight = rx-boxW/2 + screenWidth * curScreen +boxW ;
			int vBottom = ry-boxH/2-topMargin +boxH;
			Log.d(TAG, "A1 BoxGridView onTouch ACTION_MOVE curScreen:"+curScreen + " rx ry" + rx + " " + ry 
					+ " boxW:" + boxW+ " screenWidth:" + screenWidth+ " boxH" + boxH+ " topMargin" + topMargin
					+ " vLeft:"+vLeft + " vRight:"+vRight  + " curScreen:"+curScreen);
			
			int moveLeft = vLeft;
			int moveRight = vRight;
			
			// 当前窗口的左右界限
			int minLimit = moveDieLine.get(curScreen);
			int maxLimit = moveDieLine.get(curScreen+1);
			// 在当前区间内
			if (vRight <= maxLimit && vLeft >= minLimit ) {
				moveNextScreenCount = 0;
			} else { // 不在区间
				if (vRight >=maxLimit-3 ) { // 右边超过了 开始计数
					moveNextScreenCount ++;
				}
				if (vLeft <= minLimit+3) { // 左边超过了 开始计数
					moveNextScreenCount --;
				}
			}
			if (moveNextScreenCount >= 20) { // 计数到了20次了 就移动屏幕
				Log.d(TAG, " 右移");
				curScreen = curScreen + 1;
				if (curScreen>screenSize) {
					curScreen = screenSize -1;
				} else if (curScreen<0) {
					curScreen = 0;
				} else {
					snapToScreen(curScreen,rx,false);
					isMoveNextScreen = true;
					
					Log.d(TAG, "onBoxViewChanged" + curScreen + " "  +screenSize);
					if (null != onBoxViewChanged) {
						onBoxViewChanged.pagerChanged(curScreen, screenSize-1);
					}
					// 计算当前的方块需要移动的距离
					moveLeft = moveLeft + screenWidth;
					moveRight = moveLeft + boxW;
				}
				moveNextScreenCount = 0;
			} else if (moveNextScreenCount <= -20) { // 计数到了20次了 就移动屏幕
		//		Log.d("AAA", " 左移");
				curScreen = curScreen - 1;
				if (curScreen<0) {
					curScreen = 0;
				} if (curScreen > screenSize) {
					curScreen = screenSize -1;
				} else {
					snapToScreen(curScreen,rx,true);
					isMoveNextScreen = true;
					Log.d(TAG, "onBoxViewChanged" + curScreen + " "  +screenSize);
					if (null != onBoxViewChanged) {
						onBoxViewChanged.pagerChanged(curScreen, screenSize-1);
					}
					moveLeft = moveLeft - screenWidth;
					moveRight = moveLeft + boxW;
				}
				moveNextScreenCount = 0;
			}
		
			Log.d(TAG, "A2 minLimit:"+minLimit + " maxLimit:" +maxLimit 
			+ " moveDieLine.get(minLimit):"+moveDieLine.get(minLimit)
			+ " moveDieLine.get(maxLimit):"+moveDieLine.get(maxLimit)
			+ " vRight<=maxLimit:" +(vRight<=maxLimit)
			+ " vLeft >=minLimit:" +(vLeft >=minLimit) 
			+ " moveLeft:" +moveLeft
			+ " moveRight:" +moveRight
			+ " moveNextScreenCount:"+moveNextScreenCount);
			
			// 移动当前按下的方块
			v.layout(moveLeft, vTop, moveRight, vBottom);
			((BoxItem)v.getTag()).getRect().set(moveLeft, vTop, moveRight, vBottom);

			Log.d(TAG, "A3 BoxGridView onTouch ACTION_MOVE curScreen:"+curScreen + " rx ry" + rx + " " + ry 
					+ " boxW:" + boxW+ " screenWidth:" + screenWidth+ " boxH" + boxH+ " topMargin" + topMargin
					+ " vLeft:"+vLeft + " vRight:"+vRight  + " curScreen:"+curScreen);

			fromBoxId = v.getId();
			int toBoxOrder  = -1;
			int fromBoxOrder = -1;
			// 查看当前的坐标停到哪个方块里了
			for (int i=0;i<dataList.size();i++) {
				BoxItem nowBox = dataList.get(i);
				// 如果循环到当前按的那个view就不做了
				if (nowBox.getId() == fromBoxId) {
					continue;
				}
				int nrx = screenWidth * curScreen + rx;
				if (nowBox.getRect().contains(nrx, ry)){
					Log.d(TAG, "i:"+i + " rx ry" + rx + " " + ry +" left"+nowBox.getRect().left+" top"+nowBox.getRect().top+" right"+nowBox.getRect().right+" bottom"+nowBox.getRect().bottom);
					// 移动到的方块的排列序号
					toBoxOrder = nowBox.getOrder();
					// 得到移动中的方块的排序序号
					for (int m=0;m<dataList.size();m++) {
						BoxItem bd= dataList.get(m);
						if (bd.getId() == fromBoxId) {
							fromBoxOrder = bd.getOrder();
							break;
						}
					}
				
					for (int x=0;x<dataList.size();x++) {
						BoxItem xb = dataList.get(x);
						Rect tr = xb.getRect();
						int order = xb.getOrder();
						Log.d(TAG, "x1:"+x + " order" + order +" left"+tr.left+" top"+tr.top+" right"+tr.right+" bottom"+tr.bottom);
					}
					
					if (fromBoxOrder > toBoxOrder) { // 后面移动到前面 排序挨个+1
						for (int indexOrder=0;indexOrder<dataList.size();indexOrder++) {
							BoxItem bd= dataList.get(indexOrder);
							if (bd.getOrder() >= toBoxOrder &&  bd.getOrder() < fromBoxOrder) {
								int nowOrder = bd.getOrder();
								bd.setOrder(nowOrder+1);
							} else if (bd.getOrder() == fromBoxOrder) {
								bd.setOrder(toBoxOrder);
							} else {
								continue;
							}
						}
					} else if (fromBoxOrder < toBoxOrder) {// 前面移动到后面 排序挨个-1
						for (int indexOrder=0;indexOrder<dataList.size();indexOrder++) {
							BoxItem bd= dataList.get(indexOrder);
							if (bd.getOrder() > fromBoxOrder &&  bd.getOrder() <= toBoxOrder) {
								int nowOrder = bd.getOrder();
								bd.setOrder(nowOrder-1);
							} else if (bd.getOrder() == fromBoxOrder) {
								bd.setOrder(toBoxOrder);
							} else {
								continue;
							}
						}
					}
					for (int x=0;x<dataList.size();x++) {
						BoxItem xb = dataList.get(x);
						Rect tr = xb.getRect();
						int order = xb.getOrder();
			//			Log.d("AAAA", "x2:"+x + " order" + order +" left"+tr.left+" top"+tr.top+" right"+tr.right+" bottom"+tr.bottom);
					}
					moveBox();
				}
			}
		}
		
		// 弹起来
		if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
	//		Log.d("AAA", "BoxGridView ACTION_UP");
			
			moveBox();
			
			mLongPress = false;
		}
		
		// 这里必须返回false，否则 longClick收不到事件了 
		// 因为onTouch事件在前，longClick事件在后。longClick事件依靠onTouch检查 延迟检查的方式。
		return false;
	}
}
