package com.dke.grimtrigger.grimtrigger;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

public class IOIOModule implements IModule {

    public Context mContext;
    private Intent mMainIntent;
    private MainIOIOService mService;
    private boolean mServiceBound = false;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MainIOIOService.LocalBinder binder = (MainIOIOService.LocalBinder) service;
            mService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mServiceBound = false;
        }
    };


    public IOIOModule(Context context) {
        mContext = context;
        mMainIntent = new Intent(mContext, MainIOIOService.class);
    }

    public String getCrackedPSK () {
        if (mServiceBound) return mService.mCrackedPSK;
        else return "SERVICE_UNBOUND";
    }

    public void startJam () {
        if (mServiceBound) mService.mJamEnabled = true;
    }

    public void stopJam() {
        if (mServiceBound) mService.mJamEnabled = false;
    }

    public void startMonitor() {
        if (mServiceBound) mService.mMonitorEnabled = true;
    }

    public void stopMonitor() {
        if (mServiceBound) mService.mMonitorEnabled = false;
    }

    @Override
    public void Initialize() { startMonitorService(); }

    @Override
    public void Finalize() {
        stopMonitorService();
    }

    public void startMonitorService() {
        mContext.startService(mMainIntent);
        mContext.bindService(mMainIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopMonitorService() {
        mContext.stopService(mMainIntent);
        if (mServiceBound) {
            mContext.unbindService(mConnection);
            mServiceBound = false;
        }
    }
}
