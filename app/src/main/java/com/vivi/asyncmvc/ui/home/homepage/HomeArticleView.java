package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.studio.view.ViewUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.comm.listener.OnClickExListener;
import com.noober.background.drawable.DrawableCreator;

import java.util.List;

/**
 * 首页“资讯”
 *
 * @author gongwei
 * @Date 2019.1.11
 */
public class HomeArticleView extends LinearLayout implements Runnable {

    //tools
    private static final int DEFAULT_ARTICLE_COUNT = 2;//默认显示的文章数，默认2
    private static final int POST_DELAYED_TIME = 5 * 1000;//滚动的时间间隔，默认5秒
    private int duration = POST_DELAYED_TIME;
    private float mLineHeight = 0f;//每个Item的高度，通过计算之后赋值生效

    //views
    private LinearLayout lltContent;

    //data
    private List<Article> mArticles;
    private HomeArticleCallBack mCallBack;

    public HomeArticleView(Context context) {
        this(context, null);
    }

    public HomeArticleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeArticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_article, this);
        lltContent = findViewById(R.id.llt_home_article_content);
    }

    /**
     * 设置数据集
     *
     * @param articles
     */
    public void setData(List<Article> articles, HomeArticleCallBack callBack) {
        mArticles = articles;
        mCallBack = callBack;

        lltContent.removeAllViews();
        if (mArticles != null && mArticles.size() > 0) {
            int size = mArticles.size() > DEFAULT_ARTICLE_COUNT ? DEFAULT_ARTICLE_COUNT : mArticles.size();
            for (int i = 0; i < size; i++) {
                final LinearLayout line = createArticleLine(mArticles.get(i));
                if (i == 0) {
                    ViewTreeObserver vto = line.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mLineHeight = line.getHeight();
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            LayoutParams contentParam = (LayoutParams) lltContent.getLayoutParams();
                            contentParam.height = (int) (mLineHeight * DEFAULT_ARTICLE_COUNT);
                            lltContent.setLayoutParams(contentParam);
                        }
                    });
                }
                lltContent.addView(line);
            }
        }

        startAutoCycle();
    }

    /**
     * 滚动切换下一篇文章
     */
    public void nextItem() {
        Article nextArticle = mArticles.get(DEFAULT_ARTICLE_COUNT);//下一篇资讯文章
        final LinearLayout nextArticleView = createArticleLine(nextArticle);
        lltContent.addView(nextArticleView);

        //数据往上面平移100%
        Animation animationUp = AnimationUtils.loadAnimation(getContext(), R.anim.translate_out_from_top);
        for (int i = 0; i < DEFAULT_ARTICLE_COUNT; i++) {
            if (i == 0) {
                animationUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //移除页面view
                        View index0 = lltContent.getChildAt(0);
                        index0.setOnClickListener(null);
                        lltContent.removeView(index0);
                        //第一条数据移至最后
                        mArticles.add(mArticles.get(0));
                        mArticles.remove(0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
            lltContent.getChildAt(i).startAnimation(animationUp);
        }

        //用属性动画，始终都不对
        /*for (int i = 0; i < DEFAULT_ARTICLE_COUNT + 1; i++) {
            ObjectAnimator animator = new ObjectAnimator().ofFloat(lltContent.getChildAt(i), "translationY", 0, -mLineHeight);
            if (i == 0) {
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float endValue = (float) valueAnimator.getAnimatedValue();
                        if (endValue == -mLineHeight) {
                            LogCat.i("Animator end and remove first item.");
                            //移除第一个view
                            View index0 = lltContent.getChildAt(0);
                            index0.setOnClickListener(null);
                            //lltContent.removeViewAt(0);
                            lltContent.removeViewInLayout(index0);
                            //第一条数据移至最后
                            mArticles.add(mArticles.get(0));
                            mArticles.remove(0);
                        }
                    }
                });
            }
            animator.setDuration(1000);
            animator.start();
        }*/
    }

    /**
     * 一片文章即是一排
     *
     * @return LinearLayout
     */
    private LinearLayout createArticleLine(final Article article) {
        Context context = getContext();
        LinearLayout line = new LinearLayout(context);
        line.setGravity(Gravity.CENTER_VERTICAL);
        line.setOrientation(HORIZONTAL);
        line.setPadding(0, ViewUtils.dip2px(context, 2), 0, ViewUtils.dip2px(context, 2));

        ImageView dot = new ImageView(getContext());
        Drawable dotDrawable = new DrawableCreator.Builder().setShape(DrawableCreator.Shape.Oval)
                .setSolidColor(Color.parseColor("#ff4c88ff"))
                .setSizeWidth(ViewUtils.dip2px(getContext(), 5))
                .setSizeHeight(ViewUtils.dip2px(getContext(), 5))
                .build();
        dot.setBackground(dotDrawable);

        TextView tvTitle = new TextView(context);
        tvTitle.setEllipsize(TextUtils.TruncateAt.END);
        tvTitle.setMaxLines(1);
        tvTitle.setText(article.title);
        LayoutParams titleParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleParam.leftMargin = ViewUtils.dip2px(context, 7);

        line.addView(dot);
        line.addView(tvTitle, titleParam);

        if (mCallBack != null) {
            line.setOnClickListener(new OnClickExListener() {
                @Override
                public void onClickEx(View v) {
                    mCallBack.clickArticle(article);
                }
            });
        }
        return line;
    }

    public void startAutoCycle() {
        //文章数量小于默认显示数量，就不滚动了
        if (mArticles == null || mArticles.size() <= DEFAULT_ARTICLE_COUNT) {
            return;
        }

        removeCallbacks(this);
        postDelayed(this, duration);
    }

    public void stopAutoCycle() {
        removeCallbacks(this);
    }

    @Override
    public void run() {
        nextItem();
        startAutoCycle();
    }

    public interface HomeArticleCallBack {
        void clickArticle(Article article);
    }
}
