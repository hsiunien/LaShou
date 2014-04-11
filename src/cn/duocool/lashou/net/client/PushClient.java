package cn.duocool.lashou.net.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.util.Log;

import cn.duocool.lashou.utils.Tools;
import com.google.gson.Gson;

/**
 * 服务器连接用的客户端
 * @author xwood
 */
public class PushClient extends Thread {
	
	private final static String TAG = PushClient.class.getName();
	
	public boolean isRunning = true;
	
	private String userId;
	
	public static List<Cookie> cookies = new ArrayList<Cookie>();
	
	private Context context;
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public synchronized void reStart() {
		this.interrupt();
	}

	@Override
	public void run() {
		try {
			while (isRunning) {
				// 网络通畅
				if (Tools.checkNetWorkIsConnect(context) != 0) {
					// 设定了用户ID的场合 才开始发送请求
					userId = Tools.getApplication(context).getMyInfo().getUserId() + "";
					if (null != userId && (!"".equals(userId.trim())) && (!"0".equals(userId.trim()))) {
						pushedStartInUser(userId);
					}
					Thread.sleep(10);
				} else { // 网络部通畅的场合
					Thread.sleep(1000);
				}
			}
		} catch (InterruptedException e) {
			run();
		}
	}

	private PushListener pushListener;
	
	public PushClient() {
	}
	public PushClient(Context context) {
		this.context = context;
	}

	public PushListener getPushListener() {
		return pushListener;
	}

	public void setPushListener(PushListener pushListener) {
		this.pushListener = pushListener;
	}

	// 命名空间 
    String nameSpace = "http://services.server.axb.com/";  
    // 调用的方法名称   
    String methodName = "download";
    // EndPoint   
//    String baseEndPoint = "http://192.168.26.101:8080/axbserver/";
//     String baseEndPoint = "http://115.29.172.16/axbserver/" ;
     String baseEndPoint = NetClient.baseEndPoint ;
//    String baseEndPoint = "http://10.0.2.2:8080/axbserver/";
//    /String endPoint = "http://10.0.2.2:8080/axbserver/CXFService/AXBServiceBean";  
    String endPoint = baseEndPoint+"CXFService/serviceForAndroid/as/";
	
    /**
	 * 推送方法开始
	 */
	private void pushedStartInUser(String userId) {
		
		String requerUrl = baseEndPoint+"Comet";
		if (null != userId && (!("".equals(userId.trim()))))  {
			requerUrl = requerUrl + "?userId="+userId;
		}
		
		Log.i(TAG, "address:"+requerUrl);

		Log.i(TAG, " key:"+userId+" value:"+userId);

		
		HttpGet get = new HttpGet(requerUrl);
		
//		HttpContext context = new BasicHttpContext();
//		CookieStore cookieStore = new BasicCookieStore();
//
//		context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		//		post.addHeader("Accept","application/xml");
		get.addHeader("Connection", "Keep-Alive");
//		get.addHeader("Cookie", "session_id=1234567890");
		//		post.addHeader("Content-Type", "application/xml");
		
		DefaultHttpClient client = new DefaultHttpClient();
             
		try {
			for (Cookie cookie:cookies) {
				client.getCookieStore().addCookie(cookie);
			}
			
			HttpResponse response = client.execute(get);
			int code = response.getStatusLine().getStatusCode();
			Log.i(TAG, "code:"+code);
			if (code == 200) {
				
				List<Cookie> nowCookies = client.getCookieStore().getCookies();
				Log.i(TAG,"nowCookies.isEmpty():"+nowCookies.isEmpty());
				 if (!nowCookies.isEmpty()) {
					 cookies.clear();
					 for (int i = nowCookies.size()-1; i >= 0; i --) {
			        	 Cookie cookie = nowCookies.get(i);
			        	 cookies.add(cookie);
			        	 Log.i(TAG,"cookie:"+cookie.getName() + "  " + cookie.getValue());
			         }
				 }
				InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
                String strLine = null; 
                Gson gson = new Gson();
                while(null != (strLine = br.readLine())) {
                	
                	PushData pushData = gson.fromJson(strLine, PushData.class);
                	
                	if (null != pushListener) {
                		pushListener.onTransmitted(pushData);
                	}
                	
         	        Log.i(TAG, "response strLine:"+strLine + " ||||||||| " + pushData.toString());
                }
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
//	/**
//	 * 推送方法开始
//	 */
//	private void pushedStart() {
//		pushedStartInUser(null);
//	}
}
