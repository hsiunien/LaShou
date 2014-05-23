package cn.duocool.lashou.net.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import cn.duocool.lashou.CommDef;
import cn.duocool.lashou.utils.Log;
import cn.duocool.lashou.utils.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 服务器连接用的客户端
 *
 * @author xwood
 */
public class NetClient {

    private final static String TAG = NetClient.class.getName();
    private NetTranListener netTranListener;
    private NetTranProgressListener netTranProgressListener;

    private final String RET_OK = "OK";
    private final String RET_BAD = "BAD";

    private String tempDir = Environment.getExternalStorageDirectory() + "/anxinbao/temp"; // 临时文件夹

    // 命名空间
    String nameSpace = "http://services.server.axb.com/";
    public static String baseEndPoint = CommDef.serverAddress;
    String endPoint = baseEndPoint + "CXFService/serviceForAndroid/as/";

    public final static String[] methods = {
            "userRegister", // 0
            "up0101A.do", // 1
            "dl0101A.do", // 2
            "getUserInfo", // 3
            "getLocation", // 4
            "getLocationList", // 5
            "uploadLocation", // 6
            "getUserInfoByToken", // 7
            "getRelationList.do", // 8
            "getRelation", // 9
            "addRelation", // 10
            "addRelationByEmail", // 11
            "addRelationByPhone", // 12
            "delRelation", // 13
            "updateRelation", // 14
            "checkExistEmail", // 15
            "checkExistPhone", // 16
            "checkExistUID", // 17
            "updateUserInfo", // 18
            "getUserInfoByEmailPwd",  // 19
            "getUserInfoByPhonePwd", // 20
            "ra.do", // 21
            "advsSubmitA.do", // 22
            "getAppInfoA.do", // 23
            "dl0201A.do", // 24
            "getLocationByTime" // 25

    };


    public NetClient() {
    }

    public void setOnNetTranListener(NetTranListener netTranListener) {
        this.netTranListener = netTranListener;
    }

    public NetTranListener getNetTranListener() {
        return netTranListener;
    }

    public NetTranProgressListener getNetTranProgressListener() {
        return netTranProgressListener;
    }

    public void setOnNetTranProgressListener(
            NetTranProgressListener netTranProgressListener) {
        this.netTranProgressListener = netTranProgressListener;
    }

    /**
     * 注册用户信息
     *
     * @param regData 注册数据
     */
    public void userRegister(final int requestCode, final RegData regData) {

        // "userRegister"
        Gson gson = new Gson();
        String json = gson.toJson(regData);

        Map<String, String> submitParams = new HashMap<String, String>();
        submitParams.put("regData", json);

        Log.i(TAG, "key/value: " + regData + " / " + json);

        submit(requestCode, 0, submitParams);
    }

    /**
     * 更新用户信息
     *
     * @param regData 注册数据
     */
    public void updateUserInfo(final int requestCode, final RegData regData) {

        // "updateUserInfo" 18
        Gson gson = new Gson();
        String json = gson.toJson(regData);

        Map<String, String> submitParams = new HashMap<String, String>();
        submitParams.put("regData", json);

        Log.i(TAG, "key/value: " + regData + " / " + json);

        submit(requestCode, 18, submitParams);
    }


