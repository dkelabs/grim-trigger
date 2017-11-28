package com.dke.grimtrigger.grimtrigger;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.Toast;

// DJI SDK
import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.common.util.CommonCallbacks;

/**
 * Created by asmattic on 10/22/17.
 */

/**
 * Contains all DJI SDK functionality
 *
 */
// TODO: 10/22/17  Request to kill the DJI Go App background process if it is running
// https://developer.android.com/reference/android/app/ActivityManager.html#killBackgroundProcesses(java.lang.String)

public class DJIModule implements IModule {

    // Android
    private static final String TAG = "DJIModule";//MainActivity.class.getName(); // For logging
    //private SharedPreferences mSharedPreferences;
    //private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;
    private Context mContext; // Context of class that calls DJIModule
    private Context mApplicationContext; // Context of application regardless of which class calls DJIModule
    private NotificationModule mNotificationModule;
    private Button mForceLandButton;
    private Button mGoHomeButton;
    // DJI

    public static final String FLAG_CONNECTION_CHANGE = "dji_sdk_connection_change";
    private static BaseProduct mProduct;
    private Handler mHandler;
    private boolean mStartConnection;
    public Aircraft baseProduct;
    public FlightController flightController;
    public boolean isProductConnected = false;
    public boolean isFlying = false;

    DJIModule (Context context) {
        mContext = context;
        mApplicationContext = mContext.getApplicationContext();
        mNotificationModule = new NotificationModule(mContext);
    }

    DJIModule (Context context, Button forceLandButton, Button goHomeButton) {
        mContext = context;
        mApplicationContext = mContext.getApplicationContext();
        mForceLandButton = forceLandButton;
        mGoHomeButton = goHomeButton;
        //mWifiModule = new WifiModule(mContext);
        //mBTModule = new BTModule(mContext);
        mNotificationModule = new NotificationModule(mContext);
    }

    @Override
    public void Initialize() {

        // TODO: 10/22/17 Check if DJI Go app is running and tell user to force stop it
        // TODO: 10/22/17 First time through needs to be connected to real wifi, second time to drone
        // Disable button until product is connected
        //mForceLandButton.setEnabled(false);
        //mGoHomeButton.setEnabled(false);
        if (mGoHomeButton != null) {
            mGoHomeButton.post(new Runnable() {
                @Override
                public void run() {
                    mGoHomeButton.setEnabled(false);
                }
            });
        }
        if (mForceLandButton != null) {
            mForceLandButton.post(new Runnable() {
                @Override
                public void run() {
                    mForceLandButton.setEnabled(false);
                }
            });
        }
        // Initialize DJI SDK Manager
        mHandler = new Handler(Looper.getMainLooper());
        DJISDKManager.getInstance().registerApp(mContext, mDJISDKManagerCallback);

        IntentFilter filter = new IntentFilter();
        filter.addAction(FLAG_CONNECTION_CHANGE);
    }

    @Override
    public void Finalize() {}

