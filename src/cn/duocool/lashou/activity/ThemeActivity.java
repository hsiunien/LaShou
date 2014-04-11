package cn.duocool.lashou.activity;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent; 
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import cn.duocool.lashou.R;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.Tools;
import cn.duocool.lashou.utils.download.ImageLoader;

public class ThemeActivity extends BaseActivity implements OnCheckedChangeListener,OnClickListener {

	private final String myImgPath="anxinbao/bg.png";
	private RadioButton rb1,rb2,rb3,rb4,rb5  ;
	private List<LinearLayout> itemBoxs=new ArrayList<LinearLayout>();
	private ImageView themeImg[],mybgImg;
	private String []srcs={"theme/theme1.jpg","theme/theme2.jpg","theme/theme3.jpg","theme/theme4.jpg","anxinbao/bg.png"};
	private ImageLoader imageLoader;
	private final int REGCODE=888,CHOICEPICREQUEST=998,CROPREQUEST=99802,
			UPLOADPIC=666,CHECKEMAILEXIST=667,CHECKPHONEEXIST=668;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theme);
		rb1=(RadioButton)findViewById(R.id.theme1_btn);
		rb2=(RadioButton)findViewById(R.id.theme2_btn);
		rb3=(RadioButton)findViewById(R.id.theme3_btn);
		rb4=(RadioButton) findViewById(R.id.theme4_btn);
		rb5=(RadioButton) findViewById(R.id.theme5_btn);
		mybgImg=(ImageView) findViewById(R.id.theme5Img);
		mybgImg.setOnClickListener(this);
		rb1.setOnCheckedChangeListener(this);
		rb2.setOnCheckedChangeListener(this);
		rb3.setOnCheckedChangeListener(this);
		rb4.setOnCheckedChangeListener(this);
		rb5.setOnCheckedChangeListener(this);
		RelativeLayout container=(RelativeLayout)findViewById(R.id.relativeLayout1);
		imageLoader=new ImageLoader(this);
		SharedPreferences settings=getSharedPreferences("config", 0);
		String src=settings.getString("bgImg", "theme/theme1.jpg");
		for (int i = 0; i <container.getChildCount(); i++) {
			LinearLayout layout=(LinearLayout)container.getChildAt(i);
			itemBoxs.add(layout); 
			if(i<5){
			//设置图片
				if(StringUtils.equleIgnoreCase(srcs[i], src)){
					RadioButton rb=(RadioButton) layout.getChildAt(1);
					rb.setChecked(true);
				}
				if(i==4&&Tools.loadBitmapFromSdCard(srcs[i])!=null){
					((ImageView)layout.getChildAt(0)).setImageBitmap(Tools.loadBitmapFromSdCard(srcs[i]));
				 
				}else{
					((ImageView)layout.getChildAt(0)).setImageBitmap(imageLoader.loadFromAssests(srcs[i]));
				}
			}
				
		};
		
	}   
	//记录上一次为true的那个 如果取消了 可以返回上一个rb
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 
		rb1.setChecked(false);
	 	rb2.setChecked(false);
	 	rb3.setChecked(false);
	 	rb4.setChecked(false);
	 	rb5.setChecked(false);
	if(isChecked){
		
		SharedPreferences settings=getSharedPreferences("config", 0);
		SharedPreferences.Editor editor = settings.edit();
		switch (buttonView.getId()) {
		case R.id.theme1_btn:
			editor.putString("bgImg", "theme/theme1.jpg");
			break;
		case R.id.theme2_btn:
			editor.putString("bgImg", "theme/theme2.jpg");
			break;
		case R.id.theme3_btn:
			editor.putString("bgImg", "theme/theme3.jpg");
			break;
		case R.id.theme4_btn:
			editor.putString("bgImg", "theme/theme4.jpg");
			break;
		case R.id.theme5_btn:
			editor.putString("bgImg",srcs[4]);
			//Bitmap  bmp=imageLoader.loadFromSDCard(srcs[4]);
			break;
		}
		editor.commit();
		buttonView.setChecked(true);
		}
	}
	private void choicePic(){
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle("请选择")
		.setItems(new String[] { "从相册中选择" },
		new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,
									int which) {
				switch (which) {
				case 0:
					Log.d("test", ""+which);
					Intent i = new Intent();
					i.setAction(Intent.ACTION_GET_CONTENT);
					i.setType("image/*"); // 资源的MIME类型 image/jpeg
					startActivityForResult(i, CHOICEPICREQUEST);	
					break;
				case 1:
					Log.d("which", ""+which);
									
					break;
								}
						 	}
				}).create();
	dialog.show();
	dialog.setCanceledOnTouchOutside(false);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOICEPICREQUEST) { // 选择图片得到的响应
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Log.i("AAA", uri.toString());
				//通过uri获取图片路径
				String[] proj = { MediaStore.Images.Media.DATA };  
				Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);  
				int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);  
				actualimagecursor.moveToFirst(); 
				String img_path = actualimagecursor.getString(actual_image_column_index); 
				//压缩图片
				Bitmap bmp=Tools.compressLoadBitmap(img_path);
				Tools.compressAndSaveBitmapToSDCard(bmp, myImgPath, 60);
				mybgImg.setImageBitmap(bmp);
				SharedPreferences settings=getSharedPreferences("config", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("bgImg", myImgPath);
				editor.commit(); 
			}else{
				rb5.setChecked(false);
				
			}
		} 
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.theme5Img:
			choicePic();
			break;

		default:
			break;
		}
	}

}
