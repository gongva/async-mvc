package com.vivi.asyncmvc.library.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.vivi.asyncmvc.base.ActivitySupport;
import com.vivi.asyncmvc.base.BaseApplication;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.view.dialog.AlertDialog;
import com.vivi.asyncmvc.comm.view.dialog.ListDialog;
import com.vivi.asyncmvc.comm.view.dialog.LoadingDialog;

import java.util.Arrays;
import java.util.List;

/**
 * UI相关提示
 *
 * @author gongwei 2018/12/18.
 */
public class UI {

    private static Toast toast = null;

    public static void showToast(int resId) {
        showToast(BaseApplication.appContext.getString(resId));
    }

    public static void showToast(String msg) {
        showToast(BaseApplication.appContext, msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String msg, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    /**
     * Commit时的确认框，含：
     * 标题有无、一个/两个按钮、取消可否
     *
     * @param context
     * @param message
     * @param listener
     * @return
     */
    public static Dialog showLoadingDialog(Context context, String message, final DialogInterface.OnCancelListener listener) {
        LoadingDialog loadingDialog = LoadingDialog.getCustomLoadingProgressDialog(context, message, true, listener);
        loadingDialog.show();
        return loadingDialog;
    }

    public static AlertDialog showConfirmDialog(Context context, String message, String buttonText, View.OnClickListener listener) {
        return showConfirmDialog(context, message, null, null, buttonText, listener);
    }

    public static AlertDialog showConfirmDialog(Context context, boolean cancelable, String message, String buttonText, View.OnClickListener listener) {
        return showConfirmDialog(context, cancelable, null, message, null, null, buttonText, listener);
    }

    public static AlertDialog showConfirmDialog(Context context, boolean cancelable, String title, String message, String buttonText, View.OnClickListener listener) {
        return showConfirmDialog(context, cancelable, title, message, null, null, buttonText, listener);
    }

    public static AlertDialog showConfirmDialog(Context context, String title, String message, String buttonText, View.OnClickListener listener) {
        return showConfirmDialog(context, true, title, message, null, null, buttonText, listener);
    }

    public static AlertDialog showConfirmDialog(Context context, String message, String buttonLeft, View.OnClickListener listenerLeft, String buttonRight, View.OnClickListener listenerRight) {
        return showConfirmDialog(context, true, null, message, buttonLeft, listenerLeft, buttonRight, listenerRight);
    }

    public static AlertDialog showConfirmDialog(Context context, String title, String message, String buttonLeft, View.OnClickListener listenerLeft, String buttonRight, View.OnClickListener listenerRight) {
        return showConfirmDialog(context, true, title, message, buttonLeft, listenerLeft, buttonRight, listenerRight);
    }

    public static AlertDialog showConfirmDialog(Context context, boolean cancelable, String title, String message, String buttonLeft, View.OnClickListener listenerLeft, String buttonRight, View.OnClickListener listenerRight) {
        return AlertDialog.show(context, cancelable, title, message, buttonLeft, listenerLeft, buttonRight, listenerRight);
    }

    /**
     * 拨打电话的确认框
     *
     * @param activity
     * @param phone
     */
    public static void makeCall(final Activity activity, final String phone) {
        showConfirmDialog(activity, String.format("是否拨打电话?\n\n%s", phone), "取消", null, "拨打", new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ActivitySupport.requestRunPermission(activity, new String[]{Manifest.permission.CALL_PHONE}, new PermissionRequestListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {
                        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                        intentCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intentCall);
                    }
                });
            }
        });
    }

    /**
     * 图片选择时，数量达到上限的弹框
     *
     * @param context
     * @param count
     */
    public static void showMultiSelectLimitDialog(Context context, int count) {
        showConfirmDialog(context, String.format("您最多只能选择%d张照片", count), "我知道了", null);
    }

    /**
     * 列表选择器
     *
     * @param context
     * @param bases
     * @param title
     * @param callback
     */
    public static void showListDialog(Activity context, List<String> bases, CharSequence title, ListDialog.ListDialogCallback callback) {
        ListDialog.newInstance(context, title, bases, callback).show();
    }

    public static void showListDialog(Activity context, String[] bases, CharSequence title, ListDialog.ListDialogCallback callback) {
        showListDialog(context, Arrays.asList(bases), title, callback);
    }

    /**
     * EditText设置默认值
     *
     * @param edt
     * @param str
     */
    public static void setEditTextDefault(EditText edt, CharSequence str) {
        if (edt != null && !TextUtils.isEmpty(str)) {
            edt.setText(str);
            edt.setSelection(str.length());
        }
    }
}
