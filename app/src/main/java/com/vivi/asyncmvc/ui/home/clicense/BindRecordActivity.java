package com.vivi.asyncmvc.ui.home.clicense;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.CarLicenseBindRecord;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.utils.UI;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 机动车本人的备案记录
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class BindRecordActivity extends BaseActivity {

    @BindView(R.id.lv_list_common)
    ListView lvListCommon;
    @BindView(R.id.rfl_list_common)
    SmartRefreshLayout rflListCommon;
    //tools
    private ListViewDataAdapter<CarLicenseBindRecord> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_list_commom;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, BindRecordActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("备案记录");
        initView();
    }

    private void initView() {
        //refresh
        rflListCommon.setEnableLoadMore(false);
        rflListCommon.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getRecord();
            }
        });
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<CarLicenseBindRecord>() {
            @Override
            public ViewHolderBase<CarLicenseBindRecord> createViewHolder() {
                return new BindRecordHolder();
            }
        });
        lvListCommon.setAdapter(mAdapter);

        showInitLoading();
        getRecord();
    }

    private void getRecord() {
        Api.getCarLicenseBindRecord(new JsonResultCallback<JsonResultRow<CarLicenseBindRecord>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<CarLicenseBindRecord> response, int tag) {
                dismissLoadingDialog();
                mAdapter.getDataList().clear();
                mAdapter.getDataList().addAll(response.getData());
                mAdapter.notifyDataSetChanged();
                rflListCommon.finishRefresh();
                if (mAdapter.getDataList().isEmpty()) {
                    showErrorPage("您还没有备案记录哦");
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                rflListCommon.finishRefresh();
                if (mAdapter.isEmpty()) {
                    showErrorPageForHttp(statusCode, responseString);
                } else {
                    UI.showToast(responseString);
                }
            }
        });
    }

    public class BindRecordHolder extends ViewHolderBase<CarLicenseBindRecord> {

        @BindView(R.id.tv_bind_record_plat_number)
        TextView tvPlatNumber;
        @BindView(R.id.tv_bind_record_name)
        TextView tvName;
        @BindView(R.id.tv_bind_record_status)
        TextView tvStatus;
        @BindView(R.id.tv_bind_record_date)
        TextView tvDate;
        @BindView(R.id.v_bind_record_line)
        View line;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_bind_record, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, CarLicenseBindRecord record) {
            tvPlatNumber.setText(record.plateNum);
            tvName.setText(record.name);
            tvStatus.setText(record.getBindStatusText());
            tvDate.setText(record.getRecordDate());
            line.setVisibility(position == mAdapter.getCount() - 1 ? View.GONE : View.VISIBLE);//最后一条记录不要横线
        }
    }
}
