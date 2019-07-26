package com.vivi.asyncmvc.comm.listener;

import java.util.List;

/**
 * 6.0以上请求授权的Listener
 *
 * @author gongwei 2018.12.20
 */
public abstract class PermissionRequestListener {
    public abstract void onGranted();//已授权

    public void onDenied(List<String> deniedPermission){};//拒绝授权
}
