package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.studio.view.ViewUtils;
import android.util.AttributeSet;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.listview.HikRefreshFooter;
import com.noober.background.drawable.DrawableCreator;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

public class HomeRefreshFooter extends HikRefreshFooter {
    public HomeRefreshFooter(Context context) {
        this(context, null);
    }

    public HomeRefreshFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        boolean result = super.setNoMoreData(noMoreData);
        if (noMoreData) {
            mTitleText.setVisibility(GONE);
        }
        return result;
    }
}