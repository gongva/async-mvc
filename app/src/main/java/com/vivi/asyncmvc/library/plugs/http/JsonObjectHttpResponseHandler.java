package com.vivi.asyncmvc.library.plugs.http;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * JsonObject回调类
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class JsonObjectHttpResponseHandler extends JsonHttpResponseHandler {


    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

    }

    @Override
    public final void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        try {
            onSuccess(statusCode, headers, response.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {
        try {
            onSuccess(statusCode, headers, new JSONObject(responseString));
        } catch (JSONException e) {
            e.printStackTrace();
            onSuccess(statusCode, headers, new JSONObject());
        }
    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        onFailure(statusCode, headers, errorResponse == null ? "" : errorResponse.toString(), throwable);
    }

    @Override
    public final void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        onFailure(statusCode, headers, errorResponse == null ? "" : errorResponse.toString(), throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

    }
}
