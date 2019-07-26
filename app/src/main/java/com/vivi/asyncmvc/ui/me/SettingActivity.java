package com.vivi.asyncmvc.ui.me;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.studio.os.PreferencesUtils;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vivi.asyncmvc.R;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.listener.PermissionRequestListener;
import com.vivi.asyncmvc.comm.managers.LoginManager;
import com.vivi.asyncmvc.comm.managers.biometric.BiometricPromptManager;
import com.vivi.asyncmvc.comm.view.dialog.ActionSheet;
import com.vivi.asyncmvc.comm.view.dialog.ShareDialog;
import com.vivi.asyncmvc.library.plugs.umeng.ShareContent;
import com.vivi.asyncmvc.library.utils.DataFormat;
import com.vivi.asyncmvc.library.utils.FileUtil;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.login.LoginActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity {
    //static
    public static final String PREFER_KEY_FINGER = "prefer_key_finger_";
    //views
    @BindView(R.id.iv_setting_finger)
    ImageView ivSettingFinger;
    @BindView(R.id.llt_setting_change_password)
    LinearLayout lltSettingChangePassword;
    @BindView(R.id.tv_setting_about)
    TextView tvSettingAbout;
    @BindView(R.id.llt_setting_about)
    LinearLayout lltSettingAbout;
    @BindView(R.id.tv_setting_cache)
    TextView tvSettingCache;
    @BindView(R.id.llt_setting_cache)
    LinearLayout lltSettingCache;
    @BindView(R.id.llt_setting_feedback)
    LinearLayout lltSettingFeedback;
    @BindView(R.id.llt_setting_recommend)
    LinearLayout lltSettingRecommend;
    @BindView(R.id.tv_setting_logout)
    TextView tvSettingLogout;
    //data
    private long allCacheSize; //缓存大小，单位：byte
    //tools
    private BiometricPromptManager mBiometricManager;
    private boolean isDeleteRunning = false;
    private final int MSG_WHAT_CACHE_CLEAN = 1;//handler's what:clean cache done
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_CACHE_CLEAN) {
                dismissLoadingDialog();
                UI.showToast("清理完成");
            }
            tvSettingCache.setText(allCacheSize <= 0 ? "" : DataFormat.formatFileSize(allCacheSize));
            return false;
        }
    });

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_setting;
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("设置");

        checkFingerprintPermission();
        refreshFingerSetting();
        super.requestRunPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionRequestListener() {
            @Override
            public void onGranted() {
                startCalcCacheThread();
            }
        });
    }

    /**
     * 若不支持指纹或未设置指纹解锁：强制关闭开关
     */
    private void checkFingerprintPermission() {
        mBiometricManager = BiometricPromptManager.from(this);
        if (!mBiometricManager.isBiometricPromptEnable()) {
            PreferencesUtils.setBoolean(getPreferKeyFinger(), false);
        }
    }

    /**
     * 刷新指纹开关
     */
    private void refreshFingerSetting() {
        boolean fingerSettingOn = PreferencesUtils.getBoolean(getPreferKeyFinger(), false);
        ivSettingFinger.setImageResource(fingerSettingOn ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
    }

    /**
     * 开启计算缓存大小的线程
     */
    private void startCalcCacheThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                allCacheSize = FileUtil.calcAllCache(getApplicationContext());
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    /**
     * 开启清除缓存大小的线程
     */
    private void startDeleteThread() {
        if (!isDeleteRunning) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isDeleteRunning = true;
                    FileUtil.deleteAllCache(getApplicationContext());
                    Glide.get(getApplicationContext()).clearDiskCache();
                    allCacheSize = FileUtil.calcAllCache(getApplicationContext());
                    mHandler.sendEmptyMessage(MSG_WHAT_CACHE_CLEAN);
                    isDeleteRunning = false;
                }
            }).start();
        }
    }

    /**
     * 指纹开关
     */
    private void switchFingerSetting() {
        boolean fingerSettingOn = PreferencesUtils.getBoolean(getPreferKeyFinger(), false);
        if (fingerSettingOn) {
            //关闭逻辑
            PreferencesUtils.setBoolean(getPreferKeyFinger(), false);
            refreshFingerSetting();
        } else {
            //打开逻辑
            if (mBiometricManager.isBiometricPromptEnable()) {
                mBiometricManager.authenticate(new BiometricPromptManager.OnBiometricIdentifyCallback() {
                    @Override
                    public void onSucceeded() {
                        PreferencesUtils.setBoolean(getPreferKeyFinger(), true);
                        refreshFingerSetting();
                    }

                    @Override
                    public void onFailed() {
                    }

                    @Override
                    public void outOfTimes() {
                        UI.showToast("失败次数过多，请稍后重试");
                        mBiometricManager.dismiss();
                    }
                });
            } else {
                UI.showConfirmDialog(this, "温馨提示", "抱歉，您的设备不支持或未录入指纹哦", "知道了", null);
            }
        }
    }

    /**
     * 推荐好友
     */
    private void dealRecommend() {
        ShareContent weChat = ShareContent.createAppShareWeChat();
        ShareContent weChatMoments = ShareContent.createAppShareWeChatMoments();
        ShareContent qq = ShareContent.createAppShareQQ();
        ShareContent qZone = ShareContent.createAppShareQZone();
        ShareDialog.show(this, weChat, weChatMoments, qq, qZone);
    }

    /**
     * 注销
     */
    private void logout() {
        ActionSheet.create(this)
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("退出当前账号后不会删除任何历史数据", ActionSheet.SheetItemColor.Grey, 12, null)
                .addSheetItem("退出登录", ActionSheet.SheetItemColor.Red, new ActionSheet.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        LoginManager.logout();
                        LoginManager.getInstance().exitAccount();
                        exitApp();
                        LoginActivity.start(SettingActivity.this);
                    }
                }).show();
    }

    /**
     * 按用户区分指纹开关
     *
     * @return
     */
    public static String getPreferKeyFinger() {
        return PREFER_KEY_FINGER + LoginManager.getInstance().getUserId();
    }

    @OnClick({R.id.iv_setting_finger, R.id.llt_setting_change_password, R.id.llt_setting_about, R.id.llt_setting_cache, R.id.llt_setting_feedback, R.id.llt_setting_recommend, R.id.tv_setting_logout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_setting_finger:
                switchFingerSetting();
                break;
            case R.id.llt_setting_change_password:
                ChangePasswordActivity.start(this);
                break;
            case R.id.llt_setting_about:
                AboutUsActivity.start(this);
                break;
            case R.id.llt_setting_cache:
                //清除缓存
                UI.showConfirmDialog(this, "温馨提示", "清除缓存?", "取消", null, "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showLoadingDialog();
                        startDeleteThread();
                    }
                });
                break;
            case R.id.llt_setting_feedback:
                FeedBackActivity.start(this);
                break;
            case R.id.llt_setting_recommend:
                dealRecommend();
                break;
            case R.id.tv_setting_logout:
                logout();
                break;
        }
    }
}
