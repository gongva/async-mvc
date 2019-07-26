package com.vivi.asyncmvc.comm.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.vivi.asyncmvc.R;

import java.util.List;

/**
 * 图片BannerView
 */
public class ImageBannerView extends BannerView {

    private float mRoundCorner = 0; //圆角  0为直角

    public ImageBannerView(Context context) {
        this(context, null);
    }

    public ImageBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.BannerViewAttrs);
        mRoundCorner = t.getDimensionPixelSize(R.styleable.BannerViewAttrs_round_corner, 0);
    }

    @Override
    protected LoopViewPager.BannerPagerAdapter getAdapter() {
        return new ImageBannerAdapter();
    }

    @Override
    protected void initNext() {
        mBannerViewPager.setBackgroundColor(Color.BLACK);
        mBannerViewPager.setClipChildren(false);
    }

    /**
     * 设置Image数据
     *
     * @param images
     * @param callBack
     */
    public void setImage(List<String> images, ImageBannerAdapter.ImageBannerCallBack callBack) {
        ((ImageBannerAdapter)mAdapter).setRoundCorner(mRoundCorner);
        setData(R.drawable.ic_banner_dot_focus, R.drawable.ic_banner_dot_blur, 5, true, images, callBack);
    }
}
