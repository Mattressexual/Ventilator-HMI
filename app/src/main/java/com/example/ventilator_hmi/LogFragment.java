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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class LogFragment extends Fragment {

    CustomViewModel model;

    View rootView;
    LinearLayout scrollLinearLayout;
    Button removeAlarmsButton, closeButton;
    TextView logTextView;

    ArrayList<AlarmRow> alarmRows = new ArrayList<>();

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

        scrollLinearLayout = rootView.findViewById(R.id.alarm_scroll_linearLayout);

        removeAlarmsButton = rootView.findViewById(R.id.remove_active_alarms_button);
        closeButton = rootView.findViewById(R.id.close_button);
        logTextView = rootView.findViewById(R.id.log_scroll_textView);

        removeAlarmsButton.setOnClickListener(v -> {

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
    }

    class AlarmRow {
        Alarm alarm;
        LinearLayout alarmRowLinearLayout;
        TextView alarmTextView, priorityTextView, statusTextView, startTimeTextView, endTimeTextView;
        Button clearButton;
        boolean active;

        AlarmRow (Alarm alarm, Context context) {
            this.alarm = alarm;
            active = true;

            alarmRowLinearLayout = new LinearLayout(context);
            alarmRowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            alarmRowLinearLayout.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

            alarmTextView = new TextView(context);
            priorityTextView = new TextView(context);
            statusTextView = new TextView(context);
            startTimeTextView = new TextView(context);
            endTimeTextView = new TextView(context);
            clearButton = new Button(context);

            alarmTextView.setText(alarm.getAlarmText());

            priorityTextView.setText(alarm.getPriority());
            startTimeTextView.setText(alarm.getStartTime() != null ? alarm.getStartTime().toString() : "null");
            endTimeTextView.setText(alarm.getEndTime() != null ? alarm.getEndTime().toString() : "null");

            alarmRowLinearLayout.addView(alarmTextView);
            alarmRowLinearLayout.addView(priorityTextView);
            alarmRowLinearLayout.addView(statusTextView);
            alarmRowLinearLayout.addView(startTimeTextView);
            alarmRowLinearLayout.addView(endTimeTextView);
            alarmRowLinearLayout.addView(clearButton);

            clearButton.setOnClickListener(v -> {
                clearButton.setEnabled(false);
                this.alarm.setCleared(true);
                model.setRefreshAlarmBar(true);
            });
        }
    }
}