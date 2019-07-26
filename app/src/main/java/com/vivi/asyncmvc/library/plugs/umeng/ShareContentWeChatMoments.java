package com.vivi.asyncmvc.library.plugs.umeng;

import java.io.Serializable;

public class ShareContentWeChatMoments extends ShareContent implements Serializable {

    public ShareContentWeChatMoments(String picUrl, String title, String shareUrl) {
        super(picUrl, title, null, shareUrl);
        toastNoteText = "正在打开微信";
    }
}
