package com.vivi.asyncmvc.ui.home.homepage;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vivi.asyncmvc.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;

/**
 * 首页刷新的Header
 */
public class HomeRefreshHeader extends LinearLayout implements RefreshHeader {

    private ImageView mIvProgress;//刷新动画视图
    private AnimationDrawable animationDrawable;//刷新动画
    private ImageView mIvEnding;//结束动画视图
    private Animation animationEnding;

    public HomeRefreshHeader(Context context) {
        this(context, null);
    }

    public HomeRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_home_header, this);
        mIvProgress = findViewById(R.id.iv_home_header_progress);
        mIvEnding = findViewById(R.id.iv_home_header_ending);
        setMinimumHeight(DensityUtil.dp2px(60));
        animationDrawable = (AnimationDrawable) mIvProgress.getBackground();
        animationEnding = AnimationUtils.loadAnimation(getContext(), R.anim.anim_home_header_ending);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        mIvEnding.startAnimation(animationEnding);
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                break;
            case Refreshing:
                if (animationDrawable != null) {
                    animationDrawable.start();//开始动画
                }
                break;
            case ReleaseToRefresh:
                break;
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }
}
