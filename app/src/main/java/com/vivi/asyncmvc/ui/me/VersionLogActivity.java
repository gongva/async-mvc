package com.vivi.asyncmvc.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.VersionLog;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultList;
import com.vivi.asyncmvc.library.utils.CommonTools;
import com.vivi.asyncmvc.library.utils.DataFormat;
import com.vivi.asyncmvc.library.utils.UI;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * App版本历史
 *
 * @author gongwei
 * @date 2019.1.25
 */
public class VersionLogActivity extends BaseActivity {

    @BindView(R.id.rfl_list_common)
    SmartRefreshLayout rflListCommon;
    @BindView(R.id.lv_list_common)
    ListView lvListCommon;
    //tools
    private ListViewDataAdapter<VersionLog> mAdapter;
    private int mPage = 1;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_list_commom;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, VersionLogActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("版本介绍");
        initView();
    }

    private void initView() {
        //refresh
        rflListCommon.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getVersionLog(1);
            }
        });
        rflListCommon.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getVersionLog(mPage + 1);
            }
        });
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<VersionLog>() {
            @Override
            public ViewHolderBase<VersionLog> createViewHolder() {
                return new VersionLogViewHolder();
            }
        });
        lvListCommon.setAdapter(mAdapter);

        showInitLoading();
        getVersionLog(1);
    }

    private void getVersionLog(final int page) {
        Api.appVersionLog(page, new JsonResultCallback<JsonResultList<VersionLog>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultList<VersionLog> response, int tag) {
                dismissLoadingDialog();
                List<VersionLog> listResult = response.getData();
                if (listResult != null) {
                    if (page == 1) {
                        mPage = 1;
                        mAdapter.getDataList().clear();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mPage++;
                    }
                    mAdapter.getDataList().addAll(listResult);
                    mAdapter.notifyDataSetChanged();
                }
                rflListCommon.finishRefresh();
                if (response.hasNextPage()) {
                    rflListCommon.finishLoadMore();
                } else {
                    rflListCommon.finishLoadMoreWithNoMoreData();
                }
                if (mAdapter.getDataList().isEmpty()) {
                    showErrorPage("暂无历史版本");
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                dismissLoadingDialog();
                rflListCommon.finishRefresh();
                rflListCommon.finishLoadMore();
                if (mAdapter.isEmpty()) {
                    showErrorPageForHttp(statusCode, responseString);
                } else {
                    UI.showToast(responseString);
                }
            }
        });
    }

    public class VersionLogViewHolder extends ViewHolderBase<VersionLog> {

        @BindView(R.id.tv_item_version_log_name)
        TextView tvItemVersionLogName;
        @BindView(R.id.tv_item_version_log_time)
        TextView tvItemVersionLogTime;
        @BindView(R.id.tv_item_version_log)
        TextView tvItemVersionLog;
        @BindView(R.id.tv_item_version_log_more)
        TextView tvItemVersionLogMore;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_version_log, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, VersionLog versionLog) {
            tvItemVersionLogName.setText(String.format("V%s", versionLog.version));
            tvItemVersionLogTime.setText(DataFormat.formatDate(versionLog.date));
            String changelog = versionLog.changelog.replace("$^$", "\n");
            tvItemVersionLog.setText(changelog);
            //需求：描述超过2排则显示更多
            CommonTools.isOverFlowed(tvItemVersionLog, 2, new CommonTools.TextOverFlowedCallBack() {
                @Override
                public void overFlowed(boolean isOverFlowed) {
                    tvItemVersionLogMore.setVisibility(isOverFlowed ? View.VISIBLE : View.GONE);
                }
            });
        }

        @OnClick(R.id.tv_item_version_log_more)
        public void onClick() {
            if (tvItemVersionLogMore.getVisibility() == View.VISIBLE) {
                if (tvItemVersionLog.getMaxLines() == 2) {
                    tvItemVersionLog.setMaxLines(Integer.MAX_VALUE);
                    tvItemVersionLogMore.setText("收起");
                } else {
                    tvItemVersionLog.setMaxLines(2);
                    tvItemVersionLogMore.setText("展开");
                }
            }
        }
    }
}
