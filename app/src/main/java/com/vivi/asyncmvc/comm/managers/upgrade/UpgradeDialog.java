package com.vivi.asyncmvc.comm.managers.upgrade;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.api.entity.Version;
import com.vivi.asyncmvc.comm.managers.biometric.BiometricPromptDialog;
import com.vivi.asyncmvc.comm.view.dialog.TouchableDialog;

/**
 * 发现新版本的Dialog
 *
 * @author gongwei
 * @date 2019/2/1
 */
public class UpgradeDialog {
    //tools
    private UpgradeDialogCallback mCallBack;
    //views
    private TouchableDialog dialog;
    private TextView tvVersion;
    private TextView tvContent;
    private Button btnUpgrade;
    private ImageView ivClose;

    public static UpgradeDialog newInstance(Activity activity, Version version, UpgradeDialogCallback callback) {
        return new UpgradeDialog(activity, version, callback);
    }

    public UpgradeDialog(Activity activity, Version version, UpgradeDialogCallback callback) {
        this.mCallBack = callback;
        //view
        View rootView = LayoutInflater.from(activity).inflate(R.layout.dialog_upgrade, null);
        tvVersion = rootView.findViewById(R.id.tv_upgrade_version);
        tvContent = rootView.findViewById(R.id.tv_upgrade_content);
        btnUpgrade = rootView.findViewById(R.id.btn_upgrade);
        ivClose = rootView.findViewById(R.id.iv_upgrade_close);
        ivClose.setVisibility(version.isForceUpdate() ? View.GONE : View.VISIBLE);

        //listener
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_upgrade:
                        if (mCallBack != null) {
                            mCallBack.upgrade();
                        }
                        dialog.dismiss();
                        break;
                    case R.id.iv_upgrade_close:
                        if (mCallBack != null) {
                            mCallBack.cancel();
                        }
                        dialog.dismiss();
                        break;
                }
            }
        };
        btnUpgrade.setOnClickListener(clickListener);
        ivClose.setOnClickListener(clickListener);
        //data
        tvVersion.setText(String.format("V%s", version.versionNo));
        tvContent.setText(version.versionDeclare);
        //dialog
        dialog = new TouchableDialog(activity, R.style.AlertDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(rootView);
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public interface UpgradeDialogCallback {
        void upgrade();
        void cancel();
    }
}
