|AppName| Android Permissions
===============================

.. Variables
.. include:: ../vars.rst

Permissions are declared in ``AndroidManifest.xml``.

.. csv-table:: Android Required Permissions
    :header: "Permission", "Needed For"

    "WRITE_EXTERNAL_STORAGE", "Required for the **SharedPermissions** lib"
    "READ_EXTERNAL_STORAGE", "Required for the **SharedPermissions** lib"
    "ACCESS_NETWORK_STATE", "Required for general functionality."
    "MOUNT_UNMOUNT_FILESYSTEMS", "Required for general functionality."
    "CHANGE_WIFI_STATE", "Connect with the drone's controller once password has been cracked."
    "INTERNET", "Authenticate once with DJI SDK."
    "BLUETOOTH", "Used for communicating with the Raspberry Pi to transmit cracked passwords."
    "BLUETOOTH_ADMIN", "Used for communicating with the Raspberry Pi to transmit cracked passwords."
    "ACCESS_COARSE_LOCATION", "Used for interacting with a hijacked (cracked) drone via the DJI SDK."
    "ACCESS_FINE_LOCATION", "Used for interacting with a hijacked (cracked) drone via the DJI SDK."
    "VIBRATE", "Used for DRONE DETECTED DDRONE alerts."
    "WAKE_LOCK", "Used for DRONE DETECTED DDRONE alerts."
    "SYSTEM_ALERT_WINDOW", "Used for DRONE DETECTED DDRONE alerts."
    "READ_PHONE_STATE", "Used for DRONE DETECTED DDRONE alerts."
