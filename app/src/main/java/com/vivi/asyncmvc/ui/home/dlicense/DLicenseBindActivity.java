package com.vivi.asyncmvc.ui.home.dlicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.DLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.edit.ClearEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 驾驶证绑定-提交Activity
 *
 * @author gongwei
 * @date 2019/2/1
 */
public class DLicenseBindActivity extends BaseActivity {
    @BindView(R.id.edt_driver_license_bind_number)
    ClearEditText edtDriverLicenseBindNumber;
    @BindView(R.id.btn_driver_license_bind_commit)
    Button btnDriverLicenseBindCommit;
    //data
    private String strName, strIdCard, strNumber;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_dlicense_bind;
    }

    public static void start(Context context, String strName, String strIdCard) {
        Intent intent = new Intent(context, DLicenseBindActivity.class);
        intent.putExtra("strName", strName);
        intent.putExtra("strIdCard", strIdCard);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        strName = intent.getStringExtra("strName");
        strIdCard = intent.getStringExtra("strIdCard");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("填写驾驶证档案编号");

        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_driver_license_bind_number:
                        strNumber = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtDriverLicenseBindNumber.addTextChangedListener(new EditTextWatcher(edtDriverLicenseBindNumber, editCallBack));
        OS.showSoftKeyboard(edtDriverLicenseBindNumber, true);
        btnDriverLicenseBindCommit.setEnabled(false);
    }

    private void refreshBtnCommit() {
        btnDriverLicenseBindCommit.setEnabled(!TextUtils.isEmpty(strNumber));
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        showLoadingDialog();
        Api.driverLicenseBind(strName, strIdCard, strNumber, new JsonResultCallback<JsonResultVoid>() {
            @Override
            public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                dismissLoadingDialog();
                UI.showToast("绑定成功");
                BusProvider.post(new DLicenseRefreshEvent());
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

    @OnClick(R.id.btn_driver_license_bind_commit)
    public void onClick() {
        commit();
    }
}
