package com.vivi.asyncmvc.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CaptchaCheck;
import com.vivi.asyncmvc.api.entity.GetCaptcha;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.view.edit.CaptchaEditText;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 忘记密码第一步：验证手机号
 *
 * @author gongwei
 * @date 2019.1.21
 */
public class ForgetPasswordVerifyPhoneActivity extends BaseActivity {

    @BindView(R.id.edt_forget_password_phone)
    ClearEditText edtForgetPasswordPhone;
    @BindView(R.id.edt_forget_password_captcha)
    CaptchaEditText edtForgetPasswordCaptcha;
    @BindView(R.id.btn_forget_password_next)
    Button btnForgetPasswordNext;

    //data
    private String strMobile, strCaptcha;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_forget_password_verify_phone;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ForgetPasswordVerifyPhoneActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("忘记密码");

        //init views
        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_forget_password_phone:
                        strMobile = text;
                        break;
                    case R.id.edt_forget_password_captcha:
                        strCaptcha = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtForgetPasswordPhone.addTextChangedListener(new EditTextWatcher(edtForgetPasswordPhone, editCallBack));
        edtForgetPasswordCaptcha.addTextChangedListener(new EditTextWatcher(edtForgetPasswordCaptcha, editCallBack));
        edtForgetPasswordCaptcha.setCallBack(new CaptchaEditText.CaptchaEditCallBack() {
            @Override
            public void getCaptcha() {
                if (ValidateUtil.validateMobile(strMobile)) {
                    showLoadingDialog();
                    Api.getCaptcha("forgetPassword", strMobile, new JsonResultCallback<JsonResult<GetCaptcha>>() {
                        @Override
                        public void onSuccess(int statusCode, JsonResult<GetCaptcha> response, int tag) {
                            dismissLoadingDialog();
                            if (response.getData().registered) {
                                UI.showToast("验证码已发送，请注意查收");
                                edtForgetPasswordCaptcha.startCaptchaTimer();
                            } else {
                                UI.showToast("当前手机号还未注册");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                            super.onFailure(statusCode, responseString, throwable, tag);
                            dismissLoadingDialog();
                            UI.showToast(responseString);
                        }
                    });
                } else {
                    UI.showToast("请输入正确的手机号");
                }
            }
        });
        btnForgetPasswordNext.setEnabled(false);

        //set phone default
        String loginAccount = LoginManager.getInstance().getLoginAccount();
        UI.setEditTextDefault(edtForgetPasswordPhone, loginAccount);
        OS.showSoftKeyboard(edtForgetPasswordPhone, true);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strMobile) && strMobile.length() == 11 && !TextUtils.isEmpty(strCaptcha)) {
            btnForgetPasswordNext.setEnabled(true);
        } else {
            btnForgetPasswordNext.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        if (!ValidateUtil.validateMobile(strMobile)) {
            UI.showToast("请输入正确的手机号");
        } else {
            showLoadingDialog();
            Api.verifyCaptcha("forgetPassword", strMobile, strCaptcha, new JsonResultCallback<JsonResult<CaptchaCheck>>() {
                @Override
                public void onSuccess(int statusCode, JsonResult<CaptchaCheck> response, int tag) {
                    dismissLoadingDialog();
                    CaptchaCheck captchaCheck = response.getData();
                    captchaCheck.phone = strMobile;
                    if (response.getData().hasDriverLicense) {
                        ForgetPasswordVerifyLicenseActivity.start(ForgetPasswordVerifyPhoneActivity.this, captchaCheck);
                        finish();
                    } else {
                        SetPasswordActivity.startForForgetPassword(ForgetPasswordVerifyPhoneActivity.this, captchaCheck);
                        finish();
                    }
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

    @OnClick(R.id.btn_forget_password_next)
    public void onClick() {
        commit();
    }
}
