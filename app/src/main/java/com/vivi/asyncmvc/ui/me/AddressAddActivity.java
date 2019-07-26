package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Address;
import com.vivi.asyncmvc.api.entity.Area;
import com.vivi.asyncmvc.api.entity.IdEntity;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.SaveAddressEvent;
import com.vivi.asyncmvc.comm.listener.EditTextWatcher;
import com.vivi.asyncmvc.comm.view.dialog.AreaPickerDialog;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.OS;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.library.utils.ValidateUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新增/编辑收货地址
 * mAddress的id为空则为新增，否则为编辑
 *
 * @author gongwei
 * @date 2019.1.24
 */
public class AddressAddActivity extends BaseActivity {

    //views
    @BindView(R.id.edt_address_add_name)
    EditText edtAddressAddName;
    @BindView(R.id.edt_address_add_phone)
    EditText edtAddressAddPhone;
    @BindView(R.id.edt_address_add_area)
    EditText edtAddressAddArea;
    @BindView(R.id.edt_address_add_address)
    EditText edtAddressAddAddress;
    @BindView(R.id.iv_address_add_default)
    ImageView ivAddressAddDefault;
    @BindView(R.id.btn_address_add_commit)
    Button btnAddressAddCommit;

    //data
    private Address mAddress;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_address_add;
    }

    /**
     * @param context
     * @param address 新增地址时传null
     */
    public static void start(Context context, Address address) {
        Intent intent = new Intent(context, AddressAddActivity.class);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    public static void start(Context context) {
        start(context, null);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mAddress = (Address) intent.getSerializableExtra("address");
        if (mAddress == null) {
            mAddress = new Address();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(TextUtils.isEmpty(mAddress.id) ? "新增收货人信息" : "编辑收货人信息");
        initView();
        initData();
    }

    private void initView() {
        EditTextWatcher.EditTextWatcherCallBack editCallBack = new EditTextWatcher.EditTextWatcherCallBack() {
            @Override
            public void afterTextChanged(int viewId, String text) {
                switch (viewId) {
                    case R.id.edt_address_add_name:
                        mAddress.recipientName = text;
                        break;
                    case R.id.edt_address_add_phone:
                        mAddress.recipientPhone = text;
                        break;
                    case R.id.edt_address_add_address:
                        mAddress.address = text;
                        break;
                }
                refreshBtnCommit();
            }
        };
        edtAddressAddName.addTextChangedListener(new EditTextWatcher(edtAddressAddName, editCallBack));
        edtAddressAddPhone.addTextChangedListener(new EditTextWatcher(edtAddressAddPhone, editCallBack));
        edtAddressAddAddress.addTextChangedListener(new EditTextWatcher(edtAddressAddAddress, editCallBack));
        edtAddressAddArea.setKeyListener(null);//不可编辑
    }

    private void initData() {
        if (!TextUtils.isEmpty(mAddress.id)) {
            //编辑时才有默认值，且才需要判断提交按钮是否可用
            UI.setEditTextDefault(edtAddressAddName, mAddress.recipientName);
            UI.setEditTextDefault(edtAddressAddPhone, mAddress.recipientPhone);
            UI.setEditTextDefault(edtAddressAddArea, String.format("%s %s %s", mAddress.province, mAddress.city, mAddress.county));
            UI.setEditTextDefault(edtAddressAddAddress, mAddress.address);
            ivAddressAddDefault.setImageResource(mAddress.isDefault ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
            refreshBtnCommit();
        } else {
            //新增时无需默认值，且提交按钮肯定不可用
            btnAddressAddCommit.setEnabled(false);
        }
        OS.showSoftKeyboard(edtAddressAddName, true);
    }

    private void refreshBtnCommit() {
        btnAddressAddCommit.setEnabled(mAddress.isDateCompleted());
    }

    private void commit() {
        if (!ValidateUtil.validateMobile(mAddress.recipientPhone)) {
            UI.showToast("请输入正确的手机号");
        } else if (TextUtils.isEmpty(mAddress.id)) {
            //新增
            showLoadingDialog();
            Api.addressAdd(mAddress, new JsonResultCallback<JsonResult<IdEntity>>() {
                @Override
                public void onSuccess(int statusCode, JsonResult<IdEntity> response, int tag) {
                    dismissLoadingDialog();
                    UI.showToast("新增成功");
                    mAddress.id = response.getData().id;
                    BusProvider.post(new SaveAddressEvent(mAddress));
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
            //编辑
            showLoadingDialog();
            Api.addressUpdate(mAddress, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    UI.showToast("修改成功");
                    BusProvider.post(new SaveAddressEvent(mAddress));
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

    @OnClick({R.id.edt_address_add_area, R.id.iv_address_add_area, R.id.iv_address_add_default, R.id.btn_address_add_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edt_address_add_area:
            case R.id.iv_address_add_area:
                //省市区选择框
                AreaPickerDialog.show(this, mAddress.getProvinceArea(), mAddress.getCityArea(), mAddress.getCountyArea(), new AreaPickerDialog.AreaPickerCallBack() {
                    @Override
                    public void done(Area province, Area city, Area county) {
                        if (province != null) {
                            mAddress.province = province.name;
                            mAddress.provinceCode = province.code;
                        } else {
                            mAddress.province = "";
                            mAddress.provinceCode = "";
                        }
                        if (city != null) {
                            mAddress.city = city.name;
                            mAddress.cityCode = city.code;
                        } else {
                            mAddress.city = "";
                            mAddress.cityCode = "";
                        }
                        if (county != null) {
                            mAddress.county = county.name;
                            mAddress.countyCode = county.code;
                        } else {
                            mAddress.county = "";
                            mAddress.countyCode = "";
                        }
                        edtAddressAddArea.setText(String.format("%s %s %s", mAddress.province, mAddress.city, mAddress.county));
                        refreshBtnCommit();
                    }
                });
                break;
            case R.id.iv_address_add_default:
                mAddress.isDefault = !mAddress.isDefault;
                ivAddressAddDefault.setImageResource(mAddress.isDefault ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
                break;
            case R.id.btn_address_add_commit:
                commit();
                break;
        }
    }
}