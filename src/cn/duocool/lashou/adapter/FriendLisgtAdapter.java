package cn.duocool.lashou.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.duocool.lashou.R;
import cn.duocool.lashou.mywidget.Tip;
import cn.duocool.lashou.utils.download.ImageLoader;
import cn.duocool.lashou.net.client.NetClient;
import cn.duocool.lashou.net.client.RelationData;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 朋友列表适配器
 * @author hsiunien
 *
 */
public class FriendLisgtAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater li;
	private List<RelationData> list=new ArrayList<RelationData>();
	FriendItemHolder holder;
	 
	public FriendLisgtAdapter(Context context) {
		this.context = context;
		li=LayoutInflater.from(context);
		 
		
	}
	public void setFriendList(List<RelationData> list) {
		this.list=list;
	}
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = li.inflate(R.layout.friendlist_item, null);
			holder=new FriendItemHolder();
			holder.headView=(ImageView) convertView.findViewById(R.id.imgHead);
			holder.nameTv=(TextView) convertView.findViewById(R.id.name);
			Log.d(getClass().toString(), "converView isnull");
		}else{
			holder=(FriendItemHolder) convertView.getTag();
			Log.d(getClass().toString(), "converView is not null");
		}
		
		//这里如果有很多个会开很多线程 怎么优化？？
		ImageLoader imgloader=new ImageLoader(context);
		Bitmap bmp=imgloader.getImage(NetClient.getImgUrl(list.get(position).getFriendData().getUserId()));
		Log.d(getClass().toString(), position+"  "+holder.headView.getId());
		if(bmp==null){
			imgloader.downloadImage(holder.headView);
		}else{
			holder.headView.setImageBitmap(bmp);
		}
		holder.nameTv.setText(list.get(position).getFriendData().getNick());
		convertView.setTag(holder);
  		return convertView;
	}
	class FriendItemHolder{
	ImageView headView;
	TextView nameTv;
	}
}
