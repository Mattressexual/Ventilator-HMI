package com.example.ventilator_hmi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// Implementing interface gives onNewData(byte[] bytes) method
public class MainActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    // Enum for USB permission (Not sure if actually used for anything.) // TODO Get rid of enum?
    private enum UsbPermission { Granted, Denied, Requested, Unknown }
    private UsbPermission usbPermission = UsbPermission.Unknown;

    // Strings constants for BroadcastReceiver actions
    final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    final String INTENT_ACTION_USB_PERMISSION = BuildConfig.APPLICATION_ID + ".USB_PERMISSION";

    // Custom made ViewModel
    // Note: When "getting" variables from the ViewModel, the IDE will warn about possible NullPointerException
    // However this will not ever happen as all variables are given a default starting value.
    CustomViewModel model;

    // Indices for which tab to select in Settings Fragment. Could just be magic numbers but would rather define them here.
    int ventTabIndex = 0, alarmTabIndex = 1;

    // UI elements
    Button
            standbyButton, settingsButton, alarmLimitsButton, logButton, moreButton,
            bt1, peepButton, bt3, iTimeButton, iPauseButton, fio2Button, pTriggerButton,
            alarmSilenceButton, fullOxygenButton, moreAlarmsButton, clearAlarmButton;
    TextView
            bt1_valTextView, peep_valTextView, bt3_valTextView, iTime_valTextView, iPause_valTextView, fio2_valTextView, pTrigger_valTextView,
            bt1_typeTextView, bt1_unitTextView, bt3_typeTextView, alarmBarTextView;
    LinearLayout alarmBarLayout;

    // Fragment stuff
    FragmentManager fragmentManager;
    Fragment standbyFragment, settingsFragment, logFragment;

    // USB stuff
    BroadcastReceiver usbReceiver;
    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialPort port;
    SerialInputOutputManager ioManager;

    Resources resources;
    ArrayList<Alarm> alarmList;
    ArrayList<AlarmPopupRow> alarmRowList;

    Handler mainLooper;

    // Alarm debugging variable (increments how many alarms have been added)
    int alarmNumber = 0;

    String tempBuffer = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); } // Hides action bar

        // ViewModel for MVVM pattern. CustomViewModel holds variables and app state booleans for triggering events.
        // MainActivity is the "owner" of the ViewModel and child Fragments can access it freely.
        model = new ViewModelProvider(this).get(CustomViewModel.class);

        // BroadcastReceiver to detect USB events. TODO: Detect USB attached/detached events to properly reconnect on attach/disconnect on detach
        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(INTENT_ACTION_GRANT_USB)
                        || action.equals(INTENT_ACTION_USB_PERMISSION)) {

                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    if (granted) {
                        usbPermission = UsbPermission.Granted;
                        connect();
                    } else
                        usbPermission = UsbPermission.Denied;
                }
            }
        };
        registerReceiver(usbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
        mainLooper = new Handler(Looper.getMainLooper());

        fragmentManager = getSupportFragmentManager();

        // If no saved state, app starts with StandbyFragment
        if (savedInstanceState == null) {
            // Initialize Fragments. No WaveformFragment yet. Settings Fragment creates that when ventilation starts.
            standbyFragment = new StandbyFragment();
            settingsFragment = new SettingsFragment();
            logFragment = new LogFragment();

            // Add all Fragments to container, but only show StandbyFragment.
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_view, standbyFragment, Constants.STANDBY_FRAGMENT)
                    .add(R.id.fragment_container_view, settingsFragment, Constants.SETTINGS_FRAGMENT)
                    .hide(settingsFragment)
                    .add(R.id.fragment_container_view, logFragment, Constants.LOG_FRAGMENT)
                    .hide(logFragment)
                    .addToBackStack(Constants.STANDBY_FRAGMENT) // addToBackStack manually adds a String tag to the backstack to identify the last Fragment activity.
                    .commit();
        }

        resources = getResources(); // Resources for getting color by int.

        // Initializing UI elements

        // Side
        standbyButton = findViewById(R.id.standby_button);
        settingsButton = findViewById(R.id.settings_button);
        alarmLimitsButton = findViewById(R.id.alarm_limits_button);
        logButton = findViewById(R.id.log_button);
        moreButton = findViewById(R.id.more_button);

        // Top
        alarmSilenceButton = findViewById(R.id.alarm_silence_button);
        fullOxygenButton = findViewById(R.id.full_oxygen_button);
        alarmBarLayout = findViewById(R.id.alarm_bar_view);
        alarmBarTextView = findViewById(R.id.alarm_bar_textView);
        moreAlarmsButton = findViewById(R.id.more_alarms_button);
        clearAlarmButton = findViewById(R.id.clear_alarm_button);

        // Bottom
        bt1 = findViewById(R.id.bt1);
        peepButton = findViewById(R.id.peep_button);
        bt3 = findViewById(R.id.bt3);
        iTimeButton = findViewById(R.id.i_time_button);
        iPauseButton = findViewById(R.id.i_pause_button);
        fio2Button = findViewById(R.id.fio2_button);
        pTriggerButton = findViewById(R.id.p_trigger_button);
        bt1_typeTextView = findViewById(R.id.bt1_type_textView);
        bt1_valTextView = findViewById(R.id.bt1_val_textView);
        bt1_unitTextView = findViewById(R.id.bt1_unit_textView);
        bt3_typeTextView = findViewById(R.id.bt3_type_textView);
        bt3_valTextView = findViewById(R.id.bt3_val_textView);
        peep_valTextView = findViewById(R.id.peep_val_textView);
        iTime_valTextView = findViewById(R.id.iTime_val_textView);
        iPause_valTextView = findViewById(R.id.iPause_val_textView);
        fio2_valTextView = findViewById(R.id.fio2_val_textView);
        pTrigger_valTextView = findViewById(R.id.pTrigger_val_textView);


        standbyButton.setOnClickListener(v -> {
            // If ViewModel says ventilator is running
            if (model.getVentRunning().getValue()) {
                // Standby button will create popup prompt to confirm ventilation stop.
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View stopVentPopupView = inflater.inflate(R.layout.popup_stopvent, null);
                PopupWindow popupWindow = new PopupWindow(stopVentPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0); // Appears in center of screen

                // Continue button in the popup (Cancels ventilation stop)
                Button continueVentButton = stopVentPopupView.findViewById(R.id.continue_vent_button);
                continueVentButton.setOnClickListener(cont -> popupWindow.dismiss() );

                // Stop Ventilation button confirms ventilation stop
                Button stopVentButton = stopVentPopupView.findViewById(R.id.stop_vent_button);
                stopVentButton.setOnClickListener(stop -> {
                    popupWindow.dismiss();
                    model.setVentRunning(false);
                    model.setReadings(false);

                    // Switches back to StandbyFragment, hides current fragment (found by tag.)
                    String current = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                    if (!current.equals(Constants.STANDBY_FRAGMENT)) {
                        Fragment standbyFragment = fragmentManager.findFragmentByTag(Constants.STANDBY_FRAGMENT);
                        fragmentManager.beginTransaction()
                                .hide(fragmentManager.findFragmentByTag(current))
                                .show(standbyFragment)
                                .addToBackStack(Constants.STANDBY_FRAGMENT)
                                .commit();
                    }
                });
            } else {
                // If not ventilating, switch back to StandbyFragment and hide all Fragments
                // Except for VentilationTabFragment and AlarmLimitsTabFragment
                // SettingsFragment holds these so they hide with it. No need to hide them manually.
                // Hiding them makes them disappear within SettingsFragment.

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (!fragment.getTag().equals(Constants.VENTILATION_TAB)
                            && !fragment.getTag().equals(Constants.ALARM_LIMITS_TAB))
                        fragmentTransaction.hide(fragment);
                }
                standbyFragment = fragmentManager.findFragmentByTag(Constants.STANDBY_FRAGMENT);
                // If StandbyFragment somehow got destroyed, create a new one. Otherwise show the existing one.
                if (standbyFragment == null) {
                    standbyFragment = new StandbyFragment();
                    fragmentTransaction.add(R.id.fragment_container_view, standbyFragment, Constants.STANDBY_FRAGMENT);
                } else
                    fragmentTransaction.show(standbyFragment);
                fragmentTransaction
                        .addToBackStack(Constants.STANDBY_FRAGMENT)
                        .commit();
            }
        });

        settingsButton.setOnClickListener(v -> {
            // Check USB connection. If not connected, attempt USB connection.
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            // If USB connected or debugging without USB device (debug changed in ViewModel manually)
            if (model.getUsbConnected().getValue() || model.getDebug().getValue()) {
                // Set tab to be selected in SettingsFragment to be 0 (VentilationTabFragment)
                model.setTabSelected(ventTabIndex);
                // String tag for current Fragment shown
                String current = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

                if (model.getVentRunning().getValue())
                    // If ventilating, set ViewModel boolean for resetting the sliders and values to the currently selected values
                    model.setTempReset(true);
                else
                    // Otherwise, set them to default values
                    model.setResetDefault(true);
                // If currently not on SettingsFragment
                if (!current.equals(Constants.SETTINGS_FRAGMENT)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    // Hide all Fragments except VentilationTabFragment and AlarmLimitsTabFragment
                    // Hiding these manually makes them disappear even when SettingsFragment is shown again, instead displaying blank tabs.
                    // Just let them appear and disappear with SettingsFragment
                    for (Fragment fragment : fragmentManager.getFragments())
                        if (!fragment.getTag().equals(Constants.VENTILATION_TAB)
                                && !fragment.getTag().equals(Constants.ALARM_LIMITS_TAB))
                            fragmentTransaction.hide(fragment);

                    settingsFragment = fragmentManager.findFragmentByTag(Constants.SETTINGS_FRAGMENT);
                    // If SettingsFragment somehow got destroyed, create a new one, otherwise show the existing one.
                    if (settingsFragment == null) {
                        settingsFragment = new SettingsFragment();
                        fragmentTransaction.add(R.id.fragment_container_view, settingsFragment, Constants.SETTINGS_FRAGMENT);
                    } else
                        fragmentTransaction.show(settingsFragment);
                    fragmentTransaction
                            .addToBackStack(Constants.SETTINGS_FRAGMENT)
                            .commit();
                }
            }
            else
                Toast.makeText(this, "No device", Toast.LENGTH_SHORT).show();
        });

        alarmLimitsButton.setOnClickListener(v -> {
            // Attempt USB connection and continue of connected
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            if (model.getUsbConnected().getValue()) {
                // Alarm Limits button essentially does exactly the same thing as Settings button.
                // But instead opens on AlarmLimitsTabFragment instead of VentilationTabFragment.
                model.setTabSelected(alarmTabIndex);
                String current = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                if (model.getVentRunning().getValue())
                    model.setTempReset(true);
                else
                    model.setResetDefault(true);
                if (!current.equals(Constants.SETTINGS_FRAGMENT)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    settingsFragment = fragmentManager.findFragmentByTag(Constants.SETTINGS_FRAGMENT);
                    if (settingsFragment == null) {
                        settingsFragment = new SettingsFragment();
                        fragmentTransaction.add(R.id.fragment_container_view, settingsFragment, Constants.SETTINGS_FRAGMENT);
                    } else
                        fragmentTransaction.show(settingsFragment);
                    fragmentTransaction
                            .hide(fragmentManager.findFragmentByTag(current))
                            .addToBackStack(Constants.SETTINGS_FRAGMENT)
                            .commit();
                }
            }
            else
                Toast.makeText(this, "No device", Toast.LENGTH_SHORT).show();
        });

        logButton.setOnClickListener(v -> {
            // Opens LogFragment
            logFragment = fragmentManager.findFragmentByTag(Constants.LOG_FRAGMENT);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment currentFragment =
                    fragmentManager.findFragmentByTag(
                            fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
            if (currentFragment != null)
                fragmentTransaction.hide(currentFragment);

            // If LogFragment exists, show, otherwise create new one.
            // Note: LogFragment does not add to backstack and always returns to previous Fragment when closed.
            if (logFragment == null)  {
                logFragment = new LogFragment();
                fragmentTransaction.add(R.id.fragment_container_view, logFragment, Constants.LOG_FRAGMENT);
            } else
                fragmentTransaction.show(logFragment);
            fragmentTransaction.commit();
        });

        moreButton.setOnClickListener(v -> {

        });

        alarmNumber = 0;
        alarmSilenceButton.setOnClickListener(v -> {

        });

        Random r = new Random();
        fullOxygenButton.setOnClickListener(v -> {

        });

        // Alarm handling
        // Create a pop-up for the Alarm dropdown.
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alarmPopupView = inflater.inflate(R.layout.popup_alarm_list, null);
        LinearLayout alarmPopupLayout = alarmPopupView.findViewById(R.id.alarm_popup_linearLayout);
        PopupWindow alarmPopupWindow = new PopupWindow(alarmPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        // ArrayList of AlarmRow objects (defined below)
        alarmRowList = new ArrayList<>();
        // Observe when a new Alarm object is set in the ViewModel
        model.getAlarm().observe(this, alarm -> {
            model.addActiveAlarm(alarm); // Adds the alarm to the list of active Alarms in the ViewModel
            alarmList = model.getActiveAlarmList().getValue(); // Get the list of active Alarms

            // When there's only one Alarm and it just came in, the list must have been empty before so change color and assign text.
            if (alarmList.size() == 1) {
                alarmBarLayout.setBackgroundColor(resources.getColor(R.color.orange)); // Alarm color can be unique and placed in the Alarm object later on
                alarmBarTextView.setText(alarm.getAlarmText()); // Set text of Alarm
                clearAlarmButton.setVisibility(View.VISIBLE); // Button to clear the Alarm becomes visible
            } else if (alarmList.size() > 1) { // If there are multiple alarms
                moreAlarmsButton.setVisibility(View.VISIBLE); // The "X More" Alarms button becomes visible.
                String moreString = alarmList.size() - 1 + " MORE"; // Set button's text
                moreAlarmsButton.setText(moreString);

                // Create an AlarmPopupRow object to encapsulate the UI element and variables together.
                // Note: Not the same as the AlarmRow object defined in LogFragment (very similar though)
                AlarmPopupRow alarmRow = new AlarmPopupRow(alarmPopupLayout, alarm);

                // Add the View associated with the AlarmPopupRow object to the LinearLayout in the PopupWindow.
                alarmPopupLayout.addView(alarmRow.alarmRowView);
                alarmRowList.add(alarmRow); // Add the object to the list that tracks them.

                // AlarmPopupRow has a button to clear the associated Alarm.
                // Clear button listener ///////////////////////////////////////////////////////////
                alarmRow.popupClearButton.setOnClickListener(clear -> {
                    alarmRowList.remove(alarmRow); // Remove the button's associated AlarmPopupRow from the list that tracks them.
                    alarmPopupLayout.removeView(alarmRow.alarmRowView); // Remove from view
                    model.clearActiveAlarm(alarm); // Clear the associated Alarm in the ViewModel (set as cleared, not remove)
                    alarmList = model.getActiveAlarmList().getValue(); // Get the list of active Alarms
                    if (alarmList.size() <= 1) { // If there's just one (or less somehow) Alarms, more alarms button goes away.
                        moreAlarmsButton.setVisibility(View.INVISIBLE);
                        if (alarmPopupWindow.isShowing()) // If the popup is showing, dismiss it (nothing to show anymore);
                            alarmPopupWindow.dismiss();
                    } else { // Otherwise if there's more Alarms, change the button text to show the right number more.
                        String newMoreString = alarmList.size() - 1 + " MORE";
                        moreAlarmsButton.setText(newMoreString);
                    }
                    // Tells ViewModel that LogFragment has to be updated with the correct active Alarms. See LogFragment's refreshLog observer.
                    model.setRefreshLog(true);
                });
                ////////////////////////////////////////////////////////////////////////////////////
            }
        });

        // If there's more than one Alarm, toggle ScrollView.
        moreAlarmsButton.setOnClickListener(more -> {
            if (alarmPopupWindow.isShowing())
                alarmPopupWindow.dismiss();
            else
                alarmPopupWindow.showAsDropDown(alarmBarLayout);
        });
        // Button to clear top/first Alarm.
        clearAlarmButton.setOnClickListener(clear -> {
            alarmList = model.getActiveAlarmList().getValue(); // Get the list of active Alarms
            model.clearActiveAlarm(alarmList.get(0)); // Clear it (not remove).
            alarmList = model.getActiveAlarmList().getValue(); // Get the new updated list.

            if (!alarmRowList.isEmpty()) { // If there are still Alarms more than the first (ones to be shown in PopupWindow)
                alarmPopupLayout.removeViewAt(0); // Remove the first one's View
                alarmRowList.remove(0); // Remove the AlarmPopupRow object from the list
                if (alarmRowList.isEmpty()) // If there are no more AlarmPopupRows to be shown, dismiss the PopupWindow if it's showing.
                    if (alarmPopupWindow.isShowing())
                        alarmPopupWindow.dismiss();
            }
            if (alarmList.isEmpty()) { // If there are no more Alarms, change the text back to "No Alarms" and the color to the original
                alarmBarTextView.setText(R.string.no_alarms);
                alarmBarLayout.setBackgroundColor(resources.getColor(R.color.design_default_color_primary));
                clearAlarmButton.setVisibility(View.INVISIBLE); // Clear button goes away (nothing to clear)
            } else { // Otherwise just change the text to the next Alarm's text.
                alarmBarTextView.setText(alarmList.get(0).getAlarmText());
                if (alarmList.size() == 1) // If there's only one Alarm, button to show PopupWindow goes away.
                    moreAlarmsButton.setVisibility(View.INVISIBLE);
                else { // If there are more Alarms, change text to show correct number
                    String moreString = alarmList.size() - 1 + " MORE";
                    moreAlarmsButton.setText(moreString);
                }
            }
            model.setRefreshLog(true); // Tell ViewModel LogFragment has to be updated. See LogFragment's refreshLog observer.
        });

        // Observer of refreshAlarmBar LiveData in the ViewModel.
        // If user clears Alarm in LogFragment, Alarm Bar has to be updated.
        model.getRefreshAlarmBar().observe(this, refresh -> {
            if (refresh) {
                alarmRowList.clear();
                alarmPopupLayout.removeAllViews();
                alarmList = model.getActiveAlarmList().getValue();

                if (alarmList.isEmpty()) {
                    alarmBarTextView.setText(R.string.no_alarms);
                    alarmBarLayout.setBackgroundColor(resources.getColor(R.color.design_default_color_primary));
                    clearAlarmButton.setVisibility(View.INVISIBLE);
                } else {
                    alarmBarTextView.setText(alarmList.get(0).getAlarmText());
                    alarmBarLayout.setBackgroundColor(resources.getColor(R.color.orange));
                    }
                    if (alarmList.size() == 1) {
                        if (alarmPopupWindow.isShowing())
                            alarmPopupWindow.dismiss();
                        moreAlarmsButton.setVisibility(View.INVISIBLE);
                    } else if (alarmList.size() > 1) {
                        String moreString = alarmList.size() - 1 + " MORE";
                        moreAlarmsButton.setText(moreString);
                        for (int i = 1; i < alarmList.size(); i++) {
                            AlarmPopupRow popupRow = new AlarmPopupRow(alarmPopupLayout, alarmList.get(i));
                            alarmPopupLayout.addView(popupRow.alarmRowView);
                            alarmRowList.add(popupRow);
                    }
                }
                model.setRefreshAlarmBar(false); // When task is handled, revert state boolean.
            }
        });

        // Observers for venting parameters for bottom buttons
        model.getVentMode().observe(this, ventMode -> {
            // Change text for bottom buttons 1 and 3 to the corresponding value names and units (i.e. PIP in cmH20, VT in mL, etc)
            // Set text using conditional operators with ventMode value as predicate
            bt1_typeTextView.setText(ventMode == 0 ? R.string.pip : ventMode == 1 ? R.string.vt : R.string.p_support);
            bt1_valTextView.setText(
                    ventMode == 0 ? String.valueOf(model.getPip().getValue() + Constants.MIN_PIP) :
                            ventMode == 1 ? String.valueOf(model.getVt().getValue() + Constants.MIN_VT) :
                                    String.valueOf(model.getPSupport().getValue() + Constants.MIN_PSUPPORT));
            bt1_unitTextView.setText(ventMode == 1 ? R.string.ml : R.string.cmh20);
            bt3_typeTextView.setText(ventMode == 2 ? R.string.backup_rate : R.string.r_rate);
            bt3_valTextView.setText(
                    ventMode == 2 ? String.valueOf(model.getBackupRate().getValue() + Constants.MIN_BACKUPRATE) :
                            String.valueOf(model.getRRate().getValue() + Constants.MIN_RRATE));
        });
        model.getPip().observe(this, pip -> {
            if (model.getVentMode().getValue() == 0)
                bt1_valTextView.setText(String.valueOf(pip + Constants.MIN_PIP));
            updateVentParam(Constants.pipPressure, model.getPip().getValue().toString());
        });
        model.getVt().observe(this, vt -> {
            if (model.getVentMode().getValue() == 1)
                bt1_valTextView.setText(String.valueOf(vt + Constants.MIN_VT));

        });
        model.getPSupport().observe(this, pSupport -> {
            if (model.getVentMode().getValue() == 2)
                bt1_valTextView.setText(String.valueOf(pSupport + Constants.MIN_PSUPPORT));
        });
        model.getPeep().observe(this, peep -> {
            peep_valTextView.setText(String.valueOf(peep + Constants.MIN_PEEP));
            updateVentParam(Constants.peepPressure, model.getPeep().getValue().toString());
        });
        model.getITime().observe(this, iTime -> iTime_valTextView.setText(String.valueOf(iTime + Constants.MIN_ITIME / 10f)));
        model.getIPause().observe(this, iPause -> iPause_valTextView.setText(String.valueOf(iPause + Constants.MIN_IPAUSE)));
        model.getFio2().observe(this, fio2 -> fio2_valTextView.setText(String.valueOf(fio2 + Constants.MIN_FIO2)));
        model.getPTrigger().observe(this, pTrigger -> pTrigger_valTextView.setText(String.valueOf(pTrigger + Constants.MIN_PTRIGGER)));

        // IMPORTANT: This is where the commands for starting and stopping ventilation are
        // Observe ventRunning LiveData bool
        model.getVentRunning().observe(this, ventRunning -> {
            // If ventRunning just switched to true
            if (ventRunning) {
                updateVentParam("vent", "true");
            } else {
                updateVentParam("vent", "false");
            }
            send("readings");
        });

        // ViewModel boolean "attemptUsbConnect" observer.
        // Boolean becomes true when a connection attempt needs to be made. Flips to false after.
        model.getAttemptUsbConnect().observe(this, attempt -> {
            if (attempt) {
                connect(); // connect() function defined further below.
                model.setAttemptUsbConnect(false); // Flip boolean to false after attempt is made.
            }
        });

        // ViewModel boolean "usbConnected" observer.
        model.getUsbConnected().observe(this, connected -> {
            if (connected)
                Toast.makeText(this, "Device connected!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Device not connected.", Toast.LENGTH_SHORT).show();
        });

        // Observe shutdown LiveData bool
        model.getShutdown().observe(this, shutdown -> {
            if (shutdown) {
                // If shutdown is set true, send shutdown
                send("shutdown");
            }
        });
    }

    // Command for updating vent params
    private void updateVentParam(String param, String val) {
        send(String.format("set %s %s", param, val));
    }

    private void connect() {
        device = null;
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        for (UsbDevice d : usbManager.getDeviceList().values()) {
            if (d.getVendorId() == Constants.VENDOR_ID) {
                device = d;
                break;
            }
        }
        if (device == null)
            return;

        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);

        if (driver == null)
            return;
        if (driver.getPorts().size() < 1)
            return;

        port = driver.getPorts().get(0);
        connection = usbManager.openDevice(device);

        if (connection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(device)) {
            usbPermission = UsbPermission.Requested;
            PendingIntent permissionIntent = PendingIntent
                    .getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(device, permissionIntent);
            return;
        }

        if (connection == null)
            return;

        try {
            port.open(connection);
            port.setParameters(
                    Constants.BAUD_RATE, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            ioManager = new SerialInputOutputManager(port, this);
            ioManager.start();
            model.setUsbConnected(true);
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    private void disconnect() {
        model.setUsbConnected(false);
        if (ioManager != null) {
            ioManager.setListener(null);
            ioManager.stop();
        }
        ioManager = null;

        if (port != null) {
            try {
                port.close();
            } catch (IOException ignored) { }
        }
        port = null;
    }

    // Method for sending data across serial connection
    private void send(String string) {
        if (port != null && port.isOpen()) {
            byte[] data = (string + "\n").getBytes();
            try {
                port.write(data, Constants.WRITE_WAIT_MILLIS);
            } catch (IOException e) {
                onRunError(e);
            }
        }
    }

    // Implementing SerialListener gives these methods that are called automatically when data is sent across the serial connection.
    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
            for (byte b : data) {
                char c = (char) b;
                if (c == '\n') {
                    model.setReceiveBuffer(tempBuffer);
                    tempBuffer = "";
                } else
                    tempBuffer += c;
            }
        });
    }

    @Override
    public void onRunError(Exception e) { disconnect(); } // If error, sever connection

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(usbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB)); // Register receiver again
    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(usbReceiver); // Unregister receiver when not needed
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        send("set vent false");
        send("shutdown");
        disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() { /* Empty body without calling super.onBackPressed() in this function essentially disables the back button */ }

    // Set Fullscreen (Out of date way of doing it but couldn't find any documentation on something more current)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    class AlarmPopupRow {
        Alarm alarm;
        View alarmRowView;
        TextView popupAlarmTextView;
        Button popupClearButton;

        AlarmPopupRow (LinearLayout container, Alarm alarm) {
            this.alarm = alarm;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            alarmRowView = inflater.inflate(R.layout.popup_alarm_row, null);
            popupAlarmTextView = alarmRowView.findViewById(R.id.popup_alarm_textView);
            popupAlarmTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            popupAlarmTextView.setText(alarm.getAlarmText());
            popupClearButton = alarmRowView.findViewById(R.id.popup_alarm_clear_button);
            popupClearButton.setOnClickListener(clear -> {
                model.clearActiveAlarm(alarm);
                model.setRefreshLog(true);
                container.removeView(alarmRowView);
                alarmRowList.remove(this);
            });
        }
    }
}