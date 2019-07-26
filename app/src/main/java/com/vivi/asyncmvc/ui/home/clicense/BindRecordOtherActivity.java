package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.api.entity.CarLicenseBindRecord;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.errorpage.ErrorPage;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.UI;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 机动车他人的备案记录
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class BindRecordOtherActivity extends BaseActivity {

    @BindView(R.id.tv_bind_record_other_plate_number)
    TextView tvPlateNumber;
    @BindView(R.id.tv_bind_record_other_name)
    TextView tvName;
    @BindView(R.id.tv_bind_record_other_expired_date)
    TextView tvExpiredDate;
    @BindView(R.id.lv_bind_record_other)
    ListView lvOther;
    @BindView(R.id.ep_bind_record_other)
    ErrorPage epOther;
    //data
    private CarLicense mCarLicense;
    //tools
    private ListViewDataAdapter<CarLicenseBindRecord> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_bind_record_other;
    }

    /**
     * @param context
     * @param id      行驶证id
     */
    public static void start(Context context, String id) {
        Intent intent = new Intent(context, BindRecordOtherActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        String id = intent.getStringExtra("id");
        mCarLicense = CarLicense.queryById(id);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("备案记录");
        initView();
    }

    private void initView() {
        if (mCarLicense != null) {
            //base info
            tvPlateNumber.setText(mCarLicense.plateNum);
            tvName.setText(mCarLicense.ownerName);
            tvExpiredDate.setText(mCarLicense.getExpiredDate());


            //list view
            mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<CarLicenseBindRecord>() {
                @Override
                public ViewHolderBase<CarLicenseBindRecord> createViewHolder() {
                    return new BindRecordOtherHolder();
                }
            });
            lvOther.setAdapter(mAdapter);
            getRecord();
        }
    }

    private void getRecord() {
        showLoadingDialog();
        Api.getCarLicenseBindRecordOther(mCarLicense.plateNum, new JsonResultCallback<JsonResultRow<CarLicenseBindRecord>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<CarLicenseBindRecord> response, int tag) {
                dismissLoadingDialog();
                mAdapter.getDataList().clear();
                mAdapter.getDataList().addAll(response.getData());
                mAdapter.notifyDataSetChanged();
                checkListEmpty(null);
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                checkListEmpty(responseString);
            }
        });
    }

    /**
     * 并检查列表是否为空
     *
     * @param errorMsg 错误消息，接口调用失败时传入
     */
    private void checkListEmpty(String errorMsg) {
        if (mAdapter.isEmpty()) {
            lvOther.setVisibility(View.GONE);
            epOther.showErrorPage(TextUtils.isEmpty(errorMsg) ? "您的爱车未被他人备案哦" : errorMsg);
        } else {
            lvOther.setVisibility(View.VISIBLE);
            epOther.setVisibility(View.GONE);
        }
    }

    /**
     * 从列表中删除某条数据，并检查列表是否为空
     *
     * @param record
     */
    private void removeRecord(CarLicenseBindRecord record) {
        mAdapter.getDataList().remove(record);
        mAdapter.notifyDataSetChanged();
        checkListEmpty(null);
    }

    public class BindRecordOtherHolder extends ViewHolderBase<CarLicenseBindRecord> {

        @BindView(R.id.tv_bind_record_other_name)
        TextView tvName;
        @BindView(R.id.tv_bind_record_other_phone)
        TextView tvPhone;
        @BindView(R.id.tv_bind_record_other_date)
        TextView tvDate;
        //data
        private CarLicenseBindRecord record;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_bind_record_other, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, CarLicenseBindRecord record) {
            this.record = record;
            tvName.setText(record.name);
            tvPhone.setText(record.phone);
            tvDate.setText(record.getRecordDate());
        }

        @OnClick(R.id.tv_bind_record_other_unbind)
        public void onClick() {
            UI.showConfirmDialog(BindRecordOtherActivity.this, "温馨提示", "确定解除此人备案？",
                    "我再想想", null,
                    "确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unbindLicense();
                        }
                    });
        }

        /**
         * 解绑
         */
        private void unbindLicense() {
            showLoadingDialog();
            Api.carLicenseUnbind(record.id, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    removeRecord(record);
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