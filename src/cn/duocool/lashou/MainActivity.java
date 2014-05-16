package cn.duocool.lashou;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.duocool.lashou.activity.AppLockMainActivity;
import cn.duocool.lashou.activity.LoginActivity;
import cn.duocool.lashou.adapter.MenuAdapter;
import cn.duocool.lashou.adapter.MenuGridAdapter;
import cn.duocool.lashou.model.MenuGridItem;
import cn.duocool.lashou.service.LockService;
import cn.duocool.lashou.service.PushService;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMSsoHandler;

public class MainActivity extends Activity {
    //成功
    private ViewPager viewPager;
    private List<MenuGridItem> list1, list2;
    private List<View> pageList;
    ImageView myInfoImagv;
    final UMSocialService mController = UMServiceFactory.getUMSocialService(
            "com.umeng.share", RequestType.SOCIAL);
    private int[] icons = {R.drawable.lock_icon, R.drawable.hywl_icon,
            R.drawable.location_icon, R.drawable.setting_icon,
            R.drawable.share_icon, R.drawable.theme_icon,
            R.drawable.mail_us_icon, R.drawable.about_us_icon};
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialView();
        startService(new Intent(MainActivity.this, LockService.class));
        startService(new Intent(MainActivity.this, PushService.class));
    }

    private void initialView() {
        titles = new String[]{getString(R.string.appLock),
                getString(R.string.digitalFence),
                getString(R.string.friendLocation),
                getString(R.string.setting), getString(R.string.share),
                getString(R.string.theme), getString(R.string.suggest),
                getString(R.string.aboutandhelp)};

        myInfoImagv = (ImageView) findViewById(R.id.myInfo);
        myInfoImagv.setOnClickListener(clickListener);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        View gridLayout = getLayoutInflater().inflate(
                R.layout.menu_gridview_layout, null);
        GridView gridView = (GridView) gridLayout
                .findViewById(R.id.menuGridView);
        list1 = new ArrayList<MenuGridItem>();
        for (int i = 0; i < icons.length && i < 6; i++) {
            MenuGridItem item = new MenuGridItem(icons[i], titles[i]);
            list1.add(item);
        }
        MenuGridAdapter gridAdp = new MenuGridAdapter(this, list1);
        gridView.setAdapter(gridAdp);
        pageList = new ArrayList<View>();
        pageList.add(gridLayout);
        gridView.setOnItemClickListener(menuItemClickListener);

        // secondPage
        gridLayout = getLayoutInflater().inflate(R.layout.menu_gridview_layout,
                null);
        gridView = (GridView) gridLayout.findViewById(R.id.menuGridView);
        ;
        list2 = new ArrayList<MenuGridItem>();
        for (int i = 6; i < icons.length; i++) {
            MenuGridItem item = new MenuGridItem(icons[i], titles[i]);
            list2.add(item);
        }
        gridAdp = new MenuGridAdapter(this, list2);
        gridView.setAdapter(gridAdp);
        pageList.add(gridLayout);
        gridView.setOnItemClickListener(menuItemClickListener);

        MenuAdapter pageAdp = new MenuAdapter(this, pageList);
        viewPager.setAdapter(pageAdp);
        viewPager.setOnPageChangeListener(pageChangeListener);
        //删除不需要的分享平台  增加微信平台
        String appId = "wxe1222638855a9339";
        String contentUrl = "http://zscd.sinaapp.com";
        mController.getConfig().removePlatform(SHARE_MEDIA.RENREN, SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL);
        mController.getConfig().supportWXPlatform(this, appId, contentUrl);
        mController.getConfig().supportWXCirclePlatform(this, appId, contentUrl);
        //mController.getConfig().setPlatforms(SHARE_MEDIA.QZONE,SHARE_MEDIA.SINA,SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.WEIXIN);

    }

    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
//			System.out.println("arg0="+arg0);
            curcleChange(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };
    OnItemClickListener menuItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            Intent intent = null;
/*			Toast.makeText(getApplicationContext(),
                    ((MenuGridItem) view.getTag()).getTitle(),
					Toast.LENGTH_SHORT).show();*/
            switch (((MenuGridItem) view.getTag()).getImageId()) {
                case R.drawable.lock_icon:
                    intent = new Intent();
                    intent.putExtra("goToMainLock", "1");
                    intent.setClass(getApplicationContext(),
                            AppLockMainActivity.class);
                    startActivity(intent);
                    break;
                case R.drawable.hywl_icon: {
                    mController.loginout(MainActivity.this,
                            new SocializeClientListener() {
                                public void onStart() {
                                    Log.d("test--------", "start");

                                }

                                public void onComplete(int arg0,
                                                       SocializeEntity arg1) {
                                    System.out.println(arg0);
                                    Toast.makeText(getApplicationContext(), "注销成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }

                break;
                case R.drawable.mail_us_icon:

                    break;

                case R.drawable.share_icon:

                    mController.setShareContent(" @王修念  这个应用很不错哦，欢迎大家下载。。");
                    mController.setShareMedia(new UMImage(MainActivity.this,
                            R.drawable.umeng_socialize_wxcircle));
                    mController.openShare(MainActivity.this, false);
                default:

                    break;
            }

        }
    };

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.myInfo:
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    break;

                default:
                    break;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("test------" + requestCode);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
                requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    //滑动时两个小圈跟着变化
    private void curcleChange(int currentPage) {
        LinearLayout circleChangeContainer = (LinearLayout) findViewById(R.id.circleChange);
        for (int i = 0; i < circleChangeContainer.getChildCount(); i++) {
            TextView imgv = (TextView) circleChangeContainer.getChildAt(i);
            if (i == currentPage) {
                imgv.setBackgroundResource(R.drawable.circle_0);
            } else {
                imgv.setBackgroundResource(R.drawable.circle_1);
            }
        }
    }


}
