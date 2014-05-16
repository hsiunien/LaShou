package cn.duocool.lashou.utils.download;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import cn.duocool.lashou.model.UserInfo;
import cn.duocool.lashou.utils.Log;
import cn.duocool.lashou.utils.StringUtils;
import cn.duocool.lashou.utils.download.DownLoadFile.DownloadListener;

public class ImageLoader {
	private String tag = ImageLoader.class.toString();
	private Context context;
	private ImageView imageView;
	private Bitmap bmp;
	private UserInfo userInfo;
	private String url;
	private DownLoadFile downloader;
	public static String downloadSrc = "anxinbao/";// 文件保存目录

	// private
	// 缓存大小 8M
	public ImageLoader(Context context) {
		this.context = context;
	}

	private final static int hardCachedSize = 8 * 1024 * 1024;
	static LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(
			hardCachedSize) {
	};

	public Bitmap getImage(String url) {
		Bitmap bitmap = null;
		this.url = url;
		// 从缓存中加载
		// bitmap=imageCache.get(getFileName(url));
		// 如果从内存中 缓存中 sd卡加载都是为空 则 下载图片 交给downloader来处理
		if ((bitmap = imageCache.get(getFileName(url))) == null
				&& (bitmap = loadFromSDCard(url)) == null) {
			downloader = new DownLoadFile(url);
		}
		return bitmap;
	}

	public Bitmap loadFromAssests(String fileName){
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(context.getResources().getAssets().open(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return bmp;
	}
	public void setDownloadCallBack(final ImageView imageView) {
		this.imageView = imageView;
		Bitmap bitmap = getImage(url);
		//直接读取
		if (bitmap != null) {
			this.bmp=bitmap;
			this.imageView.setImageBitmap(bitmap);
		} else {
			if (downloader == null) {
				downloader = new DownLoadFile(url);
			}
			downloader.startDownLoad(downloadListener, downloader.getUrl(),
					downloadSrc + getFileName(downloader.getUrl()));
		}
	}

	private DownloadListener downloadListener = new DownloadListener() {

		@Override
		public void onDownloading(int nowSize, int readSize) {

		}

		@Override
		public void onComplete(File file) {
			Log.d(this, "complete");
			if (file == null) {
				return;
			}
 
			bmp = BitmapFactory.decodeFile(file.getPath());
			if (imageView != null) {
				imageView.setImageBitmap(bmp);
			}
			if(userInfo!=null){
				userInfo.setHeadImg(bmp);
			}
            Log.d(this,url+" bitmap是空?"+(bmp==null));

            if(bmp!=null){
                putImage(url, bmp);
            }
			// imageCache.put(getFileName(url), bmp);
		}
	};

	/**
	 * 通过url转换成需要保存的文件名
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileName(String url) {
		String fileName = "" + url.hashCode() + StringUtils.getExpansion(url);
		return fileName;
	}

	/**
	 * 从sd卡中加载一张从网络下载的图片 文件名是hashcode
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap loadFromSDCard(String url) {
		// String fileName=""+url.hashCode()+StringUtils.getExpansion(url);
		// 从sd卡中查找文件是否存在
		File f = new File(Environment.getExternalStorageDirectory()
				+ File.separator + downloadSrc + getFileName(url));
		// f=new File(Environment.getExternalStorageDirectory()
		// +File.separator+"anxinbao/myHead.png");
		if (f.exists()) {
			return BitmapFactory.decodeFile(f.getPath());
		} else {
			return null;
		}
	}

	public void downloadImage(final ImageView imageView) {
		setDownloadCallBack(imageView);
	}

	public void downloadHead(final UserInfo userInfo) {
		this.userInfo = userInfo;
		setDownloadCallBack(imageView);
	}

	public void putImage(String url, Bitmap bitmap) {
		synchronized (imageCache) {
			if (imageCache.get(getFileName(url)) == null) {
				imageCache.put(getFileName(url), bitmap);
			}
		}
	}

}
