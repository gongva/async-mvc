package com.vivi.asyncmvc.comm.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Desc: 顶层dialog
 * Author: gongwei
 * CreateTime: 2018/12/19
 */

public class TouchableDialog extends Dialog {
    private ViewGroup mRootView;
    private Context context;
    private TouchableDialogCallBack callBack;

    public TouchableDialog(@NonNull Context context) {
        super(context);
        initRootView(context);
    }

    public TouchableDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        initRootView(context);
    }

    public void setCallBack(TouchableDialogCallBack callBack) {
        this.callBack = callBack;
    }

    protected TouchableDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initRootView(context);
    }

    private void initRootView(Context context) {
        this.context = context;
        if (this.getWindow().getDecorView() instanceof ViewGroup)
            mRootView = (ViewGroup) this.getWindow().getDecorView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (callBack != null && isShowing() && shouldCloseOnTouch(getContext(), event)) {
            callBack.onDismiss();
        }
        return super.onTouchEvent(event);
    }

    public boolean shouldCloseOnTouch(Context context, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && isOutOfBounds(context, event) && getWindow().peekDecorView() != null) {
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop)
                || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    public interface TouchableDialogCallBack {
        void onDismiss();
    }
}
