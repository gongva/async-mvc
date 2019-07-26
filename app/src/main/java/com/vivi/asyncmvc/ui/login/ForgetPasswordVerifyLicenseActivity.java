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
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 忘记密码第二步：验证电子驾照
 *
 * @author gongwei
 * @date 2019.1.21
 */
public class ForgetPasswordVerifyLicenseActivity extends BaseActivity {


    @BindView(R.id.edt_forget_password_name)
    ClearEditText edtForgetPasswordName;
    @BindView(R.id.edt_forget_password_card)
    ClearEditText edtForgetPasswordCard;
    @BindView(R.id.edt_forget_password_license)
    ClearEditText edtForgetPasswordLicense;
    @BindView(R.id.btn_forget_password_next)
    Button btnForgetPasswordNext;
    //data
    private String strName, strIdCard, strLicense;
    private CaptchaCheck mCaptchaCheck;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_forget_password_verify_license;
    }

    public static void start(Context context, CaptchaCheck captchaCheck) {
        Intent intent = new Intent(context, ForgetPasswordVerifyLicenseActivity.class);
        intent.putExtra("captchaCheck", captchaCheck);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mCaptchaCheck = (CaptchaCheck) intent.getSerializableExtra("captchaCheck");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("忘记密码");

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_forget_password_name:
                        strName = text;
                        break;
                    case R.id.edt_forget_password_card:
                        strIdCard = text;
                        break;
                    case R.id.edt_forget_password_license:
                        strLicense = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtForgetPasswordName.addTextChangedListener(new EditTextWatcher(edtForgetPasswordName, editCallBack));
        edtForgetPasswordCard.addTextChangedListener(new EditTextWatcher(edtForgetPasswordCard, editCallBack));
        edtForgetPasswordLicense.addTextChangedListener(new EditTextWatcher(edtForgetPasswordLicense, editCallBack));
        btnForgetPasswordNext.setEnabled(false);
        OS.showSoftKeyboard(edtForgetPasswordName, true);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strName) && !TextUtils.isEmpty(strIdCard) && !TextUtils.isEmpty(strLicense)) {
            btnForgetPasswordNext.setEnabled(true);
        } else {
            btnForgetPasswordNext.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        if (ValidateUtil.validateIdCard(strIdCard)) {
            if (mCaptchaCheck != null) {
                showLoadingDialog();
                Api.verifyForgetPasswordLicense(mCaptchaCheck.phone, strName, strIdCard, strLicense, new JsonResultCallback<JsonResultVoid>() {
                    @Override
                    public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                        dismissLoadingDialog();
                        SetPasswordActivity.startForForgetPassword(ForgetPasswordVerifyLicenseActivity.this, mCaptchaCheck);
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
        } else {
            UI.showToast("请填写正确的身份证号码");
        }
    }

    @OnClick(R.id.btn_forget_password_next)
    public void onClick() {
        commit();
    }
}
