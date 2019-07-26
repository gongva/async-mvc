package com.vivi.asyncmvc.ui.comm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.studio.os.PreferencesUtils;
import android.support.annotation.Nullable;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.managers.biometric.BiometricPromptManager;
import com.vivi.asyncmvc.ui.login.LoginActivity;
import com.vivi.asyncmvc.ui.me.SettingActivity;

public class WelcomeActivity extends BaseActivity {

    //tools
    private BiometricPromptManager mBiometricManager;
    private int timeDelay = 1000;//页面延迟启动时间（ms）

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        if (initActivitySource()) {
            return;
        }
        super.hideActionBar();
        startAppDelayed();
    }

    /**
     * 防重复出现欢迎页
     *
     * @return
     */
    public boolean initActivitySource() {
        // 判断该Activity是不是任务空间的源Activity，“非” 表示是系统重新实例化
        if (!this.isTaskRoot()) {
            //如果放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return true;//finish()之后该活动会继续执行 加return避免可能的exception
            }
        }
        return false;
    }

    /**
     * 延时启动App
     */
    private void startAppDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LoginManager.getInstance().isLogin()) {
                    if (mBiometricManager == null) {
                        mBiometricManager = BiometricPromptManager.from(WelcomeActivity.this);
                    }
                    boolean fingerSettingOn = PreferencesUtils.getBoolean(SettingActivity.getPreferKeyFinger(), false);
                    if (fingerSettingOn && mBiometricManager.isBiometricPromptEnable()) {
                        //已登录、已开指纹：指纹验证
                        FingerprintActivity.start(WelcomeActivity.this);
                    } else {
                        //已登录，未开指纹：进入首页
                        MainPageActivity.start(WelcomeActivity.this);
                    }
                } else {
                    //未登录：进入登录
                    LoginActivity.start(WelcomeActivity.this);
                }
                finish();
            }
        }, timeDelay);
    }
}
