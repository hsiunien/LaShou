package cn.duocool.lashou.net.client;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.util.Log;

public class AxbOutputStream extends FilterOutputStream {
	private final static String TAG = "AxbOutputStream";
	private long transferred; 
	private long maxSize; 
	private int requestCode;
	private AxbNetAsyncTask<?, ?, ?> task;
	
	public AxbNetAsyncTask<?, ?, ?> getTask() {
		return task;
	}

	public void setTask(AxbNetAsyncTask<?, ?, ?> task) {
		this.task = task;
	}

	public AxbOutputStream(OutputStream out,long fileSize,int requestCode) {
		super(out);
		transferred = 0;
		maxSize = fileSize;
		this.requestCode = requestCode;
	}


	@Override
	public void write(byte[] buffer, int offset, int count) throws IOException {
		transferred += count;  
		Log.i(TAG,"111------------------------------>"+transferred);
		task.taskDoing(requestCode,transferred,maxSize);
		super.write(buffer, offset, count);
	}
	
	
	@Override
	public void write(int oneByte) throws IOException {
		transferred++;
		task.taskDoing(requestCode,transferred,maxSize);
		
		Log.i(TAG,"222------------------------------>"+transferred);
		super.write(oneByte);
	}
	
	@Override
	public void flush() throws IOException {
		super.flush();
	}
	
	@Override
	public void close() throws IOException {
		super.close();
	}

}
