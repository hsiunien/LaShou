package cn.duocool.lashou;

/**
 * 
 * 这个类里放置整个项目共通的设定
 * 如 地址 方法等。
 */
public class CommDef {
    public static final  boolean DEBUG=true;//是否显示日志
    public  static  final  boolean DEVELOP=false;//开发测试模式
    //	public final static String serverAddress = "http://duocool.cn/lashouserver/";
	public  static String serverAddress;
    static {
        if(DEVELOP){
            serverAddress="http://192.168.1.105:8080/lashouserver/";
        }else {
            serverAddress="http://115.28.147.227/lashouserver/";
        }
    }
	
	public final static String headImg = "anxinbao/myHead.png";//默认头像截图保存路径
	
	/**
	 * 锁的个数
	 */
	public final static int LOCK_SIZE = 3;
	
	
	/**
	 * 数据库的文件名
	 */
	public final static String DB_FILE_NAME = "lashoudb.db";
	
	/**
	 * 数据库版本
	 */
	public final static int DB_VERSION = 3;
	

	/**
	 * 	/**
	 * 少儿版
	 */
	public final static int EDITION_CHILD = 90;


    public  final  static int FENCECYCLE=10*1000;
	/**
	 * 家长版
	 */
	public final static int EDITION_PARENT = 91;
	
	/**
	 * 保存用的Key
	 */
	public final static String EDITION_KEY = "editionKey";
	public final static String FIRSTOPEN_KEY = "isfirstKey";
	
	/**
	 * 设定文件名字
	 */
	public final static String PREFERENCE_NAME = "lahsoupreferencesetting";
	
	
	/**
	 * 设定锁的形式
	 */
	public final static String LOCK_MODE_IMAGE = "image";
	
	/**
	 * 设定锁的形式
	 */
	public final static String LOCK_MODE_PIN = "figure";
    public final static String BAIDU_LOCATION_KEY="sIOBVdOQyFlY5hwbahFVqKKX";
    public  final  static  String WXappId="wx644ed164029da71e";
}
