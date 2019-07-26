package com.vivi.asyncmvc.api.entity;

import java.io.Serializable;

/**
 * 验证码验证接口的返回，包含：注册额、忘记密码
 */
public class CaptchaCheck implements Serializable {

    public String phone;
    public String token;//用于后续流程的验证

    //忘记密码时的校验
    public boolean hasDriverLicense;//是否有绑定的驾照
}