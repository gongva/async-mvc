package com.vivi.asyncmvc.comm;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.vivi.asyncmvc.base.BaseApplication;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {


    public static final String PHONE_TYPE_ANDROID = "Android";//客户端类型：安卓

    public static final int PASSWORD_MIN_LENGTH = 8;//密码最短长度

    public static final int PASSWORD_MAX_LENGTH = 16;//密码最长长度

    public static final int SECOND_RESEND_CAPTCHA = 60;//重新获取验证码的时间倒计时，单位：s

    public static final int SECOND_QR_CODE_AUTO_REFRESH = 60;//驾驶证、行驶证二维码自动刷新时间，单位：s

    public static final int HTTP_DEFAULT_SIZE = 20;//分页接口请求的每页条数，默认20

    public static final String SERVICE_CALL_DEFAULT = "122";//默认客服电话

    /**
     * 获取版本号Name
     *
     * @return
     */
    public static String getAppVersionName() {
        try {
            PackageInfo packageInfo = getPackageInfo(BaseApplication.getInstance());
            return packageInfo.versionName;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取版本号Code
     *
     * @return
     */
    public static int getAppVersionCode() {
        try {
            PackageInfo packageInfo = getPackageInfo(BaseApplication.getInstance());
            return packageInfo.versionCode;
        } catch (Exception e) {
            return -1;
        }
    }

    private static PackageInfo getPackageInfo(Context context) throws PackageManager.NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageInfo(context.getPackageName(), 0);
    }

    /**
     * 获取Header参数集，用于Http请求与WebView加载时传参
     *
     * @return
     */
    public static Map<String, String> getCookieMap() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Version", AppConfig.getAppVersionName());
        headerMap.put("Device-Type", PHONE_TYPE_ANDROID);
        return headerMap;
    }
}
