package com.dke.grimtrigger.grimtrigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiModule implements IModule{

    private Resources r;
    private Context mContext;
    private WifiManager mWifiManager;
    private WifiInfo mWifiConnectionInfo;
    private BroadcastReceiver mScanResultsReceiver;
    private List<ScanResult> mFoundDroneAccessPoints = new ArrayList<>();
    private String mTargetSSID;
    private String mTargetPSK;

    private String TAG = "["+getClass().getName()+"]";

    public WifiModule(Context context) {
        mContext = context;
        r = context.getResources();
    }

    @Override
    public void Initialize() {
        // WifiManager can only be derived from application context to avoid memory leaks
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mScanResultsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                for (ScanResult result : mWifiManager.getScanResults()) {
                    String upperBSSID = result.BSSID.toUpperCase();
                    // TODO: If testing_mode_preference is true, use the mac_prefix_preference
                    if (upperBSSID.contains(r.getString(R.string.dji_mac_prefix)))
                        mFoundDroneAccessPoints.add(result);
                }
                boolean shouldAbortBroadcast = this.getAbortBroadcast(); // MATT (only ordered broadcasts should be aborted, this function checks)
                if(shouldAbortBroadcast) {
                    this.abortBroadcast();
                } else {
                    Log.d("WifiModule", "Broadcast shouldn't be aborted");
                }

            }
        };

        final IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        mContext.registerReceiver(mScanResultsReceiver, filter);
    }

    @Override
    public void Finalize()
    {
        mContext.unregisterReceiver(mScanResultsReceiver);
    }

    public void startScan() {
        mWifiManager.setWifiEnabled(true);
        mWifiManager.startScan();
    }

    /**
     * WifiConfig ssid and psk must be an ASCII string enclosed in double quotations
     * @param input string to be enclosed
     * @return string enclosed in double quotations
     */
    public String encloseInDoubleQuotations(String input) {
        return "\"" + input + "\"";
    }

    public String connectToTargetController(String ssid, String psk) {
        // TODO: Ensure that target is being actively deauthenticated.
        mTargetSSID = ssid;
        mTargetPSK = psk;
        WifiConfiguration config = createWifiConfiguration(mTargetSSID, mTargetPSK);
        return connectToAccessPoint(config, mTargetSSID, mTargetPSK);
    }

    public String getTargetMAC() {
        if (!mFoundDroneAccessPoints.isEmpty()) return mFoundDroneAccessPoints.get(0).BSSID.toUpperCase();
        else return "EMPTY";
    }

    public String getTargetSSID() {
        if (!mFoundDroneAccessPoints.isEmpty()) return mFoundDroneAccessPoints.get(0).SSID;
        else return "EMPTY";
    }

    private WifiConfiguration createWifiConfiguration(String ssid, String psk) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = encloseInDoubleQuotations(ssid);
        config.preSharedKey = encloseInDoubleQuotations(psk);
        config.status = WifiConfiguration.Status.DISABLED;
        config.priority = 40;
        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        return config;
    }

    private String connectToAccessPoint(WifiConfiguration config, String ssid, String psk) {

        if (!mWifiManager.isWifiEnabled()) {
            Log.d(TAG+"|connectToAccessPoint", "Wifi not enabled, about to enable");
            boolean didWifiEnable = mWifiManager.setWifiEnabled(true);
            Log.d(TAG+"|connectToAccessPoint", "didWifiEnable " + String.valueOf(didWifiEnable));
        }
        int netId;
        boolean networkIsDisconnected;
        boolean networkIsEnabled;
        boolean networkIsReconnected;
        try {
            Log.d(TAG+"|connectToAccessPoint", "Adding ");
            netId = mWifiManager.addNetwork(config);
            Log.d(TAG+"|connectToAccessPoint", "netId " + String.valueOf(netId));

            Log.d(TAG+"|connectToAccessPoint", "Disconnecting from current access point");
            networkIsDisconnected = mWifiManager.disconnect();
            Log.d(TAG+"|connectToAccessPoint", "isDisconnected " + String.valueOf(networkIsDisconnected));

            Log.d(TAG+"|connectToAccessPoint", "About to list wifi networks");
            List<WifiConfiguration> wifiNetworks = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiNetwork : wifiNetworks) {
                Log.d(TAG+"|connectToAccessPoint", "wifiNetwork SSID " + wifiNetwork.SSID + " networkId " + String.valueOf(wifiNetwork.networkId));
                if(wifiNetwork.SSID != null && wifiNetwork.SSID.equals("\""+ssid+"\"")) {
                    Log.d(TAG+"|connectToAccessPoint", "wifiNetwork SSID is the same ======================= network Id " + String.valueOf(wifiNetwork.networkId));
                    Log.d(TAG+"|connectToAccessPoint", "wifiNetwork SSID " + wifiNetwork.SSID + " ssid given " + ssid);

                    networkIsEnabled = mWifiManager.enableNetwork(wifiNetwork.networkId, true);
                    Log.d(TAG+"|connectToAccessPoint", "networkIsEnabled " + String.valueOf(networkIsEnabled));

                    // Reconnect if currently disconnected
                    networkIsReconnected = mWifiManager.reconnect();
                    Log.d(TAG+"|connectToAccessPoint", "networkIsReconnected " + String.valueOf(networkIsReconnected));

                    break;
                }
            }

            mWifiConnectionInfo = mWifiManager.getConnectionInfo();
            Log.d(TAG+"|connectToAccessPoint", "mWifiConnectionInfo converted to string " + mWifiConnectionInfo.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Attempt to disconnect from " + config.BSSID + " failed.");
            return e.getMessage();
        }
        return "SUCCESS on " + Integer.toString(netId);
    }
}

