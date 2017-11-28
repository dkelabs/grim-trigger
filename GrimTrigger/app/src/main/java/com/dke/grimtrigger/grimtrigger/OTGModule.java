package com.dke.grimtrigger.grimtrigger;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

// TODO: Look into removing this module, it is currently unused and been replaced by IOIOModule
public class OTGModule implements IModule {

    private Context mContext;
    private UsbDevice mUSBDevice;
    private UsbInterface mUSBInterface;
    private UsbEndpoint mUSBEndpoint;
    private UsbDeviceConnection mUSBDeviceConnection;

    public OTGModule(Context context, UsbDevice usbDevice) {
        mContext = context;
        mUSBDevice = usbDevice;
    }

    public void enableModule(UsbManager usbManager, boolean forceClaim) {
        // Hardcoded interface and endpoint values for now
        mUSBInterface = mUSBDevice.getInterface(0);
        mUSBEndpoint = mUSBInterface.getEndpoint(0);
        mUSBDeviceConnection = usbManager.openDevice(mUSBDevice);
        mUSBDeviceConnection.claimInterface(mUSBInterface, forceClaim);
    }

    public void enableModule(UsbManager usbManager) {
        // true to disconnect kernel driver if necessary
        enableModule(usbManager, true);
    }

    //do in another thread
    public int transfer(byte[] bytes, int timeout) {
        return mUSBDeviceConnection.bulkTransfer(mUSBEndpoint, bytes, bytes.length, timeout);
    }

    //do in another thread
    public int transfer(String str, int timeout) {
        byte[] bytes = str.getBytes();
        return transfer(bytes, timeout);
    }

    @Override
    public void Initialize() {
    }

    @Override
    public void Finalize() {
    }
}
