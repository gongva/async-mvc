package com.vivi.asyncmvc.ui.home.article;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.api.entity.article.ArticleCategory;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.view.listview.ListViewDataAdapter;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderCreator;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultRow;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.comm.web.WebActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 安全行驶-一级文章分类
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class ArticleCategoryActivity extends BaseActivity {

    @BindView(R.id.lv_list_common)
    ListView lvListCommon;
    @BindView(R.id.rfl_list_common)
    SmartRefreshLayout rflListCommon;
    //tools
    private ListViewDataAdapter<ArticleCategory> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_list_commom;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, ArticleCategoryActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("安全行驶");
        initView();
    }

    private void initView() {
        //refresh
        rflListCommon.setEnableLoadMore(false);
        rflListCommon.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getCategory();
            }
        });
        //list view
        mAdapter = new ListViewDataAdapter<>(new ViewHolderCreator<ArticleCategory>() {
            @Override
            public ViewHolderBase<ArticleCategory> createViewHolder() {
                return new ArticleCategoryHolder();
            }
        });
        lvListCommon.setAdapter(mAdapter);

        showInitLoading();
        getCategory();
    }

    private void getCategory() {
        Api.getArticleCategory(new JsonResultCallback<JsonResultRow<ArticleCategory>>() {
            @Override
            public void onSuccess(int statusCode, JsonResultRow<ArticleCategory> response, int tag) {
                dismissLoadingDialog();
                mAdapter.getDataList().clear();
                mAdapter.getDataList().addAll(response.getData());
                mAdapter.notifyDataSetChanged();
                rflListCommon.finishRefresh();
                if (mAdapter.getDataList().isEmpty()) {
                    showErrorPage("暂无安全行驶文章哦");
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

    public class ArticleCategoryHolder extends ViewHolderBase<ArticleCategory> {

        @BindView(R.id.tv_article_category_title)
        TextView tvTitle;
        @BindView(R.id.llt_article_category_content)
        LinearLayout lltContent;
        //data
        private ArticleCategory mCategory;

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            View view = inflater.inflate(R.layout.item_article_category, parent, false);
            ButterKnife.bind(this, view);
            return view;
        }

        @Override
        public void showData(int position, ArticleCategory category) {
            mCategory = category;
            tvTitle.setText(category.name);
            int dp10 = ViewUtils.dip2px(ArticleCategoryActivity.this, 10);
            int dp15 = ViewUtils.dip2px(ArticleCategoryActivity.this, 15);
            LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (category.items != null && category.items.size() > 0) {
                lltContent.setVisibility(View.VISIBLE);
                lltContent.removeAllViews();
                for (final Article article : category.items) {
                    TextView tvArticle = new TextView(ArticleCategoryActivity.this);
                    tvArticle.setText(article.title);
                    tvArticle.setBackgroundResource(R.drawable.selector_list_item_click);
                    tvArticle.setClickable(true);
                    tvArticle.setPadding(dp15, dp10, dp15, dp10);
                    tvArticle.setMaxLines(1);
                    tvArticle.setEllipsize(TextUtils.TruncateAt.END);
                    tvArticle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            WebActivity.startArticle(ArticleCategoryActivity.this, article.id);
                        }
                    });
                    lltContent.addView(tvArticle, itemParam);
                }
            } else {
                lltContent.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.llt_article_category_title)
        public void onClick() {
            ArticleListActivity.start(ArticleCategoryActivity.this, mCategory.type, mCategory.name);
        }
    }
}
