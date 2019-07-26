package com.vivi.asyncmvc.library.plugs.http;

/**
 * Http请求回调
 *
 * @author gongwei
 * @created 2018/12/19
 */
public interface ResultCallback {

    void onSuccess(int statusCode, String responseString, int tag);

    void onFailure(int statusCode, String responseString, Throwable throwable, int tag);
}
