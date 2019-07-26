package com.vivi.asyncmvc.ui.comm.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.studio.util.StringUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.ui.comm.selectimg.BrowseImgsActivity;

import java.util.Map;

/**
 * 公共组件H5 WebView
 *
 * @author gongwei 2019.1.2
 */

public class H5WebView extends WebView {

    private ProgressBar progressBar;
    private boolean scroll = true; //页面是否滚动,，内嵌到ScrollView中时考虑设置
    private boolean wideViewPort = true; //是否完整适配屏幕，会导致字体变小，内嵌到ScrollView中时考虑设置
    private H5WebViewClientCallBack mCallBack;

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;

    private long overrideTime;

    public H5WebView(Context context) {
        super(context);
        init();
    }

    public H5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 设置ProgressBar
     *
     * @param progressBar
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * 页面是否滚动,，内嵌到ScrollView中时考虑设置
     *
     * @param scroll
     */
    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    public void setCallBack(H5WebViewClientCallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 是否完整适配屏幕，会导致字体变小，内嵌到ScrollView中时考虑设置
     *
     * @param wideViewPort
     */
    public void setWideViewPort(boolean wideViewPort) {
        this.wideViewPort = wideViewPort;
        getSettings().setUseWideViewPort(this.wideViewPort);
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_NONE, null);
        setBackgroundColor(Color.TRANSPARENT);//设置背景色
        if (getContext() instanceof Activity) {//设置图片点击放大
            addJavascriptInterface(new JsInterface((Activity) getContext()), "app");
        }
        setWebChromeClient(new H5WebChromeClient());
        setWebViewClient(new H5WebViewClient());

        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 设置图片宽度不超过屏幕
        getSettings().setJavaScriptEnabled(true);
        getSettings().setBlockNetworkImage(false);
        getSettings().setUseWideViewPort(wideViewPort);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setDefaultTextEncodingName("utf-8");
        getSettings().setDomStorageEnabled(true);
        getSettings().setAllowFileAccess(true);//设置可以访问文件
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭缓存，否则可能会存在一些不刷新的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    @Override
    public void loadUrl(String url) {
        //封装一些参数给WebView备用
        Map<String, String> header = AppConfig.getCookieMap();
        header.put("Authorization", LoginManager.getInstance().getToken());
        loadUrl(url, header);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!scroll) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            return;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private class H5WebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (progressBar != null) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
            if (mCallBack != null) {
                mCallBack.updateProgress(newProgress);
            }
        }

        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            if (getContext() instanceof Activity) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "File Chooser"), AppContext.REQUEST_CODE_WEB_SELECT_FILE_OFF_41);
            }
        }

        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            if (getContext() instanceof Activity) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(i, "File Browser"), AppContext.REQUEST_CODE_WEB_SELECT_FILE_OFF_41);
            }
        }


        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (getContext() instanceof Activity) {
                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    ((Activity) getContext()).startActivityForResult(intent, AppContext.REQUEST_CODE_WEB_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    return false;
                }
            }
            return true;
        }

        //For Android 4.1 only
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            if (getContext() instanceof Activity) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, "File Browser"), AppContext.REQUEST_CODE_WEB_SELECT_FILE_OFF_41);
            }
        }
    }

    private class H5WebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mCallBack != null) {
                mCallBack.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mCallBack != null) {
                mCallBack.onPageFinished(view, url);
            }
            //加载的是来自Server端的富文本编辑器的内容时，才添加图片放大功能
            if (url != null && url.length() > 4 && !url.substring(0, 4).equalsIgnoreCase("http")) {
                addImageClickListener();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mCallBack != null) {
                mCallBack.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            overrideTime = System.currentTimeMillis();
            //支付宝网页支付拦截
            if (parseScheme(url)) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            //自制Web协议拦截
            if (filterUrlAction(url)) return true;

            boolean result = false;
            if (mCallBack != null && !url.contains("alipay.com/")) {
                result = mCallBack.shouldOverrideUrlLoading(view, url);
            }
            if (!result) {
                result = super.shouldOverrideUrlLoading(view, url);
            }
            return result;
        }

        /**
         * 支付宝网页支付是先后调用如下网页
         * 1.https://mobilecodec.alipay.com/client_download.htm?qrcode=bax05351pgjhc4yegd2y2084
         * 2.https://ds.alipay.com/from=mobilecodec&scheme=alipayqr%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fqr.alipay.com%252Fbax05351pgjhc4yegd2y2084%253F_s%253Dweb-other
         * 之后返回一个意图，也是用这个意图来打开支付宝app
         * intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fbax05351pgjhc4yegd2y2084%3F_s%3Dwebother&_t=1474448799004#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end
         * <p>
         * 基于以上而做的拦截
         *
         * @param url
         * @return
         */
        public boolean parseScheme(String url) {
            String urlTemp = url.toLowerCase();
            return urlTemp.contains("platformapi/startapp");
        }

        /**
         * 拦截web协议的入口方法
         * 协议拦截返回tue，未拦截返回false
         *
         * @param url
         * @return
         */
        public boolean filterUrlAction(String url) {
            if (url != null && StringUtils.startsWithIgnoreCase(url, ActionProtocol.PROTOCOL_HEADER)) {
                return ActionProtocolHandler.getInstance().handleActionProtocol(getContext(), url);
            }
            return false;
        }
    }

    /**
     * 为Web里的图片注入一个onclick方法，点击可以查看大图
     */
    public void addImageClickListener() {
        loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i=0;i<objs.length;i++){" +
                "if(typeof objs[i].onclick !== 'function'){"
                + "objs[i].onclick=function(){"
                + "window.app.openImage(this.src);" +
                "}" +
                "}" +
                "}" +
                "})()");
    }

    // 点击图片放大的js通信接口
    public class JsInterface {

        private Activity context;

        public JsInterface(Activity context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(final String imgUrl) {
            //起一个定时器
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (System.currentTimeMillis() - overrideTime <= 200) {
                        //触发了本身的点击
                        overrideTime = 0;
                    } else {
                        BrowseImgsActivity.startForShowImg(context, imgUrl);
                    }
                }
            }, 200);
        }
    }
}
