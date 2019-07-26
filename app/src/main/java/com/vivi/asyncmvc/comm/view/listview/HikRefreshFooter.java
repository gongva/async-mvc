package com.vivi.asyncmvc.comm.view.listview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.studio.view.ViewUtils;
import android.util.AttributeSet;

import com.vivi.asyncmvc.R;
import com.noober.background.drawable.DrawableCreator;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

/**
 * 列表页面公共Footer
 */
public class HikRefreshFooter extends ClassicsFooter {
    public HikRefreshFooter(Context context) {
        this(context, null);
    }

    public HikRefreshFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HikRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        boolean result = super.setNoMoreData(noMoreData);
        if (noMoreData) {
            mTitleText.setText("我是有底线的");
            Drawable drawableLine = new DrawableCreator.Builder().setShape(DrawableCreator.Shape.Line)
                    .setStrokeColor(getContext().getResources().getColor(R.color.line))
                    .setStrokeWidth(1)
                    .setSizeWidth(50)
                    .setSizeHeight(10)
                    .build();
            mTitleText.setCompoundDrawables(drawableLine, null, drawableLine, null);
            mTitleText.setCompoundDrawablePadding(ViewUtils.dip2px(getContext(), 10));
        }
        return result;
    }
}