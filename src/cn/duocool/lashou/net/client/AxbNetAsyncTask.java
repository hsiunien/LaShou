package cn.duocool.lashou.net.client;

import android.os.AsyncTask;

public abstract class AxbNetAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	public abstract void taskDoing(int requestCode,long transferred,long maxSize);
}
