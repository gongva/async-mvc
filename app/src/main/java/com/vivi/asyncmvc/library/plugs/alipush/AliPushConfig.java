package com.vivi.asyncmvc.library.plugs.alipush;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;

/**
 * 阿里云推送配置文件
 *
 * @author gongwei 2019.1.5
 */
public class AliPushConfig {
    /**
     * Android8.0以上推出了NotificationChannel机制，旨在对通知进行分类管理
     * 如果未设置NotificaitonChannel，通知是不会弹出来的
     * new NotificationChannel时需要NotificationChannel id，且△△△服务端推送时需要指定同样的id△△△
     */
    public static final String NOTIFICATION_CHANNEL_ID = "1";

    /**
     * 打开推送通道
     */
    public static void turnOnPushChannel() {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.turnOnPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }

    /**
     * 关闭推送通道
     */
    public static void turnOffPushChannel() {
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.turnOffPushChannel(new CommonCallback() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFailed(String s, String s1) {

            }
        });
    }
}
