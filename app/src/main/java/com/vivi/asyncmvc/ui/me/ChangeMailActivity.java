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
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更换邮箱
 *
 * @author gongwei
 * @datea 2019.1.22
 */
public class ChangeMailActivity extends BaseActivity {

    @BindView(R.id.edt_change_mail)
    ClearEditText edtChangeMail;
    @BindView(R.id.btn_change_mail)
    Button btnChangeMail;

    private String mEmail;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_change_mail;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ChangeMailActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("更换邮箱");

        mEmail = LoginManager.getInstance().getUserInfo().email;
        UI.setEditTextDefault(edtChangeMail, mEmail);
        edtChangeMail.addTextChangedListener(new EditTextWatcher(edtChangeMail, new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                mEmail = text;
                refreshBtnCommit();
            }
        }));
        refreshBtnCommit();
        OS.showSoftKeyboard(edtChangeMail, true);
    }

    private void refreshBtnCommit() {
        btnChangeMail.setEnabled(!TextUtils.isEmpty(mEmail));
    }

    @OnClick(R.id.btn_change_mail)
    public void onClick() {
        if (ValidateUtil.validateMail(mEmail)) {
            showLoadingDialog();
            Api.changeEmail(mEmail, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    UI.showToast("修改成功");
                    LoginManager.getInstance().getUserInfo().email = mEmail;
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
        } else {
            UI.showToast("请输入正确的邮箱");
        }
    }
}
