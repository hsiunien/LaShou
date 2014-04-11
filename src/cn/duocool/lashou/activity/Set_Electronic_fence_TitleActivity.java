package cn.duocool.lashou.activity;

import cn.duocool.lashou.R;
import cn.duocool.lashou.mywidget.TitleBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 设定提醒标题，添加电子围栏的二级页面，电子围栏的三级页面
 * @author 杞桅
 *
 */
public class Set_Electronic_fence_TitleActivity extends BaseActivity {
	private TitleBar titleBar;//标题栏
	private EditText editText;//用户输入Title的EditText
	private String title;//标题
	private TextView shengyuzishu;//用户输入字数提醒


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_electronic_fence_title);

		//标题栏
		titleBar = (TitleBar) findViewById(R.id.activity_set_electronic_fence_title_titleBar);
		titleBar.setLeftButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//返回按键
				Set_Electronic_fence_TitleActivity.this.finish();
			}
		});
		titleBar.setRightButtonClick(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//确定按键
				title = editText.getText().toString();
				//判断是否为空
				if(title.equals("")){
					Toast.makeText(getApplicationContext(), "提醒标题不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
				}else{
					Handler handler = AddElectronicFenceActivity.handler;
					handler.sendMessage(handler.obtainMessage(0,title));
					Set_Electronic_fence_TitleActivity.this.finish();
				}
			}
		});

		shengyuzishu = (TextView) findViewById(R.id.shengyuzishu);
		shengyuzishu.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				Dialog dialog = new AlertDialog.Builder(Set_Electronic_fence_TitleActivity.this)
				.setTitle("提示")
				.setMessage("确定要清空内容？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						editText.setText("");
					}
				})
				.setNegativeButton("取消", null)
				.create();
				dialog.show();
			}
		});

		shengyuzishu.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					shengyuzishu.setBackgroundResource(R.drawable.button_bg_down);
				}
				else if(event.getAction()==MotionEvent.ACTION_UP)
				{
					shengyuzishu.setBackgroundResource(R.drawable.button_bg);
				}
				return false;
			}
		});

		title = getIntent().getStringExtra("title");

		shengyuzishu.setText(Integer.toString((10-title.length()))+"X");	

		editText = (EditText) findViewById(R.id.activity_set_electronic_fence_title_edittext);
		editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});  //其中10最大输入字数 
		editText.setText(title);

		//将光标移动到最后
		CharSequence text = editText.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable)text;
			Selection.setSelection(spanText, text.length());
		}

		editText.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;  

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				temp = s; 
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				shengyuzishu.setText(Integer.toString((10-temp.length()))+"X");	

			}
		});
	}

}
