|AppName| Hardware
===================

.. Variables
.. include:: ../vars.rst

.. _hardware-devices-sec:

Devices Used
-------------

.. csv-table:: Hardware Devices
    :header: "Device", "Description"

    "**Android Device**",                               "Hosts the DDRONE application. Must support OTG mode, as all newer devices do, for proper IOIO board interfacing."
    "**Alfa AWUS036H Wireless Network Adapter**",       "Enables monitor mode in order to deauthenticate the pilot and crack the drone password."
    "**IOIO-OTG Board (v2.2)**",                        "Used to interface the Android device with both the jammer and drone detector via digital I/O pins."
    "**Raspberry Pi 3**",                          "Hosts the Alfa (as monitor mode is required) and cracking utilities."
    "**RAVPower 20100mAh USB Type-C Power Bank**",      "Used to power the Raspberry Pi, IOIO board and to provide additional power to the Alfa."
    "**GrimBox Enclosure**",                            "A 3D-printed enclosure custom designed to house the aforementioned hardware. STL is available in the Github repository."
