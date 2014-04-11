package cn.duocool.lashou.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import cn.duocool.lashou.model.MyApplication;

public class Tools {
	/**
	 * 
	 * @param bitmap
	 * @param fileName
	 */
	public static void bitm2png(Bitmap bitmap, String fileName) {
		String saveFilePath = Environment.getExternalStorageDirectory()
				+ File.separator + fileName;
		System.out.println(saveFilePath);

		File bitmapFile = new File(saveFilePath);
		if (bitmapFile.getParentFile() != null
				&& !bitmapFile.getParentFile().exists()) {
			bitmapFile.getParentFile().mkdirs();
		}
		FileOutputStream bitmapWtriter = null;
		try {
			bitmapWtriter = new FileOutputStream(bitmapFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, bitmapWtriter);
	}

	public static void bitm2jpg(Bitmap bitmap, String fileName) {
		String saveFilePath = Environment.getExternalStorageDirectory()
				+ File.separator + fileName;
		System.out.println(saveFilePath);

		File bitmapFile = new File(saveFilePath);
		if (bitmapFile.getParentFile() != null
				&& !bitmapFile.getParentFile().exists()) {
			bitmapFile.getParentFile().mkdirs();
		}
		FileOutputStream bitmapWtriter = null;
		try {
			bitmapWtriter = new FileOutputStream(bitmapFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bitmapWtriter);
	}

	/**
	 * byte->bitmap
	 * 
	 * @param bytes
	 * @param opts
	 * @return
	 */
	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}

	/**
	 * inputStream to byte;
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	/**
	 * 压缩文件后存放到SD卡中
	 * 
	 * @param rawBitmap
	 * @param fileName
	 * @param quality
	 * @return
	 */
	public static String compressAndSaveBitmapToSDCard(Bitmap rawBitmap,
			String fileName, int quality) {
		String saveFilePath = Environment.getExternalStorageDirectory()
				+ File.separator + fileName;
		File saveFile = new File(saveFilePath);
		if (saveFile.exists()) {
			saveFile.delete();
		}

		try {
			saveFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
			if (fileOutputStream != null) {
				// imageBitmap.compress(format, quality, stream);
				// 把位图的压缩信息写入到一个指定的输出流中
				// 第一个参数format为压缩的格式
				// 第二个参数quality为图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩
				// 第三个参数stream为输出流
				rawBitmap.compress(Bitmap.CompressFormat.JPEG, quality,
						fileOutputStream);
			}
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return saveFilePath;
	}

	
	/**
	 * 加载一张图片 按照压缩比例
	 * @return
	 */
	public static Bitmap compressLoadBitmap(String path){
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		BitmapFactory.decodeFile(path, options);
		int height = options.outHeight;
        int width = options.outWidth; 
        int inSampleSize = 1;
        int reqHeight=800;
        int reqWidth=480;
        /*
         * heightRatio是图片原始高度与压缩后高度的倍数，widthRatio是图片原始宽度与压缩后宽度的倍数
        */
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
      options.inSampleSize =inSampleSize;
      options.inJustDecodeBounds = false;
      Bitmap bitmap= BitmapFactory.decodeFile(path, options);
      return bitmap;
	}
	/**
	 * 从sd卡中加载图片 /mnt/sdcard/ 目录下开始算起
	 * 
	 * @return
	 */
	public static Bitmap loadBitmapFromSdCard(String fileName) {
		String filePath = Environment.getExternalStorageDirectory()
				+ File.separator + fileName;
		Bitmap bmp = BitmapFactory.decodeFile(filePath);
		return bmp;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		Pattern emailer = Pattern
				.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		if (StringUtils.isBlank(email))
			return false;
		email = email.toLowerCase();
		if (email.endsWith(".con"))
			return false;
		if (email.endsWith(".cm"))
			return false;
		if (email.endsWith("@gmial.com"))
			return false;
		if (email.endsWith("@gamil.com"))
			return false;
		if (email.endsWith("@gmai.com"))
			return false;
		return emailer.matcher(email).matches();
	}

	public static MyApplication getApplication(Context context) {
		return (MyApplication) context.getApplicationContext();
	}

	/**
	 * 检查用户是否打开GPS,返回GPS打开状态（true 表示 GPS 已打开）
	 * 
	 * @author 杞桅
	 * @return boolean
	 */
	public static boolean check_GPS_is_open(Context context) {
		LocationManager alm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			// GPS已经打开
			return true;
		} else {
			// GPS没有打开
			return false;
		}
	}
	
	/**
	 * 检查是否有网络（wifi 3G 2G）
	 * @param context
	 * @return 0 没有网络  1 wifi 2 3G/2G
	 */
	public static int checkNetWorkIsConnect(Context context) {
		
		ConnectivityManager manager 
			= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		
		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
			return 2;
		}
		
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			return 1;
		}
		return 0;
	}

	// 获取指定Activity的截屏，保存到png文件
	public static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		Log.i("TAG", "" + statusBarHeight);

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	// 保存到sdcard
	public static void savePic(Bitmap b, String strFileName) {
		String path = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdPath = Environment.getExternalStorageDirectory();
			path = sdPath.getPath() + "/anxinbao/ElectronicFence/";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
