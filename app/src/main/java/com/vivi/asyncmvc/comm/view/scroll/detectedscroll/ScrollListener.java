package com.vivi.asyncmvc.comm.view.scroll.detectedscroll;

/**
 * Author: gongwei 2018/12/24
 */

public interface ScrollListener {
    void up(int deltaY, int top);

    void down(int deltaY, int top);
}
