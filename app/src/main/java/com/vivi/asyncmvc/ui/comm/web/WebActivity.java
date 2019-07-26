package com.vivi.asyncmvc.ui.comm.web;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.article.Article;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.otto.BusProvider;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContent;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContentQQ;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContentQZone;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContentWeChat;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContentWeChatMoments;
import com.vivi.asyncmvc.comm.view.dialog.ShareDialog;
import com.vivi.asyncmvc.library.utils.LogCat;
import com.vivi.asyncmvc.library.utils.UI;
import com.umeng.socialize.UMShareAPI;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 公共的内嵌Web加载Activity
 *
 * @author gongwei 2019.1.2
 */
public class WebActivity extends BaseActivity {

    @BindView(R.id.pb_web)
    ProgressBar progressBar;
    @BindView(R.id.h5wv_web)
    H5WebView mH5Wv;

    private String mTitle;
    private boolean mCanShare;

    private String mContentUrl;// 网页地址
    private String mShareImageUrl;
    private String mShareTitle;
    private String mShareContent;
    private String mShareTargetUrl;

    private boolean haveCutWebTitle = false;//分享系统消息时，需要从源码中扣文字，但要把扣出的文字中的title去了，只去一次

    /**
     * 文章详情
     *
     * @param context
     * @param id
     */
    public static void startArticle(final Context context, String id) {
        final Dialog loading = UI.showLoadingDialog(context, "加载中", null);
        Api.getArticleDetail(id, new JsonResultCallback<JsonResult<Article>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<Article> response, int tag) {
                loading.dismiss();
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", response.getData().link);
                intent.putExtra("title", response.getData().title);
                intent.putExtra("canShare", false);
                context.startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                loading.dismiss();
                UI.showToast(responseString);
            }
        });
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("canShare", false);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, String title, boolean canShare) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        intent.putExtra("canShare", canShare);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected void initIntentData(Intent intent) {
        super.initIntentData(intent);
        mContentUrl = intent.getStringExtra("url");
        mTitle = intent.getStringExtra("title");
        mCanShare = intent.getBooleanExtra("canShare", false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        BusProvider.bindLifecycle(this);
        LogCat.i("Load url : " + mContentUrl);

        setTitle("加载中...");
        initH5WebView();
        mH5Wv.loadUrl(mContentUrl);
        if (mCanShare) {
            setMenu(R.drawable.ic_share_black, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Gv. v3.9.0 容错：如果传了share ture，但又未传以下4个参数，则分享会出错
                    if (TextUtils.isEmpty(mShareImageUrl)) {
                        mShareImageUrl = ShareContent.SHARE_LOGO;
                    }
                    if (TextUtils.isEmpty(mShareTitle)) {
                        mShareTitle = (TextUtils.isEmpty(mTitle) ? ShareContent.SHARE_TITLE_DEFAULT : mTitle);
                    }
                    if (TextUtils.isEmpty(mShareContent)) {
                        mShareContent = (TextUtils.isEmpty(mTitle) ? ShareContent.SHARE_CONTENT_DEFAULT : mTitle);
                    }
                    if (TextUtils.isEmpty(mShareTargetUrl)) {
                        mShareTargetUrl = mContentUrl;
                    }
                    //end
                    if (!haveCutWebTitle && !TextUtils.isEmpty(mShareContent) && !TextUtils.isEmpty(mTitle) && mShareContent.startsWith(mTitle)) {
                        mShareContent = mShareContent.substring(mTitle.length());
                        mShareContent = mShareContent.trim();
                        haveCutWebTitle = true;
                    }
                    ShareContent weichat = new ShareContentWeChat(mShareImageUrl, mShareTitle, mShareContent, mShareTargetUrl);
                    ShareContent weichatMoment = new ShareContentWeChatMoments(mShareImageUrl, mShareTitle, mShareTargetUrl);
                    ShareContent qq = new ShareContentQQ(mShareImageUrl, mShareTitle, mShareContent, mShareTargetUrl);
                    ShareContent qzone = new ShareContentQZone(mShareImageUrl, mShareTitle, mShareContent, mShareTargetUrl);
                    ShareDialog.show(WebActivity.this, weichat, weichatMoment, qq, qzone);
                }
            });
        }
    }

    private void initH5WebView() {
        mH5Wv.setProgressBar(progressBar);
        mH5Wv.setCallBack(new H5WebViewClientCallBack() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (errorCode == -2) {
                    mH5Wv.setVisibility(View.GONE);
                    showErrorPageForHttp(errorCode, description);
                }
            }

            @Override
            public void updateProgress(int progress) {
                if (progress == 100) {
                    if (mH5Wv != null && !TextUtils.isEmpty(mH5Wv.getTitle())) {
                        mTitle = mH5Wv.getTitle();
                    }
                    setTitle(mTitle);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mH5Wv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mH5Wv.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //文件上传处理
        if (requestCode == AppContext.REQUEST_CODE_WEB_SELECT_FILE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null == mH5Wv.uploadMessage)
                    return;
                mH5Wv.uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                mH5Wv.uploadMessage = null;
            }
        } else if (requestCode == AppContext.REQUEST_CODE_WEB_SELECT_FILE_OFF_41) {
            if (null == mH5Wv.mUploadMessage)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            mH5Wv.mUploadMessage.onReceiveValue(result);
            mH5Wv.mUploadMessage = null;
        }
        //友盟的回调
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mH5Wv != null) {
            mH5Wv.loadUrl("javascript:pause()");
            mH5Wv.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mH5Wv.canGoBack()) {
            mH5Wv.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}