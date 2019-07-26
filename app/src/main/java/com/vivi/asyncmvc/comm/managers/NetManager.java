package com.vivi.asyncmvc.comm.managers;

import android.content.Context;
import android.database.Observable;
import android.studio.service.NetworkManager;
import android.studio.service.NetworkStateCallback;

/**
 * 网络监听器
 *
 * @author gongwei 2018/12/19.
 */
public final class NetManager implements NetworkStateCallback {

    private static NetManager instance;
    private NetworkManager networkManager;
    private int netType = -1;
    private final NetConnectObservable mObservers;
    private static Context appContext;

    private NetManager(Context context) {
        networkManager = new NetworkManager(context);
        networkManager.registerConnectivityReceiver(this);
        mObservers = new NetConnectObservable();
    }

    public static void init(Context context) {
        NetManager.appContext = context.getApplicationContext();
    }

    public static synchronized NetManager getInstance() {
        if (instance == null) {
            instance = new NetManager(appContext);
        }
        return instance;
    }

    public void addConnectionListener(NetConnectionListener listener) {
        if (listener != null) {
            mObservers.registerObserver(listener);
            /*if (NetUtils.isNetConnected(appContext)) {
                listener.onConnected(netType);
            } else {
                listener.onDisconnected(netType);
            }*/
        }
    }

    public void removeConnectionListener(NetConnectionListener listener) {
        if (listener != null) {
            mObservers.unregisterObserver(listener);
        }
    }

    public void unregisterAll() {
        mObservers.unregisterAll();
    }

    @Override
    public void onConnected(int netType) {
        this.netType = netType;
        mObservers.notifyConnected(netType);
    }

    @Override
    public void onDisconnected(int netType) {
        this.netType = netType;
        mObservers.notifyDisconnected(netType);
    }

    private class NetConnectObservable extends Observable<NetConnectionListener> {

        public void notifyConnected(int netType) {
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onConnected(netType);
                }
            }
        }

        public void notifyDisconnected(int netType) {
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onDisconnected(netType);
                }
            }
        }
    }

    public interface NetConnectionListener extends NetworkStateCallback {

    }
}
