package cn.duocool.lashou.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoBase {
	
	private Context mContext = null;
	private DataBaseHelper dbHelper = null;
	private SQLiteDatabase mDb = null;
	
	public DaoBase(Context mContext) {
		this.mContext = mContext;
	}
	
	/**
	 * 打开数据库
	 */
	public void openDb() {
		dbHelper=new DataBaseHelper(mContext);
	
		try{
			mDb=dbHelper.getWritableDatabase();
		} catch (Exception e) {
			mDb=dbHelper.getReadableDatabase();
		}
	}
	
	/**
	 * 关闭数据库
	 */
	public void closeDB() {

		if (null != mDb) {
			mDb.close();
			mDb = null;
		}
	}
	
	/**
	 * 获得数据库对象
	 * @return
	 */
	public SQLiteDatabase getDB() {
		if (null == mDb) {
			return reOpenDb();
		}
		
		if (mDb.isOpen()) {
			return mDb;
		} else {
			return reOpenDb();
		}
	}
	
	/**
	 * 重新打开数据库(睡眠等操作，有可能造成数据库被关闭或释放掉)
	 */
	private SQLiteDatabase reOpenDb() {
		if (null == dbHelper) {
			dbHelper=new DataBaseHelper(mContext);
		}
		
		if (null == mDb) {
			try{
				mDb=dbHelper.getWritableDatabase();
			} catch (Exception e) {
				mDb=dbHelper.getReadableDatabase();
			}
		} else {
			mDb.close();
			
			try{
				mDb=dbHelper.getWritableDatabase();
			} catch (Exception e) {
				mDb=dbHelper.getReadableDatabase();
			}
		}
		
		return mDb;
	}
}
