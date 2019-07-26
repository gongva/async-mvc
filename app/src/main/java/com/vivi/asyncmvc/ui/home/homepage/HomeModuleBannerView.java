package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.util.AttributeSet;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.banner.BannerView;
import com.vivi.asyncmvc.comm.view.banner.LoopViewPager;

public class HomeModuleBannerView extends BannerView {

    private HomeModuleBannerAdapter.HomeModuleCallBack mCallBack;

    public HomeModuleBannerView(Context context) {
        this(context, null);
    }

    public HomeModuleBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LoopViewPager.BannerPagerAdapter getAdapter() {
        return new HomeModuleBannerAdapter();
    }

    public void setHomeModule(HomeModuleBannerAdapter.HomeModuleCallBack callBack) {
        this.mCallBack = callBack;
        super.setData(R.drawable.shap_home_module_banner_on, R.drawable.shap_home_module_banner_off, 7, false, mCallBack);
        setWidthHeightRatio(mAdapter.getCount() > 1 ? 16f / 5 : 16f / 4);//小于一页时会隐藏Indicator，所以高度小一点
    }

    public void refresh() {
        setHomeModule(mCallBack);
    }
}
