package com.vivi.asyncmvc.library.plugs.http;

import android.studio.util.URLUtils;

import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.ConnectException;
import java.util.Map;

import com.vivi.asyncmvc.library.utils.LogCat;

/**
 * @author gongwei
 * @created 2018/12/19
 */
public class AHttpRequest {

    //本地缓存状态码
    public static final int HTTP_OK = 200;
    public static final int HTTP_LOCAL_CACHE = 999;
    public static final int HTTP_TIMEOUT = -500;
    public static final int HTTP_CONNECT_ERR = 0;
    private static AsyncHttpClient instance = null;

    public static AsyncHttpClient getInstance() {
        synchronized (AHttpRequest.class) {
            if (instance == null) {
                AsyncHttpClient.blockRetryExceptionClass(ConnectException.class);
                instance = new AsyncHttpClient();
                instance.setConnectTimeout(10 * 1000);
                instance.setResponseTimeout(15 * 1000); // 设置链接超时，如果不设置，默认为10s 432统一改为15秒
                instance.setMaxRetriesAndTimeout(0, AsyncHttpClient.DEFAULT_RETRY_SLEEP_TIME_MILLIS);
                Map<String, String> paramMap = AppConfig.getCookieMap();
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    instance.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        //instance.setSSLSocketFactory(getMySSLSocketFactory());
        instance.addHeader("Authorization", LoginManager.getInstance().getToken());
        return instance;
    }

    /**
     * 未使用到Https
     *
     * @param requestHandle
     */
    /*private static SSLSocketFactory getMySSLSocketFactory() {
        try {
            InputStream inStream = HikApplication.appContext.getAssets().open(AppContext.HTTPS_CRT_NAME);
            KeyStore keyStore = MySSLSocketFactory.getKeystoreOfCA(inStream);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(keyStore);
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            return socketFactory;
        } catch (Throwable e) {
            e.printStackTrace();
            return SSLSocketFactory.getSocketFactory();
        }
    }*/
    public static void cancel(RequestHandle requestHandle) {
        if (requestHandle != null) {
            requestHandle.cancel(true);
        }
    }

    /**
     * 是否缓存
     *
     * @param statusCode
     * @return
     */
    public static boolean hasCacheCode(int statusCode) {
        return statusCode == HTTP_LOCAL_CACHE;
    }

    public static boolean hasTimeoutCode(int statusCode) {
        return statusCode == HTTP_TIMEOUT;
    }

    public static boolean hasCode(int statusCode, int httCode) {
        return statusCode == httCode;
    }

    public static int getRandomRequestCode() {
        return (int) (Math.random() * -100);
    }

    /**
     * 获取数据返回json
     *
     * @param url
     * @param params
     * @param responseHandler
     * @return
     */
    public static RequestHandle get(String url, RequestParams params, TextHttpResponseHandler responseHandler) {
        return get(url, params, responseHandler, getRandomRequestCode(), false);
    }

    public static RequestHandle get(String url, RequestParams params, TextHttpResponseHandler responseHandler, boolean localCache) {
        return get(url, params, responseHandler, getRandomRequestCode(), localCache);
    }

    public static RequestHandle get(String url, RequestParams params, TextHttpResponseHandler responseHandler, int requestCode, boolean localCache) {
        String encodeUrl = URLUtils.encodeURL(url);
        LogCat.i("get[%s]: %s -> %s", requestCode, encodeUrl, params);
        return getInstance().get(encodeUrl, params, new TextHttpResponseHandlerWapper(encodeUrl, params, responseHandler, localCache));
    }

    public static RequestHandle get(String url, RequestParams params, ResultCallback callback) {
        return get(url, params, callback, false);
    }

    public static RequestHandle get(String url, RequestParams params, ResultCallback callback, boolean localCache) {
        int requestCode = getRandomRequestCode();
        return get(url, params, newTextHttpResponse(callback, requestCode), requestCode, localCache);
    }

    public static void delete(String url, RequestParams params, JsonResultCallback callback) {
        String encodeUrl = URLUtils.encodeURL(url);
        TextHttpResponseHandlerCode responseHandler = newTextHttpResponse(callback, getRandomRequestCode());
        LogCat.i("delete[%s]: %s -> %s", responseHandler.getRequestCode(), encodeUrl, params);
        getInstance().delete(encodeUrl, params, new TextHttpResponseHandlerWapper(encodeUrl, params, responseHandler, false));
    }

    public static RequestHandle put(String url, RequestParams params, JsonResultCallback callback) {
        String encodeUrl = URLUtils.encodeURL(url);
        TextHttpResponseHandlerCode responseHandler = newTextHttpResponse(callback, getRandomRequestCode());
        LogCat.i("put[%s]: %s -> %s", responseHandler.getRequestCode(), encodeUrl, params);
        return getInstance().put(encodeUrl, params, new TextHttpResponseHandlerWapper(encodeUrl, params, responseHandler, false));
    }

    public static RequestHandle post(String url, RequestParams params, TextHttpResponseHandler responseHandler) {
        return post(url, params, responseHandler, getRandomRequestCode(), false);
    }

    public static RequestHandle post(String url, RequestParams params, TextHttpResponseHandler responseHandler, int requestCode, boolean localCache) {
        String encodeUrl = URLUtils.encodeURL(url);
        LogCat.i("post[%s]: %s -> %s", requestCode, encodeUrl, params);
        return getInstance().post(encodeUrl, params, new TextHttpResponseHandlerWapper(encodeUrl, params, responseHandler, localCache));
    }

    public static RequestHandle post(String url, RequestParams params, ResultCallback callback) {
        return post(url, params, callback, false);
    }

    public static RequestHandle post(String url, RequestParams params, ResultCallback callback, boolean localCache) {
        int requestCode = getRandomRequestCode();
        return post(url, params, newTextHttpResponse(callback, requestCode), requestCode, localCache);
    }

    private static TextHttpResponseHandlerCode newTextHttpResponse(final ResultCallback callback, final int requestCode) {
        return new TextHttpResponseHandlerCode(callback, requestCode);
    }

}
