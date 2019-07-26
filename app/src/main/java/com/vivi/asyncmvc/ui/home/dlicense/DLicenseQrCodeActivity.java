package com.vivi.asyncmvc.ui.home.dlicense;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.DriverLicense;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.AppLifecycle;
import com.vivi.asyncmvc.comm.SystemConfig;
import com.vivi.asyncmvc.library.plugs.glide.AImage;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.DESUtil;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 驾驶证二维码页面
 *
 * @author gongwei
 * @date 2019/2/12
 */
public class DLicenseQrCodeActivity extends BaseActivity {
    @BindView(R.id.iv_d_code_head)
    ImageView ivHead;
    @BindView(R.id.tv_d_code_name)
    TextView tvName;
    @BindView(R.id.tv_d_code_car_type)
    TextView tvCarType;
    @BindView(R.id.tv_d_code_date)
    TextView tvDate;
    @BindView(R.id.iv_d_code_license)
    ImageView ivLicense;

    //data
    private DriverLicense mLicense;
    //tools
    private Timer mTimerAutoRefresh;
    private int mSecondAutoRefresh;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dlicense_qr_code;
    }

    public static void start(Context context, DriverLicense driverLicense) {
        Intent intent = new Intent(context, DLicenseQrCodeActivity.class);
        intent.putExtra("driverLicense", driverLicense);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mLicense = (DriverLicense) intent.getSerializableExtra("driverLicense");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        hideActionBar();
        //data
        AImage.load(mLicense.head, ivHead);
        tvName.setText(String.format("姓名：%s", mLicense.realName));
        tvCarType.setText(String.format("准驾车型：%s", mLicense.licenseType));
        tvDate.setText(String.format("有效期：%s", mLicense.getExpiredDate()));
        AppLifecycle.setLifecycle(this, new AppLifecycle.LifecycleCallback() {
            @Override
            public void onActivityResumed(Activity activity) {
                super.onActivityResumed(activity);
                dealQrCode();
            }

            @Override
            public void onActivityPaused(Activity activity) {
                super.onActivityPaused(activity);
                cancelTimer();
            }
        });
    }

    /**
     * 生成二维码
     * 第一步：将mLicense.qrInfo进行DES加密
     * 第二步：在末尾追加",时间戳"，时间用服务器时间
     * 第三布：生成二维码并显示
     */
    private void dealQrCode() {
        cancelTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //encode mLicense.qrInfo
                String infoEncode = DESUtil.encrypt(mLicense.qrInfo);

                //add timestamp
                String infoTemp = String.format("%s,%s", infoEncode, SystemConfig.getInstance().getServerTimeLong());

                //create qr code
                final Bitmap qrCodeBitmap = CommonTools.createCode(0, ViewUtils.dip2px(DLicenseQrCodeActivity.this, 240), infoTemp, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivLicense.setImageBitmap(qrCodeBitmap);
                        startTimer();
                    }
                });
            }
        }).start();
    }

    private void startTimer() {
        mSecondAutoRefresh = AppConfig.SECOND_QR_CODE_AUTO_REFRESH;
        mTimerAutoRefresh = new Timer();
        mTimerAutoRefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                mSecondAutoRefresh -= 1;
                if (mSecondAutoRefresh < 0) {
                    dealQrCode();
                }
            }
        }, 0, 1000);
    }

    private void cancelTimer() {
        if (mTimerAutoRefresh != null) {
            mTimerAutoRefresh.cancel();
        }
    }

    @OnClick({R.id.tv_d_code_back, R.id.tv_d_code_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_d_code_back:
                finish();
                break;
            case R.id.tv_d_code_refresh:
                dealQrCode();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimer();
    }
}
