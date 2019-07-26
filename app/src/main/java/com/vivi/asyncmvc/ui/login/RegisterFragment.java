package com.vivi.asyncmvc.ui.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CaptchaCheck;
import com.vivi.asyncmvc.api.entity.GetCaptcha;
import com.vivi.asyncmvc.api.entity.UrlEntity;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.edit.CaptchaEditText;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.ui.comm.web.WebActivity;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册第一步：验证手机号
 *
 * @author gongwei
 * @date 2019.1.19
 */
public class RegisterFragment extends BaseFragment {

    //views
    @BindView(R.id.edt_register_phone)
    ClearEditText edtRegisterPhone;
    @BindView(R.id.edt_register_captcha)
    CaptchaEditText edtRegisterCaptcha;
    @BindView(R.id.btn_register_next)
    Button btnRegisterNext;
    @BindView(R.id.tv_register_protocol)
    TextView tvRegisterProtocol;

    //data
    private String strMobile, strCaptcha;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.hideActionBar();

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_register_phone:
                        strMobile = text;
                        break;
                    case R.id.edt_register_captcha:
                        strCaptcha = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtRegisterPhone.addTextChangedListener(new EditTextWatcher(edtRegisterPhone, editCallBack));
        edtRegisterCaptcha.addTextChangedListener(new EditTextWatcher(edtRegisterCaptcha, editCallBack));
        edtRegisterCaptcha.setCallBack(new CaptchaEditText.CaptchaEditCallBack() {
            @Override
            public void getCaptcha() {
                if (ValidateUtil.validateMobile(strMobile)) {
                    showLoadingDialog();
                    Api.getCaptcha("register", strMobile, new JsonResultCallback<JsonResult<GetCaptcha>>() {
                        @Override
                        public void onSuccess(int statusCode, JsonResult<GetCaptcha> response, int tag) {
                            dismissLoadingDialog();
                            if (response.getData().registered) {
                                UI.showToast("当前手机号已注册");
                            } else {
                                UI.showToast("验证码已发送，请注意查收");
                                edtRegisterCaptcha.startCaptchaTimer();
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
        btnRegisterNext.setEnabled(false);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strMobile) && strMobile.length() == 11 && !TextUtils.isEmpty(strCaptcha)) {
            btnRegisterNext.setEnabled(true);
        } else {
            btnRegisterNext.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(getActivity());
        if (!ValidateUtil.validateMobile(strMobile)) {
            UI.showToast("请输入正确的手机号");
        } else {
            showLoadingDialog();
            Api.verifyCaptcha("register", strMobile, strCaptcha, new JsonResultCallback<JsonResult<CaptchaCheck>>() {
                @Override
                public void onSuccess(int statusCode, JsonResult<CaptchaCheck> response, int tag) {
                    dismissLoadingDialog();
                    CaptchaCheck captchaCheck = response.getData();
                    captchaCheck.phone = strMobile;
                    SetPasswordActivity.startForRegister(getActivity(), captchaCheck);
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

    @OnClick({R.id.btn_register_next, R.id.tv_register_protocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register_next:
                commit();
                break;
            case R.id.tv_register_protocol:
                showLoadingDialog();
                Api.getRegisterProtocol(new JsonResultCallback<JsonResult<UrlEntity>>() {
                    @Override
                    public void onSuccess(int statusCode, JsonResult<UrlEntity> response, int tag) {
                        dismissLoadingDialog();
                        WebActivity.start(getActivity(), response.getData().url);
                    }

                    @Override
                    public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                        super.onFailure(statusCode, responseString, throwable, tag);
                        dismissLoadingDialog();
                    }
                });
                break;
        }
    }
}