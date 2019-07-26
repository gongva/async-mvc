package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-机动车年检提醒
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class VehicleInspectionExpand implements MessageExpand {
    public String licensePlateNumber;//车牌号
    public long deadline;//截止日期

    public static VehicleInspectionExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, VehicleInspectionExpand.class);
    }
}