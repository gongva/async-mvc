package com.vivi.asyncmvc.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CaptchaCheck;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.event.ResetPasswordEvent;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.view.edit.EyeEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册第二步：设置密码
 * 忘记密码第二步：设置密码
 *
 * @author gongwei
 * @date 2019.1.19
 */
public class SetPasswordActivity extends BaseActivity {

    private static final String ACTION_REGISTER = "register";//action:注册
    private static final String ACTION_FORGET_PASSWORD = "forgetPassword";//action:忘记密码-设置密码
    //views
    @BindView(R.id.edt_set_password)
    EyeEditText edtSetPassword;
    @BindView(R.id.edt_set_password_confirm)
    EditText edtSetPasswordConfirm;
    @BindView(R.id.btn_set_password)
    Button btnSetPassword;
    //data
    private String strPassword, strPasswordConfirm;
    private String mActionType; //@see ACTION_REGISTER and ACTION_FORGET_PASSWORD
    private CaptchaCheck mCaptchaCheck;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_set_password;
    }

    /**
     * 注册第二步
     *
     * @param context
     */
    public static void startForRegister(Context context, CaptchaCheck captchaCheck) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra("actionType", ACTION_REGISTER);
        intent.putExtra("captchaCheck", captchaCheck);
        context.startActivity(intent);
    }

    /**
     * 忘记密码第二步
     *
     * @param context
     */
    public static void startForForgetPassword(Context context, CaptchaCheck captchaCheck) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra("actionType", ACTION_FORGET_PASSWORD);
        intent.putExtra("captchaCheck", captchaCheck);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mActionType = intent.getStringExtra("actionType");
        mCaptchaCheck = (CaptchaCheck) intent.getSerializableExtra("captchaCheck");
    }

    private String getPageTitle() {
        switch (mActionType) {
            case ACTION_REGISTER:
                return "设置密码";
            case ACTION_FORGET_PASSWORD:
                return "修改密码";
            default:
                return "设置密码";
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getPageTitle());

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_set_password:
                        strPassword = text.trim();
                        break;
                    case R.id.edt_set_password_confirm:
                        strPasswordConfirm = text.trim();
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtSetPassword.addTextChangedListener(new EditTextWatcher(edtSetPassword, editCallBack));
        edtSetPasswordConfirm.addTextChangedListener(new EditTextWatcher(edtSetPasswordConfirm, editCallBack));
        btnSetPassword.setEnabled(false);
        OS.showSoftKeyboard(edtSetPassword, true);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strPassword) && strPassword.length() >= AppConfig.PASSWORD_MIN_LENGTH
                && !TextUtils.isEmpty(strPasswordConfirm) && strPasswordConfirm.length() >= AppConfig.PASSWORD_MIN_LENGTH) {
            btnSetPassword.setEnabled(true);
        } else {
            btnSetPassword.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_set_password)
    public void onClick() {
        if (mCaptchaCheck == null) {
            UI.showToast("未通过短信验证");
        } else if (!strPassword.equals(strPasswordConfirm)) {
            UI.showToast("两次输入密码不一致");
        } else if (ValidateUtil.validatePasswordRule(strPassword)) {
            showLoadingDialog();
            Api.passwordCommit(mActionType, mCaptchaCheck.phone, strPassword, mCaptchaCheck.token, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    switch (mActionType) {
                        case ACTION_REGISTER:
                            UI.showToast("注册成功，请重新登录");
                            break;
                        case ACTION_FORGET_PASSWORD:
                            UI.showToast("密码修改成功，请重新登录");
                            break;
                    }
                    LoginManager.getInstance().saveAccount(mCaptchaCheck.phone);
                    BusProvider.post(new ResetPasswordEvent());
                    finish();
                }

                @Override
                public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                    super.onFailure(statusCode, responseString, throwable, tag);
                    dismissLoadingDialog();
                    UI.showToast(responseString);
                }
            });
        }
    }
}
