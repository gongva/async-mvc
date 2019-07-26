package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.view.edit.CaptchaEditText;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更换手机号
 *
 * @author gongwei
 * @datea 2019.1.23
 */
public class ChangePhoneActivity extends BaseActivity {

    @BindView(R.id.edt_change_phone_password)
    ClearEditText edtChangePhonePassword;
    @BindView(R.id.edt_change_phone_new)
    ClearEditText edtChangePhoneNew;
    @BindView(R.id.edt_change_phone_captcha)
    CaptchaEditText edtChangePhoneCaptcha;
    @BindView(R.id.btn_change_phone)
    Button btnChangePhone;

    //data
    private String strPassword, strMobile, strCaptcha;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_change_phone;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChangePhoneActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("验证密码");

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_change_phone_password:
                        strPassword = text;
                        break;
                    case R.id.edt_change_phone_new:
                        strMobile = text;
                        break;
                    case R.id.edt_change_phone_captcha:
                        strCaptcha = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtChangePhonePassword.addTextChangedListener(new EditTextWatcher(edtChangePhonePassword, editCallBack));
        edtChangePhoneNew.addTextChangedListener(new EditTextWatcher(edtChangePhoneNew, editCallBack));
        edtChangePhoneCaptcha.addTextChangedListener(new EditTextWatcher(edtChangePhoneCaptcha, editCallBack));
        edtChangePhoneCaptcha.setCallBack(new CaptchaEditText.CaptchaEditCallBack() {
            @Override
            public void getCaptcha() {
                if (TextUtils.isEmpty(strPassword)) {
                    UI.showToast("请输入密码");
                } else if (!ValidateUtil.validateMobile(strMobile)) {
                    UI.showToast("请输入正确的手机号");
                } else {
                    showLoadingDialog();
                    Api.getCaptchaChangePhone(strMobile, strPassword, new JsonResultCallback<JsonResultVoid>() {
                        @Override
                        public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                            dismissLoadingDialog();
                            UI.showToast("验证码已发送，请注意查收");
                            edtChangePhoneCaptcha.startCaptchaTimer();
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
        });
        btnChangePhone.setEnabled(false);
        OS.showSoftKeyboard(edtChangePhonePassword, true);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strMobile) && strMobile.length() == 11 && !TextUtils.isEmpty(strCaptcha) && !TextUtils.isEmpty(strPassword)) {
            btnChangePhone.setEnabled(true);
        } else {
            btnChangePhone.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        if (!ValidateUtil.validateMobile(strMobile)) {
            UI.showToast("请输入正确的手机号");
        } else {
            showLoadingDialog();
            Api.changePhone(strMobile, strCaptcha, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    UI.showToast("修改成功");
                    LoginManager.getInstance().getUserInfo().phone = strMobile;
                    LoginManager.getInstance().setLoginAccount(strMobile);
                    LoginManager.getInstance().save();
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

    @OnClick(R.id.btn_change_phone)
    public void onClick() {
        commit();
    }
}
