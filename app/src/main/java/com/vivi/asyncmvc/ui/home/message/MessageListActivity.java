package com.vivi.asyncmvc.ui.home.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Message;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultTsList;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.UI;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 消息中心
 *
 * @author gongwei
 * @date 2019/2/15
 */
public class MessageListActivity extends BaseActivity implements MessageAdapter.MessageOnItemLongClickListener {

    @BindView(R.id.lv_list_common)
    ListView lvListCommon;
    @BindView(R.id.rfl_list_common)
    SmartRefreshLayout rflListCommon;

    //tools
    private MessageAdapter mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_list_commom;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, MessageListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("消息中心");
        initView();
    }

    private void initView() {
        //refresh
        rflListCommon.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getMessage(0);
            }
        });
        rflListCommon.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getMessage(mAdapter.getLastMessageGmtCreate());
            }
        });
        //list view
        mAdapter = new MessageAdapter(this);
        mAdapter.setOnItemLongClickListener(this);
        lvListCommon.setAdapter(mAdapter);

        List<Message> messagesDb = Message.query(AppConfig.HTTP_DEFAULT_SIZE);
        if (messagesDb != null && messagesDb.size() > 0) {
            mAdapter.addAll(messagesDb);
            mAdapter.notifyDataSetChanged();
        } else {
            showInitLoading();
        }
        getMessage(0);
    }

    private void getMessage(final long timestamp) {
        Api.getMessageList(false, timestamp, new JsonResultCallback<JsonResultTsList<Message>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultTsList<Message> response, int tag) {
                dismissLoadingDialog();
                List<Message> listResult = response.getData();
                if (listResult != null) {
                    if (timestamp == 0) {
                        mAdapter.clear();
                    }
                    mAdapter.addAll(listResult);
                    mAdapter.notifyDataSetChanged();
                    Message.save(listResult);
                }
                rflListCommon.finishRefresh();
                rflListCommon.finishLoadMore();
                rflListCommon.setEnableLoadMore(response.hasNextPage());
                if (mAdapter.isEmpty()) {
                    showErrorPage("暂无消息");
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

    @Override
    public void onItemLongClick(final Message message) {
        UI.showConfirmDialog(this, "您要删除这条消息吗？", "取消", null, "删除", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                Api.messageDelete(message.id, new JsonResultCallback<JsonResultVoid>() {
                    @Override
                    public void onSuccess(int statusCode, JsonResultVoid response, int tag) {
                        dismissLoadingDialog();
                        mAdapter.getData().remove(message);
                        mAdapter.notifyDataSetChanged();
                        Message.delete(message.id);
                    }

                    @Override
                    public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                        super.onFailure(statusCode, responseString, throwable, tag);
                        dismissLoadingDialog();
                        UI.showToast(responseString);
                    }
                });
            }
        });
    }
}
