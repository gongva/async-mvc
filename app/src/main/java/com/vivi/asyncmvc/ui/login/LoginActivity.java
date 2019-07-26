package com.vivi.asyncmvc.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.ResetPasswordEvent;
import com.vivi.asyncmvc.comm.managers.upgrade.UpgradeManager;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.UI;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录Activity
 *
 * @author gongwei
 * @date 20191.17
 */
public class LoginActivity extends BaseActivity {

    //views
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.iv_login_arrow)
    ImageView ivLoginArrow;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    @BindView(R.id.iv_register_arrow)
    ImageView ivRegisterArrow;
    @BindView(R.id.llt_login_fragment)
    LinearLayout lltLoginFragment;

    //fragments
    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    //tools
    private String strLogoff;//挤下线时的提示文字

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_login;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void startForLogoff(Context context, String logoffMsg) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("logoffMsg", logoffMsg);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        strLogoff = intent.getStringExtra("logoffMsg");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setStatusBarTransparent(true);
        super.setStatusIconDark(false);
        super.hideActionBar();
        BusProvider.bindLifecycle(this);

        loginFragment = new LoginFragment();
        switchFragment(loginFragment);

        //被挤下线
        if (!TextUtils.isEmpty(strLogoff)) {
            UI.showConfirmDialog(this, "温馨提示", strLogoff, "确定", null);
        }
        //检查更新
        UpgradeManager.getInstance().checkAppUpgradeStart(null);
    }

    private void switchToLogin() {
        tvLogin.setTextColor(Color.parseColor("#ffffffff"));
        tvRegister.setTextColor(Color.parseColor("#99ffffff"));
        ivLoginArrow.setVisibility(View.VISIBLE);
        ivRegisterArrow.setVisibility(View.GONE);
        loginFragment.setLoginAccount();
        switchFragment(loginFragment);
    }

    private void switchToRegister() {
        tvLogin.setTextColor(Color.parseColor("#99ffffff"));
        tvRegister.setTextColor(Color.parseColor("#ffffffff"));
        ivLoginArrow.setVisibility(View.GONE);
        ivRegisterArrow.setVisibility(View.VISIBLE);
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        switchFragment(registerFragment);
    }

    public void switchFragment(Fragment f) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.llt_login_fragment, f);
        transaction.commitAllowingStateLoss();
    }

    @OnClick({R.id.tv_login, R.id.tv_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                switchToLogin();
                break;
            case R.id.tv_register:
                switchToRegister();
                break;
        }
    }

    @Subscribe
    public void onEvent(ResetPasswordEvent event) {
        switchToLogin();
    }
}