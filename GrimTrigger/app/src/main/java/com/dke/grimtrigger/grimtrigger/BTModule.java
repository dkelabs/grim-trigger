package com.dke.grimtrigger.grimtrigger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

public class BTModule implements IModule{

    private Resources r;
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mPiDevice;
    private BluetoothSocket mPiSocket;
    private String mResponse;
    private boolean mResponseValid;
    private boolean mBluetoothSupported;
    private boolean mInitialized;

    private byte MESSAGE_DELIMITER = 33;
    private final String TAG = "["+getClass().getName()+"]";

    public BTModule(Context context) {
        mContext = context;
        mInitialized = false;
        r = mContext.getResources();
    }

    public boolean isInitialized() { return mInitialized; }

    public String getResponse() {
        if (mResponseValid) return mResponse;
        else return "RESPONSE_INVALID_BTM";
    }

    public boolean getResponseValid() {
        return mResponseValid;
    }

    private String[] parseRawReponse(String rawResponse) {
        return rawResponse.split("--");
    }

    public boolean getBluetoothSupported() {
        if (mBluetoothAdapter != null) return mBluetoothSupported;
        // TODO: create custom "uninitialized module" exception
        else throw new IllegalStateException();
    }

    private void checkForPairedPiDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(r.getString(R.string.bt_device_hostname))) {
                    mPiDevice = device;
                }
            }
        }
    }

    public boolean refreshModule() {
        // TODO: Add error handling
        checkForPairedPiDevice();
        return true;
    }

    public void sendMessage(String message) {
        refreshModule();
        (new Thread(new connectionThread(message))).start();
    }

    public void sendCrackRequest(String targetMAC) {
        if (!targetMAC.startsWith(r.getString(R.string.dji_mac_prefix)))
            Toast.makeText(mContext, "Warning: MAC address does not correspond to a DJI access point.", Toast.LENGTH_LONG).show();
        mResponseValid = false;
        sendMessage("START_CRACK--" + targetMAC);
    }

    public void sendDeauthRequest(String targetMAC) {
        sendMessage("DEAUTH_CLIENT--" + targetMAC);
    }

    /**
     * General command to send requests
     *
     * @param cmd String all caps command with underscores as spaces
     */
    public void sendCommand(String cmd) {
        //Toast.makeText(mContext, "Sending command: " + cmd + "--!", Toast.LENGTH_LONG).show();
        sendMessage(cmd + "--!");
    }

    @Override
    public void Initialize() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothSupported = (mBluetoothAdapter != null);

        // TODO: handle case where user refuses request to enable Bluetooth
        if (mBluetoothSupported) {

            // Enable BlueTooth is currently disabled
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mContext.startActivity(enableBtIntent);
            }

            checkForPairedPiDevice();
            if (mPiDevice == null)
                Toast.makeText(mContext, "Please pair with a raspberrypi (dkelabspi)", Toast.LENGTH_LONG).show();
        }

        mInitialized = true;
    }

    @Override
    public void Finalize() {}

    final class connectionThread implements Runnable {

        private String mMessage;

        public connectionThread(String message) {
            mMessage = message;
        }

        private void openConnection() {
            try {
                if (mPiDevice != null) {
                    // Create RFCOMM socket and open a connection
                    mPiSocket = mPiDevice.createRfcommSocketToServiceRecord(UUID.fromString(r.getString(R.string.bt_device_uuid)));
                    mPiSocket.connect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessage(String message) {
            try {
                // Send message
                if (mPiSocket != null) mPiSocket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void waitForResponse() {

            int bytesAvailable;
            int readBufferPos = 0;
            String responseData = "";

            while (!Thread.currentThread().isInterrupted()) {
                boolean done = false;
                try {
                    if (mPiSocket != null) {
                        // Listen for response from Pi.
                        InputStream piInputStream = mPiSocket.getInputStream();
                        bytesAvailable = piInputStream.available();

                        // Process response.
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            byte[] readBuffer = new byte[1024];
                            piInputStream.read(packetBytes);

                            for (int i = 0; i < bytesAvailable; i++) {
                                byte curByte = packetBytes[i];
                                if (curByte == MESSAGE_DELIMITER) {
                                    byte[] encodedBytes = new byte[readBufferPos];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    responseData = new String(encodedBytes, "US-ASCII");
                                    readBufferPos = 0;
                                    mPiSocket.close();
                                    mResponse = responseData;
                                    done = true;
                                    break;
                                } else readBuffer[readBufferPos++] = curByte;
                            }
                            if (done) break;
                        }
                    }
                    mResponseValid = true;
                } catch (IOException e) {
                    mResponseValid = false;
                    e.printStackTrace();
                    break;
                } catch (NullPointerException e) {
                    mResponseValid = false;
                    e.printStackTrace();
                    break;
                }
            }
        }

        @Override
        public void run() {
            openConnection();
            sendMessage(mMessage);
            waitForResponse();
        }
    }
}
