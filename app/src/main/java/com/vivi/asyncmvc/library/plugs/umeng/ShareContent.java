package com.vivi.asyncmvc.library.plugs.umeng;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 分享Entity
 *
 * @author gongwei 2019.1.2
 */
public class ShareContent implements Serializable {
    public static final String SHARE_TITLE_DEFAULT = "电子驾照";
    public static final String SHARE_CONTENT_DEFAULT = "我正在使用电子驾照应用，便捷呈现驾照信息、机动车信息，更有诸多智慧服务方便你我的交通生活！";
    public static final String SHARE_LOGO = "http://edl.hikcreate.com:10014/group1/M00/00/00/rBAZD1xih0iAMUVWAABAE34_VcU138.png";
    public static final String SHARE_TARGET_URL = "http://edl.hikcreate.com:20085/share/share.html?pkgname=com.vivi.asyncmvc";

    public String picUrl;
    public String title;
    public String content;
    public String targetUrl;

    //toast 提示文字，子类重定义
    public String toastNoteText = "请稍候";

    public ShareContent(String picUrl, String title, String content, String targetUrl) {
        this.picUrl = picUrl;
        this.title = title;
        this.content = content;
        this.targetUrl = targetUrl;
    }

    public String getShareTitle() {
        if (!TextUtils.isEmpty(title)) {
            return title;
        }
        return SHARE_TITLE_DEFAULT;
    }

    public String getShareContent() {
        if (!TextUtils.isEmpty(content)) {
            return content;
        }
        return SHARE_CONTENT_DEFAULT;
    }

    public String getShareImage() {
        if (!TextUtils.isEmpty(picUrl)) {
            return picUrl;
        }
        return SHARE_LOGO;
    }

    /**
     * 获取App分享的Content：微信
     *
     * @return
     */
    public static ShareContentWeChat createAppShareWeChat() {
        return new ShareContentWeChat(SHARE_LOGO, SHARE_TITLE_DEFAULT, SHARE_CONTENT_DEFAULT, SHARE_TARGET_URL);
    }

    /**
     * 获取App分享的Content：微信朋友圈
     *
     * @return
     */
    public static ShareContentWeChatMoments createAppShareWeChatMoments() {
        return new ShareContentWeChatMoments(SHARE_LOGO, SHARE_TITLE_DEFAULT, SHARE_TARGET_URL);
    }

    /**
     * 获取App分享的Content：QQ
     *
     * @return
     */
    public static ShareContentQQ createAppShareQQ() {
        return new ShareContentQQ(SHARE_LOGO, SHARE_TITLE_DEFAULT, SHARE_CONTENT_DEFAULT, SHARE_TARGET_URL);
    }

    /**
     * 获取App分享的Content：QQ控件
     *
     * @return
     */
    public static ShareContentQZone createAppShareQZone() {
        return new ShareContentQZone(SHARE_LOGO, SHARE_TITLE_DEFAULT, SHARE_CONTENT_DEFAULT, SHARE_TARGET_URL);
    }
}
