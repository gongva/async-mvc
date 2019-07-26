package com.vivi.asyncmvc.comm.event.selectimg;

import com.vivi.asyncmvc.library.plugs.otto.Event;

import java.util.ArrayList;

/**
 * Created by gongwei on 2019.1.3
 * 选择相册，已选照片列表改变时的Event
 */
public class SelectImageDoingEvent extends Event {
    public ArrayList<String> r;

    public SelectImageDoingEvent(ArrayList<String> r) {
        this.r = r;
    }
}
