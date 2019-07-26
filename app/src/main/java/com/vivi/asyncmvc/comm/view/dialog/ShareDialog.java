package com.vivi.asyncmvc.comm.view.dialog;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContent;
import com.vivi.asyncmvc.library.plugs.umeng.ShareUtil;
import com.vivi.asyncmvc.library.utils.UI;

/**
 * 分享弹框
 *
 * @author gongwei 2019.1.2
 */
public class ShareDialog {

    private TouchableDialog dialog;
    private Display display;

    public static void show(final Activity activity, final ShareContent wechat, final ShareContent wechatMoment, final ShareContent qq, final ShareContent qzone) {
        new ShareDialog(activity, wechat, wechatMoment, qq, qzone).show();
        /*String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE, Manifest.permission.READ_LOGS, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_DEBUG_APP,
                Manifest.permission.SYSTEM_ALERT_WINDOW, Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_APN_SETTINGS};
        ActivitySupport.requestRunPermission(activity, mPermissionList, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                new ShareDialog(activity, wechat, wechatMoment, qq, qzone).show();
            }
        });*/
    }

    public ShareDialog(final Activity activity, ShareContent wechat, ShareContent wechatMoment, ShareContent qq, ShareContent qzone) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
        // 获取Dialog布局
        View view = createView(activity);
        view.setMinimumWidth(display.getWidth()); // 设置Dialog最小宽度为屏幕宽度

        // 获取自定义Dialog布局中的控件
        OnClickListener itemClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareContent sc = (ShareContent) v.getTag();
                dialog.dismiss();
                if (!TextUtils.isEmpty(sc.toastNoteText)) {
                    UI.showToast(sc.toastNoteText);
                }
                ShareUtil.shareToSinglePlatform(activity, sc);
            }
        };
        LinearLayout lltWeChat = view.findViewById(R.id.llt_share_wechat);
        LinearLayout lltWeChatMoments = view.findViewById(R.id.llt_share_wechat_moments);
        LinearLayout lltQQ = view.findViewById(R.id.llt_share_qq);
        LinearLayout lltQZone = view.findViewById(R.id.llt_share_qzone);

        lltWeChat.setTag(wechat);
        lltWeChatMoments.setTag(wechatMoment);
        lltQQ.setTag(qq);
        lltQZone.setTag(qzone);

        lltWeChat.setOnClickListener(itemClickListener);
        lltWeChatMoments.setOnClickListener(itemClickListener);
        lltQQ.setOnClickListener(itemClickListener);
        lltQZone.setOnClickListener(itemClickListener);
        view.findViewById(R.id.tv_share_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // 定义Dialog布局和参数
        dialog = new TouchableDialog(activity, R.style.AlertDialogBottomStyle);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    protected View createView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.dialog_share, null);
    }

    public void show() {
        dialog.show();
    }
}