package com.vivi.asyncmvc.comm.managers.upgrade;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import com.vivi.asyncmvc.api.Api;
import com.vivi.asyncmvc.api.entity.Version;
import com.vivi.asyncmvc.base.BaseApplication;
import com.vivi.asyncmvc.base.BaseActivity;
import com.vivi.asyncmvc.comm.AppConfig;
import com.vivi.asyncmvc.library.plugs.http.JsonResultCallback;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResult;
import com.vivi.asyncmvc.library.plugs.http.entity.JsonResultVoid;
import com.vivi.asyncmvc.library.utils.FileProvider7;
import com.vivi.asyncmvc.library.utils.UI;
import com.vivi.asyncmvc.ui.comm.OPermissionActivity;

import java.io.File;
import java.io.IOException;

/**
 * App更新的工具类
 */
public class UpgradeManager {

    private static UpgradeManager INSTANCE = null;

    //控制器：是否正在下载
    public boolean isUpgradeDoing = false;
    //apk名称
    private final String APK_NAME = "ElecDrivLic.apk";
    //download title
    private final String DOWNLOAD_TITLE = "ElecDrivLic";
    //download title
    private final String DOWNLOAD_DESC = "电子驾照";

    //下载器
    private DownloadManager downloadManager;
    //下载地址
    private String mUrl;
    //下载的ID
    private long downloadId;
    //apk路径
    private String apkPath;

    public static synchronized UpgradeManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UpgradeManager();
        }
        return INSTANCE;
    }

    private UpgradeManager() {
        if (downloadManager == null) {
            downloadManager = (DownloadManager) BaseApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        }
    }

    /**
     * App更新检查的起点：
     * 获取最新版本信息
     */
    public void checkAppUpgradeStart(final JsonResultCallback<JsonResultVoid> callback) {
        if (isUpgradeDoing) {
            return;
        }
        isUpgradeDoing = true;
        Api.versionCheck(new JsonResultCallback<JsonResult<Version>>() {
            @Override
            public void onSuccess(int statusCode, JsonResult<Version> response, int tag) {
                Version version = response.getData();
                if (version != null) {
                    checkVersion(version);
                }
                if (callback != null) {
                    callback.onSuccess(statusCode, response, tag);
                }
            }

            @Override
            public void onFailure(int statusCode, String responseString, Throwable throwable, int tag) {
                super.onFailure(statusCode, responseString, throwable, tag);
                isUpgradeDoing = false;
                if (callback != null) {
                    callback.onFailure(statusCode, responseString, throwable, tag);
                }
            }
        });

        //test data
        /*Version temp = new Version();
        temp.versionMark = 600;
        temp.versionNo = "6.0.0";
        temp.url = "https://imtt.dd.qq.com/16891/282A515C0346CF45FC545AF0550D291E.apk";
        temp.versionDeclare = "1.修复页面 \n2.优化用户体验优化用户体验 \n3.新增快处快赔功能\n4.新增快处快赔功能";
        temp.forceUpdate = false;
        temp.forceUpdateVersionMark = 500;
        if (callback != null) {
            callback.onSuccess(0, new JsonResult<Version>(), 0);
        }
        checkVersion(temp);*/
    }

    /**
     * 检查是否需要更新，需要则弹框
     *
     * @param version
     */
    private void checkVersion(final Version version) {
        int versionCodeLocal = AppConfig.getAppVersionCode();
        if (versionCodeLocal < version.versionMark) {//有更新，弹框
            UpgradeDialog upgradeDialog = UpgradeDialog.newInstance(BaseApplication.getInstance().getActivityTop(), version, new UpgradeDialog.UpgradeDialogCallback() {
                @Override
                public void upgrade() {
                    downloadLatestVersion(version);
                }

                @Override
                public void cancel() {
                    isUpgradeDoing = false;
                }
            });
            upgradeDialog.show();
        } else {
            isUpgradeDoing = false;
        }
    }

    /**
     * 下载最新版本
     *
     * @param version
     */
    private void downloadLatestVersion(final Version version) {
        long downloadId = UpgradeManager.getInstance().downloadAPK(version.url);
        if (version.isForceUpdate()) {
            Activity activityTop = BaseApplication.getInstance().getActivityTop();
            if (activityTop instanceof BaseActivity) {
                final BaseActivity baseActivity = (BaseActivity) activityTop;
                //弹出进度条
                baseActivity.showDownloadProgress(version);
                DownloadChangeObserver observer = new DownloadChangeObserver(new Handler(), UpgradeManager.getInstance().getDownloadManager(), downloadId, new DownloadChangeObserver.UpgradeProgressCallBack() {
                    @Override
                    public void updateProgress(int downloadedBytes, int totalBytes) {
                        //更新进度
                        baseActivity.updateDownloadProgress(downloadedBytes, totalBytes);
                    }
                });
                activityTop.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, observer);
            }
        } else {
            UI.showToast("正在后台更新");
        }
    }

    public DownloadManager getDownloadManager() {
        if (INSTANCE != null) {
            return INSTANCE.downloadManager;
        }
        return (DownloadManager) BaseApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    //下载apk
    public long downloadAPK(String url) {
        mUrl = url;
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUrl));
        //移动网络情况下是否允许漫游
        request.setAllowedOverRoaming(false);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setTitle(DOWNLOAD_TITLE);
        request.setDescription(DOWNLOAD_DESC);
        request.setVisibleInDownloadsUi(true);

        //设置下载的路径
        File file = new File(BaseApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationUri(Uri.fromFile(file));
        apkPath = file.getAbsolutePath();
        //将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
        if (downloadManager != null) {
            downloadId = downloadManager.enqueue(request);
        }

        return downloadId;
    }

    //检查下载状态
    public void checkDownloadStatus() {
        isUpgradeDoing = false;
        DownloadManager.Query query = new DownloadManager.Query();
        //通过下载的id查找
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    installAPK();
                    cursor.close();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    UI.showToast("安装文件下载失败");
                    cursor.close();
                    break;
            }
        }
    }

    private void installAPK() {
        setPermission(apkPath);
        final Activity activityTop = BaseApplication.getInstance().getActivityTop();
        if (activityTop != null) {
            //兼容8.0的安全授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean canInstall = BaseApplication.getInstance().getPackageManager().canRequestPackageInstalls();
                if (canInstall) {
                    FileProvider7.installApk(activityTop, new File(apkPath));
                } else {
                    OPermissionActivity.start(activityTop, new InstallPermissionCallback() {
                        @Override
                        public void permissionSuccess() {
                            FileProvider7.installApk(activityTop, new File(apkPath));
                        }

                        @Override
                        public void permissionFail() {
                            UI.showToast("抱歉您未进行授权，安装已取消");
                        }
                    });
                }
            } else {
                FileProvider7.installApk(activityTop, new File(apkPath));
            }
        }
    }

    //修改文件权限
    private void setPermission(String absolutePath) {
        String command = "chmod 777 " + absolutePath;
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface InstallPermissionCallback {
        void permissionSuccess();

        void permissionFail();
    }
}
