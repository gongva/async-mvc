package com.vivi.asyncmvc.comm.event.selectimg;

import com.vivi.asyncmvc.library.plugs.otto.Event;

/**
 * 选择照片：选择相册
 * Created by gongwei on 2019.1.3
 */
public class SelectImageAlbumChangeEvent extends Event {
    public String albumName;
    public String albumPath;
    public SelectImageAlbumChangeEvent(String albumName, String albumPath) {
        this.albumName = albumName;
        this.albumPath = albumPath;
    }
}
