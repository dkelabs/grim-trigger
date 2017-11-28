package com.dke.grimtrigger.grimtrigger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.net.Uri;
import android.util.Log;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();
    private CrackModule mCrackModule;
    private IOIOModule mIOIOModule;
    private NotificationModule mNotificationModule;
    private DJIModule mDJIModule;
    private TextView mSystemStatus;
    private TextView mSystemAlert;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener mSharedPreferenceChangeListener;

    // Buttons
    private boolean mMonitorButtonActivated = false;
    private boolean mJamButtonActivated = false;
    private boolean mCrackButtonActivated = false;
    private boolean mForceLandButtonActivated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When the compile and target version is higher than 22, please request the following permission at runtime to ensure the SDK works correctly.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN

                    }
                    , 1);
        }

        setContentView(R.layout.activity_main);

        final Resources r = getResources();

        // Setup top toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Floating email looking button on the bottom right
        // TODO: Change to a plus sign that opens up multiple options for sending data

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will send an email summary of data gathered", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Define button objects.
        final Button monitorButton = (Button) findViewById(R.id.monitor_button);
        final Button jamButton = (Button) findViewById(R.id.force_button);
        final Button crackButton = (Button) findViewById(R.id.crack_button);
        final Button forceLandButton = (Button) findViewById(R.id.force_land_button);
        final Button goHomeButton = (Button) findViewById(R.id.go_home_button);

        // Create modules with current context.
        mCrackModule = new CrackModule(MainActivity.this, crackButton);
        mIOIOModule = new IOIOModule(MainActivity.this);
        mNotificationModule = new NotificationModule(MainActivity.this);
        mDJIModule = new DJIModule(MainActivity.this, forceLandButton, goHomeButton);

        // Initialize modules.
        mCrackModule.Initialize();
        mIOIOModule.Initialize();
        mNotificationModule.Initialize();
        mDJIModule.Initialize();
        // Set enabled to false because initialize will happen after crack
        //forceLandButton.setEnabled(false);
        //goHomeButton.setEnabled(false);

        // Create and register listener for shared preference changes.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(mSharedPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    handleSharedPreferenceChanged(sharedPreferences, key);
                }
            });

        // System status text (top of app)
        mSystemStatus = (TextView) findViewById(R.id.system_status);

        // System alert text (bottom of app)
        // Should mirror Android System Notifications somewhat
        mSystemAlert = (TextView) findViewById(R.id.system_alert);
        mSystemAlert.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_dialog_alert, 0, 0, 0);

        // TODO: Check if IOIO is attached and working before allowing this button to be pressed
        monitorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMonitorButtonActivated = !mMonitorButtonActivated;
                String monitorButtonText;
                if (mMonitorButtonActivated) {
                    monitorButtonText = r.getString(R.string.monitor_button_stop_text);
                    disableButtons(new Button[] {jamButton, crackButton});
                    mIOIOModule.startMonitor();
                } else {
                    monitorButtonText = r.getString(R.string.monitor_button_start_text);
                    enableButtons(new Button[] {jamButton, crackButton});
                    mIOIOModule.stopMonitor();
                }
                monitorButton.setText(monitorButtonText);
            }
        });

        // Stops any other blocking processes so that the radio Jammer
        // can start immediately.
        jamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mJamButtonActivated = !mJamButtonActivated;
                String jamButtonText;
                if (mJamButtonActivated) {
                    jamButtonText = r.getString(R.string.jam_button_stop_button);
                    //mSystemStatus.setText(mIOIOModule.getCrackedPSK());
                    mIOIOModule.startJam();
                } else {
                    jamButtonText = r.getString(R.string.jam_button_start_text);
                    //mSystemStatus.setText(mIOIOModule.getCrackedPSK());
                    mIOIOModule.stopJam();
                }
                jamButton.setText(jamButtonText);
            }
        });

        // Force crack
        // the detect DJI drone.
        crackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCrackModule.crackDrone();
                crackButton.setText(R.string.crack_button_active_text);
            }
        });

        forceLandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDJIModule.flightControllerAction("FORCE_LAND");
                //mForceLandButtonActivated = !mForceLandButtonActivated;
                //String forceLandButtonText;
                //if (mDJIModule.isFlying) {
                    //Toast.makeText(MainActivity.this, "Force Land Clicked", Toast.LENGTH_SHORT).show();
                    //forceLandButtonText = r.getString(R.string.force_land_button_text);
                    //disableButtons(new Button[] {jamButton, crackButton, monitorButton});

                //}/* else {
                    //forceLandButtonText = "DRONE NOT FLYING";
                    //enableButtons(new Button[] {jamButton, crackButton, monitorButton});
                    //mIOIOModule.stopMonitor();
               // }*/
                //forceLandButton.setText(forceLandButtonText);
            }
        });

        goHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDJIModule.flightControllerAction("GO_HOME");
                //mGoHomeButtonActivated = !mForceLandButtonActivated;
                //String goHomeButtonText;
                //goHomeButtonText = r.getString(R.string.go_home_button_text);
                //disableButtons(new Button[] {jamButton, crackButton, monitorButton});
                //if (mDJIModule.isFlying) {
                    //Toast.makeText(MainActivity.this, "Go Home Clicked", Toast.LENGTH_SHORT).show();

               // }

                //goHomeButton.setText(goHomeButtonText);
            }
        });

        // Request permissions listed in AndroidManifest.xml
        requestAndroidPermissions();
        mSystemStatus.setText(r.getString(R.string.system_status_text_ready));

    }

    /**
     * Handle drawer state if back button is pressed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Actions to take when the settings menu is created
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Top Bar Navigation, now hidden in the three dots menu
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, MainPreferenceActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Drawer menu item selected
     *
     * @param item Can be to a link, activity, fragment or quick setting
     * @return boolean
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        switch (id) {
            case R.id.action_hardware:
                Intent j = new Intent(this, HardwareDiagnostics.class);
                startActivity(j);
                return true;
            case R.id.action_wake_screen:
                mCrackModule.wakeScreenPi();
                Toast.makeText(this, "Wake the pi screen", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_reboot:
                mCrackModule.rebootPi();
                Toast.makeText(this, "Rebooting the pi", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_shutdown:
                mCrackModule.shutdownPi();
                Toast.makeText(this, "Shutting down the pi", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.start_deauth_mobile_target:
                mCrackModule.deauthMobileTarget("START");
                Toast.makeText(this, "Start deauthing mobile target " + mCrackModule.mobileMACAddressToDeauth, Toast.LENGTH_SHORT).show();

                return true;
            case R.id.start_monitor_mode:
                mCrackModule.toggleMonitorMode("START");
                Toast.makeText(this, "Starting monitor mode", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.stop_monitor_mode:
                mCrackModule.toggleMonitorMode("STOP");
                Toast.makeText(this, "Stopping monitor mode", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_docs:
                /**
                 * Docs require a login currently, the url format
                 * http://<username>:<password>@subdomain.example.com
                 * will automatically login to the
                 * NOTE: Chrome Deprecated this and it messed up jQuery so
                 *       this link structure was turned off
                 */
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://grim-trigger.dkelabs.com"));
                startActivity(browserIntent);
                // Toast.makeText(this, "Opening docs", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_contact:
                Intent contactIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:contact@dkelabs.com"));
                startActivity(contactIntent);
                // Toast.makeText(this, "Opening docs", Toast.LENGTH_SHORT).show();
                return true;
            default:
                Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        }
        // Gets the drawer layout and closes it after selection is made
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Act on various setting changes
    private void handleSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // If testing print special message
        if(key.equals("testing_mode_preference")) {
            Toast.makeText(
                    this,
                    "The preference key (" + key + ") " + String.valueOf(sharedPreferences.getBoolean(key, false)),
                    Toast.LENGTH_SHORT
            ).show();

            // TODO: If mac_prefix_preference not set,
            // change back to false and let and let the user know to set it
        } else if(key.equals("mac_prefix_preference")) {
            Toast.makeText(
                    this,
                    "The preference key (" + key + ") " + sharedPreferences.getString(key, ""),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(this, String.valueOf(sharedPreferences.getBoolean(key, false)), Toast.LENGTH_SHORT).show();
        }
    }

    // Request permissions at runtime if SDK version after Lollipop MR1
    // TODO: handle case where user failures to grant permission
    public void requestAndroidPermissions() {
        String[] permissions = {
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.ACCESS_COARSE_LOCATION",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.INTERNET",
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.VIBRATE",
                "android.permission.WAKE_LOCK",
                "android.permission.MOUNT_UNMOUNT_FILESYSTEMS",
                "android.permission.SYSTEM_ALERT_WINDOW",
                "android.permission.READ_PHONE_STATE"
        };

        int appRequestCode = 200;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            requestPermissions(permissions, appRequestCode);
        }
    }

    private void disableButton(Button button) { disableButtons(new Button[] {button}); }
    private void disableButtons(Button [] buttons) {
        for (Button button : buttons) button.setEnabled(false);
    }

    private void enableButton(Button button) { enableButtons(new Button[] {button}); }
    private void enableButtons(Button [] buttons) {
        for (Button button : buttons) button.setEnabled(true);
    }

    public void finalizeModules() {
        if(mCrackModule != null) mCrackModule.Finalize();
        if(mIOIOModule != null) mIOIOModule.Finalize();
        if(mNotificationModule != null) mNotificationModule.Finalize();
        if(mDJIModule != null) mDJIModule.Finalize();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG+"|finalizeModules", "onDestroy");
        super.onDestroy();
        finalizeModules();
    }
}
