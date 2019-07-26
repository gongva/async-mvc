package com.vivi.asyncmvc.comm.event.selectimg;

import com.vivi.asyncmvc.library.plugs.otto.Event;

import java.util.ArrayList;

/**
 * Created by gw on 2019.1.3
 * 选择相册，完成时的Event
 */
public class SelectImageCompleteEvent extends Event {
    public ArrayList<String> r;

    public SelectImageCompleteEvent(ArrayList<String> r) {
        this.r = r;
    }
}
