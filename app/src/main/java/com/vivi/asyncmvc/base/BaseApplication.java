package com.vivi.asyncmvc.base;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.studio.os.PreferencesUtils;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.APPStatus;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.SystemConfig;
import com.vivi.asyncmvc.comm.managers.NetManager;
import com.vivi.asyncmvc.comm.view.listview.HikRefreshFooter;
import com.vivi.asyncmvc.library.plugs.alipush.AliPushConfig;
import com.vivi.asyncmvc.library.plugs.umeng.UMengConfig;
import com.vivi.asyncmvc.library.utils.FileUtil;
import com.vivi.asyncmvc.library.utils.LogCat;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class BaseApplication extends Application {

    public static BaseApplication appContext;
    //管理App状态
    private APPStatus appStatus;
    //统一管理所有的Activity
    public List<Activity> activityList = new LinkedList<>();

    //SmartRefreshLayout默认的Header和Footer构造，static代码段可以防止内存泄露
    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, R.color.color_5a5a5a);
                return new ClassicsHeader(context).setEnableLastTime(false).setTextSizeTitle(12).setDrawableArrowSize(15).setDrawableProgressSize(15);
            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new HikRefreshFooter(context).setDrawableSize(20).setTextSizeTitle(12).setDrawableArrowSize(15).setDrawableProgressSize(15);
            }
        });
    }

    public static BaseApplication getInstance() {
        return appContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        appContext = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogCat.setDebug(AppContext.isDebug);
        PreferencesUtils.init(this);
        NetManager.init(this);
        initAppRootFilePath();
        initUmeng();
        initAppStatus();
        intiAliPushChannel();
        initBaiduMap();
    }

    /**
     * 初始化App根路径
     */
    private void initAppRootFilePath() {
        try {
            String appRootFilePath;
            if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
                appRootFilePath = Environment.getExternalStorageDirectory().toString() + FileUtil.APP_ROOT_FILE;
            } else {
                appRootFilePath = this.getCacheDir().getAbsolutePath() + FileUtil.APP_ROOT_FILE;
            }
            FileUtil.initCachePath(appRootFilePath);
        } catch (Exception e) {
            android.studio.os.LogCat.e(e, null);
        }
    }

    /**
     * 初始化友盟统计和分享
     */
    private void initUmeng() {
        UMConfigure.init(this, UMengConfig.APP_KEY_UMENG, UMengConfig.APP_CHANEL_UMENG, UMConfigure.DEVICE_TYPE_PHONE, null);
        PlatformConfig.setWeixin(UMengConfig.APP_KEY_WE_CHAT, UMengConfig.APP_SECRET_WE_CHAT);
        PlatformConfig.setQQZone(UMengConfig.APP_ID_QQ, UMengConfig.APP_KEY_QQ);
    }

    /**
     * 初始化自制AppStatus控制器
     */
    private void initAppStatus() {
        appStatus = new APPStatus();
        registerActivityLifecycleCallbacks(appStatus);
        appStatus.setStatusCallback(new APPStatus.AppStatusCallback() {
            @Override
            public void appEnterBackground(APPStatus status) {

            }

            @Override
            public void appEnterForeground(APPStatus status) {
                SystemConfig.updateConfig();
            }
        });

        appStatus.setExitCallback(new APPStatus.AppExitCallback() {
            @Override
            public void appExit(APPStatus status) {
                onTerminate();
            }
        });
    }

    /**
     * 初始化阿里云推送
     */
    private void intiAliPushChannel() {
        PushServiceFactory.init(this);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(this, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LogCat.d("init cloudchannel success");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogCat.d("init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
            }
        });
        pushService.bindAccount("zhaodong", new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogCat.d("Ali bindAccount success.");
            }

            @Override
            public void onFailed(String s, String s1) {
                LogCat.d("Ali bindAccount fail.");
            }
        });
        //Android 8.0以上设备需要设置NotificaitonChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = AliPushConfig.NOTIFICATION_CHANNEL_ID;
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "电子驾照";
            // 用户可以看到的通知渠道的描述
            String description = "您的智慧交通助手";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    /**
     * 初始化百度地图SDK
     */
    private void initBaiduMap() {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public APPStatus getAppStatus() {
        return appStatus;
    }

    public Activity getActivityTop() {
        return appStatus.getCurrentActivity();
    }

    public Activity getActivitySecond() {
        return appStatus.getSecondActivity();
    }

    // 添加Activity
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 移除Activity
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // 遍历所有Activity并finish
    public void exit() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 这里可以做一些退出时的资源清理
    }
}
