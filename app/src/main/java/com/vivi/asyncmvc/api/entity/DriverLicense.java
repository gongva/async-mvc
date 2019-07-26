package com.vivi.asyncmvc.api.entity;

import android.studio.util.DateUtils;

import com.vivi.asyncmvc.library.plugs.sqlite.AOrm;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Description: 驾驶证
 *
 * @Author: gongwei
 * @Date: 2019/1/10
 */
@Table("DriverLicense")
public class DriverLicense implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    public String id;// 驾照ID
    public String head;// 证件照
    public long clearDate;// 清分日期
    public int residualScore;// 剩余计分
    public String realName;// 实名姓名
    public String licenseType;//  准驾类型:C1
    public long expiredDate;// 有效期
    public String qrInfo;// 驾驶证二维码基础信息

    public String getClearDate() {
        return DateUtils.formatDate(new Date(clearDate));
    }

    public String getExpiredDate() {
        return DateUtils.formatDate(new Date(expiredDate));
    }

    public static DriverLicense query() {
        DriverLicense result = null;
        QueryBuilder<DriverLicense> qb = new QueryBuilder(DriverLicense.class);
        List<DriverLicense> list = AOrm.getUserOrm().query(qb);
        if (list != null && list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }

    public static void save(DriverLicense... driverLicenses) {
        save(Arrays.asList(driverLicenses));
    }

    public static void save(List<DriverLicense> driverLicenses) {
        AOrm.getUserOrm().deleteAll(DriverLicense.class);
        AOrm.getUserOrm().save(driverLicenses);
    }
}
