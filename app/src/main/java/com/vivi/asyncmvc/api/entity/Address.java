package com.vivi.asyncmvc.api.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 收货地址
 *
 * @author gongwei
 * @date 2019.1.23
 */
public class Address implements Serializable {
    public String id;
    public String recipientName;//收货人
    public String recipientPhone;//收货电话
    public String province;//内蒙古
    public String provinceCode;
    public String city;//呼和浩特市
    public String cityCode;
    public String county;//托克托县
    public String countyCode;
    public String address;//酒税镇
    public boolean isDefault;//是否为默认地址

    /**
     * 除了id，其他数据是否完整
     *
     * @return
     */
    public boolean isDateCompleted() {
        return !TextUtils.isEmpty(recipientName) && !TextUtils.isEmpty(recipientPhone)
                && !TextUtils.isEmpty(province) && !TextUtils.isEmpty(provinceCode)
                && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(cityCode)
                && !TextUtils.isEmpty(county) && !TextUtils.isEmpty(countyCode)
                && !TextUtils.isEmpty(address);
    }

    public Area getProvinceArea() {
        if (TextUtils.isEmpty(provinceCode) || TextUtils.isEmpty(province)) {
            return null;
        }
        Area p = new Area();
        p.code = provinceCode;
        p.name = province;
        return p;
    }

    public Area getCityArea() {
        if (TextUtils.isEmpty(cityCode) || TextUtils.isEmpty(city)) {
            return null;
        }
        Area c = new Area();
        c.code = cityCode;
        c.name = city;
        return c;
    }

    public Area getCountyArea() {
        if (TextUtils.isEmpty(countyCode) || TextUtils.isEmpty(county)) {
            return null;
        }
        Area c = new Area();
        c.code = countyCode;
        c.name = county;
        return c;
    }
}
