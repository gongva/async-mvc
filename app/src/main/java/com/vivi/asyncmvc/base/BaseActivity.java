package com.vivi.asyncmvc.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Version;
import com.vivi.asyncmvc.comm.view.errorpage.ErrorPage;
import com.vivi.asyncmvc.comm.view.nav.NavigationBar;
import com.vivi.asyncmvc.library.utils.OS;

/**
 * Activity基类
 *
 * @author gongwei
 * @created 2018/12/19.
 */
public abstract class BaseActivity extends ActivitySupport {

    public static final String DEFAULT_BACK_TEXT = null;

    protected ViewGroup mRootView;
    protected NavigationBar mNavigationBar;

    private ViewGroup contentLayout;
    private ErrorPage errorPage;

    private ViewGroup loadingPanel;
    private TextView loadingText;

    private View contentView;
    protected Unbinder unbinder;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hik_base);

        initViews();

        setSupportActionBar(mNavigationBar.getToolbar());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void initViews() {
        mRootView = findViewById(R.id.rootView);
        mNavigationBar = findViewById(R.id.navigationBar);
        contentLayout = findViewById(R.id.contentLayout);
        loadingPanel = findViewById(R.id.loadingPanel);
        loadingText = findViewById(R.id.loadingText);
        errorPage = findViewById(R.id.error_page_base);
        int contentResID = getContentLayoutId();
        if (contentResID > 0) {
            contentView = getLayoutInflater().inflate(contentResID, null);
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
    public void setBack(View.OnClickListener listener) {
        OS.hideSoftKeyboard(this);
        setBack(DEFAULT_BACK_TEXT, listener);
    }

    public void setBack(String title, final View.OnClickListener listener) {
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OS.hideSoftKeyboard(this);
    }

    @Override
    public void showInitLoading() {
        showInitLoading("加载中...");
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
        super.dismissLoadingDialog();
        dismissInitLoading();
        dismissErrorPage();
        contentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        OS.hideSoftKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private Dialog mApkDownloadDialog;
    private ProgressBar mApkDownloadProgress;
    private TextView mTvApkDownloadProgress;

    /**
     * 显示App下载进度框
     */
    public void showDownloadProgress(Version version) {
        // 构造软件下载对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("正在更新:V" + version.versionNo);
        // 给下载对话框增加进度条
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_app_upgreade_progress, null);
        mApkDownloadProgress = v.findViewById(R.id.pb_app_upgrade);
        mApkDownloadProgress.setMax(100);
        mTvApkDownloadProgress = v.findViewById(R.id.tv_app_upgrade_progress);
        builder.setView(v);
        builder.setCancelable(false);
        mApkDownloadDialog = builder.create();
        mApkDownloadDialog.show();
    }

    /**
     * 更新App下载进度条
     */
    public void updateDownloadProgress(int downloadedBytes, int totalBytes) {
        int progress = (int) (((float) downloadedBytes / totalBytes) * 100);
        if (progress >= 0 && progress <= 100) {
            if (mApkDownloadProgress != null) {
                mApkDownloadProgress.setProgress(progress);
            }
            if (mTvApkDownloadProgress != null) {
                mTvApkDownloadProgress.setText(progress == 100 ? "下载完成" : progress + "%");
            }
        }
    }

    protected abstract int getContentLayoutId();
}
