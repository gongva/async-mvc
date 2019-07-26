package com.vivi.asyncmvc.library.plugs.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

/**
 *
 * @author gongwei
 * @created 2018/12/19
 */
public abstract class BitmapRequestCallback extends TextHttpResponseHandler {

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBytes) {
        //super.onSuccess(statusCode, headers, responseBytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(responseBytes, 0, responseBytes.length);
        onSuccess(statusCode, headers, bitmap);
    }

    public abstract void onSuccess(int statusCode, Header[] headers, Bitmap bitmap);

    @Override
    public final void onSuccess(int statusCode, Header[] headers, String responseString) {

    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

    }
}
