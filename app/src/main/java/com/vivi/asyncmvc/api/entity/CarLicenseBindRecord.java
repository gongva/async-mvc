package com.vivi.asyncmvc.api.entity;

import android.studio.util.DateUtils;

import java.util.Date;

/**
 * 机动车备案记录
 * 含：用户备案记录、他人备案记录
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class CarLicenseBindRecord {
    public static final String BIND_STATUS_BIND = "bind";//机动车绑定记录
    public static final String BIND_STATUS_UNBIND = "unbind";//机动车解绑记录

    public String plateNum;//车牌号码
    public String status;//绑定状态
    public long date;//操作时间
    public String name;//作人姓名
    public String id;//机动车备案ID
    public String phone;//手机号

    public String getBindStatusText() {
        switch (status) {
            case BIND_STATUS_UNBIND:
                return "解除绑定";
            case BIND_STATUS_BIND:
            default:
                return "绑定";
        }
    }

    public String getRecordDate() {
        return DateUtils.formatDate(new Date(date));
    }
}