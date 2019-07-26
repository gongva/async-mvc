package com.vivi.asyncmvc.library.plugs.http;

import android.os.Message;

import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.UnknownHostException;

import cz.msebera.android.httpclient.Header;

/**
 * 请求包装使支持状态码
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class TextHttpResponseHandlerWapper extends TextHttpResponseHandler {

    private String url;
    private RequestParams params;
    private TextHttpResponseHandler responseHandler;
    private boolean localCache;


    public TextHttpResponseHandlerWapper(String url, RequestParams params, TextHttpResponseHandler responseHandler, boolean localCache) {
        this.url = url;
        this.params = params;
        this.responseHandler = responseHandler;
        this.localCache = localCache;
    }

    @Override
    public void setRequestURI(URI requestURI) {
        super.setRequestURI(requestURI);
    }

    @Override
    protected void handleMessage(Message message) {
        try {
            super.handleMessage(message);
        } catch (Throwable error) {
            onFailure(-1, null, "LoginUser-space exception detected!", error);
        }
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        //super.onSuccess(statusCode, headers, responseBytes);
        if (localCache) {
            String cacheKey = AHttpCache.newUrl(url, params);
            AHttpCache.putCache(cacheKey, responseBytes);
        }
        if (responseHandler != null) {
            responseHandler.onSuccess(statusCode, headers, responseBytes);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBytes, Throwable throwable) {
        //super.onFailure(statusCode, headers, responseBytes, throwable);
        /*byte[] jsonCache = AHttpCache.getCacheBytes(getRequestURI().toString());
        if (jsonCache != null && jsonCache.length > 0) {
            //LogCat.i("response cache[%s, %s]: %s", -1, statusCode, jsonCache);
            responseHandler.onSuccess(AHttpRequest.HTTP_LOCAL_CACHE, headers, jsonCache);
        } else {*/

        if (localCache) {
            String cacheKey = AHttpCache.newUrl(url, params);
            byte[] jsonCache = AHttpCache.getCacheBytes(cacheKey);
            if (jsonCache != null && jsonCache.length > 0) {
                if (responseHandler != null) {
                    responseHandler.onSuccess(AHttpRequest.HTTP_LOCAL_CACHE, headers, jsonCache);
                    return;
                }
            }
        }
        if (responseHandler != null) {
            statusCode = getErrorCode(statusCode, throwable);
            responseHandler.onFailure(statusCode, headers, responseBytes, throwable);
        }
    }

    private static int getErrorCode(int statusCode, Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            statusCode = AHttpRequest.HTTP_TIMEOUT;
        } else if (throwable instanceof ConnectException || throwable instanceof UnknownHostException) {
            statusCode = AHttpRequest.HTTP_CONNECT_ERR;
        }
        return statusCode;
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
    }
}
