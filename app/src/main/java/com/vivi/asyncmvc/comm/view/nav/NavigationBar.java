package com.vivi.asyncmvc.comm.view.nav;

import android.app.Activity;
import android.content.Context;
import android.studio.view.ViewUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.listener.OnDoubleClickListener;
import com.vivi.asyncmvc.library.utils.OS;


/**
 * 导航栏
 *
 * @author gongwei 2018/12/19
 */
public class NavigationBar extends FrameLayout {

    Context mContext;
    FrameLayout actionBar;
    Toolbar toolbar;
    LinearLayout titleLayout;
    TextView titleView;
    TextView back;
    LinearLayout menuLayout;
    TextView mCustomMenu;
    ImageView mCustomMenuIcon;
    OnDoubleClickListener doubleClickListener;
    OnClickListener doubleClick, singleTapListener;

    public NavigationBar(Context context) {
        this(context, null);
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        doubleClickListener = new OnDoubleClickListener(context) {
            @Override
            public void onDoubleClick(View v) {
                if (doubleClick != null) {
                    doubleClick.onClick(v);
                }
            }

            @Override
            public void onClick(View v) {
                if (singleTapListener != null) {
                    singleTapListener.onClick(v);
                }
            }
        };
        View.inflate(context, R.layout.view_base_navigation_bar, this);
        actionBar = findViewById(R.id.actionBar);
        toolbar = findViewById(R.id.toolbar);
        titleLayout = findViewById(R.id.titleLayout);
        titleView = findViewById(R.id.title);
        back = findViewById(R.id.back);
        menuLayout = findViewById(R.id.menuLayout);
        mCustomMenu = findViewById(R.id.customMenu);
        mCustomMenuIcon = findViewById(R.id.customMenuIcon);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) mContext;
                    OS.hideSoftKeyboard(activity);
                    activity.finish();
                }
            }
        });
    }

    public TextView getBack() {
        return back;
    }

    public void setBack(TextView back) {
        this.back = back;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 设置双击事件
     *
     * @param listener
     */
    public void setOnDoubleClickListener(final OnClickListener listener) {
        this.doubleClick = listener;
    }

    /**
     * 设置标题单击事件
     *
     * @param listener
     */
    public void setOnSingleClickListener(OnClickListener listener) {
        this.singleTapListener = listener;
    }

    /**
     * 设置双击滚动到顶部
     *
     * @param scrollView
     */
    public void setScrollTop(final ScrollView scrollView) {
        setOnDoubleClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0, 0);
            }
        });
    }

    /**
     * 设置双击滚动到顶部
     *
     * @param listView
     */
    public void setScrollTop(final AbsListView listView) {
        setOnDoubleClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        doubleClickListener.onTouch(this, ev);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 是否启用导航菜单
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void hide() {
        setEnabled(false);
    }

    public void show() {
        setEnabled(true);
    }

    public void setTitleTextColor(int color) {
        titleView.setTextColor(color);
    }

    public void setText(CharSequence title) {
        titleView.setText(title);
    }

    public View getTitleView() {
        return titleView;
    }

    /**
     * 是否启用左边返回键
     *
     * @param enabled
     */
    public void setLeftEnabled(boolean enabled) {
        back.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void disableButtonClick() {
        back.setClickable(false);
        menuLayout.setClickable(false);
    }

    public void enableButtonClick() {
        back.setClickable(true);
        menuLayout.setClickable(true);
    }

    /**
     * 设置返回按钮事件
     *
     * @param listener
     */
    public void setLeftOnClickListener(OnClickListener listener) {
        setLeftTitle(null, R.drawable.ic_back_black, listener);
    }

    public void setLeftOnClickListener(int iconResId, OnClickListener listener) {
        setLeftTitle(null, iconResId, listener);
    }

    public void setLeftTitle(String title, int iconResId, OnClickListener listener) {
        setEnabled(true);
        back.setText(title == null ? "" : title);
        back.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        back.setOnClickListener(listener);
    }

    /**
     * 隐藏菜单不显示
     */
    public void hideMenu() {
        menuLayout.setVisibility(GONE);
    }

    /**
     * contentView当Menu
     *
     * @param contentView
     * @param listener
     * @return
     */
    public View setMenu(View contentView, OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.rightMargin = ViewUtils.dip2px(mContext, 15);
        menuLayout.addView(contentView, params);
        contentView.setOnClickListener(listener);
        menuLayout.setVisibility(VISIBLE);
        return menuLayout;
    }

    /**
     * 图片+文字 当Menu
     *
     * @param iconResID
     * @param text
     * @param listener
     * @return
     */
    public View setMenu(int iconResID, String text, OnClickListener listener) {
        menuLayout.setVisibility(VISIBLE);
        mCustomMenuIcon.setVisibility(GONE);
        if (iconResID == 0 && TextUtils.isEmpty(text)) {
            mCustomMenu.setVisibility(View.GONE);
        } else {
            mCustomMenu.setVisibility(View.VISIBLE);
            mCustomMenu.setText(text);
            mCustomMenu.setCompoundDrawablesWithIntrinsicBounds(iconResID, 0, 0, 0);
            mCustomMenu.setOnClickListener(listener);
        }
        return mCustomMenu;
    }

    /**
     * 文字Menu
     *
     * @param text
     * @param listener
     * @return
     */
    public TextView setMenu(String text, OnClickListener listener) {
        menuLayout.setVisibility(VISIBLE);
        mCustomMenuIcon.setVisibility(GONE);
        if (TextUtils.isEmpty(text)) {
            mCustomMenu.setVisibility(View.GONE);
        } else {
            mCustomMenu.setVisibility(View.VISIBLE);
            mCustomMenu.setText(text);
            mCustomMenu.setOnClickListener(listener);
        }
        return mCustomMenu;
    }

    /**
     * 图标Menu
     *
     * @param iconResID
     * @param listener
     */
    public void setMenu(int iconResID, OnClickListener listener) {
        menuLayout.setVisibility(VISIBLE);
        mCustomMenu.setVisibility(GONE);
        mCustomMenuIcon.setVisibility(VISIBLE);
        mCustomMenuIcon.setImageResource(iconResID);
        mCustomMenuIcon.setOnClickListener(listener);
    }

    public TextView getCustomMenu() {
        return mCustomMenu;
    }
}
