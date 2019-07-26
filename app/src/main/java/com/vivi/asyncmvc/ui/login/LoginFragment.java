package com.vivi.asyncmvc.ui.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseFragment;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.comm.view.edit.EyeEditText;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;
import com.vivi.asyncmvc.ui.comm.MainPageActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录
 *
 * @author gongwei
 * @date 2019.1.18
 */
public class LoginFragment extends BaseFragment {

    //views
    @BindView(R.id.edt_login_phone)
    ClearEditText edtLoginPhone;
    @BindView(R.id.edt_login_password)
    EyeEditText edtLoginPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;

    //data
    private String strMobile, strPassword;

    @Override
    public int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.hideActionBar();

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_login_phone:
                        strMobile = text;
                        break;
                    case R.id.edt_login_password:
                        strPassword = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtLoginPhone.addTextChangedListener(new EditTextWatcher(edtLoginPhone, editCallBack));
        edtLoginPassword.addTextChangedListener(new EditTextWatcher(edtLoginPassword, editCallBack));

        setLoginAccount();
        refreshBtnCommit();
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strMobile) && strMobile.length() == 11 && !TextUtils.isEmpty(strPassword)) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(getActivity());
        if (ValidateUtil.validateMobile(strMobile)) {
            showLoadingDialog("正在登录");
            LoginManager.login(strMobile, strPassword, new LoginManager.LoginCallback() {
                @Override
                public void onSuccess() {
                    dismissLoadingDialog();
                    MainPageActivity.start(getActivity());
                    getActivity().finish();
                }

                @Override
                public void onError(int errCode, String response) {
                    dismissLoadingDialog();
                    UI.showToast(response);
                }
            });
        } else {
            UI.showToast("请输入正确的手机号");
        }
    }

    /**
     * 将登录账号放置输入框中
     */
    public void setLoginAccount() {
        String loginAccount = LoginManager.getInstance().getLoginAccount();
        UI.setEditTextDefault(edtLoginPhone, loginAccount);
    }

    @OnClick({R.id.btn_login, R.id.tv_forget_password})
    public void onCLick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                commit();
                break;
            case R.id.tv_forget_password:
                ForgetPasswordVerifyPhoneActivity.start(getActivity());
                break;
        }
    }
}
