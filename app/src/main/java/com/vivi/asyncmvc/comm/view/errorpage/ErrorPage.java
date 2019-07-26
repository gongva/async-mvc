package com.vivi.asyncmvc.comm.view.errorpage;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.library.plugs.http.AHttpRequest;

/**
 * 错误页，空白页
 *
 * @author gongwei 2018/12/18
 */
public class ErrorPage extends FrameLayout {

    private Context context;

    //view
    private RelativeLayout root;
    private TextView tvMessage;
    private ImageView ivIcon;
    private TextView tvAction;

    public ErrorPage(Context context) {
        this(context, null);
    }

    public ErrorPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ErrorPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inf = LayoutInflater.from(context);
        View v = inf.inflate(R.layout.view_error_page, this);
        root = v.findViewById(R.id.rlt_error_page_root);
        tvMessage = v.findViewById(R.id.tv_error_page_message);
        ivIcon = v.findViewById(R.id.iv_error_page_icon);
        tvAction = v.findViewById(R.id.tv_error_page_action);
    }

    public void showErrorPage() {
        showErrorPage(null);
    }

    public void showErrorPage(String message) {
        showErrorPage(0, message);
    }

    public void showErrorPage(int icResource, String message) {
        showErrorPage(icResource, message, null, null);
    }

    public void showErrorPageForHttp(int statusCode, String responseString) {
        if (statusCode == AHttpRequest.HTTP_CONNECT_ERR) {
            showErrorPage(R.drawable.ic_error_no_net, "糟糕，网络连接有问题");
        } else if (statusCode >= 400 && statusCode < 500) {
            showErrorPage(R.drawable.ic_error_no_net, "抱歉，页面丢失了");
        } else if (statusCode == AHttpRequest.HTTP_TIMEOUT) {
            showErrorPage(R.drawable.ic_error_no_net, "请求服务器超时");
        } else if (statusCode >= 500 && statusCode < 600) {
            showErrorPage(R.drawable.ic_error_no_net, "糟糕，无法连接服务器");
        } else {
            showErrorPage(R.drawable.ic_error_no_content, responseString);
        }
    }

    public void showErrorPage(int icResource, String message, String action, View.OnClickListener actionListener) {
        if (icResource > 0) {
            ivIcon.setImageResource(icResource);
        }
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
        if (!TextUtils.isEmpty(action)) {
            tvAction.setVisibility(VISIBLE);
            tvAction.setText(action);
            tvAction.setOnClickListener(actionListener);
        } else {
            tvAction.setVisibility(GONE);
        }
        this.setVisibility(VISIBLE);
    }

    public void dismiss() {
        this.setVisibility(GONE);
    }
}