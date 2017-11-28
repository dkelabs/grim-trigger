package com.dke.grimtrigger.grimtrigger;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class MainIOIOService extends IOIOService {

    boolean mButtonState;
    boolean mMonitorEnabled;
    boolean mJamEnabled;
    boolean mNotificationEnabled;
    NotificationModule mNotificationModule;
    CrackModule mCrackModule;
    String mCrackedPSK = "TEST_RESPONSE";

    protected final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        MainIOIOService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MainIOIOService.this;
        }
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return new BaseIOIOLooper() {

            private DigitalOutput led_;
            private DigitalInput button_;

            @Override
            protected void setup() throws ConnectionLostException, InterruptedException
            {
                led_ = ioio_.openDigitalOutput(15, true);
                button_ = ioio_.openDigitalInput(9, DigitalInput.Spec.Mode.PULL_UP);
                mNotificationEnabled = true;
                mMonitorEnabled = false;
                mJamEnabled = false;
            }

            @Override
            public void loop() throws ConnectionLostException, InterruptedException
            {
                mButtonState = button_.read();

                led_.write(mJamEnabled);
                if (mMonitorEnabled && mButtonState) {
                    mNotificationModule.showNotification("DRONE DETECTED!");
                    mCrackModule.crackDrone();
                    mMonitorEnabled = false;
                }

                mCrackedPSK = mCrackModule.crackedPSK;

                Thread.sleep(100);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        mNotificationModule = new NotificationModule(this);
        mCrackModule = new CrackModule(this);
        mNotificationModule.Initialize();
        mCrackModule.Initialize();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onDestroy()
    {
        mNotificationModule.Finalize();
        mCrackModule.Finalize();
    }
}