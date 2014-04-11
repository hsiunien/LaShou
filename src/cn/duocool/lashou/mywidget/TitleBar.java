package cn.duocool.lashou.mywidget;

import cn.duocool.lashou.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleBar extends RelativeLayout {
	View leftButton;
	TextView titleView;
	Button rightButton;
	public TitleBar(Context context) {
		super(context);

	}
	private OnClickListener  myOnclikListenr=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnBack:
				((Activity)getContext()).finish();
				break;

			default:
				break;
			}
			
		}
	};
	public void setTitle(String title){
		titleView.setText(title);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater li = LayoutInflater.from(context);
		View v = li.inflate(R.layout.title_widget, this);
		if (isInEditMode()) { return; }
		leftButton = v.findViewById(R.id.btnBack);
		leftButton.setOnClickListener(myOnclikListenr);
		titleView = (TextView) v.findViewById(R.id.titleBar_title);
		titleView.setText(attrs.getAttributeValue(null, "title") == null ? ""
				: attrs.getAttributeValue(null, "title"));
		rightButton = (Button) v.findViewById(R.id.title_btnRight);

		int selector = attrs.getAttributeResourceValue(null, "rightBtnBg", -1);
		if (selector != -1) {
			rightButton.setBackgroundResource(selector);
		}
		if (attrs.getAttributeValue(null, "rightBtn") != null) {
			rightButton
					.setText(attrs.getAttributeValue(null, "rightBtn") == null ? ""
							: attrs.getAttributeValue(null, "rightBtn"));
		} else {
			rightButton.setVisibility(View.INVISIBLE);
		}

	}

	public void setLeftButtonClick(OnClickListener listenser) {
		leftButton.setOnClickListener(listenser);
	}

	/**
	 *  id为 title_btnRight
	 * @param listenser
	 */
	public void setRightButtonClick(OnClickListener listenser) {
		rightButton.setOnClickListener(listenser);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

}
