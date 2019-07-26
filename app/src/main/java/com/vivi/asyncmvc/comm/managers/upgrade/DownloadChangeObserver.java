package com.vivi.asyncmvc.comm.managers.upgrade;

import android.app.DownloadManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

import com.vivi.asyncmvc.library.utils.LogCat;

/**
 * Apk下载进度的观察者
 *
 * @author gongwei
 * @date 2019/2/2
 */
public class DownloadChangeObserver extends ContentObserver {
    private long mReqId;//需要根据这个ID 去查找apk文件
    private UpgradeProgressCallBack mUpdateListener;
    private DownloadManager mDownloadManager;

    public DownloadChangeObserver(Handler handler, DownloadManager downloadManager, long reqId, UpgradeProgressCallBack callBack) {
        super(handler);
        this.mDownloadManager = downloadManager;
        this.mUpdateListener = callBack;
        this.mReqId = reqId;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        updateView();
    }

    private void updateView() {
        int[] bytesAndStatus = new int[]{0, 0, 0};
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mReqId);
        Cursor c = null;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                //已经下载的字节数
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //总需下载的字节数
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //状态所在的列索引
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }

        if (mUpdateListener != null) {
            mUpdateListener.updateProgress(bytesAndStatus[0], bytesAndStatus[1]);
        }
        LogCat.i("下载进度：" + bytesAndStatus[0] + "/" + bytesAndStatus[1]);
    }

    public interface UpgradeProgressCallBack {
        void updateProgress(int downloadedBytes, int totalBytes);
    }
}
