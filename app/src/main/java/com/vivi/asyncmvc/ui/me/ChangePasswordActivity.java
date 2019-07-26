package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.edit.EyeEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.edt_change_password_old)
    EyeEditText edtChangePasswordOld;
    @BindView(R.id.edt_change_password_new)
    EyeEditText edtChangePasswordNew;
    @BindView(R.id.edt_change_password_confirm)
    EditText edtChangePasswordConfirm;
    @BindView(R.id.btn_change_password)
    Button btnChangePassword;
    //data
    private String oldPassword, newPassword, confirmPassword;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_change_password;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChangePasswordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("修改密码");

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_change_password_old:
                        oldPassword = text;
                        break;
                    case R.id.edt_change_password_new:
                        newPassword = text;
                        break;
                    case R.id.edt_change_password_confirm:
                        confirmPassword = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtChangePasswordOld.addTextChangedListener(new EditTextWatcher(edtChangePasswordOld, editCallBack));
        edtChangePasswordNew.addTextChangedListener(new EditTextWatcher(edtChangePasswordNew, editCallBack));
        edtChangePasswordConfirm.addTextChangedListener(new EditTextWatcher(edtChangePasswordConfirm, editCallBack));
        btnChangePassword.setEnabled(false);
        OS.showSoftKeyboard(edtChangePasswordOld, true);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(oldPassword)
                && !TextUtils.isEmpty(newPassword) && newPassword.length() >= AppConfig.PASSWORD_MIN_LENGTH
                && !TextUtils.isEmpty(confirmPassword) && confirmPassword.length() >= AppConfig.PASSWORD_MIN_LENGTH) {
            btnChangePassword.setEnabled(true);
        } else {
            btnChangePassword.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_change_password)
    public void onViewClicked() {
        if (!newPassword.equals(confirmPassword)) {
            UI.showToast("两次输入密码不一致");
        } else if (ValidateUtil.validatePasswordRule(newPassword)) {
            showLoadingDialog();
            Api.changePassword(oldPassword, newPassword, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    UI.showToast("密码修改成功");
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
