package com.vivi.asyncmvc.ui.comm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.managers.upgrade.UpgradeManager;

import java.util.List;

/**
 * 兼容8.0安装授权的过度页面
 */
public class OPermissionActivity extends BaseActivity {

    private static UpgradeManager.InstallPermissionCallback mInstallPermissionCallback;//8.0以上安装授权回调

    public static void start(Context context, UpgradeManager.InstallPermissionCallback installPermissionCallback){
        OPermissionActivity.mInstallPermissionCallback = installPermissionCallback;
        Intent intent = new Intent(context, OPermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.requestRunPermission(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, new PermissionRequestListener() {
                @Override
                public void onGranted() {
                    if (mInstallPermissionCallback != null) {
                        mInstallPermissionCallback.permissionSuccess();
                    }
                    finish();
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    //8.0安装授权API
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, AppContext.REQUEST_CODE_O_PERMISSION);
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppContext.REQUEST_CODE_O_PERMISSION) {//apk安装授权onActivityResult
            if (resultCode == RESULT_OK) {
                if (mInstallPermissionCallback != null) {
                    mInstallPermissionCallback.permissionSuccess();
                }
            } else {
                if (mInstallPermissionCallback != null) {
                    mInstallPermissionCallback.permissionFail();
                }
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInstallPermissionCallback = null;
    }
}
