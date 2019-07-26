package com.vivi.asyncmvc.comm.managers.biometric;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.comm.view.dialog.TouchableDialog;

/**
 * 指纹识别Dialog
 *
 * @author gongwei
 * @date 2019/1/31
 */
public class BiometricPromptDialog {

    public static final int STATE_NORMAL = 1;
    public static final int STATE_FAILED = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_SUCCEED = 4;

    //views
    private TouchableDialog dialog;
    private TextView tvStatus;
    private TextView tvCancel;
    //tools
    private BiometricPromptDialogCallback mCallBack;

    public static BiometricPromptDialog newInstance(Activity activity, BiometricPromptDialogCallback callback) {
        return new BiometricPromptDialog(activity, callback);
    }

    public BiometricPromptDialog(Activity activity, BiometricPromptDialogCallback callback) {
        this.mCallBack = callback;
        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_biometric_prompt, null);
        tvStatus = rootView.findViewById(R.id.tv_biometric_status);
        tvCancel = rootView.findViewById(R.id.tv_biometric_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        dialog = new TouchableDialog(activity, R.style.AlertDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(rootView);
    }

    public void setState(int state) {
        switch (state) {
            case STATE_NORMAL:
                tvStatus.setText("请验证指纹");
                tvCancel.setClickable(true);
                break;
            case STATE_FAILED:
                tvStatus.setText("验证失败，请重试");
                tvCancel.setClickable(true);
                break;
            case STATE_ERROR:
                tvStatus.setText("验证失败");
                tvCancel.setClickable(true);
                break;
            case STATE_SUCCEED:
                tvStatus.setText("验证成功");
                tvCancel.setClickable(false);

                tvStatus.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 500);
                break;
        }
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (mCallBack != null) {
                mCallBack.onDialogDismiss();
            }
        }
    }

    public interface BiometricPromptDialogCallback {
        void onDialogDismiss();
    }
}
