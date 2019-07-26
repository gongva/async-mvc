package com.vivi.asyncmvc.library.plugs.umeng;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.vivi.asyncmvc.library.utils.UI;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 友盟分享SDK
 *
 * @author gongwei 2019/1/2
 */
public abstract class ShareSdk {


    public static void shareToWechat(final Activity activity, final ShareContent wechat) {
        UMShareAPI umShareAPI = UMShareAPI.get(activity);
        if (!umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
            UI.showToast("需要安装微信才能分享");
            return;
        }
        dealShare(activity, wechat, SHARE_MEDIA.WEIXIN);
    }

    public static void shareToWechatMoments(final Activity activity, final ShareContent wechatMoments) {
        UMShareAPI umShareAPI = UMShareAPI.get(activity);
        if (!umShareAPI.isInstall(activity, SHARE_MEDIA.WEIXIN)) {
            UI.showToast("需要安装微信才能分享");
            return;
        }
        dealShare(activity, wechatMoments, SHARE_MEDIA.WEIXIN_CIRCLE);
    }


    public static void shareToQQ(Activity activity, final ShareContent qq) {
        UMShareAPI umShareAPI = UMShareAPI.get(activity);
        if (!umShareAPI.isInstall(activity, SHARE_MEDIA.QQ)) {
            UI.showToast("需要安装QQ才能使用");
            return;
        }
        dealShare(activity, qq, SHARE_MEDIA.QQ);
    }

    public static void shareToQZone(Activity activity, final ShareContent qzone) {
        UMShareAPI umShareAPI = UMShareAPI.get(activity);
        if (!umShareAPI.isInstall(activity, SHARE_MEDIA.QQ)) {
            UI.showToast("需要安装QQ才能使用");
            return;
        }
        dealShare(activity, qzone, SHARE_MEDIA.QZONE);
    }

    /**
     * 执行分享动作
     *
     * @param activity
     * @param shareContent
     * @param platform
     */
    public static void dealShare(final Activity activity, final ShareContent shareContent, final SHARE_MEDIA platform) {
        isValidURL(shareContent.picUrl, new ValidURLCallback() {
            @Override
            public void onValidURL(boolean success) {
                UMImage thumb = new UMImage(activity, shareContent.getShareImage());

                UMWeb web = new UMWeb(shareContent.targetUrl);
                web.setTitle(shareContent.getShareTitle());//标题
                web.setThumb(thumb);  //缩略图
                web.setDescription(shareContent.getShareContent());//描述

                new ShareAction(activity)
                        .setPlatform(platform)
                        .withMedia(web)//分享内容
                        .setCallback(null)//回调监听器
                        .share();
            }
        });
    }

    /**
     * 判断URL地址是否有效
     *
     * @param url 地址
     */
    public static void isValidURL(final String url, final ValidURLCallback callback) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                boolean success = msg.what >= 200 && msg.what < 300;
                if (callback != null) {
                    callback.onValidURL(success);
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    int code = conn.getResponseCode();
                    conn.disconnect();
                    handler.sendEmptyMessage(code);
                } catch (Exception e) {
                    handler.sendEmptyMessage(404);
                }
            }
        }).start();
    }

    interface ValidURLCallback {
        void onValidURL(boolean success);
    }
}
