package com.vivi.asyncmvc.comm.listener;

import android.view.View;

/**
 * Android 防止控件被重复点击
 *
 * @author gongwei 2018/12/19.
 */
public abstract class OnClickExListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime = 0;
    private static int customerClickDelayTime = MIN_CLICK_DELAY_TIME;

    @Override
    public final void onClick(View v) {
        if (isDelayClick()) {
            onClickEx(v);
        }
    }

    public OnClickExListener setCustomerClickDelayTime(int delayTime){
       customerClickDelayTime = delayTime;
       return this;
    }

    /**
     * 迟延点击事件
     *
     * @param v
     */
    public abstract void onClickEx(View v);

    /**
     * 是否可以继续点击
     *
     * @return
     */
    public static boolean isDelayClick() {
        long currentTime = System.currentTimeMillis();
        int delayTime = MIN_CLICK_DELAY_TIME;
        if(customerClickDelayTime != MIN_CLICK_DELAY_TIME){
           delayTime = customerClickDelayTime;
        }
        if (currentTime - lastClickTime > delayTime) {
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }
}
