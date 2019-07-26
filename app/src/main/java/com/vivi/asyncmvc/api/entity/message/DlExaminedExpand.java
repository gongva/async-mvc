package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-驾驶证年审提醒
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class DlExaminedExpand implements MessageExpand {
    public long deadline;//截止日期

    public static DlExaminedExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, DlExaminedExpand.class);
    }
}