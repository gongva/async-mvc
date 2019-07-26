package com.vivi.asyncmvc.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.UUID;

import com.vivi.asyncmvc.base.BaseApplication;

/**
 * 系统相关工具方法类
 *
 * @author gongwei
 */
public abstract class OS {

    /**
     * 获取屏幕Display
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获得状态栏高度
     * ps:非全屏+非沉浸式的Activity才可用
     *
     * @param activity
     */
    public static int getStateHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 开启软键盘
     *
     * @param editText
     */
    public static void showSoftKeyboard(final EditText editText) {
        showSoftKeyboard(editText, false);
    }

    /**
     * 开启软键盘
     *
     * @param editText
     * @param lazy
     */
    public static void showSoftKeyboard(final EditText editText, boolean lazy) {
        if (lazy) {
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }, 200);
        } else {
            editText.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 开启软键盘
     *
     * @param activity
     */
    public static void showSoftKeyboard(final Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null && activity.getCurrentFocus() instanceof EditText) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
                }
            }, 200);
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = activity.getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * 获取设备名称
     *
     * @return
     */
    public static String getDeviceName() {
        return String.format("%s %s", Build.BRAND, Build.MODEL); //手机厂商 手机型号
    }

    /**
     * 获得MobileCode
     *
     * @return
     */
    public static String getIEMI() {
        String result = getDeviceId();
        if (TextUtils.isEmpty(result)) {
            result = getBuildSerial();
        }
        return result;
    }

    /**
     * 获取Device Id。(Android手机可以获取到，其他手持设备不行，如平板)
     *
     * @return DeviceId or null
     */
    @SuppressLint("MissingPermission")
    private static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = null;
        try {
            deviceId = tm.getDeviceId();
        } catch (Exception e) {
        }
        if (!TextUtils.isEmpty(deviceId)) {
            try {
                UUID deviceUuid = new UUID(deviceId.hashCode(), deviceId.hashCode() << 32 | deviceId.hashCode());
                return deviceUuid.toString().replace("-", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取Serial号
     * 失败时则通过硬件信息生成的伪设备码
     *
     * @return Build.SERIAL or Pseudo DeviceId
     */
    public static String getBuildSerial() {
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 位
        String uuid = "";
        try {
            //API>=9 使用serial号
            uuid = new UUID(m_szDevIDShort.hashCode(), Build.SERIAL.hashCode()).toString();
        } catch (Exception exception) {
            //使用硬件信息构建出来的15位号码
            uuid = new UUID(m_szDevIDShort.hashCode(), "unknown".hashCode()).toString();
        }
        return uuid.replace("-", "");
    }

    /**
     * 根据宽度和比例计算高度
     *
     * @param ratio eg: 16f/7
     * @param width
     * @return
     */
    public static int getHeightByRatioAndWidth(float ratio, int width) {
        return (int) (width / ratio);
    }

    /**
     * 根据屏幕宽度和比例计算高度
     *
     * @param context
     * @param ratio   eg: 16f/7
     * @return
     */
    public static int getHeightByRatioAndScreenWidth(Activity context, float ratio) {
        return getHeightByRatioAndWidth(ratio, getScreenWidth(context));
    }
}
