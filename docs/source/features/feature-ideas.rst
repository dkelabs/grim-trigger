Features
===========

.. Variables
.. include:: ../vars.rst

.. _features_todo_sec:

TODO
------

Hardware status check
~~~~~~~~~~~~~~~~~~~~~~~

Save runtime data
~~~~~~~~~~~~~~~~~~~

Save the data of each run to a SQL/PostgreSQL database

Table format

.. csv-table:: Chmod digits
    :header: "Key", "Datatype", "Description"

    "time_start", "datetime" , "Time crack was called"
    "time_finish", "datetime" , "Time either a key was returned or fail of some type"
    "target_ap_mac", "String" , "Mac address of the access pooint (controller)"
    "gps_location", "String" , "GPS coordinates of the phone format: ('LAT,LON')"
    "status", "String" , "success / fail format: ()"
    "cracked_key", "String" , "e.g. password1"
..    "", "" , ""

Send data to third party
~~~~~~~~~~~~~~~~~~~~~~~~~~~

* Email

.. code-block:: java

    if(view.getId() == R.id.sendEmail) {
        intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        String[] to = { "person1@gmail.com", "person2@gmail.com" };
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "some subjectline");
        intent.putExtra(Intent.EXTRA_TEXT, "some extra text");
        intent.setType("message/rrc822");
        chooser = Intent.createChooser(intent, "SendEmail");
        startActivity(chooser);
    }

* Text

GPS intent
~~~~~~~~~~~~

Create and intent to open up the map position of the GPS coordinates of a previously run crack to see if it is in the same location or develop patterns of drone movement if the crack happens many times around certain locations indicating a standardized flight path.

Poll bluetooth operation
~~~~~~~~~~~~~~~~~~~~~~~~~

Check if bluetooth has stopped listening on either end and restart that section of the system to that commands can once again be communicated.

Fallback if handshake can't be cracked
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Send the handshake back to the phone from the pi so that it can potentially be sent to a cloud cracking service with either a larger password list or much faster brutforcing capabilities.

Exclude client's from being deauth'd
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Feed a list of clients to avoid deauthenticating while trying to capture the handshake. Most useful for testing and avoiding deauthenticating friendly devices if they are already on the network.

ISSUE: Force crack only works on second click
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Seems like we just need to check the setup and make sure it is properly set up before the first click.

`W/BluetoothAdapter: getBluetoothService() called with no BluetoothManagerCallback
08-29 11:38:20.973 14800-14800/com.dke.grimtrigger.grimtrigger E/BroadcastReceiver: BroadcastReceiver trying to return result during a non-ordered broadcast`

Do initial \"handshake\" with app and Pi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Once the pi is booted send `PI_BLUETOOTH_READY--!`

and respond `PHONE_BLUETOOTH_READY--!`

Then do a full hardware check

Then allow crack to start