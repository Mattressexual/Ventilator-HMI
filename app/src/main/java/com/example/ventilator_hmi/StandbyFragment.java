package com.example.ventilator_hmi;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class StandbyFragment extends Fragment {

    CustomViewModel model;

    FragmentManager fragmentManager;
    Fragment settingsFragment;

    View rootView;
    Button newPatientButton, samePatientButton, shutDownButton;
    TextView dateTextView;

    public StandbyFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.fragment_standby, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        model.getFirstRun().observe(requireActivity(), bool -> {
            if (!bool)
                samePatientButton.setEnabled(true);
        });

        fragmentManager = getParentFragmentManager();

        newPatientButton = rootView.findViewById(R.id.new_patient_button);
        samePatientButton = rootView.findViewById(R.id.same_patient_button);
        shutDownButton = rootView.findViewById(R.id.shut_down_button);

        dateTextView = rootView.findViewById(R.id.system_check_date_textView);
        dateTextView.setText("");

        newPatientButton.setOnClickListener(v -> {
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            if (model.getUsbConnected().getValue() || model.getDebug().getValue()) {
                model.setTabSelected(0);
                String current = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

                model.setResetDefault(true);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (!fragment.getTag().equals(Constants.VENTILATION_TAB)
                            && !fragment.getTag().equals(Constants.ALARM_LIMITS_TAB))
                        fragmentTransaction.hide(fragment);
                }
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
            else
                Toast.makeText(getActivity(), "No device", Toast.LENGTH_SHORT).show();
        });

        samePatientButton.setOnClickListener(v -> {
            // TODO: Send vent -> true command
            if (!model.getUsbConnected().getValue())
                model.setAttemptUsbConnect(true);

            if (model.getUsbConnected().getValue()) {
                model.setVentRunning(true);

                fragmentManager.beginTransaction()
                        .hide(this)
                        .show(fragmentManager.findFragmentByTag(Constants.WAVEFORM_FRAGMENT))
                        .addToBackStack(Constants.WAVEFORM_FRAGMENT)
                        .commit();
            }
            else
                Toast.makeText(getActivity(), "No device", Toast.LENGTH_SHORT).show();
        });
        if (savedInstanceState == null) { samePatientButton.setEnabled(false); }
        model.getFirstRun().observe(requireActivity(), firstRun -> {
            if (!firstRun)
                samePatientButton.setEnabled(true);
        });

        shutDownButton.setOnClickListener(v -> {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popup_shutdown, null);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            Button cancelShutdownButton = popupView.findViewById(R.id.cancel_shutdown_button);
            Button confirmShutdownButton = popupView.findViewById(R.id.confirm_shutdown_button);

            cancelShutdownButton.setOnClickListener(cancel -> popupWindow.dismiss());
            confirmShutdownButton.setOnClickListener(confirm -> {
                model.setShutdown(true);
                popupWindow.dismiss();
            });
        });
    }
}
