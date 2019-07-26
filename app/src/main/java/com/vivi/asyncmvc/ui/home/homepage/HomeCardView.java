package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.comm.view.banner.BannerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页顶部的卡片
 * @author gongwei
 * @Date 2019.1.10
 */
public class HomeCardView extends FrameLayout {

    @BindView(R.id.v_home_card_bg)
    View vHomeCardBg;
    @BindView(R.id.bv_home_card)
    HomeLicenseBannerView bvHomeCard;

    public HomeCardView(@NonNull Context context) {
        this(context, null);
    }

    public HomeCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_card, this);
        ButterKnife.bind(this);

        bvHomeCard.setWidthHeightRatio(32f/17);//16:8.5
        bvHomeCard.setPageSelectedCallBack(new BannerView.BannerPageSelectedCallBack() {
            @Override
            public void onPageSelected(int position) {
                vHomeCardBg.setBackgroundColor(getContext().getResources().getColor(position == 0 ? R.color.color_327BEE : R.color.color_3E5DC7));
            }
        });
    }

    /**
     * 设置首页卡片数据
     *
     * @param driverLicense  驾驶证
     * @param carLicenseList 行驶证列表
     * @param callBack
     */
    public void setLicense(DriverLicense driverLicense, List<CarLicense> carLicenseList, HomeLicenseBannerAdapter.HomeLicenseBannerCallBack callBack) {
        bvHomeCard.setLicense(driverLicense, carLicenseList, callBack);
    }
}