    /**
     * 上传头像
     *
     * @param #requestCode regData 注册数据
     */
    public void uploadHeadIcon(final int requestCode, final UploadData uploadData) {
        // Params, Progress, Result
        new AxbNetAsyncTask<Integer, Map<String, String>, ResponseData>() {

            @Override
            protected ResponseData doInBackground(Integer... params) {
//				String methodName = "up0101A.do";

                HttpPost post = new HttpPost(baseEndPoint + methods[1]);

                Log.i(TAG, "address:" + baseEndPoint + methods[1]);

                AxbMultipartEntity entity = null;

//				post.addHeader("Accept","application/xml");
                post.addHeader("Connection", "Keep-Alive");
//				post.addHeader("Content-Type", "application/xml");

                HttpClient client = new DefaultHttpClient();

                try {
                    File uploadFile = null;
                    InputStream fileStream = null;
                    if (null != uploadData.getHeadIconPath()
                            && !("".equals(uploadData.getHeadIconPath().trim()))) {
                        uploadFile = new File(uploadData.getHeadIconPath());
                        fileStream = new FileInputStream(uploadFile);
                    } else {
                        Bitmap bitmap = uploadData.getBitmap();
                        if (null != bitmap) {
                            String filePath = PicTool.compressAndSaveBitmapToSDCard(bitmap, "axbtemppicFile.jpg", 70);
                            uploadFile = new File(filePath);
                            fileStream = new FileInputStream(uploadFile);
                        }
                    }
                    entity = new AxbMultipartEntity(requestCode, fileStream.available()); //文件传输
                    entity.setTask(this);
                    entity.addPart("upload", new FileBody(uploadFile)); // <input type="file" name="userfile" />  对应的
                    entity.addPart("userId", new StringBody(String.valueOf(uploadData.getUserId())));

                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                post.setEntity(entity);


                try {
                    HttpResponse response = client.execute(post);
                    int code = response.getStatusLine().getStatusCode();
                    Log.i(TAG, "code:" + code);
                    if (code == 200) {
                        StringBuilder builder = new StringBuilder();
                        InputStream is = response.getEntity().getContent();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String strLine = null;
                        while (null != (strLine = br.readLine())) {
                            builder.append(strLine);
                        }

                        Log.i(TAG, "response:" + builder.toString());

                        ResponseData rd = new ResponseData();
                        rd.setResponseStatus(RET_OK);
                        rd.setResponseMsg("OK");
                        rd.setUserId(String.valueOf(uploadData.getUserId()));
                        return rd;
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ResponseData rd = new ResponseData();
                rd.setResponseStatus(RET_BAD);
                rd.setResponseMsg("注册失败！");
                rd.setUserId("");
                return rd;
            }

            @Override
            protected void onPostExecute(ResponseData result) {
                if (null != netTranListener) {
                    netTranListener.onTransmitted(requestCode, result);
                }
            }

            @Override
            protected void onProgressUpdate(Map<String, String>... values) {
                if (null != netTranProgressListener) {
                    Map<String, String> map = values[0];
                    int code = Integer.valueOf(map.get("requestCode"));
                    long transferred = Long.valueOf(map.get("transferred"));
                    long maxSize = Long.valueOf(map.get("maxSize"));
                    netTranProgressListener.onTransmitting(code, transferred, maxSize);
                }
            }

            @Override
            public void taskDoing(int requestCode, long transferred, long maxSize) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("requestCode", String.valueOf(requestCode));
                map.put("transferred", String.valueOf(transferred));
                map.put("maxSize", String.valueOf(maxSize));

                Map<String, String>[] maps = new Map[1];
                maps[0] = map;
                publishProgress(maps);
            }
        }.execute(requestCode);
    }

    /**
     * 下载头像
     *
     * @param requestCode 注册数据
     */
    public void downloadHeadIcon(final int requestCode, final String userId) {
        // Params, Progress, Result
        new AxbNetAsyncTask<Integer, Map<String, String>, ResponseData>() {

            @Override
            protected ResponseData doInBackground(Integer... params) {
//				String methodName = "dl0101A.do";

                HttpPost post = new HttpPost(baseEndPoint + methods[2]);

                Log.i(TAG, "address:" + baseEndPoint + methods[2]);

//				post.addHeader("Accept","application/xml");
                post.addHeader("Connection", "Keep-Alive");
//				post.addHeader("Content-Type", "application/xml");

                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                NameValuePair nvp = new BasicNameValuePair("d", userId);
                parameters.add(nvp);
                Log.i(TAG, "key/value: " + "d / " + userId);

                HttpClient client = new DefaultHttpClient();

                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                post.setEntity(entity);

                HttpResponse response = null;
                try {
                    response = client.execute(post);
                } catch (ClientProtocolException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (null == response) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseMsg("下载失败！");
                    rd.setUserId(userId);
                    return rd;
                }

                int code = response.getStatusLine().getStatusCode();
                Log.i(TAG, "code:" + code);
                if (code == 200) {
                    try {
                        response = client.execute(post);

                        // 获得下载文件信息
                        Header[] headers = response.getAllHeaders();

                        String extName = "";
                        for (Header header : headers) {
                            String name = header.getName();
                            String value = header.getValue();
                            // Content-Disposition filename="E:\\uploadfiles\\aa.png"
                            if (name.equals("Content-Disposition")) {
                                extName = value.substring(value.lastIndexOf("."), value.length() - 1);
                                Log.i(TAG, "extName : " + extName);
                            }
                            Log.i(TAG, "name-value : " + name + "-" + value);
                        }

                        HttpEntity retEntity = response.getEntity();
                        long totleSize = retEntity.getContentLength();
                        InputStream is = retEntity.getContent();
                        Long nowTime = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String nowTimeStr = sdf.format(new Date(nowTime));
                        String localTempFileDirStr = tempDir + "/downloadTempPic/" + userId;
                        File localTempDir = new File(localTempFileDirStr);
                        if (!localTempDir.exists()) {
                            localTempDir.mkdirs();
                        }
                        File targetFile = new File(localTempFileDirStr + "/t" + nowTimeStr + extName);
                        Log.i(TAG, "localFilePath : " + localTempFileDirStr + "/t" + nowTimeStr + extName);
                        if (!targetFile.exists()) {
                            targetFile.createNewFile();
                        }

                        FileOutputStream fos = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[2048];
                        int nowSize = 0;
                        int readSize = 0;
                        while ((nowSize = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, nowSize);
                            readSize = nowSize + readSize;
                            Log.d(TAG, "nowSize/readSize totleSize" + nowSize + "/" + readSize + " " + totleSize);
                            taskDoing(requestCode, readSize, totleSize);
                        }
                        fos.flush();
                        fos.close();

//							Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Log.d(TAG, "tempFilePath : " + targetFile.toString());
                        FileInputStream fis = new FileInputStream(targetFile);
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        fis.close();

                        ResponseData rd = new ResponseData();
                        rd.setResponseMsg("下载完成！");
                        rd.setUserId(userId);
                        rd.setFilePath(targetFile.toString());
                        rd.setBitmap(bitmap);
                        return rd;
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ResponseData rd = new ResponseData();
                rd.setResponseMsg("下载失败！");
                rd.setUserId(userId);
                return rd;
            }

            @Override
            protected void onPostExecute(ResponseData result) {
                if (null != netTranListener) {
                    netTranListener.onTransmitted(requestCode, result);
                }
            }

            @Override
            protected void onProgressUpdate(Map<String, String>... values) {
                if (null != netTranProgressListener) {
                    Map<String, String> map = values[0];
                    int code = Integer.valueOf(map.get("requestCode"));
                    long transferred = Long.valueOf(map.get("transferred"));
                    long maxSize = Long.valueOf(map.get("maxSize"));
                    netTranProgressListener.onTransmitting(code, transferred, maxSize);
                }
            }

            @Override
            public void taskDoing(int requestCode, long transferred, long maxSize) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("requestCode", String.valueOf(requestCode));
                map.put("transferred", String.valueOf(transferred));
                map.put("maxSize", String.valueOf(maxSize));

                Map<String, String>[] maps = new Map[1];
                maps[0] = map;
                publishProgress(maps);
            }
        }.execute(requestCode);
    }

    /**
     * 下载头像
     *
     * @param userId 注册数据
     */
    public void downloadHeadIconInStream(final int requestCode, final String userId) {
        // Params, Progress, Result
        new AsyncTask<Integer, Map<String, String>, ResponseData>() {

            @Override
            protected ResponseData doInBackground(Integer... params) {
//				String methodName = "dl0101A.do";

                HttpPost post = new HttpPost(baseEndPoint + methods[2]);

                Log.i(TAG, "address:" + baseEndPoint + methods[2]);

//				post.addHeader("Accept","application/xml");
                post.addHeader("Connection", "Keep-Alive");
//				post.addHeader("Content-Type", "application/xml");

                List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                NameValuePair nvp = new BasicNameValuePair("d", userId);
                parameters.add(nvp);
                Log.i(TAG, "key/value: " + "d / " + userId);

                HttpClient client = new DefaultHttpClient();

                UrlEncodedFormEntity entity = null;
                try {
                    entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

                post.setEntity(entity);

                HttpResponse response = null;
                try {
                    response = client.execute(post);
                } catch (ClientProtocolException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (null == response) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseMsg("下载失败！");
                    rd.setUserId(userId);
                    return rd;
                }

                int code = response.getStatusLine().getStatusCode();
                Log.i(TAG, "code:" + code);
                if (code == 200) {
                    try {
                        response = client.execute(post);
                        HttpEntity retEntity = response.getEntity();
                        InputStream is = retEntity.getContent();

                        ResponseData rd = new ResponseData();
                        rd.setInputStream(is);
                        return rd;
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ResponseData rd = new ResponseData();
                rd.setResponseMsg("下载失败！");
                rd.setUserId(userId);
                return rd;
            }

            @Override
            protected void onPostExecute(ResponseData result) {
                if (null != netTranListener) {
                    netTranListener.onTransmitted(requestCode, result);
                }
            }
        }.execute(requestCode);
    }

    /**
     * 下载APK
     *
     * @param requestCode 注册数据
     */
    public void downloadApk(final int requestCode) {
        // Params, Progress, Result
        new AsyncTask<Integer, Map<String, String>, ResponseData>() {

            @Override
            protected ResponseData doInBackground(Integer... params) {
//				String methodName = "dl0101A.do";

                HttpPost post = new HttpPost(baseEndPoint + methods[24]);

                Log.i(TAG, "address:" + baseEndPoint + methods[24]);

//				post.addHeader("Accept","application/xml");
                post.addHeader("Connection", "Keep-Alive");
//				post.addHeader("Content-Type", "application/xml");

                HttpClient client = new DefaultHttpClient();


                HttpResponse response = null;
                try {
                    response = client.execute(post);
                } catch (ClientProtocolException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (null == response) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseMsg("下载失败！");
                    return rd;
                }

                int code = response.getStatusLine().getStatusCode();
                Log.i(TAG, "code:" + code);
                if (code == 200) {
                    try {
                        response = client.execute(post);

                        // 获得下载文件信息
                        Header[] headers = response.getAllHeaders();

                        String fileName = "";
                        for (Header header : headers) {
                            String name = header.getName();
                            String value = header.getValue();
                            // Content-Disposition filename="E:\\uploadfiles\\aa.png"
                            if (name.equals("Content-Disposition")) {
                                fileName = value;
                                Log.i(TAG, "fileName : " + fileName);
                                break;
                            }
                        }

                        HttpEntity retEntity = response.getEntity();
                        long totleSize = retEntity.getContentLength();
                        InputStream is = retEntity.getContent();
                        Long nowTime = System.currentTimeMillis();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        String nowTimeStr = sdf.format(new Date(nowTime));
                        String localTempFileDirStr = tempDir + "/downloadTempApk/";
                        File localTempDir = new File(localTempFileDirStr);
                        if (!localTempDir.exists()) {
                            localTempDir.mkdirs();
                        }
                        File targetFile = new File(localTempFileDirStr + "_" + nowTimeStr + "_" + fileName);
                        Log.i(TAG, "localFilePath : " + localTempFileDirStr + "_" + nowTimeStr + "_" + fileName);
                        if (!targetFile.exists()) {
                            targetFile.createNewFile();
                        }

                        FileOutputStream fos = new FileOutputStream(targetFile);
                        byte[] buffer = new byte[2048];
                        int nowSize = 0;
                        int readSize = 0;
                        while ((nowSize = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, nowSize);
                            readSize = nowSize + readSize;
                            Log.d(TAG, "nowSize/readSize totleSize" + nowSize + "/" + readSize + " " + totleSize);

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("requestCode", String.valueOf(requestCode));
                            map.put("transferred", String.valueOf(readSize));
                            map.put("maxSize", String.valueOf(totleSize));

                            Map<String, String>[] maps = new Map[1];
                            maps[0] = map;
                            publishProgress(maps);
//							taskDoing(requestCode,readSize,totleSize);
                        }
                        fos.flush();
                        fos.close();
                        //is.close();
                        retEntity.consumeContent();
                        post.abort();

//						client.getConnectionManager().shutdown();

                        ResponseData rd = new ResponseData();
                        rd.setResponseStatus(RET_OK);
                        rd.setResponseMsg("下载完成！");
                        rd.setFilePath(targetFile.toString());
                        return rd;
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ResponseData rd = new ResponseData();
                rd.setResponseStatus(RET_BAD);
                rd.setResponseMsg("下载失败！");
                return rd;
            }

            @Override
            protected void onPostExecute(ResponseData result) {
                if (null != netTranListener) {
                    netTranListener.onTransmitted(requestCode, result);
                }
            }

            @Override
            protected void onProgressUpdate(Map<String, String>... values) {
                if (null != netTranProgressListener) {
                    Map<String, String> map = values[0];
                    int code = Integer.valueOf(map.get("requestCode"));
                    long transferred = Long.valueOf(map.get("transferred"));
                    long maxSize = Long.valueOf(map.get("maxSize"));
                    netTranProgressListener.onTransmitting(code, transferred, maxSize);
                }
            }

//			@Override
//			public void taskDoing(int requestCode,long transferred,long maxSize) {
//				Map<String,String> map = new HashMap<String,String>();
//				map.put("requestCode", String.valueOf(requestCode));
//				map.put("transferred", String.valueOf(transferred));
//				map.put("maxSize", String.valueOf(maxSize));
//				
//				Map<String,String>[] maps = new Map[1];
//				maps[0] = map;
//				publishProgress(maps);
//			}
        }.execute(requestCode);
    }

    /**
     * 注册用户信息
     *
     * @param requestCode 注册数据
     */
    public void getUserInfo(final int requestCode, final String userId) {

        // 3 getUserInfo
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);

        Log.i(TAG, "userId:" + userId);

        submit(requestCode, 3, param);
    }

    /**
     * 获得用户的位置
     *
     * @param userId 指定用户的位置
     */
    public void getLocation(final int requestCode, final String userId) {

        // "getLocation", // 4

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);

        Log.i(TAG, "userId:" + userId);

        submit(requestCode, 4, param);
    }

    /**
     * 获得用户的位置(指定用户，指定条数)
     *
     * @param userId 指定用户的位置
     * @param count  获得数据的条数
     */
    public void getLocationList(final int requestCode, final String userId, final String count) {
        // "getLocationList", // 5
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("count", count);

        Log.i(TAG, "userId + count" + userId + " + " + count);

        submit(requestCode, 5, param);
    }

    /**
     * 获得用户的位置(指定用户，指定时间段)
     *
     * @param userId      指定用户的位置
     * @param requestCode 获得数据的条数
     */
    public void getLocationByTime(final int requestCode, final String userId, final String minTime, final String maxTime) {
        // "getLocationByTime", // 25
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("minTime", minTime);
        param.put("maxTime", maxTime);

        Log.i(this, "getLocationByTime" + userId + " " + minTime + " " + minTime);
        Log.d("测试输出");

        submit(requestCode, 25, param);
    }

    /**
     * 获得用户的位置(指定用户，指定条数) 次方法为同步方法，用于在子线程中使用
     *
     * @param userId 指定用户的位置
     * @param count  获得数据的条数
     */
    public ResponseData getLocationListBySync(final String userId, final String count) {
        // "getLocationList", // 5
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("count", count);

        Log.i(TAG, "getLocationListBySync userId + count" + userId + " + " + count);

        ResponseData responseData = submitBySync(5, param);
        return responseData;
    }

    /**
     * 获得用户的位置(指定用户，指定时间段)
     *
     * @param userId  指定用户的位置
     * @param minTime 获得数据的条数
     */
    public ResponseData getLocationByTimeSync(final String userId, final String minTime, final String maxTime) {
        // "getLocationByTime", // 25
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("minTime", minTime);
        param.put("maxTime", maxTime);

        Log.i(TAG, "getLocationByTimeSync" + userId + " " + minTime + " " + minTime);
        ResponseData responseData = submitBySync(25, param);
        return responseData;
//		submit(requestCode,25,param);
    }

    /**
     * 上传用户的位置(同步版)
     *
     * @param userId       指定用户
     * @param locationData 位置对象
     */
    public ResponseData uploadLocationSync(final String userId, final LocationData locationData) {
        // "uploadLocation", // 6
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        Gson gson = new Gson();
        String jsonLocationData = gson.toJson(locationData);
        param.put("locationData", jsonLocationData);

        Log.e(TAG, "uploadLocationSync userId --》locationData" + userId + "---》" + locationData.toString());

        ResponseData responseData = submitBySync(6, param);
        return responseData;
    }

    /**
     * 上传用户的位置
     *
     * @param userId       指定用户
     * @param locationData 位置对象
     */
    public void uploadLocation(final int requestCode, final String userId, final LocationData locationData) {
        // "uploadLocation", // 6
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        Gson gson = new Gson();
        String jsonLocationData = gson.toJson(locationData);
        param.put("locationData", jsonLocationData);

        Log.i(TAG, "userId --》locationData" + userId + "---》" + locationData.toString());

        submit(requestCode, 6, param);
    }


    /**
     * 根据用户uid 和 token 获得用户信息
     *
     * @param uid
     * @param token
     * @param type  1 sina 2 qq
     */
    public void getUserInfoByToken(final int requestCode, final String uid, final String token, final String type) {

        // "getUserInfoByToken", // 7
        Map<String, String> param = new HashMap<String, String>();
        param.put("uid", uid);
        param.put("token", token);
        param.put("type", type);

        Log.i(TAG, "uid:" + uid + "token" + token + "type:" + type);

        submit(requestCode, 7, param);
    }


    /**
     * 根据用户 邮箱 密码 获得用户信息
     *
     * @param requestCode
     * @param email
     * @param pwd         1 sina 2 qq
     */
    public void getUserInfoByEmailPwd(final int requestCode, final String email, final String pwd) {

        // "getUserInfoByEmailPwd", // 19
        Map<String, String> param = new HashMap<String, String>();
        param.put("email", email);
        param.put("pwd", pwd);

        Log.i(TAG, "email:" + email + " pwd" + pwd);

        submit(requestCode, 19, param);
    }

    /**
     * 根据用户 电话  密码 获得用户信息
     *
     * @param requestCode
     * @param pwd
     * @param phone       1 sina 2 qq
     */
    public void getUserInfoByPhonePwd(final int requestCode, final String phone, final String pwd) {

        // "getUserInfoByPhonePwd", // 20
        Map<String, String> param = new HashMap<String, String>();
        param.put("phone", phone);
        param.put("pwd", pwd);

        Log.i(TAG, "phone:" + phone + " pwd" + pwd);

        submit(requestCode, 20, param);
    }

    /**
     * 获得好友列表
     *
     * @param requestCode
     * @param userId
     */
    public void getRelationList(final int requestCode, final String userId) {
        // "getRelationList", // 8
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);

        Log.i(TAG, "userId:" + userId);

        submit(requestCode, 8, param);
    }

    /**
     * 获得指定两个用户之间的关系
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param friendId    对方的ID
     */
    public void getRelation(final int requestCode, final String userId, final String friendId) {
        // "getRelation", // 9
        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("friendId", friendId);

        Log.i(TAG, "userId:" + userId + "friendId:" + friendId);

        submit(requestCode, 9, param);
    }

    /**
     * 请求添加好友(邮箱)
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param email       对方的邮箱
     */
    public void requestAddFriendByEmail(final int requestCode, final String userId, final String email) {
        // "addRelation", // 10
//		String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + "email:" + email);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
//		param.put("toUserId", friendId);
//		param.put("phone", friendId);
        param.put("email", email);
//		param.put("result", friendId);
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限
        param.put("type", "0");

        submit(requestCode, 21, param);
    }

    /**
     * 变更查看权限
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param friendId    好友的Id
     * @param role        3 可以查看  1 不可以查看
     */
    public void changViewRole(final int requestCode, final String userId, final String friendId, int role) {
        // "ra.do", // 21
        String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + "friendId:" + friendId + " role:" + role);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("toUserId", friendId);
//		param.put("phone", friendId);
//		param.put("email", email);
//		param.put("result", friendId);
        param.put("changeRole", String.valueOf(role));
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限  5 :变更权限
        param.put("type", "5");

        submit(requestCode, 21, param);
    }

    /**
     * 请求添加好友(电话)
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param phone       对方的电话
     */
    public void requestAddFriendByPhone(final int requestCode, final String userId, final String phone) {
        // "addRelation", // 10
//		String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + "phone:" + phone);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
//		param.put("toUserId", friendId);
        param.put("phone", phone);
//		param.put("email", email);
//		param.put("result", friendId);
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限
        param.put("type", "1");

        submit(requestCode, 21, param);
    }

    /**
     * 响应请求添加好友
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param toUserId    对方的id
     * @param result      Y 同意   N 不同意
     */
    public void responseAddFriend(final int requestCode, final String userId, final String toUserId, final String result) {
        // "addRelation", // 10
//		String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + " toUserId:" + toUserId);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("toUserId", toUserId);
//		param.put("phone", phone);
//		param.put("email", email);
        param.put("result", result);
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限
        param.put("type", "2");

        submit(requestCode, 21, param);
    }

    /**
     * 请求查看位置权限
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param toUserId    对方的id
     */
    public void requestSeeRole(final int requestCode, final String userId, final String toUserId) {
        // "addRelation", // 10
//		String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + " toUserId:" + toUserId);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("toUserId", toUserId);
//		param.put("phone", phone);
//		param.put("email", email);
//		param.put("result", result);
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限
        param.put("type", "3");

        submit(requestCode, 21, param);
    }

    /**
     * 响应请求查看位置权限
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param toUserId    对方的id
     * @param result      Y 同意   N 不同意
     */
    public void responseSeeRole(final int requestCode, final String userId, final String toUserId, final String result) {
        // "addRelation", // 10
//		String methodName = "addRelation";

        Log.i(TAG, "userId:" + userId + " toUserId:" + toUserId);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("toUserId", toUserId);
//		param.put("phone", phone);
//		param.put("email", email);
        param.put("result", result);
        // 0：用email请求加好友  1：用phone请求加好友 2：响应加好友  3：请求权限 4：响应权限
        param.put("type", "4");

        submit(requestCode, 21, param);
    }


    /**
     * 反馈意见提交
     *
     * @param requestCode
     * @param userId      用户id 可以不填 （有就填）
     * @param nick        用户nic 可以不填 （有就填）
     * @param advs        意见 必须填
     */
    public void advsSubmit(final int requestCode, final String userId, final String nick, final String advs) {
        //	"advsSubmitA.do" // 22
//		String methodName = "addRelation";

        Log.i(TAG, "advsSubmit:" + " advs:" + advs);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", StringUtils.isBlank(userId) ? "" : userId);
        param.put("nick", StringUtils.isBlank(nick) ? "" : nick);
        param.put("advs", advs);

        submit(requestCode, 22, param);
    }

    /**
     * 查询当前应用程序的最新版本
     *
     * @param requestCode
     */
    public void getLastVersion(final int requestCode) {
        // "getAppInfoA.do" // 23
//		String methodName = "addRelation";

        Log.i(TAG, "getLastVersion");

        submit(requestCode, 23, null);
    }

//	/**
//	 * 添加好友
//	 * @param requestCode
//	 * @param userId  自己的ID
//	 * @param friendId  对方的ID
//	 */
//	public void addRelation(final int requestCode,final String userId,final String friendId) {
//		// "addRelation", // 10
////		String methodName = "addRelation";
//		
//		Log.i(TAG, "userId:"+userId+"friendId:"+friendId);
//		
//		Map<String,String> param = new HashMap<String,String>();
//		param.put("userId", userId);
//		param.put("friendId", friendId);
//	
//		submit(requestCode,10,param);
//	}

//	/**
//	 * 请求添加好友
//	 * @param requestCode
//	 * @param userId
//	 * @param email
//	 */
//	public void addRelationByEmail(final int requestCode,final String userId,final String email) {
//		// "addRelationByEmail", // 11
//		//String methodName = "addRelationByEmail";
//		
//		Log.i(TAG, "userId:"+userId+"email:"+email);
//		
//		Map<String,String> param = new HashMap<String,String>();
//		param.put("userId", userId);
//		param.put("email", email);
//	
//		submit(requestCode,11,param);
//	}

//	/**
//	 * 添加好友
//	 * @param requestCode
//	 * @param userId  自己的ID
//	 * @param friendId  对方的ID
//	 */
//	public void addRelationByPhone(final int requestCode,final String userId,final String phone) {
//		// "addRelationByPhone", // 12
////			String methodName = "addRelationByPhone";
//			
//		Log.i(TAG, "userId:"+userId+"phone:"+phone);
//		
//		Map<String,String> param = new HashMap<String,String>();
//		param.put("userId", userId);
//		param.put("phone", phone);
//	
//		submit(requestCode,12,param);
//	}

    /**
     * 删除好友
     *
     * @param requestCode
     * @param userId      自己的ID
     * @param friendId    对方的ID
     */
    public void delRelation(final int requestCode, final String userId, final String friendId) {
        // "delRelation", // 13
        //	String methodName = "delRelation";

        Log.i(TAG, "userId:" + userId + "friendId:" + friendId);

        Map<String, String> param = new HashMap<String, String>();
        param.put("userId", userId);
        param.put("friendId", friendId);

        submit(requestCode, 13, param);
    }

    /**
     * 更新好友关系
     *
     * @param requestCode
     * @param relationData 自己的ID
     */
    public void updateRelation(final int requestCode, final RelationData relationData) {
        // updateRelation 14
//			String methodName = "updateRelation";

        Gson gson = new Gson();
        String jsonRelationData = gson.toJson(relationData);

        Log.i(TAG, "relationData:" + jsonRelationData);

        Map<String, String> param = new HashMap<String, String>();
        param.put("relationData", jsonRelationData);

        submit(requestCode, 14, param);
    }

    /**
     * 检查唯一性：邮箱
     *
     * @param requestCode
     * @param requestCode 自己的ID
     * @param email       对方的ID
     */
    public void checkExistEmail(final int requestCode, final String email) {
        // "checkExistEmail", // 15
        //	String methodName = "checkExistEmail";

        Log.i(TAG, "email:" + email);

        Map<String, String> param = new HashMap<String, String>();
        param.put("email", email);

        // 检查唯一性 返回值放在 responseMsg 里面 1 已经存在 0 不存在
        submit(requestCode, 15, param);
    }

    /**
     * 检查唯一性：电话
     *
     * @param requestCode
     * @param phone       自己的ID
     */
    public void checkExistPhone(final int requestCode, final String phone) {
        // "checkExistPhone", // 16
//			String methodName = "checkExistPhone";

        Log.i(TAG, "phone:" + phone);

        Map<String, String> param = new HashMap<String, String>();
        param.put("phone", phone);

        // 检查唯一性 返回值放在 responseMsg 里面 1 已经存在 0 不存在
        submit(requestCode, 16, param);
    }

    /**
     * 检查唯一性：uid绑定
     * type ： 1 sina  2 qq
     *
     * @param requestCode
     * @param uid         自己的ID
     * @param type        对方的ID
     */
    public void checkExistUID(final int requestCode, final String uid, final String type) {

        // checkExistUID 17
//		String methodName = "checkExistUID";

        Log.i(TAG, "uid:" + uid + " type:" + type);

        Map<String, String> param = new HashMap<String, String>();
        param.put("uid", uid);
        param.put("type", type);

        // 检查唯一性 返回值放在 responseMsg 里面 1 已经存在 0 不存在
        submit(requestCode, 17, param);
    }


    private boolean isNull(String checkStr) {
        if (null == checkStr || "".equals(checkStr.trim())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提交网络（同步版 用于子线程中使用）
     *
     * @param methodId
     * @param param
     */
    private ResponseData submitBySync(final int methodId, final Map<String, String> param) {

        String serverURL = endPoint + methods[methodId];
        if (methodId == 8 || methodId == 21 || methodId == 22 || methodId == 23) {
            serverURL = baseEndPoint + methods[methodId];
        }

        HttpPost post = new HttpPost(serverURL);

        if (null != param) {
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();

            for (String key : param.keySet()) {
                String value = param.get(key);
                NameValuePair nvp1 = new BasicNameValuePair(key, value);
                parameters.add(nvp1);
                Log.i(TAG, " key:" + key + " value:" + value);
            }

            UrlEncodedFormEntity entity = null;

//				post.addHeader("Accept","application/xml");
            post.addHeader("Connection", "Keep-Alive");
//				post.addHeader("Content-Type", "application/xml");

            try {
                entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            post.setEntity(entity);
        }

        // 设置一些基本参数
        HttpParams params = new BasicHttpParams();
//		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//		HttpProtocolParams.setContentCharset(params, CHARSET);            
        HttpProtocolParams.setUseExpectContinue(params, true);
//		HttpProtocolParams.setUserAgent(
//				params,
//				"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
//				+"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");            
        // 超时设置
        /* 从连接池中取连接的超时时间 */
        ConnManagerParams.setTimeout(params, 5000);
        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params, 5000);
		/* 请求超时 */
        HttpConnectionParams.setSoTimeout(params, 5000);
        // 设置我们的HttpClient支持HTTP和HTTPS两种模式
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        // 使用线程安全的连接管理来创建HttpClient
        ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
        HttpClient client = new DefaultHttpClient(conMgr, params);


        try {
            HttpResponse response = client.execute(post);
            int code = response.getStatusLine().getStatusCode();
            Log.i(TAG, "code:" + code + " methodId:" + methodId);
            if (code == 200) {
                StringBuilder builder = new StringBuilder();
                InputStream is = response.getEntity().getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String strLine = null;
                while (null != (strLine = br.readLine())) {
                    builder.append(strLine);
                }

                String retString = builder.toString();
                Log.i(this, "response:" + retString);

                return parserResponseData(methodId, retString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ResponseData rd = new ResponseData();
        rd.setResponseStatus(RET_BAD);
        return rd;
    }

    /**
     * 提交网络
     *
     * @param requestCode
     * @param param
     */
    private void submit(final int requestCode, final int methodId, final Map<String, String> param) {
        // Params, Progress, Result
        new AsyncTask<Integer, Integer, ResponseData>() {

            @Override
            protected ResponseData doInBackground(Integer... params) {

//				Looper.prepare();
                String serverURL = endPoint + methods[methodId];
                if (methodId == 8 || methodId == 21 || methodId == 22 || methodId == 23) {
                    serverURL = baseEndPoint + methods[methodId];
                }

                HttpPost post = new HttpPost(serverURL);

                if (null != param) {
                    List<NameValuePair> parameters = new ArrayList<NameValuePair>();

                    for (String key : param.keySet()) {
                        String value = param.get(key);
                        NameValuePair nvp1 = new BasicNameValuePair(key, value);
                        parameters.add(nvp1);
                        Log.i(TAG, " key:" + key + " value:" + value);
                    }


                    UrlEncodedFormEntity entity = null;

                    //				post.addHeader("Accept","application/xml");
                    post.addHeader("Connection", "Keep-Alive");
                    //				post.addHeader("Content-Type", "application/xml");

                    try {
                        entity = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    post.setEntity(entity);
                }

                HttpClient client = new DefaultHttpClient();

                try {
                    HttpResponse response = client.execute(post);
                    int code = response.getStatusLine().getStatusCode();
                    Log.i(TAG, "code:" + code);
                    if (code == 200) {
                        StringBuilder builder = new StringBuilder();
                        InputStream is = response.getEntity().getContent();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String strLine = null;
                        while (null != (strLine = br.readLine())) {
                            builder.append(strLine);
                        }

                        String retString = builder.toString();
                        Log.d(this, retString);

                        // 分析结果
                        return parserResponseData(methodId, retString);
                    }
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ResponseData rd = new ResponseData();
                rd.setResponseStatus(RET_BAD);
                return rd;
            }

            @Override
            protected void onPostExecute(ResponseData result) {
                Log.i(TAG, "onPostExecute");
                if (null != netTranListener) {

                    netTranListener.onTransmitted(requestCode, result);
                }
            }
        }.execute(requestCode);
    }


    private ResponseData parserResponseData(int methodId, String retString) {
        Gson gonsRequest = new Gson();
        ResponseHeader responseHeader = gonsRequest.fromJson(retString, ResponseHeader.class);

        switch (methodId) {
            case 0: // userRegister
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                }
            }
            case 1:

                break;
            case 2:

                break;
            case 3: // 3 getUserInfo
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gson = new Gson();
                        RegData regData = gson.fromJson(responseHeader.getBody(), RegData.class);
                        rd.setRegData(regData);
                    }
                    return rd;
                }
            }
            case 4: // // "getLocation", // 4
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        List<LocationData> locationDataList = gons.fromJson(
                                responseHeader.getBody(),
                                new TypeToken<List<LocationData>>() {
                                }.getType()
                        );
                        rd.setLocationDataList(locationDataList);
                    }
                    return rd;
                }
            }
            case 5: // // "getLocationList", // 5
            case 25: // // "getLocationListByTime", // 25
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        List<LocationData> locationDataList = gons.fromJson(
                                responseHeader.getBody(),
                                new TypeToken<List<LocationData>>() {
                                }.getType()
                        );
                        rd.setLocationDataList(locationDataList);
                    }
                    return rd;
                }
            }
            case 6: // // "uploadLocation", // 6
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                }
            }
            case 7: // // "getUserInfoByToken", // 7
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        RegData regData = gons.fromJson(responseHeader.getBody(), RegData.class);
                        rd.setRegData(regData);
                    }
                    return rd;
                }
            }
            case 8: // // "getRelationList", // 8
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Log.i(TAG, "body:" + responseHeader.getBody());
                        Gson gons = new Gson();
                        List<RelationData> locationDataList = gons.fromJson(
                                responseHeader.getBody(),
                                new TypeToken<ArrayList<RelationData>>() {
                                }.getType()
                        );
                        rd.setRelationDataList(locationDataList);
                    }
                    return rd;
                }
            }
            case 9: // "getRelation", // 9
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        RelationData locationData = gons.fromJson(
                                responseHeader.getBody(),
                                RelationData.class);
                        rd.setRelationData(locationData);
                    }
                    return rd;
                }
            }
            case 10: // // "addRelation", // 10
            case 11: // "addRelationByEmail", // 11
            case 12: // "addRelationByPhone", // 12
            case 13: // "delRelation", // 13
            case 14: // updateRelation 14
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        RelationData relationData = gons.fromJson(
                                responseHeader.getBody(),
                                RelationData.class);
                        rd.setRelationData(relationData);
                    }
                    return rd;
                }
            }
            case 15: // "checkExistEmail", // 15
            case 16: // "checkExistPhone", // 16
            case 17: // "checkExistUID", // 17
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        rd.setCheckResult(responseHeader.getBody());
                    }
                    return rd;
                }
            }
            case 18: // // "updateUserInfo" 18
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                }
            }
            case 19: // "getUserInfoByEmailPwd",  // 19
            case 20: // "getUserInfoByPhonePwd", // 20
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    if (!isNull(responseHeader.getBody())) {
                        rd.setUserId(responseHeader.getBody());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gson = new Gson();
                        RegData regData = gson.fromJson(responseHeader.getBody(), RegData.class);
                        rd.setRegData(regData);
                    }
                    return rd;
                }
            }
            case 21: // "ra.do" // 21
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        PushData pushData = gons.fromJson(
                                responseHeader.getBody(),
                                PushData.class);
                        rd.setPushData(pushData);
                    }
                    return rd;
                }
            }
            case 22: // "advsSubmitA.do" // 22
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                }
            }
            case 23: // "getAppInfoA.do" // 23
            {
                if (responseHeader.getStatus().equals(RET_BAD)) {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getMessage())) {
                        rd.setResponseMsg(responseHeader.getMessage());
                    }
                    return rd;
                } else {
                    ResponseData rd = new ResponseData();
                    rd.setResponseStatus(responseHeader.getStatus());
                    if (!isNull(responseHeader.getBody())) {
                        Gson gons = new Gson();
                        AppData appData = gons.fromJson(
                                responseHeader.getBody(),
                                AppData.class);
                        rd.setAppData(appData);
                    }
                    return rd;
                }
            } // "getAppInfoA.do" // 23
            default:
                break;
        }

        ResponseData rd = new ResponseData();
        rd.setResponseStatus(RET_BAD);
        return rd;
    }

    /**
     * 返回用户头像url
     *
     * @param userId
     * @return
     */
    public static String getImgUrl(int userId) {
        return baseEndPoint + "dl0101A.do?d=" + userId;
    }
}
