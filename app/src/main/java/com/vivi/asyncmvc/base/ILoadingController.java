package com.vivi.asyncmvc.base;

import android.content.DialogInterface;
import android.view.View;

/**
 * Created by gongwei on 2018/12/18.
 */
public interface ILoadingController {

    void showLoadingDialog();

    void showLoadingDialog(CharSequence message);

    void showLoadingDialog(CharSequence message, DialogInterface.OnCancelListener listener);

    void dismissLoadingDialog();

    void showInitLoading();

    void showInitLoading(String text);

    void dismissInitLoading();

    void showErrorPage();

    void showErrorPage(String message);

    void showErrorPage(int icResource, String message);

    void showErrorPage(String message, String action, View.OnClickListener listener);

    void showErrorPage(int icResource, String message, String action, View.OnClickListener listener);

    void dismissErrorPage();
}
