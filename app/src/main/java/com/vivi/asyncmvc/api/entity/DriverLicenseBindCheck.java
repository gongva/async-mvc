package com.vivi.asyncmvc.api.entity;

/**
 * 绑定驾驶证检查结果
 *
 * @author gongwei
 * @date 2019/2/12
 */
public class DriverLicenseBindCheck {
    public boolean status;
    public String message;//status为false是错误描述，eg：你暂无驾驶证
}
