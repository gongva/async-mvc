package com.vivi.asyncmvc.library.plugs.http;

import android.studio.plugins.GsonUtils;

import com.google.gson.reflect.TypeToken;

import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.event.LoginErrorEvent;
import com.vivi.asyncmvc.comm.event.LogoffEvent;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.LogCat;

/**
 * Json解析成对象
 *
 * @author gongwei
 * @created 2018/12/19
 */
public abstract class JsonResultCallback<T extends JsonResultVoid> extends TypeToken<T> implements ResultCallback {

    @Override
    public final void onSuccess(int statusCode, String responseString, int tag) {
        T t = GsonUtils.jsonDeserializer(responseString, this);
        if (t == null) {
            LogCat.e("jsonDeserializer[%s, %s]: is null", tag, statusCode);
            onFailure(statusCode, "解析出错", new NullPointerException("jsonDeserializer is null"), tag);
            return;
        }

        if (t.isError()) {
            if (t.isLogoff()) {
                LogCat.e("jsonDeserializer[%s, %s]: %s", tag, statusCode, "账号被挤下线");
                BusProvider.post(new LogoffEvent(t.msg));
            } else if (t.isLoginError()) {
                LogCat.e("jsonDeserializer[%s, %s]: %s", tag, statusCode, "登录失效");
                BusProvider.post(new LoginErrorEvent(t.msg));
            } else {
                LogCat.e("jsonDeserializer[%s, %s]: %s", tag, statusCode, t.getMsg());
                onFailure(statusCode, t.getMsg(), new Exception(responseString), tag);
            }
            return;
        }

        onSuccess(statusCode, t, tag);
    }

    public abstract void onSuccess(int statusCode, T response, int tag);

    @Override
    public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {

    }
}
