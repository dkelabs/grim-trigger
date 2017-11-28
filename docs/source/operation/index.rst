Operation Manual
==================

.. Variables
.. include:: ../vars.rst

Live Senario
--------------

#. Turn on the |droneName| controller and paired |droneName| drone.
#. Connect to the |droneName| contoller access point with a phone running the DJI Go (before Phantom 4) app
#. Plug in all three wires on the side labled *power* to the battery at the same time to boot up the pi, give power to the |pi|, |alfa| and IOIO board.
#. Wait about 1 minute for it to boot.
#. With an Android device running the |AppName| app, plug in the micro USB to the phone coming out of the side labeled *phone*.
#. Accept the permissions as they come in, *Bluetooth*, *pictures and videos*, etc.
#. Once the app is started up and the status says (ready) at the bottom, click ``start monitoring`` to allow the drone detector to send the signal when a drone has been detected.
#. There is a switch on the opposite side of the phone cable to simulate the detection of the drone. Switch it and a notification should appear saying that a drone has been detected.
#. At any point in the process from here on out there is a button labeled ``start jamming`` that is a radio frequency jammer failsafe mechanism that will jam anything around the 2.4 GHz portion of the spectrum to disrupt the |drone|'s communication.
#.


Testing
--------
#. Plug in all three wires to the |pi| battery at the same time.
