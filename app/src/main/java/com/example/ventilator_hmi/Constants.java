package com.example.ventilator_hmi;

import java.util.ArrayList;

public class Constants {

    // Fragment tags
    static final String
            STANDBY_FRAGMENT = "StandbyFragment",
            SETTINGS_FRAGMENT = "SettingsFragment",
            VENTILATION_TAB = "VentilationTab",
            ALARM_LIMITS_TAB = "AlarmLimitsTab",
            WAVEFORM_FRAGMENT = "WaveformFragment",
            LOG_FRAGMENT = "LogFragment";

    static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    static final String INTENT_ACTION_USB_PERMISSION = BuildConfig.APPLICATION_ID + ".USB_PERMISSION";

    static final int WRITE_WAIT_MILLIS = 2000;

    // Minimum and maximum settings
    static final int
            MIN_PIP = 10, MAX_PIP = 35,
            MIN_VT = 200, MAX_VT = 1000,
            MIN_PSUPPORT = 10, MAX_PSUPPORT = 35,
            MIN_PEEP = 5, MAX_PEEP = 20,
            MIN_RRATE = 10, MAX_RRATE = 40,
            MIN_BACKUPRATE = 10, MAX_BACKUPRATE = 35,
            MIN_IPAUSE = 0, MAX_IPAUSE = 80,
            MIN_FIO2 = 20, MAX_FIO2 = 100,
            MIN_PTRIGGER = 1, MAX_PTRIGGER = 5;
    static final double MIN_ITIME = 3, MAX_ITIME = 30;

    static final int
            PIP_RANGE = MAX_PIP - MIN_PIP,
            VT_RANGE = MAX_VT - MIN_VT,
            PSUPPORT_RANGE = MAX_PSUPPORT - MIN_PSUPPORT,
            PEEP_RANGE = MAX_PEEP - MIN_PEEP,
            RRATE_RANGE = MAX_RRATE - MIN_RRATE,
            BACKUP_RANGE = MAX_BACKUPRATE - MIN_BACKUPRATE,
            ITIME_RANGE = (int) (MAX_ITIME - MIN_ITIME),
            IPAUSE_RANGE = MAX_IPAUSE - MIN_IPAUSE,
            FIO2_RANGE = MAX_FIO2 - MIN_FIO2,
            PTRIGGER_RANGE = MAX_PTRIGGER - MIN_PTRIGGER;

    // Default starting value settings
    static final int
            DEFAULT_PIP_PROGRESS = 20,
            DEFAULT_VT_PROGRESS = 500,
            DEFAULT_PSUPPORT_PROGRESS = 20,
            DEFAULT_PEEP_PROGRESS = 0,
            DEFAULT_RRATE_PROGRESS = 5,
            DEFAULT_BACKUPRATE_PROGRESS = 5,
            DEFAULT_ITIME_PROGRESS = 12,
            DEFAULT_IPAUSE_PROGRESS = 0,
            DEFAULT_FIO2_PROGRESS = 50,
            DEFAULT_PTRIGGER_PROGRESS = 3;

    static final int
            ALARM_MAX_PRESSURE = 70, ALARM_MIN_PRESSURE = 10,
            ALARM_MAX_PIP = 37, ALARM_MIN_PIP = 8,
            ALARM_MAX_PEEP = 23, ALARM_MIN_PEEP = 2,
            ALARM_MAX_VT = 1050, ALARM_MIN_VT = 150,
            ALARM_MAX_RRATE = 40, ALARM_MIN_RRATE = 10;
    static final double ALARM_MAX_MVOL = 40, ALARM_MIN_MVOL = 2;
    static final int
            ALARM_PRESSURE_RANGE = ALARM_MAX_PRESSURE - ALARM_MIN_PRESSURE,
            ALARM_PIP_RANGE = ALARM_MAX_PIP - ALARM_MIN_PIP,
            ALARM_PEEP_RANGE = ALARM_MAX_PEEP - ALARM_MIN_PEEP,
            ALARM_VT_RANGE = ALARM_MAX_VT - ALARM_MIN_VT,
            ALARM_MVOL_RANGE = (int)((ALARM_MAX_MVOL - ALARM_MIN_MVOL) * 10),
            ALARM_RRATE_RANGE = ALARM_MAX_RRATE - ALARM_MIN_RRATE;

    static final ArrayList<Integer> ALARM_MIN_MAX_LIST = new ArrayList<Integer>() {
        {
            add(ALARM_MIN_PRESSURE); add(ALARM_MAX_PRESSURE);
            add(ALARM_MIN_PIP); add(ALARM_MAX_PIP);
            add(ALARM_MIN_PEEP); add(ALARM_MAX_PEEP);
            add(ALARM_MIN_VT); add(ALARM_MAX_VT);
            add(0); add(0);
            add(ALARM_MIN_RRATE); add(ALARM_MAX_RRATE);
        }
    };

    static final int
            DEFAULT_ALARM_PRESSURE_PROGRESS = 30,
            DEFAULT_ALARM_HI_PIP_PROGRESS = 29, DEFAULT_ALARM_LO_PIP_PROGRESS = 17,
            DEFAULT_ALARM_HI_PEEP_PROGRESS = 18, DEFAULT_ALARM_LO_PEEP_PROGRESS = 2,
            DEFAULT_ALARM_HI_VT_PROGRESS = 500, DEFAULT_ALARM_LO_VT_PROGRESS = 300,
            DEFAULT_ALARM_HI_MVOL_PROGRESS = 80, DEFAULT_ALARM_LO_MVOL_PROGRESS = 25,
            DEFAULT_ALARM_HI_RRATE_PROGRESS = 10, DEFAULT_ALARM_LO_RRATE_PROGRESS = 0;

    static final String
            MIN_PIP_TEXT = String.valueOf(MIN_PIP) , MAX_PIP_TEXT = String.valueOf(MAX_PIP),
            MIN_VT_TEXT = String.valueOf(MIN_VT), MAX_VT_TEXT = String.valueOf(MAX_VT),
            MIN_PSUPPORT_TEXT = String.valueOf(MIN_PSUPPORT), MAX_PSUPPORT_TEXT = String.valueOf(MAX_PSUPPORT),
            MIN_PEEP_TEXT = String.valueOf(MIN_PEEP), MAX_PEEP_TEXT = String.valueOf(MAX_PEEP),
            MIN_RRATE_TEXT = String.valueOf(MIN_RRATE), MAX_RRATE_TEXT = String.valueOf(MAX_RRATE),
            MIN_BACKUPRATE_TEXT = String.valueOf(MIN_BACKUPRATE), MAX_BACKUPRATE_TEXT = String.valueOf(MAX_BACKUPRATE),
            MIN_ITIME_TEXT = String.valueOf(MIN_ITIME / 10), MAX_ITIME_TEXT = String.valueOf(MAX_ITIME / 10),
            MIN_IPAUSE_TEXT = String.valueOf(MIN_IPAUSE), MAX_IPAUSE_TEXT = String.valueOf(MAX_IPAUSE),
            MIN_FIO2_TEXT = String.valueOf(MIN_FIO2), MAX_FIO2_TEXT = String.valueOf(MAX_FIO2),
            MIN_PTRIGGER_TEXT = String.valueOf(MIN_PTRIGGER), MAX_PTRIGGER_TEXT = String.valueOf(MAX_PTRIGGER);

    static final int VENDOR_ID = 0x0403, PRODUCT_ID = 0x6001, BAUD_RATE = 115200;
}
