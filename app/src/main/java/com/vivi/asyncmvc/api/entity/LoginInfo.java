package com.vivi.asyncmvc.api.entity;

import java.io.Serializable;

/**
 * 登录返回的信息
 */
public class LoginInfo implements Serializable {
    public String token;
    public UserInfo userInfo;
}
