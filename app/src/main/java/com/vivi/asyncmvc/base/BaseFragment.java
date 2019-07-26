package com.vivi.asyncmvc.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.errorpage.ErrorPage;
import com.vivi.asyncmvc.comm.view.nav.NavigationBar;

/**
 * Fragment基类
 *
 * @author gongwei
 * @created 2018/12/20.
 */
public abstract class BaseFragment extends Fragment implements ILoadingController {

    private ILoadingController loadingController;
    private ViewGroup mRootView;
    private NavigationBar mNavigationBar;
    private ViewGroup contentLayout;

    private ErrorPage errorPage;

    private ViewGroup loadingPanel;
    private TextView loadingText;
    private View contentView;

    protected Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        loadingController = (ILoadingController) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.activity_hik_base, container, false);
        initViews();
        return mRootView;
    }

    private void initViews() {
        mRootView = mRootView.findViewById(R.id.rootView);
        mNavigationBar = mRootView.findViewById(R.id.navigationBar);
        contentLayout = mRootView.findViewById(R.id.contentLayout);
        loadingPanel = mRootView.findViewById(R.id.loadingPanel);
        loadingText = mRootView.findViewById(R.id.loadingText);
        errorPage = mRootView.findViewById(R.id.error_page_base);
        int contentResID = getContentLayoutId();
        if (contentResID > 0) {
            contentView = View.inflate(getContext(), contentResID, null);
            unbinder = ButterKnife.bind(this, contentView);
            contentLayout.addView(contentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public View getRootView() {
        return mRootView;
    }

    public View getContentLayoutView() {
        return contentView;
    }

    public NavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    /**
     * 是否启用导航菜单
     *
     * @param enabled
     */
    public void setActionBarEnabled(boolean enabled) {
        mNavigationBar.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

    public void hideActionBar() {
        setActionBarEnabled(false);
    }

    public void showActionBar() {
        setActionBarEnabled(true);
    }

    public void setTitle(CharSequence title) {
        mNavigationBar.setText(title);
    }

    //-------------------Back-------------------

    public void setHomeAsUpTitle(View.OnClickListener listener) {
        setHomeAsUpTitle(BaseActivity.DEFAULT_BACK_TEXT, listener);
    }

    public void setHomeAsUpTitle(String title, final View.OnClickListener listener) {
        mNavigationBar.setLeftTitle(title, R.drawable.ic_back_black, listener);
    }

    //-------------------Menu-------------------

    /**
     * View as menu.
     */
    public View setMenu(View contentView, View.OnClickListener listener) {
        return mNavigationBar.setMenu(contentView, listener);
    }

    /**
     * 图标Menu
     */
    public void setMenu(int iconResId, View.OnClickListener listener) {
        mNavigationBar.setMenu(iconResId, listener);
    }

    /**
     * 文字Menu
     */
    public void setMenu(String text, View.OnClickListener listener) {
        mNavigationBar.setMenu(text, listener);
    }

    /**
     * 图片+文字Menu
     */
    public void setMenu(String text, int iconResId, View.OnClickListener listener) {
        mNavigationBar.setMenu(iconResId, text, listener);
    }

    public void hideMenu() {
        mNavigationBar.hideMenu();
    }

    @Override
    public void showLoadingDialog() {
        loadingController.showLoadingDialog("正在加载");
    }

    @Override
    public void showLoadingDialog(CharSequence message) {
        loadingController.showLoadingDialog(message);
    }

    @Override
    public void showLoadingDialog(CharSequence message, DialogInterface.OnCancelListener listener) {
        loadingController.showLoadingDialog(message, listener);
    }

    @Override
    public void showInitLoading() {
        showInitLoading("正在加载中");
    }

    @Override
    public void showInitLoading(String text) {
        dismissErrorPage();
        contentLayout.setVisibility(View.GONE);
        loadingPanel.setVisibility(View.VISIBLE);
        loadingText.setText(text);
    }

    @Override
    public void dismissInitLoading() {
        loadingPanel.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
        loadingController.dismissLoadingDialog();
    }

    @Override
    public void showErrorPage() {
        showErrorPage(null);
    }

    @Override
    public void showErrorPage(String message) {
        showErrorPage(0, message);
    }

    @Override
    public void showErrorPage(int icResource, String message) {
        showErrorPage(icResource, message, null, null);
    }

    @Override
    public void showErrorPage(String message, String action, View.OnClickListener listener) {
        showErrorPage(0, message, action, listener);
    }

    @Override
    public void showErrorPage(int icResource, String message, String action, View.OnClickListener listener) {
        dismissInitLoading();
        dismissLoadingDialog();
        loadingPanel.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        errorPage.showErrorPage(icResource, message, action, listener);
    }

    public void showErrorPageForHttp(int statusCode, String responseString) {
        dismissInitLoading();
        dismissLoadingDialog();
        loadingPanel.setVisibility(View.GONE);
        contentLayout.setVisibility(View.GONE);
        errorPage.showErrorPageForHttp(statusCode, responseString);
    }

    @Override
    public void dismissErrorPage() {
        errorPage.setVisibility(View.GONE);
    }

    @Override
    public void dismissLoadingDialog() {
        loadingController.dismissLoadingDialog();
        dismissInitLoading();
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public abstract int getContentLayoutId();
}
