package com.vivi.asyncmvc.comm.event;

import com.vivi.asyncmvc.library.plugs.otto.Event;

/**
 * 登录失效
 *
 * @author gongwei
 * @date 2019/2/13
 */
public class LoginErrorEvent extends Event {
    public String msg;

    public LoginErrorEvent(String msg) {
        this.msg = msg;
    }
}
