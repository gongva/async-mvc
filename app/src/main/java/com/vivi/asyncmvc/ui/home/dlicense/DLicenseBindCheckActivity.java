package com.vivi.asyncmvc.ui.home.dlicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.DriverLicenseBindCheck;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.DLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 驾驶证绑定-身份信息校验Activity
 *
 * @author gongwei
 * @date 2019/2/1
 */
public class DLicenseBindCheckActivity extends BaseActivity {

    @BindView(R.id.edt_driver_license_bind_name)
    ClearEditText edtDriverLicenseBindName;
    @BindView(R.id.edt_driver_license_bind_card)
    ClearEditText edtDriverLicenseBindCard;
    @BindView(R.id.btn_driver_license_bind_next)
    Button btnDriverLicenseBindNext;
    //data
    private String strName, strIdCard;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dlicense_bind_check;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, DLicenseBindCheckActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("输入身份信息");
        BusProvider.bindLifecycle(this);

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_driver_license_bind_name:
                        strName = text;
                        break;
                    case R.id.edt_driver_license_bind_card:
                        strIdCard = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtDriverLicenseBindName.addTextChangedListener(new EditTextWatcher(edtDriverLicenseBindName, editCallBack));
        edtDriverLicenseBindCard.addTextChangedListener(new EditTextWatcher(edtDriverLicenseBindCard, editCallBack));
        OS.showSoftKeyboard(edtDriverLicenseBindName, true);
        btnDriverLicenseBindNext.setEnabled(false);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strName) && !TextUtils.isEmpty(strIdCard)) {
            btnDriverLicenseBindNext.setEnabled(true);
        } else {
            btnDriverLicenseBindNext.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        if (ValidateUtil.validateIdCard(strIdCard)) {
            showLoadingDialog();
            Api.driverLicenseBindCheck(strName, strIdCard, new JsonResultCallback<JsonResult<DriverLicenseBindCheck>>() {
                @Override
                public void onSuccess(int statusCode, JsonResult<DriverLicenseBindCheck> response, int tag) {
                    dismissLoadingDialog();
                    if (response.getData() != null && response.getData().status) {
                        //next step
                        DLicenseBindActivity.start(DLicenseBindCheckActivity.this, strName, strIdCard);
                    } else {
                        //business error
                        DLicenseBindFailActivity.start(DLicenseBindCheckActivity.this, String.format("很遗憾：%s", response.getData().message));
                    }
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
            UI.showToast("请填写正确的身份证号码");
        }
    }

    @OnClick(R.id.btn_driver_license_bind_next)
    public void onClick() {
        commit();
    }

    @Subscribe
    public void onEvent(DLicenseRefreshEvent event) {
        finish();
    }
}
