package com.vivi.asyncmvc.library.plugs.http;

import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * 请求包装使支持状态码
 *
 * @author gongwei
 * @created 2018/12/19
 */
public class ResponseHandlerWapper implements ResponseHandlerInterface {

    private ResponseHandlerInterface responseHandler;
    private int requestCode;

    public ResponseHandlerWapper(ResponseHandlerInterface responseHandler, int requestCode) {
        this.responseHandler = responseHandler;
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        responseHandler.sendResponseMessage(response);
    }

    @Override
    public void sendStartMessage() {
        responseHandler.sendStartMessage();
    }

    @Override
    public void sendFinishMessage() {
        responseHandler.sendFinishMessage();
    }

    @Override
    public void sendProgressMessage(long bytesWritten, long bytesTotal) {
        responseHandler.sendProgressMessage(bytesWritten, bytesTotal);
    }

    @Override
    public void sendCancelMessage() {
        responseHandler.sendCancelMessage();
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
        responseHandler.sendSuccessMessage(statusCode, headers, responseBody);
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        responseHandler.sendFailureMessage(statusCode, headers, responseBody, error);
    }

    @Override
    public void sendRetryMessage(int retryNo) {
        responseHandler.sendRetryMessage(retryNo);
    }

    @Override
    public URI getRequestURI() {
        return responseHandler.getRequestURI();
    }

    @Override
    public void setRequestURI(URI requestURI) {
        responseHandler.setRequestURI(requestURI);
    }

    @Override
    public Header[] getRequestHeaders() {
        return responseHandler.getRequestHeaders();
    }

    @Override
    public void setRequestHeaders(Header[] requestHeaders) {
        responseHandler.setRequestHeaders(requestHeaders);
    }

    @Override
    public boolean getUseSynchronousMode() {
        return responseHandler.getUseSynchronousMode();
    }

    @Override
    public void setUseSynchronousMode(boolean useSynchronousMode) {
        responseHandler.setUseSynchronousMode(useSynchronousMode);
    }

    @Override
    public boolean getUsePoolThread() {
        return responseHandler.getUsePoolThread();
    }

    @Override
    public void setUsePoolThread(boolean usePoolThread) {
        responseHandler.setUsePoolThread(usePoolThread);
    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
        responseHandler.onPreProcessResponse(instance, response);
    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
        responseHandler.onPostProcessResponse(instance, response);
    }

    @Override
    public Object getTag() {
        return responseHandler.getTag();
    }

    @Override
    public void setTag(Object TAG) {
        responseHandler.setTag(TAG);
    }
}
