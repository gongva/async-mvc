package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-行驶证锁定
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class VlLockExpand implements MessageExpand {
    public String licensePlateNumber;//车牌号

    public static VlLockExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, VlLockExpand.class);
    }
}