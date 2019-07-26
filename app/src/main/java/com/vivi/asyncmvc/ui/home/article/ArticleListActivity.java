package com.vivi.asyncmvc.ui.home.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultList;
import com.vivi.asyncmvc.library.utils.UI;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * 安全行驶-二级文章列表
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class ArticleListActivity extends BaseActivity {

    @BindView(R.id.lv_list_common)
    ListView lvListCommon;
    @BindView(R.id.rfl_list_common)
    SmartRefreshLayout rflListCommon;
    //data
    private String mType, mName;
    //tools
    private ListViewDataAdapter<Article> mAdapter;
    private int mPage = 1;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_list_commom;
    }

    /**
     * @param context
     * @param type    @see ArticleCategory.type
     * @param name    @see ArticleCategory.name
     */
    public static void start(Context context, String type, String name) {
        Intent intent = new Intent(context, ArticleListActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    @Override
    protected void initIntentData(Intent intent) {
        super.initIntentData(intent);
        mType = intent.getStringExtra("type");
        mName = intent.getStringExtra("name");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(mName);
        initView();
    }

    private void initView() {
        //refresh
        rflListCommon.setPadding(0, ViewUtils.dip2px(this, 10), 0, 0);
        rflListCommon.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getArticles(1);
            }
        });
        rflListCommon.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getArticles(mPage + 1);
            }
        });
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<Article>() {
            @Override
            public ViewHolderBase<Article> createViewHolder() {
                return new ArticleViewHolder(ArticleListActivity.this);
            }
        });
        lvListCommon.setAdapter(mAdapter);

        showInitLoading();
        getArticles(1);
    }

    private void getArticles(final int page) {
        Api.getArticleList(mType, page, new JsonResultCallback<JsonResultList<Article>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultList<Article> response, int tag) {
                dismissLoadingDialog();
                List<Article> listResult = response.getData();
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
                    showErrorPage("暂无安全行驶文章哦");
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
}
