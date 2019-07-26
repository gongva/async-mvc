package com.vivi.asyncmvc.api.entity;

import android.studio.util.DateUtils;

import com.vivi.asyncmvc.library.plugs.sqlite.AOrm;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Description: 行驶证
 *
 * @Author: gongwei
 * @Date: 2019/1/10
 */
@Table("CarLicense")
public class CarLicense implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    public String id;// 行驶证ID
    public String plateNum;// 车牌号
    public String operationType;// 汽车使用性质:A
    public String operationTypeName;//汽车使用性质名称:营运车辆
    public String vehicleType;// 车辆类型:02
    public String vehicleTypeName;//车辆类型名称:小型车辆
    public long issueDate;// 发证日期
    public String ownerName;// 所有人名字
    public String qrInfo;// 行驶证二维码基础信息
    public boolean isOwner;// 是否本人机动车
    public String phone;
    public int unhandleCount;//未处理违法数
    public long expiredDate;//有效期止


    public String getIssueDate() {
        return DateUtils.formatDate(new Date(issueDate));
    }

    public String getExpiredDate() {
        return DateUtils.formatDate(new Date(expiredDate));
    }

    public static List<CarLicense> queryAll() {
        QueryBuilder<CarLicense> qb = new QueryBuilder(CarLicense.class);
        List<CarLicense> list = AOrm.getUserOrm().query(qb);
        return list;
    }

    public static CarLicense queryById(String id) {
        return AOrm.getUserOrm().queryById(id, CarLicense.class);
    }

    /**
     * 查本人/非本人机动车
     *
     * @return
     */
    public static List<CarLicense> query(boolean isOwner) {
        QueryBuilder<CarLicense> qb = new QueryBuilder(CarLicense.class);
        qb.whereEquals("isOwner", isOwner);
        return AOrm.getUserOrm().query(qb);
    }

    public static void save(CarLicense... carLicenses) {
        save(Arrays.asList(carLicenses));
    }

    public static void save(List<CarLicense> carLicenseList) {
        AOrm.getUserOrm().deleteAll(CarLicense.class);
        AOrm.getUserOrm().save(carLicenseList);
    }

    public static void delete(String id) {
        AOrm.getUserOrm().delete(new WhereBuilder(CarLicense.class, "id=?", new Object[]{id}));
    }
}
