package com.vivi.asyncmvc.ui.comm.map;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.vivi.asyncmvc.comm.event.location.LocationEvent;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;

/**
 * 百度定位Receiver
 */
public class HikLocationListener extends BDAbstractLocationListener {

    private LocationEvent event;

    /**
     * 定位成功之后的Event
     *
     * @param ins
     */
    public <T extends LocationEvent> HikLocationListener(Class<T> ins) {
        super();
        try {
            event = ins.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        location.getLocType();
        if (event != null) {
            event.setBdLocation(location);
            BusProvider.post(event);
        }
    }

    /**
     * 获取定位参数
     *
     * @return
     */
    public static LocationClientOption getLocationOption() {
        LocationClientOption option = new LocationClientOption();
        //设置定位模式
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 打开gps
        option.setOpenGps(true);
        // 设置坐标类型:bd09ll百度经纬度坐标
        option.setCoorType("bd09ll");
        //发起定位请求的间隔，int类型，单位ms
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        return option;
    }
}
