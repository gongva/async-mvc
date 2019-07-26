package com.vivi.asyncmvc.api.entity;

import com.vivi.asyncmvc.library.plugs.sqlite.AOrm;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.model.ColumnsValue;

import java.util.Arrays;
import java.util.List;

/**
 * 消息中心实体
 *
 * @author gongwei
 * @date 2019/2/14
 */
@Table("Message")
public class Message {
    public static final int APPLY_EDL = 1;//申领电子驾照
    public static final int FILING_VEHICLE = 2;//机动车备案提醒
    public static final int DISMISS_AL_VEHICLE = 3;//机动车解除备案提醒
    public static final int FN = 4;//限号提醒
    public static final int MOVING_CAR = 5;//挪车通知
    public static final int ILLEGAL = 6;//违法行为
    public static final int DL_LOCK = 7;//驾驶证锁定提醒
    public static final int VL_LOCK = 8;//行驶证锁定提醒
    public static final int DL_EXAMINED = 9;//驾驶证年审提醒
    public static final int VEHICLE_INSPECTION = 10;//机动车车检提醒

    @PrimaryKey(AssignType.BY_MYSELF)
    public String id;
    public int msgType;//消息类型
    public String msgTitle;//消息标题
    public String msgExt;//消息扩展字段
    public long gmtCreate;//创建时间，时间戳
    public boolean read;//消息是否已读
    public boolean homeShow;//是否在首页的智慧服务显示

    public static List<Message> queryAll() {
        QueryBuilder<Message> qb = new QueryBuilder(Message.class);
        List<Message> list = AOrm.getUserOrm().query(qb);
        return list;
    }

    /**
     * 按时间倒叙query
     *
     * @param limit 取多少条
     * @return
     */
    public static List<Message> query(int limit) {
        QueryBuilder<Message> qb = new QueryBuilder(Message.class);
        qb.appendOrderDescBy("gmtCreate");
        qb.limit(0, limit);
        List<Message> list = AOrm.getUserOrm().query(qb);
        return list;
    }

    /**
     * 获取首页智慧服务消息
     * 按时间倒叙query
     *
     * @param limit 取多少条
     * @return
     */
    public static List<Message> queryForHome(int limit) {
        QueryBuilder<Message> qb = new QueryBuilder(Message.class);
        qb.whereEquals("homeShow", true);
        qb.appendOrderDescBy("gmtCreate");
        qb.limit(0, limit);
        List<Message> list = AOrm.getUserOrm().query(qb);
        return list;
    }

    public static void save(Message... messages) {
        save(Arrays.asList(messages));
    }

    public static void save(List<Message> messages) {
        AOrm.getUserOrm().save(messages);
    }

    public static void delete(String id) {
        AOrm.getUserOrm().delete(new WhereBuilder(Message.class, "id=?", new Object[]{id}));
    }

    public static void updateAsRead(String id) {
        AOrm.getUserOrm().update(new WhereBuilder(Message.class, "id=?", new Object[]{id}), new ColumnsValue(new String[]{"read"}, new Object[]{true}), null);
    }

    public static void updateAsHomeNotShow(String id) {
        AOrm.getUserOrm().update(new WhereBuilder(Message.class, "id=?", new Object[]{id}), new ColumnsValue(new String[]{"homeShow"}, new Object[]{false}), null);
    }
}