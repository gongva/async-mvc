package com.vivi.asyncmvc.library.plugs.http.entity;

import android.studio.os.PreferencesUtils;

import java.util.Collection;

/**
 * Json获取统一返回接口
 */
public class JsonResult<T> extends JsonResultVoid {

    private T data;

    public T getData() {
        return data;
    }

    //视频是否受权播放
    public boolean isVideoNonAuthoritative() {
        return code == 301;
    }

    public static <T> JsonResult<T> createJsonResult(T data, boolean success) {
        JsonResult<T> jsonResult = new JsonResult<>();
        jsonResult.success = success;
        jsonResult.data = data;
        return jsonResult;
    }

    /**
     * 判断数据是否正确
     *
     * @param jsonResult
     * @return
     */
    public static boolean hasSuccess(JsonResult<?> jsonResult) {
        return jsonResult != null && jsonResult.isSuccess();
    }

    /**
     * 判断是否有内容
     *
     * @param jsonResult
     * @return
     */
    public static boolean isEmpty(JsonResult<? extends Collection> jsonResult) {
        return jsonResult == null || jsonResult.isError() || jsonResult.getData() == null || jsonResult.getData().isEmpty();
    }

    public static boolean isNotEmpty(JsonResult<? extends Collection> jsonResult) {
        return !isEmpty(jsonResult);
    }
}
