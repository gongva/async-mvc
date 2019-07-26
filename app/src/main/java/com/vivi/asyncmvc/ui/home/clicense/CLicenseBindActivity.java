package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Dict;
import com.vivi.asyncmvc.api.entity.HomeCard;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.CLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.dialog.CarPlateDialog;
import com.vivi.asyncmvc.comm.view.dialog.ListDialog;
import com.vivi.asyncmvc.comm.view.edit.CaptchaEditText;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 备案机动车-提交
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class CLicenseBindActivity extends BaseActivity {
    @BindView(R.id.tv_clicnese_bind_province)
    TextView tvProvince;
    @BindView(R.id.tv_clicnese_bind_type)
    TextView tvType;
    @BindView(R.id.tv_clicnese_bind_plate_number)
    EditText tvPlateNumber;
    @BindView(R.id.tv_clicnese_bind_engine_number)
    EditText tvEngineNumber;
    @BindView(R.id.tv_clicnese_bind_name)
    EditText tvOwnerName;
    @BindView(R.id.edt_clicnese_bind_captcha)
    CaptchaEditText edtCaptcha;
    @BindView(R.id.btn_clicnese_bind_commit)
    Button btnCommit;
    //data
    private List<Dict> mTypeList = new ArrayList();//号牌类型
    private String strTypeCode, strTypeName, strPlateNumberProvince, strPlateNumber, strEngineNumber, strOwnerName, strCaptcha;
    //tools
    private final String PLATE_NUMBER_PROVINCE_DEFAULT = "贵";

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_bind;
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, CLicenseBindActivity.class);
        context.startActivity(intent);
    }

    /**
     * @param context
     * @param typeCode     车辆类型Code
     * @param typeName     车辆类型：小型汽车
     * @param plateNumber  车牌号
     * @param engineNumber 发动机后6位
     * @param ownerName    车主姓名
     */
    public static void start(Context context, String typeCode, String typeName, String plateNumber, String engineNumber, String ownerName) {
        Intent intent = new Intent(context, CLicenseBindActivity.class);
        intent.putExtra("typeCode", typeCode);
        intent.putExtra("typeName", typeName);
        intent.putExtra("plateNumber", plateNumber);
        intent.putExtra("engineNumber", engineNumber);
        intent.putExtra("ownerName", ownerName);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        super.initIntentData(intent);
        strTypeCode = intent.getStringExtra("typeCode");
        strTypeName = intent.getStringExtra("typeName");
        strPlateNumber = intent.getStringExtra("plateNumber");
        strEngineNumber = intent.getStringExtra("engineNumber");
        strOwnerName = intent.getStringExtra("ownerName");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("备案机动车");
        initViewAndData();
    }

    private void initViewAndData() {
        //init view
        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.tv_clicnese_bind_plate_number:
                        strPlateNumber = text;
                        break;
                    case R.id.tv_clicnese_bind_engine_number:
                        strEngineNumber = text;
                        break;
                    case R.id.tv_clicnese_bind_name:
                        strOwnerName = text;
                        break;
                    case R.id.edt_clicnese_bind_captcha:
                        strCaptcha = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        tvPlateNumber.addTextChangedListener(new EditTextWatcher(tvPlateNumber, editCallBack));
        tvEngineNumber.addTextChangedListener(new EditTextWatcher(tvEngineNumber, editCallBack));
        tvOwnerName.addTextChangedListener(new EditTextWatcher(tvOwnerName, editCallBack));
        edtCaptcha.addTextChangedListener(new EditTextWatcher(edtCaptcha, editCallBack));
        edtCaptcha.setCallBack(new CaptchaEditText.CaptchaEditCallBack() {
            @Override
            public void getCaptcha() {
                if (TextUtils.isEmpty(strTypeCode)) {
                    UI.showToast("请选择号牌种类");
                } else if (TextUtils.isEmpty(strPlateNumber)) {
                    UI.showToast("请输入车牌号码");
                } else if (TextUtils.isEmpty(strEngineNumber)) {
                    UI.showToast("请输入发动机号后6位");
                } else if (TextUtils.isEmpty(strOwnerName)) {
                    UI.showToast("请输入车主姓名");
                } else {
                    showLoadingDialog();
                    Api.getCaptchaCarLicenseBind(strTypeCode, strPlateNumber, strEngineNumber, strOwnerName, new JsonResultCallback<JsonResult<HomeCard>>() {
                        @Override
                        public void onSuccess(int statusCode, JsonResult<HomeCard> response, int tag) {
                            dismissLoadingDialog();
                            UI.showToast("验证码已发送，请注意查收");
                            edtCaptcha.startCaptchaTimer();
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
        btnCommit.setEnabled(false);
        //init data
        tvType.setText(TextUtils.isEmpty(strTypeName) ? "请选择号牌种类" : strTypeName);
        if (!TextUtils.isEmpty(strPlateNumber)) {
            strPlateNumberProvince = strPlateNumber.substring(0, 1);
            UI.setEditTextDefault(tvPlateNumber, strPlateNumber.substring(1));
        } else {
            strPlateNumberProvince = PLATE_NUMBER_PROVINCE_DEFAULT;
        }
        tvProvince.setText(strPlateNumberProvince);
        UI.setEditTextDefault(tvEngineNumber, strEngineNumber);
        UI.setEditTextDefault(tvOwnerName, strOwnerName);
    }

    private void refreshBtnCommit() {
        if (!TextUtils.isEmpty(strTypeCode) && !TextUtils.isEmpty(strPlateNumberProvince) && !TextUtils.isEmpty(strPlateNumber)
                && !TextUtils.isEmpty(strEngineNumber) && !TextUtils.isEmpty(strOwnerName) && !TextUtils.isEmpty(strCaptcha)) {
            btnCommit.setEnabled(true);
        } else {
            btnCommit.setEnabled(false);
        }
    }

    private void commit() {
        OS.hideSoftKeyboard(this);
        showLoadingDialog();
        Api.carLicenseBind(strTypeCode, strPlateNumber, strEngineNumber, strOwnerName, strCaptcha, new JsonResultCallback<JsonResult<HomeCard>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<HomeCard> response, int tag) {
                dismissLoadingDialog();
                BusProvider.post(new CLicenseRefreshEvent());
                CLicenseBindSuccessActivity.start(CLicenseBindActivity.this);
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

    /**
     * 获取号牌种类字典数据
     */
    private void dealTypeList() {
        if (mTypeList.isEmpty()) {
            showLoadingDialog();
            Api.getDictList("number_plate_code", new JsonResultCallback<JsonResultRow<Dict>>() {
                @Override
                public void onSuccess(int statusCode, JsonResultRow<Dict> response, int tag) {
                    dismissLoadingDialog();
                    mTypeList = response.getData();
                    showTypeListSelect();
                }

                @Override
                public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                    super.onFailure(statusCode, responseString, throwable, tag);
                    dismissLoadingDialog();
                    UI.showToast(responseString);
                }
            });
        } else {
            showTypeListSelect();
        }
    }

    /**
     * 号牌种类选择器
     */
    private void showTypeListSelect() {
        if (mTypeList != null && !mTypeList.isEmpty()) {
            UI.showListDialog(CLicenseBindActivity.this, Dict.getNameList(mTypeList), "号牌种类", new ListDialog.ListDialogCallback() {
                @Override
                public void choose(int which) {
                    Dict temp = mTypeList.get(which);
                    strTypeCode = temp.code;
                    strTypeName = temp.name;
                    tvType.setText(temp.name);
                    refreshBtnCommit();
                }
            });
        } else {
            UI.showToast("抱歉，未获取到号牌种类");
        }
    }

    @OnClick({R.id.tv_clicnese_bind_province, R.id.tv_clicnese_bind_type, R.id.btn_clicnese_bind_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clicnese_bind_province:
                CarPlateDialog.newInstance(this, new CarPlateDialog.CarPlateDialogCallback() {
                    @Override
                    public void choose(String province) {
                        strPlateNumberProvince = province;
                        tvProvince.setText(province);
                    }
                }).show();
                break;
            case R.id.tv_clicnese_bind_type:
                dealTypeList();
                break;
            case R.id.btn_clicnese_bind_commit:
                commit();
                break;
        }
    }
}