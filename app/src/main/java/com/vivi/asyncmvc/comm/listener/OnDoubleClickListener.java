package com.vivi.asyncmvc.comm.listener;

import android.content.Context;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * 双击事件
 *
 * @author  2018/12/19.
 */
public abstract class OnDoubleClickListener implements View.OnTouchListener {

    private Handler handler = new Handler();
    private GestureDetector gestureDetector;
    private View view;
    private long lastClickTime;
    private boolean doubleClickEnabled = true;

    public OnDoubleClickListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                if (System.currentTimeMillis() - lastClickTime <= ViewConfiguration.getDoubleTapTimeout()) {
                    handler.removeCallbacks(performClick);
                }
                lastClickTime = System.currentTimeMillis();
                return super.onDown(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handler.removeCallbacks(performClick);
                onDoubleClick(view);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (doubleClickEnabled) {
                    long timeout = System.currentTimeMillis() - lastClickTime;
                    if (timeout <= 150) { //100 太快了 ，有些屏幕调校得跟不上
                        handler.postDelayed(performClick, ViewConfiguration.getDoubleTapTimeout() - timeout);
                    }
                } else {
                    onClick(view);
                }
                return true;
            }
        });
    }

    private Runnable performClick = new Runnable() {
        @Override
        public void run() {
            onClick(view);
        }
    };

    public void setDoubleClickEnabled(boolean enabled) {
        this.doubleClickEnabled = enabled;
    }

    @Override
    public final boolean onTouch(View v, MotionEvent event) {
        this.view = v;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            lastClickTime = System.currentTimeMillis();
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 双击事件
     *
     * @param v
     */
    public abstract void onDoubleClick(View v);

    /**
     * 单击事件
     *
     * @param v
     */
    public void onClick(View v) {

    }

}
