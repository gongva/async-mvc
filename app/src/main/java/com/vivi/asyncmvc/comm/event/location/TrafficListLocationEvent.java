package com.vivi.asyncmvc.comm.event.location;

/**
 * 实时路况列表的定位成功Event
 */
public class TrafficListLocationEvent extends LocationEvent {
    public static TrafficListLocationEvent newInstance() {
        return new TrafficListLocationEvent();
    }
}
