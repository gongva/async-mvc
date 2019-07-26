package com.vivi.asyncmvc.comm.event.location;

/**
 * 选择某个地址时的定位成功Event，如：违法举报、文明报修、上报路况
 */
public class SelectMapLocationEvent extends LocationEvent {

    public static SelectMapLocationEvent newInstance() {
        return new SelectMapLocationEvent();
    }
}
