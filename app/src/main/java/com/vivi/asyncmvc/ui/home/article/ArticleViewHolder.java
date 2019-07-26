package com.vivi.asyncmvc.ui.home.article;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.comm.view.listview.ViewHolderBase;
import com.vivi.asyncmvc.ui.comm.web.WebActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 文章列表适配器
 *
 * @author gongwei
 * @date 2019/2/18
 */
public class ArticleViewHolder extends ViewHolderBase<Article> {

    @BindView(R.id.llt_article_list_root)
    LinearLayout lltRoot;
    @BindView(R.id.tv_article_list_title)
    TextView tvTitle;
    //tools
    private Context mContext;

    public ArticleViewHolder(Context context) {
        this.mContext = context;
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_article, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void showData(int position, final Article article) {
        tvTitle.setText(article.title);
        lltRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.startArticle(mContext, article.id);
            }
        });
    }
}
