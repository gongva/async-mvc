package com.vivi.asyncmvc.comm.view.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.studio.view.ViewUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.noober.background.drawable.DrawableCreator;

public class AlertDialog {

    private Context context;

    private TouchableDialog dialog;

    //views
    protected TextView txt_title;
    protected TextView txt_msg;
    protected TextView btn_neg;
    protected TextView btn_pos;
    protected ImageView img_line;
    protected LinearLayout add_view;
    protected RelativeLayout btnLayout;
    private ImageView iv_close;

    private boolean showClose = false;
    private boolean showTitle = false;
    private boolean showMsg = false;
    private boolean showPosBtn = false;
    private boolean showNegBtn = false;
    private boolean showView = false;

    public AlertDialog(Context context) {
        this.context = context;
    }

    public static AlertDialog create(Context context) {
        return new AlertDialog(context).builder();
    }

    /**
     * 封装一个最常见的双按钮的确认框，其他类型的弹框需要自己构建
     *
     * @param context
     * @param cancelAble
     * @param title
     * @param message
     * @param buttonLeft
     * @param listenerLeft
     * @param buttonRight
     * @param listenerRight
     * @return
     */
    public static AlertDialog show(Context context, boolean cancelAble, String title, String message, String buttonLeft, View.OnClickListener listenerLeft, String buttonRight, View.OnClickListener listenerRight) {
        AlertDialog dialog = create(context)
                .setMsg(message);
        if (!cancelAble) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
        if (!TextUtils.isEmpty(buttonLeft)) {
            dialog.setPositiveButton(buttonLeft, listenerLeft);
        }
        if (!TextUtils.isEmpty(buttonRight)) {
            dialog.setNegativeButton(buttonRight, listenerRight);
        }
        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.checkPosAndNegBackground();
        dialog.show();
        return dialog;
    }

    protected View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.dialog_confirm_dialog, null);
    }

    public AlertDialog builder() {
        // 获取Dialog布局
        View view = createView(context);
        initView(view);
        // 定义Dialog布局和参数
        dialog = new TouchableDialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        return this;
    }

    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        }
        return false;
    }

    protected void initView(View view) {
        // 获取自定义Dialog布局中的控件
        txt_title = view.findViewById(R.id.txt_title);
        txt_title.setVisibility(View.GONE);
        txt_msg = view.findViewById(R.id.txt_msg);
        iv_close = view.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.GONE);
        txt_msg.setLineSpacing(ViewUtils.dip2px(this.context, 4), 1);
        txt_msg.setVisibility(View.GONE);
        btnLayout = view.findViewById(R.id.btn_layout);
        btn_neg = view.findViewById(R.id.btn_neg);
        btn_neg.setVisibility(View.GONE);
        btn_pos = view.findViewById(R.id.btn_pos);
        btn_pos.setVisibility(View.GONE);
        img_line = view.findViewById(R.id.img_line);
        img_line.setVisibility(View.GONE);

        add_view = view.findViewById(R.id.add_view);
        add_view.setVisibility(View.GONE);
    }

    public AlertDialog addClose(final View.OnClickListener closeListener) {
        showClose = true;
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (closeListener != null) {
                    closeListener.onClick(iv_close);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    public AlertDialog setTitle(String title) {
        showTitle = true;
        txt_title.setText(title);
        return this;
    }

    public AlertDialog setMsg(String msg) {
        showMsg = true;
        txt_msg.setText(msg);
        return this;
    }

    public AlertDialog addView(View view) {
        showView = true;
        if (view == null) {
            view = new TextView(context);
            add_view.addView(view);
        } else {
            add_view.addView(view);
        }
        return this;
    }

    public AlertDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public AlertDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public AlertDialog setPositiveButton(String text, final View.OnClickListener listener) {
        showPosBtn = true;
        if (TextUtils.isEmpty(text)) {
            btn_pos.setText("取消");
        } else {
            btn_pos.setText(text);
        }
        btn_pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    public AlertDialog setNegativeButton(String text, final View.OnClickListener listener) {
        showNegBtn = true;
        if (TextUtils.isEmpty(text)) {
            btn_neg.setText("确定");
        } else {
            btn_neg.setText(text);
        }
        btn_neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(v);
                }
                dialog.dismiss();
            }
        });
        return this;
    }

    public void checkPosAndNegBackground() {
        int dp6 = ViewUtils.dip2px(context, 6);
        if (showPosBtn && showNegBtn) {
            Drawable btnLeft = getBtnDrawableCreator().setCornersRadius(dp6, 0, 0, 0).build();
            Drawable btnRight = getBtnDrawableCreator().setCornersRadius(0, dp6, 0, 0).build();
            btn_pos.setBackground(btnLeft);
            btn_neg.setBackground(btnRight);
        } else {
            Drawable btnSingle = getBtnDrawableCreator().setCornersRadius(dp6, dp6, 0, 0).build();
            btn_pos.setBackground(btnSingle);
            btn_neg.setBackground(btnSingle);
        }
    }

    private DrawableCreator.Builder getBtnDrawableCreator() {
        return new DrawableCreator.Builder().setPressedSolidColor(Color.parseColor("#FFE5E5E5"), Color.parseColor("#fff8f8f8"));
    }

    public void cancelDialog() {
        dialog.dismiss();
    }

    //返回显示Message的TextView，有时候会有需要设置一下Gravity居中or居左的需求
    public TextView getMessageView() {
        return txt_msg;
    }

    private void setLayout() {
        if (showClose) {
            iv_close.setVisibility(View.VISIBLE);
        }

        if (!showTitle && !showMsg) {
            txt_title.setText("提示");
            txt_title.setVisibility(View.VISIBLE);
        }

        if (showTitle) {
            txt_title.setVisibility(View.VISIBLE);
        } else {
            txt_title.setVisibility(View.GONE);
        }

        if (showMsg) {
            txt_msg.setVisibility(View.VISIBLE);
        }

        if (showView) {
            add_view.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && !showNegBtn) {
            btnLayout.setVisibility(View.GONE);
        } else {
            btnLayout.setVisibility(View.VISIBLE);
        }

        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_neg.setVisibility(View.VISIBLE);
            img_line.setVisibility(View.VISIBLE);
        } else {
            img_line.setVisibility(View.GONE);
        }

        if (showPosBtn && !showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
        }

        if (!showPosBtn && showNegBtn) {
            btn_neg.setVisibility(View.VISIBLE);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }
}
