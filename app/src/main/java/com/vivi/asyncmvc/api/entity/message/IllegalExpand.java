package com.vivi.asyncmvc.api.entity.message;

import android.studio.plugins.GsonUtils;

/**
 * 消息中心扩展信息-违法信息
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class IllegalExpand implements MessageExpand {
    public String id;//违法id
    public String licensePlateNumber;//车牌号
    public long illegalTime;//违法时间
    public String illegalAddr;//eg：贵阳市西山东路33号
    public String illegalContent;//eg：闯红灯
    public int illegalScores;//扣多少分
    public long fines;//罚多少款，单位：分

    public static IllegalExpand getExpand(String expand) {
        return GsonUtils.jsonDeserializer(expand, IllegalExpand.class);
    }
}