package com.vivi.asyncmvc.comm.event;

import com.vivi.asyncmvc.library.plugs.otto.Event;

/**
 * 账号被挤下线
 *
 * @author gongwei
 * @date 2019/1/30
 */
public class LogoffEvent extends Event {
    public String msg;

    public LogoffEvent(String msg) {
        this.msg = msg;
    }
}
