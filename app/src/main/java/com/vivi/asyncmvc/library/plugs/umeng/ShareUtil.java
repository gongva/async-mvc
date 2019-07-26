package com.vivi.asyncmvc.library.plugs.umeng;

import android.app.Activity;
import android.text.TextUtils;

/**
 * 分享Util
 * @author gongwei 2019.1.2
 */

public class ShareUtil {

    /**
     * 处理需要在分享URL后面增加参数的情况
     *
     * @param url
     * @param paramKey   参数key
     * @param paramValue 参数value
     * @return
     */
    public static String dealUrlWithNeedParam(String url, String paramKey, String paramValue) {
        if (!TextUtils.isEmpty(url)) {
            String concat = url.lastIndexOf('?') != -1 ? "&" : "?";
            url = url + concat + paramKey + "=" + paramValue;
        }
        return url;
    }

    /**
     * 分享到单个平台
     *
     * @param activity
     * @param shareContent
     */
    public static void shareToSinglePlatform(final Activity activity, final ShareContent shareContent) {
        if (shareContent == null) {
            return;
        }
        if (shareContent instanceof ShareContentWeChat) {
            ShareSdk.shareToWechat(activity, shareContent);
        } else if (shareContent instanceof ShareContentWeChatMoments) {
            ShareSdk.shareToWechatMoments(activity, shareContent);
        } else if (shareContent instanceof ShareContentQQ) {
            ShareSdk.shareToQQ(activity, shareContent);
        } else if (shareContent instanceof ShareContentQZone) {
            ShareSdk.shareToQZone(activity, shareContent);
        }
    }
}
