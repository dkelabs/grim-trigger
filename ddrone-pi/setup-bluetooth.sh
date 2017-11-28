#!/bin/bash

# General variables
base_dir="/home/pi/ddrone-pi" # no trailing slash

# android app uses this to match devices
device_name="dkelabspi"

# Start up the pi bluetooth and make discoverable
echo "Making bluetooth discoverable"
sudo hciconfig hci0 piscan

# Set the name
echo "Setting bluetooth name"
sudo hciconfig hci0 name $device_name

# Start bluetooth server
echo "Starting bluetooth server"
sudo python $base_dir/ddrone-bt.py
