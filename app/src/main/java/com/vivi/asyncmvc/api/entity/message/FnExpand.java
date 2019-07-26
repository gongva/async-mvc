package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-限号提醒
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class FnExpand implements MessageExpand {
    public String licensePlateNumber;//车牌号
    public String content;//eg:一环内今日限号
    public String limitTimeRange;//eg：7:00-20:00
    public String description;//跳转详情后显示的详细描述

    public static FnExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, FnExpand.class);
    }
}