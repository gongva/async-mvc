package com.vivi.asyncmvc.library.plugs.umeng;

import java.io.Serializable;

public class ShareContentWeChat extends ShareContent implements Serializable {

    public ShareContentWeChat(String picUrl, String title, String content, String shareUrl) {
        super(picUrl, title, content, shareUrl);
        toastNoteText = "正在打开微信";
    }
}
