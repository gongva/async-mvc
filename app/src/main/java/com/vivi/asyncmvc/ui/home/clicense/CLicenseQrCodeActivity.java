package com.vivi.asyncmvc.ui.home.clicense;

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
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.AppLifecycle;
import com.vivi.asyncmvc.library.utils.CommonTools;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 行驶证二维码页面
 *
 * @author gongwei
 * @date 2019/2/13
 */
public class CLicenseQrCodeActivity extends BaseActivity {
    @BindView(R.id.tv_c_code_name)
    TextView tvName;
    @BindView(R.id.tv_c_code_car_type)
    TextView tvCarType;
    @BindView(R.id.tv_c_code_issue_date)
    TextView tvIssueDate;
    @BindView(R.id.tv_c_code_plate_num)
    TextView tvPlateNum;
    @BindView(R.id.tv_c_code_operation_type)
    TextView tvOperationType;
    @BindView(R.id.iv_c_code_license)
    ImageView ivLicense;

    //data
    private CarLicense mLicense;
    //tools
    private Timer mTimerAutoRefresh;
    private int mSecondAutoRefresh;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_qr_code;
    }

    public static void start(Context context, CarLicense carLicense) {
        Intent intent = new Intent(context, CLicenseQrCodeActivity.class);
        intent.putExtra("carLicense", carLicense);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mLicense = (CarLicense) intent.getSerializableExtra("carLicense");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        hideActionBar();
        //data
        tvName.setText(mLicense.ownerName);
        tvCarType.setText(mLicense.vehicleTypeName);
        tvIssueDate.setText(mLicense.getIssueDate());
        tvPlateNum.setText(mLicense.plateNum);
        tvOperationType.setText(mLicense.operationTypeName);
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
     */
    private void dealQrCode() {
        cancelTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //todo 二维码数据协议尚未知
                //create qr code
                final Bitmap qrCodeBitmap = CommonTools.createCode(0, ViewUtils.dip2px(CLicenseQrCodeActivity.this, 240), mLicense.qrInfo + System.currentTimeMillis(), null);
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

    @OnClick({R.id.tv_back, R.id.tv_c_code_refresh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_c_code_refresh:
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