    /**
     * Lands the drone where it's at
     * actionId: FORCE_LAND
     * @param actionId should be FORCE_LAND
     */
    public void forceLand(final String actionId) {
        Log.d(TAG+"|"+actionId, actionId + " function called from flightControllerAction");
        flightController.startLanding(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if(djiError != null) { Log.e(TAG+"|"+actionId, "Aircraft DJIError " + djiError.getDescription()); }
            }
        });
    }

    /**
     * Sends the drone to it's initial location or a custom set "home" location
     * actionId: GO_HOME
     * @param actionId should be GO_HOME
     */
    private void goHome(final String actionId) {
        Log.d(TAG+"|"+actionId, actionId + " function called from flightControllerAction");
        flightController.startGoHome(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError djiError) {
                if(djiError != null) {
                    Log.e(TAG+"|"+actionId, "Aircraft DJIError djiError.getDescription() " + djiError.getDescription());
                }
            }
        });
    }

    /**
     * General flight controller actions
     * @param actionId possible values (GO_HOME, FORCE_LAND, )
     */
    public void flightControllerAction(final String actionId) {

        Log.d(TAG+"|"+actionId, actionId + " called ");
        baseProduct = (Aircraft) DJISDKManager.getInstance().getProduct();

        // Run the action if baseProduct is connected
        if(baseProduct != null && baseProduct.isConnected()) {
            // List out the model and connection status
            String productModel = String.valueOf(baseProduct.getModel());
            Toast.makeText(mContext, productModel + " is Connected", Toast.LENGTH_SHORT).show();
            Log.d(TAG+"|"+actionId, "Aircraft model " + productModel);

            // Get the flight controller
            flightController = baseProduct.getFlightController();

            // Call any flightController actions from here
            if(actionId.equals("GO_HOME")) {

                goHome(actionId);

            } else if (actionId.equals("FORCE_LAND")) {

                forceLand(actionId);

            }
        } else {
            Log.d(TAG+"|"+actionId, "Aircraft is " + String.valueOf(baseProduct) + " and isConnected " + String.valueOf(baseProduct.isConnected()));
        }
    }

    // Register DJI SDK app
    private DJISDKManager.SDKManagerCallback mDJISDKManagerCallback = new DJISDKManager.SDKManagerCallback() {
        @Override
        public void onRegister(DJIError error) {
            Log.d("DJI-onRegister", error == null ? "success" : error.getDescription());
            if(error == DJISDKError.REGISTRATION_SUCCESS) {

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Register Success", Toast.LENGTH_LONG).show();
                        Log.d("onRegister", "Runnable ran ");
                    }
                });
                mStartConnection = DJISDKManager.getInstance().startConnectionToProduct();
            } else {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(mContext, "register sdk failed, check if network is available", Toast.LENGTH_LONG).show();
                        Log.d("runnabledji", "register sdk failed, check if network is available ");
                    }
                });
            }
            Log.e(TAG+"|onRegister", error.toString());
        }

        @Override
        public void onProductChange(BaseProduct oldProduct, BaseProduct newProduct) {

            mProduct = newProduct;
            if(mProduct != null) {
                Log.d("|onProductChange", "product is not null");
                String productModel = String.valueOf(mProduct.getModel());
                String isConnected = String.valueOf(mProduct.isConnected());
                Log.d("|onProductChange", productModel + " product is not null isConnected " + isConnected);
                isProductConnected = mProduct.isConnected();
                baseProduct = (Aircraft) mProduct;
                flightController = baseProduct.getFlightController();
                if(isProductConnected) {
                    //mForceLandButton.setEnabled(true);
                    //mGoHomeButton.setEnabled(true);
                    if (mGoHomeButton != null) {
                        mGoHomeButton.post(new Runnable() {
                            @Override
                            public void run() {
                                mGoHomeButton.setEnabled(true);
                            }
                        });
                    }
                    if (mForceLandButton != null) {
                        mForceLandButton.post(new Runnable() {
                            @Override
                            public void run() {
                                mForceLandButton.setEnabled(true);
                            }
                        });
                    }
                    isFlying = flightController.getState().isFlying();
                    Log.d("|onProductChange", productModel + " is Connected " + isConnected + " isFlying " + String.valueOf(isFlying));
                    Toast.makeText(mContext, productModel + " is Connected " + isConnected + " isFlying " + String.valueOf(isFlying), Toast.LENGTH_LONG).show();
                }
                Toast.makeText(mContext, productModel + " is Connected " + isConnected, Toast.LENGTH_SHORT).show();
                Log.d("|onProductChange", productModel + " is Connected " + isConnected);
                mProduct.setBaseProductListener(mDJIBaseProductListener);
            } else {
                Log.d("|onProductChange", "(null) mProduct is " + String.valueOf(newProduct) + ". oldProduct is " + String.valueOf(oldProduct));
            }
            notifyStatusChange();
        }
    };

    // DJI methods
    private BaseProduct.BaseProductListener mDJIBaseProductListener = new BaseProduct.BaseProductListener() {
        @Override
        public void onComponentChange(BaseProduct.ComponentKey key, BaseComponent oldComponent, BaseComponent newComponent) {
            if(newComponent != null) {
                Log.d("|onComponentChange", "newComponent isConnected " + String.valueOf(newComponent.isConnected()));
                newComponent.setComponentListener(mDJIComponentListener);
            }
            notifyStatusChange();
        }
        @Override
        public void onConnectivityChange(boolean isConnected) {
            mStartConnection = isConnected;
            Log.d("|onConnectivityChange", "BaseProductListener: isConnected " + String.valueOf(isConnected));
            notifyStatusChange();
        }
    };

    private BaseComponent.ComponentListener mDJIComponentListener = new BaseComponent.ComponentListener() {
        @Override
        public void onConnectivityChange(boolean isConnected) {
            Log.d("|onConnectivityChange", "ComponentListener: isConnected " + String.valueOf(isConnected));
            notifyStatusChange();
        }
    };

    private void notifyStatusChange() {
        mHandler.removeCallbacks(updateRunnable);
        mHandler.postDelayed(updateRunnable, 500);
    }

    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(FLAG_CONNECTION_CHANGE);
            mContext.sendBroadcast(intent);
        }
    };
}
