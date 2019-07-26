package com.vivi.asyncmvc.ui.home.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.comm.listener.OnClickExListener;
import com.vivi.asyncmvc.comm.view.banner.LoopViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:首页的卡片Banner适配器
 *
 * @Author: gongwei
 * @Date: 2019/1/10
 */
public class HomeLicenseBannerAdapter extends LoopViewPager.BannerPagerAdapter {
    private LayoutInflater mInflater;
    private DriverLicense mDriverLicense;
    private List<CarLicense> mCarLicenseList = new ArrayList();
    private HomeLicenseBannerCallBack mCallBack;

    public void clear() {
        mDriverLicense = null;
        mCarLicenseList.clear();
    }

    /**
     * 设置数据集
     * @param objects
     * objects[0]:DriverLicense
     * objects[1]:List<CarLicense>
     * objects[2]:HomeLicenseBannerCallBack
     */
    @Override
    public void setData(Object... objects) {
        this.mDriverLicense = (DriverLicense) objects[0];
        this.mCarLicenseList = (List<CarLicense>) objects[1];
        this.mCallBack = (HomeLicenseBannerCallBack) objects[2];
        if (this.mCarLicenseList == null) {
            this.mCarLicenseList = new ArrayList();
        }
    }

    @Override
    public int getCount() {
        if (mDriverLicense == null) {
            return 1;// 起码有一个驾驶证绑定入口
        } else {
            int carLicenseSize = mCarLicenseList == null ? 0 : mCarLicenseList.size();
            return carLicenseSize + 2;//+ 驾驶证详情  + 新的行驶证绑定入口
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(container.getContext());
        }
        View viewTemp;

        //init click listener
        OnClickExListener clickExListener = null;
        if (mCallBack != null) {
            clickExListener = new OnClickExListener() {
                @Override
                public void onClickEx(View v) {
                    switch (v.getId()) {
                        //绑定驾驶证
                        case R.id.tv_driver_license_bind:
                            mCallBack.clickDriverLicenseBind();
                            break;
                        //驾驶证分数
                        case R.id.tv_banner_driver_license_score:
                            mCallBack.clickScoreLast();
                            break;
                        //驾驶证二维码
                        case R.id.tv_banner_driver_license_qrcode:
                            mCallBack.clickQRCodeDriverLicense(mDriverLicense);
                            break;
                        //绑定行驶证
                        case R.id.tv_car_license_bind:
                            mCallBack.clickCarLicenseBind();
                            break;
                        //行驶证二维码
                        case R.id.tv_banner_car_license_qrcode:
                            mCallBack.clickQRCodeCarLicense(mCarLicenseList.get(position - 1));//减一的原因是驾驶证占了第一页
                            break;

                    }
                }
            };
        }

        //init views and listeners
        if (mDriverLicense == null) {
            //只有绑定驾驶证
            viewTemp = mInflater.inflate(R.layout.item_banner_driver_license_bind, null);
            viewTemp.findViewById(R.id.tv_driver_license_bind).setOnClickListener(clickExListener);
        } else if (position == 0) {
            //加载驾驶证
            viewTemp = mInflater.inflate(R.layout.item_banner_driver_license, null);
            TextView tvScore = viewTemp.findViewById(R.id.tv_banner_driver_license_score);
            TextView tvDate = viewTemp.findViewById(R.id.tv_banner_driver_license_date);
            TextView tvQrcode = viewTemp.findViewById(R.id.tv_banner_driver_license_qrcode);
            tvScore.setText(String.valueOf(mDriverLicense.residualScore));
            tvDate.setText(mDriverLicense.getClearDate());
            tvScore.setOnClickListener(clickExListener);
            tvQrcode.setOnClickListener(clickExListener);
        } else if (position == getCount() - 1) {
            //最后一页加载绑定行驶证
            viewTemp = mInflater.inflate(R.layout.item_banner_car_license_bind, null);
            viewTemp.findViewById(R.id.tv_car_license_bind).setOnClickListener(clickExListener);
        } else {
            //加载行驶证
            CarLicense dataTemp = mCarLicenseList.get(position - 1);//减一的原因是驾驶证占了第一页
            viewTemp = mInflater.inflate(R.layout.item_banner_car_license, null);
            TextView tvPlateNumber = viewTemp.findViewById(R.id.tv_banner_car_license_plate_number);
            TextView tvLicenseType = viewTemp.findViewById(R.id.tv_banner_car_license_type);
            TextView tvDate = viewTemp.findViewById(R.id.tv_banner_car_license_date);
            TextView tvQrcode = viewTemp.findViewById(R.id.tv_banner_car_license_qrcode);
            tvPlateNumber.setText(dataTemp.plateNum);
            tvLicenseType.setText(String.format("车辆类型：%s", dataTemp.vehicleTypeName));
            tvDate.setText(dataTemp.getIssueDate());
            tvQrcode.setOnClickListener(clickExListener);
        }
        container.addView(viewTemp);
        return viewTemp;
    }

    /**
     * 首页卡片不允许循环滚动
     *
     * @return
     */
    @Override
    public boolean canCyclePlay() {
        return false;
    }

    public interface HomeLicenseBannerCallBack {
        void clickDriverLicenseBind();

        void clickCarLicenseBind();

        void clickScoreLast();

        void clickQRCodeDriverLicense(DriverLicense driverLicense);

        void clickQRCodeCarLicense(CarLicense carLicense);
    }
}
