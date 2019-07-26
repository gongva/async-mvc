package com.vivi.asyncmvc.library.plugs.http.entity;

import android.studio.plugins.GsonUtils;

import com.vivi.asyncmvc.comm.AppContext;

/**
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonResultVoid {

    public boolean success;
    public String msg;
    public int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isError() {
        return !isSuccess();
    }

    public String getMsg() {
        return msg;
    }

    public static JsonResultVoid createErrorVoid(String response) {
        JsonResultVoid resultVoid = GsonUtils.jsonDeserializer(response, JsonResultVoid.class);
        if (resultVoid == null) {
            resultVoid = new JsonResultVoid();
            resultVoid.msg = response;
        }
        return resultVoid;
    }

    /**
     * 业务异常：100001 账号被挤下线
     *
     * @return
     */
    public boolean isLogoff() {
        return code == AppContext.ERROR_CODE_LOGOFF;
    }

    /**
     * 业务异常：100401 登录失效
     *
     * @return
     */
    public boolean isLoginError() {
        return code == AppContext.ERROR_CODE_LOGIN_ERROR;
    }
}
