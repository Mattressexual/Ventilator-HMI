package com.example.ventilator_hmi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class SettingsFragment extends Fragment {

    boolean debug;

    CustomViewModel model;
    FragmentManager fragmentManager;
    Fragment ventilationTab, alarmLimitsTab, waveformFragment;
    View rootView;
    Button startVentilationButton, cancelButton;
    TabLayout tabLayout;

    public SettingsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        debug = model.getDebug().getValue();

        fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ventilationTab = new VentilationTabFragment();
        alarmLimitsTab = new AlarmLimitsTabFragment();
        fragmentTransaction
                .add(R.id.settings_fragment_container_view, alarmLimitsTab, Constants.ALARM_LIMITS_TAB)
                .add(R.id.settings_fragment_container_view, ventilationTab, Constants.VENTILATION_TAB);

        int selectedTab = model.getTabSelected().getValue();
        fragmentTransaction
                .hide(selectedTab == 0 ? alarmLimitsTab : ventilationTab)
                .commit();

        tabLayout = rootView.findViewById(R.id.settings_tabLayout);
        model.getTabSelected().observe(requireActivity(), integer ->
                tabLayout.selectTab(tabLayout.getTabAt(integer)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction tabFragmentTransaction = fragmentManager.beginTransaction();
                if (tab.getPosition() == 0)
                    tabFragmentTransaction
                            .show(ventilationTab)
                            .hide(alarmLimitsTab);
                if (tab.getPosition() == 1)
                    tabFragmentTransaction
                            .show(alarmLimitsTab)
                            .hide(ventilationTab);
                tabFragmentTransaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        startVentilationButton = rootView.findViewById(R.id.settings_start_ventilation_button);
        model.getVentRunning().observe(requireActivity(), ventRunning -> {
            if (ventRunning)
                startVentilationButton.setText(R.string.apply);
            else
                startVentilationButton.setText(R.string.start_ventilation);
        });

        startVentilationButton.setOnClickListener(v -> {
            boolean connected = model.getUsbConnected().getValue();
            if (connected || debug) {

                if (model.getAlarmRangesOkay()) {
                    model.setTempReset(false);
                    model.setSaveChanges(true);
                    model.setVentRunning(true);
                    model.setFirstRun(false);

                    waveformFragment = fragmentManager.findFragmentByTag(Constants.WAVEFORM_FRAGMENT);

                    FragmentTransaction startVentFragmentTransaction = fragmentManager.beginTransaction();
                    if (waveformFragment == null) {
                        waveformFragment = new WaveformFragment();
                        startVentFragmentTransaction
                                .add(R.id.fragment_container_view, waveformFragment, Constants.WAVEFORM_FRAGMENT);
                    } else
                        startVentFragmentTransaction.show(waveformFragment);

                    startVentFragmentTransaction
                            .hide(this)
                            .addToBackStack(Constants.WAVEFORM_FRAGMENT)
                            .commit();
                } else
                    Toast.makeText(getActivity(), "Alarm Limit ranges invalid. High limit must be greater than Low limit.", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton = rootView.findViewById(R.id.settings_cancel_button);
        cancelButton.setOnClickListener(v -> {
            String previous = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 2).getName();
            model.setTempReset(true);

            if (!model.getVentRunning().getValue()) {
                model.setTempPip(Constants.DEFAULT_PIP_PROGRESS);
                model.setTempVt(Constants.DEFAULT_VT_PROGRESS);
                model.setTempPSupport(Constants.DEFAULT_PSUPPORT_PROGRESS);
                model.setTempPeep(Constants.DEFAULT_PEEP_PROGRESS);
                model.setTempRRate(Constants.DEFAULT_RRATE_PROGRESS);
                model.setTempBackupRate(Constants.DEFAULT_BACKUPRATE_PROGRESS);
                model.setTempITime(Constants.DEFAULT_ITIME_PROGRESS);
                model.setTempIPause(Constants.DEFAULT_IPAUSE_PROGRESS);
                model.setTempFio2(Constants.DEFAULT_FIO2_PROGRESS);
                model.setTempPTrigger(Constants.DEFAULT_PTRIGGER_PROGRESS);
            }

            fragmentManager.beginTransaction()
                    .show(fragmentManager.findFragmentByTag(previous))
                    .hide(this)
                    .addToBackStack(previous)
                    .commit();
        });
    }
}