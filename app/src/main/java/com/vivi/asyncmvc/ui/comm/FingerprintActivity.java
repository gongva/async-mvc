package com.vivi.asyncmvc.ui.comm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.UserInfo;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.managers.biometric.BiometricPromptManager;
import com.vivi.asyncmvc.comm.view.roundimg.RoundedImageView;
import com.vivi.asyncmvc.library.plugs.glide.AImage;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.login.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 指纹识别页面
 *
 * @author gongwei
 * @date 2019/1/31
 */
public class FingerprintActivity extends BaseActivity {

    @BindView(R.id.iv_fingerprint_head)
    RoundedImageView ivFingerprintHead;
    @BindView(R.id.tv_fingerprint_phone)
    TextView tvFingerprintPhone;
    //tools
    private BiometricPromptManager mBiometricManager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_fingerprint;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, FingerprintActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        super.hideActionBar();
        //data
        UserInfo userInfo = LoginManager.getInstance().getUserInfo();
        AImage.load(userInfo.avatar, ivFingerprintHead);
        tvFingerprintPhone.setText(userInfo.phone);
        //biometric
        mBiometricManager = BiometricPromptManager.from(this);
        startFingerprint();
    }

    private void startFingerprint() {
        if (mBiometricManager == null) {
            mBiometricManager = BiometricPromptManager.from(this);
        }
        if (mBiometricManager.isBiometricPromptEnable()) {
            mBiometricManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                @Override
                public void onSucceeded() {
                    MainPageActivity.start(FingerprintActivity.this);
                    finish();
                }

                @Override
                public void onFailed() {
                }

                @Override
                public void outOfTimes() {
                    UI.showToast("失败次数过多，请使用账号登录");
                    mBiometricManager.dismiss();
                    startToLogin();
                }
            });
        } else {
            UI.showToast("您的设备未设置指纹");
            startToLogin();
        }
    }

    private void startToLogin() {
        LoginManager.logout();
        LoginManager.getInstance().exitAccount();
        exitApp();
        LoginActivity.start(this);
    }

    @OnClick({R.id.iv_fingerprint_icon, R.id.tv_fingerprint_click, R.id.tv_fingerprint_2login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fingerprint_icon:
            case R.id.tv_fingerprint_click:
                startFingerprint();
                break;
            case R.id.tv_fingerprint_2login:
                startToLogin();
                break;
        }
    }
}
