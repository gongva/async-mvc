package com.vivi.asyncmvc.comm.managers.upgrade;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Apk下载状态的监听者
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class DownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UpgradeManager.getInstance().checkDownloadStatus();
    }
}
