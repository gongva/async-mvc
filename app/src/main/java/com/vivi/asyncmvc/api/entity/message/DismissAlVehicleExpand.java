package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-解除备案提醒
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class DismissAlVehicleExpand implements MessageExpand {
    public String licensePlateNumber;//车牌号
    public String description;//跳转详情后显示的详细描述

    public static DismissAlVehicleExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, DismissAlVehicleExpand.class);
    }
}