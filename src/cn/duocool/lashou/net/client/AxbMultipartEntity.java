package cn.duocool.lashou.net.client;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class AxbMultipartEntity extends MultipartEntity {
	
	private int requestCode;
	private long fileSize;
	private AxbNetAsyncTask<?, ?, ?> task;

	public AxbNetAsyncTask<?, ?, ?> getTask() {
		return task;
	}
	public void setTask(AxbNetAsyncTask<?, ?, ?> task) {
		this.task = task;
	}
	public AxbMultipartEntity() {
		super();
		// TODO Auto-generated constructor stub
	}
	public AxbMultipartEntity(int requestCode,long fileSize) {
		super();
		this.requestCode = requestCode;
		this.fileSize = fileSize;
	}
	
	public AxbMultipartEntity(HttpMultipartMode mode, String boundary,
			Charset charset,int requestCode,long fileSize) {
		super(mode, boundary, charset);
		this.requestCode = requestCode;
		this.fileSize = fileSize;
	}

	public AxbMultipartEntity(HttpMultipartMode mode, String boundary,
			Charset charset) {
		super(mode, boundary, charset);
		// TODO Auto-generated constructor stub
	}
	
	public AxbMultipartEntity(HttpMultipartMode mode) {
		super(mode);
		// TODO Auto-generated constructor stub
	}

	public AxbMultipartEntity(HttpMultipartMode mode,int requestCode,long fileSize) {
		super(mode);
		this.requestCode = requestCode;
		this.fileSize = fileSize;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		AxbOutputStream aos = new AxbOutputStream(outstream,fileSize,requestCode);
		aos.setTask(task);
		super.writeTo(aos);
	}

}
