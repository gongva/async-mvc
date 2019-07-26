package com.vivi.asyncmvc.api.entity;

import java.io.Serializable;

/**
 * 用户信息
 */
public class UserInfo implements Serializable {
    public String id;//033cb271-e52b-4a97-8cf3-3b79d3a403dd
    public String username;//**彪
    public String avatar;//mock
    public String idCardNum;//522627********2615
    public String userType;//驾驶员
    public long registerTime;//注册时间
    public String phone;//155****3868
    public String email;//785133598@qq.com
    public String address;//观山湖高新区
    public boolean hasDriverLicense;//是否有驾照
    public String trafficCredit;//良好
    public int driverLicenseAvailableScore;//驾照剩余积分：9

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", idCardNum='" + idCardNum + '\'' +
                ", userType='" + userType + '\'' +
                ", registerTime=" + registerTime +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", hasDriverLicense=" + hasDriverLicense +
                ", trafficCredit='" + trafficCredit + '\'' +
                ", driverLicenseAvailableScore=" + driverLicenseAvailableScore +
                '}';
    }
}
