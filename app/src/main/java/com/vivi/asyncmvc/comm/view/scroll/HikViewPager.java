package com.vivi.asyncmvc.comm.view.scroll;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 首页所用ViewPage
 */
public class HikViewPager extends ViewPager {

    public HikViewPager(Context context) {
        super(context, null);
    }

    public HikViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
        //return false; //禁用手势滑动
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
        //return false; //禁用手势滑动
    }

    @Override
    public void setCurrentItem(int item) {
        if (Math.abs(getCurrentItem() - item) > 1) {
            super.setCurrentItem(item, false);
        } else {
            super.setCurrentItem(item, true);
        }
    }
}