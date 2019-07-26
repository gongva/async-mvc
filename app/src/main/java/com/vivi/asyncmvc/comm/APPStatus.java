package com.vivi.asyncmvc.comm;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 监听app在前台or后台
 */
public class APPStatus implements Application.ActivityLifecycleCallbacks {

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private boolean appForeground = false;

    private List<Activity> activityList = new ArrayList<>();
    private AppStatusCallback appStatusCallback;
    private AppExitCallback appExitCallback;

    public void setStatusCallback(AppStatusCallback callback) {
        this.appStatusCallback = callback;
    }

    public void setExitCallback(AppExitCallback appExitCallback) {
        this.appExitCallback = appExitCallback;
    }

    public boolean isAppExit() {
        return activityList.size() == 0;
    }

    public boolean isAppForeground() {
        return appForeground;
    }

    /**
     * 获取当前
     *
     * @return
     */
    public Activity getCurrentActivity() {
        int activitySize = activityList.size();
        return activitySize > 0 ? activityList.get(activitySize - 1) : null;
    }

    /**
     * 如果存在的话，获取当前栈第二个Activity
     *
     * @return
     */
    public Activity getSecondActivity() {
        int activitySize = activityList.size();
        return activitySize > 1 ? activityList.get(activitySize - 2) : null;
    }

    public void cleanSameActivity(Class<?> targetActivity) {
        if (activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                if (activity != null && activity.getClass().getName().equals(targetActivity.getName())) {
                    activity.finish();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityList.add(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityList.remove(activity);
        if (activityList.isEmpty()) {
            appForeground = false;
            APPStatus.resumed = 0;
            APPStatus.paused = 0;
            APPStatus.started = 0;
            APPStatus.stopped = 0;
            if (appExitCallback != null) {
                appExitCallback.appExit(this);
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
        boolean foreground = started > stopped;
        if (foreground == appForeground) {
            return;
        }

        if (foreground) {
            appForeground = foreground;
            Log.i("APPStatus", "application is in foreground");
            if (appStatusCallback != null) {
                appStatusCallback.appEnterForeground(this);
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        if (started == stopped) {
            appForeground = false;
            Log.i("APPStatus", "application is in background");
            if (appStatusCallback != null) {
                appStatusCallback.appEnterBackground(this);
            }
        }
    }

    /**
     * 是否应用可见
     *
     * @return
     */
    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    /**
     * @return
     */
    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public interface AppStatusCallback {

        /**
         * 当程序被推送到后台的时候调用。所以要设置后台继续运行，则在这个函数里面设置即可
         *
         * @param status
         */
        void appEnterBackground(APPStatus status);

        /**
         * 当程序从后台将要重新回到前台时候调用，这个刚好跟上面的那个方法相反。
         *
         * @param status
         */
        void appEnterForeground(APPStatus status);
    }

    public interface AppExitCallback {
        void appExit(APPStatus status);
    }
}