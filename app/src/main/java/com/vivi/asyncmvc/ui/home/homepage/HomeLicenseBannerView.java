package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.util.AttributeSet;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.comm.view.banner.BannerView;
import com.vivi.asyncmvc.comm.view.banner.LoopViewPager;

import java.util.List;

/**
 * 首页的驾驶证、行驶证BannerView
 */
public class HomeLicenseBannerView extends BannerView {

    public HomeLicenseBannerView(Context context) {
        this(context, null);
    }

    public HomeLicenseBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LoopViewPager.BannerPagerAdapter getAdapter() {
        return new HomeLicenseBannerAdapter();
    }

    /**
     * 设置首页卡片数据
     *
     * @param driverLicense  驾驶证
     * @param carLicenseList 行驶证列表
     * @param callBack
     */
    public void setLicense(DriverLicense driverLicense, List<CarLicense> carLicenseList, HomeLicenseBannerAdapter.HomeLicenseBannerCallBack callBack) {
        super.setData(R.drawable.ic_banner_dot_focus, R.drawable.ic_banner_dot_blur, 40, false, driverLicense, carLicenseList, callBack);
    }
}
