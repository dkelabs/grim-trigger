|pi|
=====

.. Variables
.. include:: ../vars.rst

.. _piToUsbGadget: https://cdn-learn.adafruit.com/downloads/pdf/turning-your-raspberry-pi-zero-into-a-usb-gadget.pdf

Bluetooth
-----------

Install on pi

.. code-block:: bash

	$ sudo apt-get install bluez python-bluez

Append ``DisablePlugins = pnat`` to ``/etc/bluetooth/main.conf`` because the ``pnat`` plugin messes with bluez

Make device discoverable

``sudo hciconfig hci0 piscan``

Set the device name

``sudo hciconfig hci0 name 'Device Name'``


Wake console from sleep
~~~~~~~~~~~~~~~~~~~~~~~~~

Useful for testing with the pi commands and there is an hdmi screen plugged in.

.. code-block:: bash

    sudo bash -c 'echo -ne "\033[9;0]" > /dev/tty1'