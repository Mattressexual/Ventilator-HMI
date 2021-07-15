package com.example.ventilator_hmi;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class LogFragment extends Fragment {

    CustomViewModel model;

    ScrollView bufferScrollView;

    View rootView;
    LinearLayout alarmScroll;
    Button removeAlarmsButton, closeButton;
    TextView logTextView;

    ArrayList<AlarmRow> alarmRows;

    FragmentManager fragmentManager;

    public LogFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = getParentFragmentManager();

        bufferScrollView = rootView.findViewById(R.id.log_buffer_scrollView);
        alarmScroll = rootView.findViewById(R.id.alarm_scroll_linearLayout);

        removeAlarmsButton = rootView.findViewById(R.id.remove_active_alarms_button);
        closeButton = rootView.findViewById(R.id.close_button);
        logTextView = rootView.findViewById(R.id.log_scroll_textView);

        model.getReceiveBuffer().observe(requireActivity(), buffer -> {
            logTextView.append(buffer);
        });

        alarmRows = new ArrayList<>();
        model.getAlarm().observe(requireActivity(), alarm -> {
            AlarmRow alarmRow = new AlarmRow(alarm);
            alarmRows.add(alarmRow);
            alarmScroll.addView(alarmRow.view);
        });

        removeAlarmsButton.setOnClickListener(v -> {
            ArrayList<Alarm> clearList = new ArrayList<>();
            for (Alarm alarm : model.getAllAlarmList().getValue()) {
                if (alarm.isCleared())
                    clearList.add(alarm);
            }
            for (Alarm alarm : clearList) {
                model.removeAlarm(alarm);
            }

            alarmScroll.removeAllViews();
            for (Alarm alarm : model.getAllAlarmList().getValue()) {
                AlarmRow alarmRow = new AlarmRow(alarm);

                alarmRows.add(alarmRow);
                alarmScroll.addView(alarmRow.view);
            }
        });

        closeButton.setOnClickListener(v -> {
            Fragment previousFragment =
                    fragmentManager.findFragmentByTag(
                            fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName());
            fragmentManager.beginTransaction()
                    .show(previousFragment)
                    .hide(this)
                    .commit();
        });

        model.getRefreshLog().observe(requireActivity(), refresh -> {
            if (refresh) {
                alarmScroll.removeAllViews();
                alarmRows.clear();

                for (Alarm alarm : model.getAllAlarmList().getValue()) {
                    AlarmRow alarmRow = new AlarmRow(alarm);
                    alarmRows.add(alarmRow);
                    alarmScroll.addView(alarmRow.view);
                }
                model.setRefreshLog(false);
            }
        });
    }

    class AlarmRow {
        Alarm alarm;
        View view;
        TextView alarmTextView, priorityTextView, statusTextView, startTimeTextView, endTimeTextView;
        Button clearButton;

        AlarmRow (Alarm alarm) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.view = inflater.inflate(R.layout.log_alarm_row, null);
            this.alarm = alarm;

            alarmTextView = view.findViewById(R.id.log_alarm_textView);
            priorityTextView = view.findViewById(R.id.log_priority_textView);
            statusTextView = view.findViewById(R.id.log_status_textView);
            startTimeTextView = view.findViewById(R.id.log_startTime_textView);
            endTimeTextView = view.findViewById(R.id.log_endTime_textView);
            clearButton = view.findViewById(R.id.log_clear_button);

            alarmTextView.setText(alarm.getAlarmText());
            priorityTextView.setText(alarm.getPriority());
            statusTextView.setText(alarm.isCleared() ? R.string.cleared : R.string.active);
            startTimeTextView.setText(alarm.getStartTime().toString());
            if (alarm.getEndTime() != null)
                endTimeTextView.setText(alarm.getEndTime().toString());
            else
                endTimeTextView.setText(R.string.dash);
            clearButton.setEnabled(!alarm.isCleared());

            clearButton.setOnClickListener(clear -> {
                statusTextView.setText(R.string.cleared);
                alarm.setEndTime(new Date());
                endTimeTextView.setText(alarm.getEndTime().toString());
                clearButton.setEnabled(false);
                model.clearActiveAlarm(alarm);
                model.setRefreshAlarmBar(true);
            });
        }
    }
}