package com.vivi.asyncmvc.api.entity;

import android.content.Context;

import com.vivi.asyncmvc.comm.AppConfig;

/**
 * 版本更新接口的返回
 *
 * @author gongwei
 * @date 2019/2/1
 */
public class Version {
    public int versionMark;// 当前版本号
    public String versionNo;// 当前版本号文字
    public String url;// 下载地址
    public String versionDeclare;// 版本说明
    public boolean forceUpdate;// 是否强制更新
    public int forceUpdateVersionMark;// 强制更新版本号

    /**
     * 是否为强制更新
     *
     * @return
     */
    public boolean isForceUpdate() {
        int versionCodeLocal = AppConfig.getAppVersionCode();
        return forceUpdate && versionCodeLocal <= forceUpdateVersionMark;
    }
}
