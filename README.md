# DDRONE

An Android implementation of [the original ddrone application](https://github.com/Howchoo/drone-wars) utilizing external hardware for SUAS (Small Unmanned Aerial Systems) detection.

Firmware version updated to ``1.9.20``
[Email python](https://medium.freecodecamp.org/send-emails-using-code-4fcea9df63f)
 
## Documentation

[Grim Trigger Documentation](http://grim-trigger.dkelabs.com/)

## Important Notes

* *Bluetooth must be enabled prior to using DDRONE for DDRONE to function properly.*
* *USB Debugging must be disabled prior to launching the DDRONE app.*
* *Make sure to force stop DJI Go App before any DJI SDK methods can be used; otherwise, getProduct will not work.*
* *DJI SDK works on Google Nexus 6 running Android 7.x.x*
* *DJI SDK does not work on Google Pixel running Android version 8.x.x*
* *Cannot have 2 devices connected to drone, must deauth target mobile device*
* *TODO: find a way to detect if another mobile device is connected and maybe get their mac address or any other info*


## Raspberry Pi

## GrimBox Enclosure
The 3D printed enclosure houses all required off-the-shelf hardware needed for successful use of the application.

### STL File:
* [GrimBox Body](grimbox-stl/grim_box_c.STL)
* [GrimBox Lid](grimbox-stl/grim_lid_c.STL)

### Optional Features
The following features make it easier to access Pi functionality without needing to open the enclosure. Note: You can also reboot or shut down the Pi from within the DDRONE Android application.
* [Raspberry Pi power button](https://howchoo.com/g/mwnlytk3zmm/how-to-add-a-power-button-to-your-raspberry-pi)
* [Raspberry Pi Status LED](https://howchoo.com/g/ytzjyzy4m2e/build-a-simple-raspberry-pi-led-power-status-indicator)

## DJI SDK Functionality
Notes on usage of DDRONE's DJI SDK features:
* As with any custom DJI application, a brand new Phantom 3 (product) must be connected *once* to the DJI GO app before it can be used with DDRONE.

### Control Mode Switch S1 (Debugging)
The positions of the S1 (right) switch on the standard P3 controller are as follows. This is [used for testing purposes](https://developer.dji.com/mobile-sdk/documentation/application-development-workflow/workflow-run.html#remote-controller-flight-mode-switch) as the switch is unlabeled on the controller and is normally identified as changed within the DJI GO app:
* Backward (To Antenna): P (GPS)
* Center: A (Attitude/Atti)
* Forward (To Pilot): F (Fail Safe)
