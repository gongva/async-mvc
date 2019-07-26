package com.vivi.asyncmvc.comm.view.banner;

import android.app.Activity;
import android.content.Context;
import android.studio.view.ViewUtils;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.AppLifecycle;


/**
 * BannerView
 * 其他类型Banner请继承并重写：getAdapter，刷新数据请调用setData()
 *
 * @author gongwei 2019.1.10
 */

public abstract class BannerView extends FrameLayout {

    //views
    protected LoopViewPager mBannerViewPager;
    protected LinearLayout lltIndicatorLayout;
    protected LoopViewPager.BannerPagerAdapter mAdapter;
    //res
    private int indicatorOnResId, indicatorOffResId;
    //controllers
    private int customWidth = 0;
    private float ratio = 0;//宽高比
    private Context mContext;
    private BannerPageSelectedCallBack mPageSelectedCallBack;//选中每个pager的回调

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }


    private void init() {
        View.inflate(mContext, R.layout.view_banner, this);
        mBannerViewPager = findViewById(R.id.bannerViewPager);
        lltIndicatorLayout = findViewById(R.id.indicatorLayout);
        lltIndicatorLayout.removeAllViews();
        lltIndicatorLayout.setGravity(Gravity.CENTER);

        mAdapter = getAdapter();
        mBannerViewPager.setAdapter(mAdapter);
        mBannerViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setIndicator(position);
                if (mPageSelectedCallBack != null) {
                    mPageSelectedCallBack.onPageSelected(position);
                }
            }
        });
        initNext();
    }

    /**
     * 留给子类的扩展空间
     */
    protected void initNext() {
    }

    /**
     * 刷新Indicator
     */
    protected void notifyIndicator(int onResID, int offResID, int marginBottomDp) {
        setIndicator(onResID, offResID);
        initIndicatorLayout(marginBottomDp);
        setIndicator(0);
    }

    private void initIndicatorLayout(int marginBottomDp) {
        lltIndicatorLayout.removeAllViews();
        int count = mAdapter.getCount();
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(indicatorOffResId);
                int marginLeft = ViewUtils.dip2px(getContext(), 4);
                int marginTop = ViewUtils.dip2px(getContext(), marginBottomDp);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(marginLeft, marginTop, marginLeft, marginTop);
                lltIndicatorLayout.addView(imageView, lp);
            }
        }
    }

    private void setIndicator(int position) {
        int count = lltIndicatorLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView imageView = (ImageView) lltIndicatorLayout.getChildAt(i);
            imageView.setImageResource(i == position ? indicatorOnResId : indicatorOffResId);
        }
    }

    private void setIndicator(int onResID, int offResID) {
        this.indicatorOnResId = onResID;
        this.indicatorOffResId = offResID;
    }

    /**
     * 设置数据，子类Banner调用
     * 其中objects参数：子类Banner与子类Banner的适配器需统一
     *
     * @param onResID
     * @param offResID
     * @param isAutoCycle
     * @param objects
     */
    protected void setData(int onResID, int offResID, int marginBottomDp, boolean isAutoCycle, Object... objects) {
        mAdapter.clear();
        mAdapter.setData(objects);
        mAdapter.notifyDataSetChanged();
        mBannerViewPager.setCurrentItem(0);
        notifyIndicator(onResID, offResID, marginBottomDp);
        if (isAutoCycle) {
            mBannerViewPager.startAutoCycle();
            AppLifecycle.setLifecycle(mContext, new AppLifecycle.LifecycleCallback() {
                @Override
                public void onActivityResumed(Activity activity) {
                    super.onActivityResumed(activity);
                    mBannerViewPager.startAutoCycle();
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    super.onActivityPaused(activity);
                    mBannerViewPager.stopAutoCycle();
                }
            });
        } else {
            mBannerViewPager.setAutoCycle(false);
        }
    }

    public void setPageSelectedCallBack(BannerPageSelectedCallBack callBack) {
        this.mPageSelectedCallBack = callBack;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = customWidth == 0 ? displayMetrics.widthPixels : customWidth;
        int heightSpecSize = width * 7 / 16;
        if (ratio != 0) {//如果传了比例则只算新的比例
            heightSpecSize = (int) (width / ratio);
        }
        super.onMeasure(customWidth == 0 ? widthMeasureSpec : MeasureSpec.makeMeasureSpec(customWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY));
    }

    /**
     * 设置宽度，不设默认为屏幕宽
     *
     * @param customWidth
     */
    public void setCustomWidth(int customWidth) {
        this.customWidth = customWidth;
    }

    /**
     * 设置宽高比，不设默认为16f/7
     *
     * @param ratio
     */
    public void setWidthHeightRatio(float ratio) {
        this.ratio = ratio;
    }

    /**
     * 获取适配器，子类请重写
     *
     * @return
     */
    protected abstract LoopViewPager.BannerPagerAdapter getAdapter();

    public interface BannerPageSelectedCallBack {
        void onPageSelected(int position);
    }
}
