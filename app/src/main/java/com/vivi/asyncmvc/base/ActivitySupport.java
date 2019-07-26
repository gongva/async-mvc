package com.vivi.asyncmvc.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import com.vivi.asyncmvc.comm.AppContext;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.managers.NetManager;
import com.vivi.asyncmvc.comm.managers.SystemBarTintManager;
import com.vivi.asyncmvc.comm.view.dialog.LoadingDialog;
import com.noober.background.BackgroundLibrary;

public abstract class ActivitySupport extends AppCompatActivity implements ILoadingController, NetManager.NetConnectionListener {

    protected Dialog loadingDialog;
    private boolean destroyed = false;
    private LocalBroadcastManager mBroadcastManager;//用户主动调用exitApp时关闭所有页面
    private AudioManager audioManager;//设置应用为媒体音量

    private static PermissionRequestListener mPermissionRequestListener;//6.0以上动态授权回调

    private BroadcastReceiver exitReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //BackgroundLibrary初始化
        BackgroundLibrary.inject(this);
        super.onCreate(savedInstanceState);
        //将Activity纳入Application管理
        BaseApplication.getInstance().addActivity(this);
        //用于接收来自Intent的参数
        Intent intent = getIntent();
        if (intent != null) {
            initIntentData(intent);
        }
        //标题栏文字默认黑色，因为主题色是白色
        setStatusIconDark(true);
        //默认竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //监听是否关闭App
        mBroadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastManager.registerReceiver(exitReceiver, new IntentFilter(getPackageName()));
        //监听网络
        NetManager.getInstance().addConnectionListener(this);
        //设置媒体音量
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            initIntentData(intent);
        }
    }

    /**
     * 设置沉浸式状态栏，4.4以上生效
     */
    protected void setStatusBarTransparent(boolean trans) {
        if (trans) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // 5.0+ 实现
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 4.4 实现
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        } else {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }

    /**
     * 设置状态栏文字和标图的颜色,6.0以上生效
     *
     * @param dark
     */
    protected void setStatusIconDark(boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView == null) return;

            int vis = decorView.getSystemUiVisibility();
            if (dark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        } else {
            setStatusBarColor(Color.BLACK);
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(color);//通知栏所需颜色
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApplication.getInstance().removeActivity(this);
        destroyed = true;
        mBroadcastManager.unregisterReceiver(exitReceiver);
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        mBroadcastManager.sendBroadcast(new Intent(getPackageName()));
    }

    @Override
    public void showLoadingDialog() {
        showLoadingDialog("请稍候...");
    }

    @Override
    public void showLoadingDialog(CharSequence message) {
        showLoadingDialog(message, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dismissLoadingDialog();
            }
        });
    }

    @Override
    public void showLoadingDialog(CharSequence message, final DialogInterface.OnCancelListener listener) {
        if (loadingDialog == null) {
            loadingDialog = newLoadingDialog(message);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.setOnCancelListener(null);
            loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        if (listener != null) {
                            listener.onCancel(dialog);
                        }
                    }
                    return true;
                }
            });
        }
        loadingDialog.show();
    }

    public Dialog newLoadingDialog(CharSequence message) {
        return LoadingDialog.getCustomLoadingProgressDialog(this, TextUtils.isEmpty(message) ? "请稍候..." : message, true, null);
    }

    @Override
    public void dismissLoadingDialog() {
        if (loadingDialog != null && !destroyed) {
            loadingDialog.dismiss();
        }
        dismissInitLoading();
        dismissErrorPage();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 请求动态权限
     *
     * @param permissions
     * @param listener
     */
    public static void requestRunPermission(String[] permissions, PermissionRequestListener listener) {
        final Activity topActivity = BaseApplication.getInstance().getActivityTop();
        if (topActivity == null) {
            return;
        }
        requestRunPermission(topActivity, permissions, listener);
    }

    public static void requestRunPermission(Activity activity, String[] permissions, PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPermissionRequestListener = listener;
            List<String> deniedPermissions = new ArrayList<>();//存放用户未授权的权限
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                    /*if (ActivityCompat.shouldShowRequestPermissionRationale(topActivity, permission)) {
                        UI.showConfirmDialog(topActivity, "需要开启权限才能使用此功能", "去设置", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", topActivity.getPackageName(), null));
                                topActivity.startActivity(intent);
                            }
                        }, "取消", null);
                    }*/
                }
            }

            if (deniedPermissions.isEmpty()) {
                //已授权
                mPermissionRequestListener.onGranted();
            } else {
                //未授权 or 未全部授权
                ActivityCompat.requestPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), AppContext.REQUEST_CODE_PERMISSION);
            }
        } else if (listener != null) {
            listener.onGranted();
        }
    }

    /**
     * 动态权限请求系统回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppContext.REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0) {
                    //存放没授权的权限
                    StringBuffer stringBuffer = new StringBuffer();
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                            stringBuffer.append(permission);
                            stringBuffer.append(" ");
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //用户全部同意授权
                        mPermissionRequestListener.onGranted();
                    } else {
                        //返回用户拒绝的授权
                        //UI.showToast(String.format("您拒绝了权限：%s", stringBuffer.toString()));
                        mPermissionRequestListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    protected void initIntentData(Intent intent) {

    }

    @Override
    public void onConnected(int i) {

    }

    @Override
    public void onDisconnected(int i) {

    }
}
