package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-挪车通知
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class MovingCarExpand implements MessageExpand {
    public String id;//一键挪车id
    public String licensePlateNumber;//车牌号
    public String addr;//地址，eg：贵阳市西山东路33号

    public static MovingCarExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, MovingCarExpand.class);
    }
}