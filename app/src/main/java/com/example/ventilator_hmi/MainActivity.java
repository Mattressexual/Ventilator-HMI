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
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SerialInputOutputManager.Listener {

    private enum UsbPermission { Granted, Denied, Requested, Unknown }
    private UsbPermission usbPermission = UsbPermission.Unknown;

    final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    final String INTENT_ACTION_USB_PERMISSION = BuildConfig.APPLICATION_ID + ".USB_PERMISSION";
    final String INTENT_ACTION_USB_STATE_CHANGE = "android.hardware.usb.action.USB_STATE"; // BuildConfig.APPLICATION_ID + ".USB_STATE";

    CustomViewModel model;
    int ventTabIndex = 0, alarmTabIndex = 1;
    Button
            standbyButton, settingsButton, alarmLimitsButton, logButton, moreButton,
            bt1, peepButton, bt3, iTimeButton, iPauseButton, fio2Button, pTriggerButton,
            alarmSilenceButton, fullOxygenButton, moreAlarmsButton, clearAlarmButton;
    TextView
            bt1_valTextView, peep_valTextView, bt3_valTextView, iTime_valTextView, iPause_valTextView, fio2_valTextView, pTrigger_valTextView,
            bt1_typeTextView, bt1_unitTextView, bt3_typeTextView, alarmBarTextView;
    LinearLayout alarmBarLayout;

    FragmentManager fragmentManager;
    Fragment standbyFragment, settingsFragment, logFragment;

    BroadcastReceiver usbReceiver;

    UsbManager usbManager;
    UsbDevice device;
    UsbDeviceConnection connection;
    UsbSerialPort port;
    SerialInputOutputManager ioManager;
    Handler mainLooper;

    boolean showingAlarms = false;
    Resources resources;
    ArrayList<Alarm> alarmList;
    PopupWindow alarmPopupWindow;
    View alarmPopupView;
    int alarmNumber = 0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) { getSupportActionBar().hide(); }
        model = new ViewModelProvider(this).get(CustomViewModel.class);

        usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (
                        action.equals(INTENT_ACTION_GRANT_USB) ||
                        action.equals(INTENT_ACTION_USB_PERMISSION) ||
                        action.equals(INTENT_ACTION_USB_STATE_CHANGE)) {

                    boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
                    if (granted) {
                        usbPermission = UsbPermission.Granted;
                        connect();
                    } else
                        usbPermission = UsbPermission.Denied;
                }
            }
        };
        mainLooper = new Handler(Looper.getMainLooper());
        registerReceiver(usbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));

        model.getAttemptUsbConnect().observe(this, attempt -> {
            if (attempt) {
                connect();
                model.setAttemptUsbConnect(false);
            }
        });

        model.getUsbConnected().observe(this, connected -> {
            if (connected)
                Toast.makeText(this, "Device connected!", Toast.LENGTH_SHORT).show();
            else
                model.setAttemptUsbConnect(true);
        });

        // Start with StandbyFragment
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            standbyFragment = new StandbyFragment();
            settingsFragment = new SettingsFragment();
            logFragment = new LogFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container_view, standbyFragment, Constants.STANDBY_FRAGMENT)
                    .add(R.id.fragment_container_view, settingsFragment, Constants.SETTINGS_FRAGMENT)
                    .hide(settingsFragment)
                    .add(R.id.fragment_container_view, logFragment, Constants.LOG_FRAGMENT)
                    .hide(logFragment)
                    .addToBackStack(Constants.STANDBY_FRAGMENT)
                    .commit();
        }

        resources = getResources();

        standbyButton = findViewById(R.id.standby_button);
        settingsButton = findViewById(R.id.settings_button);
        alarmLimitsButton = findViewById(R.id.alarm_limits_button);
        logButton = findViewById(R.id.log_button);
        moreButton = findViewById(R.id.more_button);
        alarmSilenceButton = findViewById(R.id.alarm_silence_button);
        fullOxygenButton = findViewById(R.id.full_oxygen_button);
        alarmBarLayout = findViewById(R.id.alarm_bar_view);
        alarmBarTextView = findViewById(R.id.alarm_bar_textView);
        moreAlarmsButton = findViewById(R.id.more_alarms_button);
        clearAlarmButton = findViewById(R.id.clear_alarm_button);

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
            if (model.getVentRunning().getValue()) {
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View stopVentPopupView = inflater.inflate(R.layout.popup_stopvent, null);
                PopupWindow popupWindow = new PopupWindow(stopVentPopupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

                Button continueVentButton = stopVentPopupView.findViewById(R.id.continue_vent_button);
                continueVentButton.setOnClickListener(cont -> popupWindow.dismiss() );

                Button stopVentButton = stopVentPopupView.findViewById(R.id.stop_vent_button);
                stopVentButton.setOnClickListener(stop -> {
                    popupWindow.dismiss();
                    model.setVentRunning(false);

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
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (!fragment.getTag().equals(Constants.VENTILATION_TAB)
                            && !fragment.getTag().equals(Constants.ALARM_LIMITS_TAB))
                        fragmentTransaction.hide(fragment);
                }

                standbyFragment = fragmentManager.findFragmentByTag(Constants.STANDBY_FRAGMENT);
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
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            if (model.getUsbConnected().getValue() || model.getDebug().getValue()) {

                model.setTabSelected(ventTabIndex);
                String current = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                if (model.getVentRunning().getValue())
                    model.setTempReset(true);
                else
                    model.setResetDefault(true);
                if (!current.equals(Constants.SETTINGS_FRAGMENT)) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    for (Fragment fragment : fragmentManager.getFragments())
                        if (!fragment.getTag().equals(Constants.VENTILATION_TAB)
                                && !fragment.getTag().equals(Constants.ALARM_LIMITS_TAB))
                            fragmentTransaction.hide(fragment);

                    settingsFragment = fragmentManager.findFragmentByTag(Constants.SETTINGS_FRAGMENT);
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
        });

        alarmLimitsButton.setOnClickListener(v -> {
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            if (model.getUsbConnected().getValue()) {
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
        });

        logButton.setOnClickListener(v -> {
            logFragment = fragmentManager.findFragmentByTag(Constants.LOG_FRAGMENT);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment currentFragment =
                    fragmentManager.findFragmentByTag(
                            fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
            if (currentFragment != null)
                fragmentTransaction.hide(currentFragment);

            if (logFragment == null)  {
                logFragment = new LogFragment();
                fragmentTransaction.add(R.id.fragment_container_view, logFragment, Constants.LOG_FRAGMENT);
            } else
                fragmentTransaction.show(logFragment);
            fragmentTransaction.commit();
        });

        moreButton.setOnClickListener(v -> {

        });

        alarmSilenceButton.setOnClickListener(v -> {
            // TODO: Implement alarm silence mode

        });

        alarmNumber = 0;
        fullOxygenButton.setOnClickListener(v -> {
            // TODO: Implement 100% oxygen mode

            model.appendReceiveBuffer("[ALARM] " + alarmNumber + "\n");
            alarmNumber++;
        });

        model.getReceiveBuffer().observe(this, buffer -> {
            // Toast.makeText(this, model.getReceiveBuffer().getValue(), Toast.LENGTH_SHORT).show();
            if (buffer.length() > 0) {
                String[] rows = buffer.split("\n");

                for (int i = 0; i < rows.length - 1; i++) {
                    model.setBufferRow(rows[i]);
                }

                if (buffer.endsWith("\n")) {
                    model.setBufferRow(rows[rows.length - 1]);
                    model.setReceiveBuffer("");
                } else
                    model.setReceiveBuffer(rows[rows.length - 1]);
            }
        });

        model.getBufferRow().observe(this, row -> {
            String[] tokens = row.split(" ");

            if (tokens[0].equals("[ALARM]")) {
                model.setAlarm(new Alarm(tokens[1], null, new Date(), null));
            }
        });

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View alarmPopupView = inflater.inflate(R.layout.popup_alarm_list, null);
        LinearLayout alarmPopupLayout = alarmPopupView.findViewById(R.id.alarm_popup_linearLayout);
        PopupWindow alarmPopupWindow = new PopupWindow(alarmPopupView, 800, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        ArrayList<AlarmPopupRow> alarmRowList = new ArrayList<>();
        model.getAlarm().observe(this, alarm -> {
            model.addActiveAlarm(alarm);
            alarmList = model.getActiveAlarmList().getValue();

            if (alarmList.size() == 1) {
                alarmBarLayout.setBackgroundColor(resources.getColor(R.color.orange));
                alarmBarTextView.setText(alarm.getAlarmText());
                clearAlarmButton.setVisibility(View.VISIBLE);
            } else if (alarmList.size() > 1) {
                moreAlarmsButton.setVisibility(View.VISIBLE);
                String moreString = alarmList.size() - 1 + " MORE";
                moreAlarmsButton.setText(moreString);

                AlarmPopupRow alarmRow = new AlarmPopupRow(this, alarm, resources.getColor(R.color.orange), resources.getColor(R.color.white));
                alarmPopupLayout.addView(alarmRow.linearLayout);
                alarmRowList.add(alarmRow);
            }
        });

        moreAlarmsButton.setOnClickListener(more -> {
            if (alarmPopupWindow.isShowing())
                alarmPopupWindow.dismiss();
            else
                alarmPopupWindow.showAsDropDown(alarmBarLayout);
        });

        clearAlarmButton.setOnClickListener(clear -> {
            model.clearActiveAlarm(alarmList.get(0));
            ArrayList<Alarm> alarmList = model.getActiveAlarmList().getValue();

            if (alarmList.isEmpty()) {
                alarmBarTextView.setText(R.string.no_alarms);
                alarmBarLayout.setBackgroundColor(resources.getColor(R.color.design_default_color_primary));
                clearAlarmButton.setVisibility(View.INVISIBLE);
            } else {
                alarmBarTextView.setText(alarmList.get(0).getAlarmText());

                if (alarmList.size() == 1) {
                    moreAlarmsButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        model.getVentMode().observe(this, ventMode -> {
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
        });
        model.getVt().observe(this, vt -> {
            if (model.getVentMode().getValue() == 1)
                bt1_valTextView.setText(String.valueOf(vt + Constants.MIN_VT));
        });
        model.getPSupport().observe(this, pSupport -> {
            if (model.getVentMode().getValue() == 2)
                bt1_valTextView.setText(String.valueOf(pSupport + Constants.MIN_PSUPPORT));
        });
        model.getPeep().observe(this, peep -> peep_valTextView.setText(String.valueOf(peep + Constants.MIN_PEEP)));
        model.getITime().observe(this, iTime -> iTime_valTextView.setText(String.valueOf(iTime + Constants.MIN_ITIME / 10f)));
        model.getIPause().observe(this, iPause -> iPause_valTextView.setText(String.valueOf(iPause + Constants.MIN_IPAUSE)));
        model.getFio2().observe(this, fio2 -> fio2_valTextView.setText(String.valueOf(fio2 + Constants.MIN_FIO2)));
        model.getPTrigger().observe(this, pTrigger -> pTrigger_valTextView.setText(String.valueOf(pTrigger + Constants.MIN_PTRIGGER)));

        model.getVentRunning().observe(this, ventRunning -> {
            if (ventRunning) {
                // TODO: Set vent params here
                // send("set ");


                send("set vent true");
                // send("readings");
            } else
                send("set vent false");
        });

        model.getShutdown().observe(this, shutdown -> {
            if (shutdown)
                send("shutdown");
        });
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
        if (device == null) {
            Toast.makeText(this, "No device found", Toast.LENGTH_SHORT).show();
            return;
        }
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            // TODO CustomProber if necessary
        }
        if (driver == null) {
            Toast.makeText(this, "No driver found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (driver.getPorts().size() < 1) {
            Toast.makeText(this, "No port available", Toast.LENGTH_SHORT).show();
            return;
        }

        port = driver.getPorts().get(0);
        connection = usbManager.openDevice(device);

        if (connection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(device)) {
            usbPermission = UsbPermission.Requested;
            PendingIntent permissionIntent = PendingIntent
                    .getBroadcast(this, 0, new Intent(Constants.INTENT_ACTION_GRANT_USB), 0);
            usbManager.requestPermission(device, permissionIntent);
            return;
        }

        if (connection == null) {
            if (!usbManager.hasPermission(device))
                Toast.makeText(this, "Permission for device denied.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Connection to device failed to open.", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void send(String string) {
        if (!model.getUsbConnected().getValue()) {
            Toast.makeText(this, "No device connected", Toast.LENGTH_SHORT).show();
        } else {

            byte[] data = (string + "\n").getBytes();
            try {
                port.write(data, Constants.WRITE_WAIT_MILLIS);
            } catch (IOException e) {
                onRunError(e);
            }
        }
    }

    @Override
    public void onNewData(byte[] data) {
        mainLooper.post(() -> {
                String receivedData = new String(data);
                model.appendReceiveBuffer(receivedData);
        });
    }

    @Override
    public void onRunError(Exception e) {
        disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(usbReceiver, new IntentFilter(INTENT_ACTION_GRANT_USB));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(usbReceiver);
    }

    @Override
    public void onBackPressed() { /* Empty body without calling super.onBackPressed() in this function essentially disables the back button */ }
}

class AlarmPopupRow {
    Alarm alarm;
    LinearLayout linearLayout;
    TextView textView;
    Button button;

    String clear = "Clear";
    int backgroundColor;
    int textColor;

    AlarmPopupRow (Context context, Alarm alarm, int backgroundColor, int textColor) {
        this.alarm = alarm;
        this.backgroundColor = backgroundColor;
        this.textColor = textColor;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 65);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 10, 0, 0);

        linearLayout = new LinearLayout(context);
        linearLayout.setPadding(5, 0, 5, 0);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackgroundColor(backgroundColor);
        linearLayout.setLayoutParams(layoutParams);


        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        textViewParams.weight = 1.0f;
        textView = new TextView(context);
        textView.setLayoutParams(textViewParams);
        textView.setText(alarm.getAlarmText());
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setAllCaps(true);
        textView.setTextColor(textColor);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 60);
        button = new Button(context);
        button.setLayoutParams(buttonParams);
        button.setText(clear);

        linearLayout.addView(textView);
        linearLayout.addView(button);
    }
}