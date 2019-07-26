package com.vivi.asyncmvc.library.plugs.http;

import android.text.TextUtils;

import com.loopj.android.http.TextHttpResponseHandler;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.vivi.asyncmvc.library.utils.LogCat;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ConnectTimeoutException;

/**
 * @author gongwei
 * @created 2018/12/19
 */
public class TextHttpResponseHandlerCode extends TextHttpResponseHandler {

    private ResultCallback callback;
    private int requestCode;

    public TextHttpResponseHandlerCode(ResultCallback callback) {
        this(callback, AHttpRequest.getRandomRequestCode());
    }

    public TextHttpResponseHandlerCode(ResultCallback callback, int requestCode) {
        super();
        this.callback = callback;
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        LogCat.e("responseString[%s, %s]: %s", requestCode, statusCode, responseString);
        if (callback != null) {
            responseString = getErrorMessage(responseString, throwable);
            callback.onFailure(statusCode, responseString, throwable, requestCode);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        if (TextUtils.isEmpty(responseString)) {
            onFailure(statusCode, headers, "返回数据为空", new Exception("responseString is null"));
            return;
        }

        LogCat.i("responseString[%s, %s]: %s", requestCode, statusCode, responseString);
        if (callback != null) {
            callback.onSuccess(statusCode, responseString, requestCode);
        }
    }

    private static String getErrorMessage(String responseString, Throwable throwable) {
        if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectTimeoutException) {
            responseString = "请求超时";
        } else if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            responseString = "网络连接失败";
        }
        return TextUtils.isEmpty(responseString) ? "请求失败" : responseString;
    }
}
