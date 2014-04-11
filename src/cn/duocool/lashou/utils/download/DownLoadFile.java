package cn.duocool.lashou.utils.download;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownLoadFile {
	private final static String TAG = DownLoadFile.class.getName();
	private String url = "";
	private Handler handler;
	private String fileName="anxinbao/myHead.png";
	public interface DownloadListener{
		void onComplete(File file);
		void onDownloading(int nowSize ,int readSize);
	}
	public DownLoadFile(String url,Handler handler) {
		this.url = url;
		this.handler = handler;
	}
	
	public DownLoadFile(String url ) {
		this.url = url;
	}
	
 
	public int startDownLoad(String targetFilePath) {
		int ret  = 0;
		final String tempFilePath = targetFilePath;
		new Thread() {
			public void run() {
				
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				HttpResponse response;
				try {
					response = client.execute(post);
					HttpEntity entity = response.getEntity();
					long totleSize = entity.getContentLength();
					InputStream is = entity.getContent();
					
					File targetFile  = new File(tempFilePath);
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					
					FileOutputStream fos = new FileOutputStream(targetFile);
					byte[] buffer = new byte[2048];
					int nowSize = 0;
					int readSize = 0;
					while((nowSize = is.read(buffer)) != -1) {
						fos.write(buffer,0,nowSize);
						readSize = nowSize + readSize;
						Log.d(TAG, "nowSize/readSize totleSize"+nowSize+"/"+readSize + " "+totleSize);
					}
					fos.flush();
					fos.close();
					
//					Bitmap bitmap = BitmapFactory.decodeStream(is);
					Log.d(TAG, "tempFilePath : "+tempFilePath);
					FileInputStream fis = new FileInputStream(tempFilePath);
					Bitmap bitmap = BitmapFactory.decodeStream(fis);
					Message msg = new Message();
					msg.what = 200;
					msg.obj = bitmap;
					handler.sendMessage(msg);
				
					is.close();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();
		
		ret = 200;
		return ret;
	}	
 
	/**
	 * 
	 *  hsiunien
	 *   asyncTask实现
	 *   返回null代表网络错误 
	 * @param downloadListener
	 * @param imageUrl [0] 为url [1]为本地 的路径 在sd卡根目录下的相对路径 
	 */
	public void startDownLoad(final DownloadListener downloadListener,String ...imageUrl) {
		
	 	if(imageUrl.length>0){
	 		if(imageUrl[0]!=null){
	 			url=imageUrl[0];
	 		}
	 	}
	 	if(imageUrl.length>1){
	 		fileName=imageUrl[1];
	 	}
		final String tempFilePath = Environment.getExternalStorageDirectory() +File.separator+fileName;
		 
		new  AsyncTask<String, Integer, File>() {
			@Override
			protected File doInBackground(String... params) {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				File targetFile=null;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long totleSize = entity.getContentLength();
					InputStream is = entity.getContent();
					  targetFile  = new File(tempFilePath);
					if(!targetFile.getParentFile().exists()){
						targetFile.getParentFile().mkdirs();
					}
					if (!targetFile.exists()) {
						targetFile.createNewFile();
					}
					
					FileOutputStream fos = new FileOutputStream(targetFile);
					byte[] buffer = new byte[2048];
					int nowSize = 0;
					int readSize = 0;
					while((nowSize = is.read(buffer)) != -1) {
						fos.write(buffer,0,nowSize);
						readSize = nowSize + readSize;
						publishProgress(nowSize,readSize);
						Log.d(TAG, "nowSize/readSize totleSize"+nowSize+"/"+readSize + " "+totleSize);
					}
					fos.flush();
					fos.close();				
					is.close();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
					return null;

				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
				
				return targetFile;
			}
			@Override
			protected void onProgressUpdate(Integer... values) {
				downloadListener.onDownloading(values[0], values[1]);
				super.onProgressUpdate(values);
			}
			@Override
			protected void onPostExecute(File file) {
				downloadListener.onComplete(file);
				super.onPostExecute(file);
			}
		}.execute(); 
	}	
	public String getUrl(){
		return url;
	}
	public void setFileName(String fileName){
		this.fileName=fileName;
	}
}
