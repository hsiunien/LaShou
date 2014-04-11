package cn.duocool.lashou.view.box;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BoxItem {
	
	private int id;
	private int iconResId;
	private Drawable iconDrawable;
	private int textResId;
	private String text;
	private int backGroundResId;
	private Rect rect;
	private int order;
	
	private View layoutView;
	private ImageView ivBG;
	private ImageView ivIcon;
	private TextView tvTitle;
	private Context context;
	private Class startClass;
	private OnBoxClick onBoxClick;
	
	public BoxItem() {
	}
	public BoxItem(Context context) {
		this.context = context;
	}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIconResId() {
		return iconResId;
	}
	public void setIconResId(int iconResId) {
		this.iconResId = iconResId;
	}
	public Drawable getIconDrawable() {
		return iconDrawable;
	}
	public void setIconDrawable(Drawable iconDrawable) {
		this.iconDrawable = iconDrawable;
	}
	public int getTextResId() {
		return textResId;
	}
	public void setTextResId(int textResId) {
		this.textResId = textResId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getBackGroundResId() {
		return backGroundResId;
	}
	public void setBackGroundResId(int backGroundResId) {
		this.backGroundResId = backGroundResId;
	}
	public Rect getRect() {
		return rect;
	}
	public void setRect(Rect rect) {
		this.rect = rect;
	}
	public View getLayoutView() {
		return layoutView;
	}
	public void setLayoutView(View layoutView) {
		this.layoutView = layoutView;
	}
	public ImageView getIvBG() {
		return ivBG;
	}
	public void setIvBG(ImageView ivBG) {
		this.ivBG = ivBG;
	}
	public ImageView getIvIcon() {
		return ivIcon;
	}
	public void setIvIcon(ImageView ivIcon) {
		this.ivIcon = ivIcon;
	}
	public TextView getTvTitle() {
		return tvTitle;
	}
	public void setTvTitle(TextView tvTitle) {
		this.tvTitle = tvTitle;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public Class getStartClass() {
		return startClass;
	}
	public void setStartClass(Class startClass) {
		this.startClass = startClass;
	}
	public OnBoxClick getOnBoxClick() {
		return onBoxClick;
	}
	public void setOnBoxClick(OnBoxClick onBoxClick) {
		this.onBoxClick = onBoxClick;
	}
}
