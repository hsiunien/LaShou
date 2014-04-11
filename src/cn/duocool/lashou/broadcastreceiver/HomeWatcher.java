package cn.duocool.lashou.broadcastreceiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class HomeWatcher {

	static final String TAG = "hg";
	private Context mContext;
	private IntentFilter mFilter;
	private OnHomePressedListener mListener;
	private InnerRecevier mRecevier;

	public HomeWatcher(Context context) {
		mContext = context;
		mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}

	public void setOnHomePressedListener(OnHomePressedListener listener) {
		mListener = listener;
		mRecevier = new InnerRecevier();
	}

	public void startWatch() {
		if (mRecevier != null) {
			try {
//				mContext.unregisterReceiver(mRecevier);
				mContext.registerReceiver(mRecevier, mFilter);
			}catch(IllegalArgumentException  e) {
				Log.e(TAG, "已经注册过了");
			}
		}
	}

	public void stopWatch() {
		if (mRecevier != null) {
			try {
				mContext.unregisterReceiver(mRecevier);
			}catch(IllegalArgumentException  e) {
				Log.e(TAG, "已经解注册过了");
			}
			
		}
	}

	class InnerRecevier extends BroadcastReceiver {
		final String SYSTEM_DIALOG_REASON_KEY = "reason";
		final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					Log.e(TAG, "action:" + action + ",reason:" + reason);
					if (mListener != null) {
						if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
							// home��
							mListener.onHomePressed();
						} else if (reason
								.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
							// ����home��
							mListener.onHomeLongPressed();
						}
					}
				}
			}
		}
	}
}
