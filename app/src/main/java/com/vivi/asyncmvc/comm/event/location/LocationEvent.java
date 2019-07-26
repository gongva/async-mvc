package com.vivi.asyncmvc.comm.event.location;

import com.baidu.location.BDLocation;
import com.vivi.asyncmvc.library.plugs.otto.Event;

/**
 * 百度定位成功Event
 * 每个定位业务所用时，请用子类
 */
public class LocationEvent extends Event {

    public BDLocation bdLocation;

    public void setBdLocation(BDLocation bdLocation) {
        this.bdLocation = bdLocation;
    }
}
