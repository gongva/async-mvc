package com.vivi.asyncmvc.library.plugs.http;

import android.studio.plugins.GsonUtils;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Json数据参数
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonRequestParams extends RequestParams {

    private final Map<String, Object> jsonParams = new HashMap();
    private String jsonBody;

    public JsonRequestParams() {
        super();
    }

    public JsonRequestParams(Object jsonObj) {
        this(GsonUtils.jsonSerializer(jsonObj));
    }

    public JsonRequestParams(String jsonBody) {
        super();
        this.jsonBody = jsonBody;
    }

    @Override
    public void put(String key, String value) {
        jsonParams.put(key, value);
    }

    @Override
    public void put(String key, int value) {
        jsonParams.put(key, value);
    }

    @Override
    public void put(String key, long value) {
        jsonParams.put(key, value);
    }

    @Override
    public void remove(String key) {
        super.remove(key);
        jsonParams.remove(key);
    }

    public boolean has(String key) {
        return jsonParams.get(key) != null || super.has(key);
    }

    @Override
    public HttpEntity getEntity(ResponseHandlerInterface progressHandler) throws IOException {
        //return super.getEntity(progressHandler);
        return new StringEntity(getJsonBody(), ContentType.APPLICATION_JSON);
    }

    private String getJsonBody() {
        if (jsonBody != null) {
            return jsonBody;
        }

        Map<String, Object> entity = new HashMap<>();

        // Add string params
        for (ConcurrentHashMap.Entry<String, Object> entry : jsonParams.entrySet()) {
            entity.put(entry.getKey(), entry.getValue());
        }

        // Add non-string params
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParamsWithObjects.entrySet()) {
            entity.put(entry.getKey(), entry.getValue());
        }

        // Add file params
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            entity.put(entry.getKey(), entry.getValue());
        }

        // Add stream params
        for (ConcurrentHashMap.Entry<String, StreamWrapper> entry : streamParams.entrySet()) {
            StreamWrapper stream = entry.getValue();
            if (stream.inputStream != null) {
                entity.put(entry.getKey(), new StreamWrapper(stream.inputStream, stream.name, stream.contentType, stream.autoClose));
            }
        }

        return GsonUtils.jsonSerializer(entity);
    }

    @Override
    public String toString() {
        return getJsonBody();
    }
}
