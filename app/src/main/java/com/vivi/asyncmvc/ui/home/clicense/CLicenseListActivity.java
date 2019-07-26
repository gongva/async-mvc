package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CarLicense;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.event.CLicenseRefreshEvent;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.utils.UI;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 机动车列表，含：本人机动车列表、非本人机动车列表、从分类进来的某一辆机动车的“详情”，噗…。
 *
 * @author gongwei
 * @date 2019/2/14
 */
public class CLicenseListActivity extends BaseActivity {

    //static
    private static final int TYPE_OWNER = 1;
    private static final int TYPE_NOT_OWNER = 2;
    private static final int TYPE_SINGLE = 3;

    @BindView(R.id.tv_c_list_title)
    TextView tvCListTitle;
    @BindView(R.id.lv_c_list)
    ListView lvCList;

    //tools
    private ListViewDataAdapter<CarLicense> mAdapter;
    //data
    private int mType;//此页面的用途，@see TYPE_...

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_clicense_list;
    }

    public static void startForOwner(Context context) {
        Intent intent = new Intent(context, CLicenseListActivity.class);
        intent.putExtra("type", TYPE_OWNER);
        context.startActivity(intent);
    }

    public static void startForNotOwner(Context context) {
        Intent intent = new Intent(context, CLicenseListActivity.class);
        intent.putExtra("type", TYPE_NOT_OWNER);
        context.startActivity(intent);
    }

    public static void startForSingle(Context context, String id) {
        Intent intent = new Intent(context, CLicenseListActivity.class);
        intent.putExtra("type", TYPE_SINGLE);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        mType = intent.getIntExtra("type", TYPE_OWNER);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("机动车详情");
        initView();
        initData();
    }

    private void initView() {
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<CarLicense>() {
            @Override
            public ViewHolderBase<CarLicense> createViewHolder() {
                return new CLicenseViewHolder();
            }
        });
        lvCList.setAdapter(mAdapter);
    }

    /**
     * init data from DB.
     */
    private void initData() {
        mAdapter.getDataList().clear();
        switch (mType) {
            case TYPE_OWNER:
                List<CarLicense> tempOwner = CarLicense.query(true);
                if (tempOwner == null || tempOwner.size() == 0) {
                    finish();
                } else {
                    tvCListTitle.setVisibility(View.VISIBLE);
                    tvCListTitle.setText("我的机动车");
                    mAdapter.getDataList().addAll(tempOwner);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case TYPE_NOT_OWNER:
                List<CarLicense> tempNotOwner = CarLicense.query(false);
                if (tempNotOwner == null || tempNotOwner.size() == 0) {
                    finish();
                } else {
                    tvCListTitle.setVisibility(View.VISIBLE);
                    tvCListTitle.setText("非本人机动车");
                    mAdapter.getDataList().addAll(tempNotOwner);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case TYPE_SINGLE:
                tvCListTitle.setVisibility(View.GONE);
                String id = getIntent().getStringExtra("id");
                CarLicense temp = CarLicense.queryById(id);
                if (temp == null) {
                    finish();
                } else {
                    mAdapter.getDataList().add(temp);
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public class CLicenseViewHolder extends ViewHolderBase<CarLicense> {

        @BindView(R.id.tv_item_c_plate_number)
        TextView tvItemCPlateNumber;
        @BindView(R.id.tv_item_c_name)
        TextView tvItemCName;
        @BindView(R.id.tv_item_c_expired_date)
        TextView tvItemCExpiredDate;
        @BindView(R.id.tv_item_c_phone)
        TextView tvItemCPhone;
        @BindView(R.id.tv_item_c_illegal)
        TextView tvItemCIllegal;
        @BindView(R.id.tv_item_c_un_handle_count)
        TextView tvItemCUnHandleCount;
        @BindView(R.id.llt_item_c_bind_record)
        LinearLayout lltItemCBindRecord;
        @BindView(R.id.llt_item_c_unbind)
        LinearLayout lltItemCUnbind;
        //data
        private CarLicense carLicense;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_c_list, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, CarLicense license) {
            this.carLicense = license;
            tvItemCPlateNumber.setText(license.plateNum);
            tvItemCName.setText(license.ownerName);
            tvItemCExpiredDate.setText(license.getExpiredDate());
            tvItemCPhone.setText(license.phone);
            if (license.unhandleCount > 0) {
                tvItemCIllegal.setVisibility(View.VISIBLE);
                tvItemCUnHandleCount.setText(String.format("%s次", license.unhandleCount));
            } else {
                tvItemCIllegal.setVisibility(View.GONE);
                tvItemCUnHandleCount.setText("正常");
            }
            if (license.isOwner) {
                lltItemCBindRecord.setVisibility(View.VISIBLE);
                lltItemCUnbind.setVisibility(View.GONE);
            } else {
                lltItemCBindRecord.setVisibility(View.GONE);
                lltItemCUnbind.setVisibility(View.VISIBLE);
            }
        }

        @OnClick({R.id.llt_item_c_status, R.id.llt_item_c_bind_record, R.id.llt_item_c_unbind})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.llt_item_c_status:
                    //todo
                    UI.showToast("todo");
                    break;
                case R.id.llt_item_c_bind_record:
                    BindRecordOtherActivity.start(CLicenseListActivity.this, carLicense.id);
                    break;
                case R.id.llt_item_c_unbind:
                    UI.showConfirmDialog(CLicenseListActivity.this, "温馨提示", "解除备案车辆后，首页机动车行驶证也一并解除。确定解除？",
                            "我再想想", null,
                            "确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    unbindLicense();
                                }
                            });
                    break;
            }
        }

        /**
         * 解绑
         */
        private void unbindLicense() {
            showLoadingDialog();
            Api.carLicenseUnbind(carLicense.id, new JsonResultCallback<JsonResultVoid>() {
                @Override
                public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                    dismissLoadingDialog();
                    CarLicense.delete(carLicense.id);
                    BusProvider.post(new CLicenseRefreshEvent());
                    initData();
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