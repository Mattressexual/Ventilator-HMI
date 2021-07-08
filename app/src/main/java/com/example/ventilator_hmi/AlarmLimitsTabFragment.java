package com.example.ventilator_hmi;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmLimitsTabFragment extends Fragment {

    CustomViewModel model;
    Resources resources;
    View rootView;
    Button
            pressureUpperButton, pressureLowerButton,
            pipUpperButton, pipLowerButton,
            peepUpperButton, peepLowerButton,
            vtUpperButton, vtLowerButton,
            mVolUpperButton, mVolLowerButton,
            rRateUpperButton, rRateLowerButton,
            minusButton, plusButton;
    TextView
            pressureCurrentTextView, pipCurrentTextView, peepCurrentTextView, vtCurrentTextView, mVolCurrentTextView, rRateCurrentTextView,
            minTextView, maxTextView;
    SeekBar seekBar;

    public AlarmLimitsTabFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.fragment_alarm_limits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resources = getResources();

        pressureCurrentTextView = rootView.findViewById(R.id.pressure_current_textView);
        pipCurrentTextView = rootView.findViewById(R.id.pip_current_textView);
        peepCurrentTextView = rootView.findViewById(R.id.peep_current_textView);
        vtCurrentTextView = rootView.findViewById(R.id.vt_current_textView);
        mVolCurrentTextView = rootView.findViewById(R.id.mVol_current_textView);
        rRateCurrentTextView = rootView.findViewById(R.id.rRate_current_textView);

        pressureUpperButton = rootView.findViewById(R.id.pressure_upper_limit_button);
        pressureLowerButton = rootView.findViewById(R.id.pressure_lower_limit_button);
        pipUpperButton = rootView.findViewById(R.id.pip_upper_limit_button);
        pipLowerButton = rootView.findViewById(R.id.pip_lower_limit_button);
        peepUpperButton = rootView.findViewById(R.id.peep_upper_limit_button);
        peepLowerButton = rootView.findViewById(R.id.peep_lower_limit_button);
        vtUpperButton = rootView.findViewById(R.id.vt_upper_limit_button);
        vtLowerButton = rootView.findViewById(R.id.vt_lower_limit_button);
        mVolUpperButton = rootView.findViewById(R.id.mVol_upper_limit_button);
        mVolLowerButton = rootView.findViewById(R.id.mVol_lower_limit_button);
        rRateUpperButton = rootView.findViewById(R.id.rRate_upper_limit_button);
        rRateLowerButton = rootView.findViewById(R.id.rRate_lower_limit_button);

        seekBar = rootView.findViewById(R.id.seekBar);
        minTextView = rootView.findViewById(R.id.seekBar_min_textView);
        maxTextView = rootView.findViewById(R.id.seekBar_max_textView);
        minusButton = rootView.findViewById(R.id.minus_button);
        plusButton = rootView.findViewById(R.id.plus_button);

        ArrayList<Button> buttonList = new ArrayList<>();
        buttonList.add(pressureUpperButton);
        buttonList.add(pressureLowerButton);
        pressureLowerButton.setEnabled(false);
        buttonList.add(pipUpperButton);
        buttonList.add(pipLowerButton);
        buttonList.add(peepUpperButton);
        buttonList.add(peepLowerButton);
        buttonList.add(vtUpperButton);
        buttonList.add(vtLowerButton);
        buttonList.add(mVolUpperButton);
        buttonList.add(mVolLowerButton);
        buttonList.add(rRateUpperButton);
        buttonList.add(rRateLowerButton);

        pressureUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_PRESSURE_PROGRESS + Constants.ALARM_MIN_PRESSURE));
        pipUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_HI_PIP_PROGRESS + Constants.ALARM_MIN_PIP));
        pipLowerButton.setText(String.valueOf(Constants.DEFAULT_ALARM_LO_PIP_PROGRESS + Constants.ALARM_MIN_PIP));
        peepUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_HI_PEEP_PROGRESS + Constants.ALARM_MIN_PEEP));
        peepLowerButton.setText(String.valueOf(Constants.DEFAULT_ALARM_LO_PEEP_PROGRESS + Constants.ALARM_MIN_PEEP));
        vtUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_HI_VT_PROGRESS + Constants.ALARM_MIN_VT));
        vtLowerButton.setText(String.valueOf(Constants.DEFAULT_ALARM_LO_VT_PROGRESS + Constants.ALARM_MIN_VT));
        mVolUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_HI_MVOL_PROGRESS / 10f + Constants.ALARM_MIN_MVOL));
        mVolLowerButton.setText(String.valueOf(Constants.DEFAULT_ALARM_LO_MVOL_PROGRESS / 10f + Constants.ALARM_MIN_MVOL));
        rRateUpperButton.setText(String.valueOf(Constants.DEFAULT_ALARM_HI_RRATE_PROGRESS + Constants.ALARM_MIN_RRATE));
        rRateLowerButton.setText(String.valueOf(Constants.DEFAULT_ALARM_LO_RRATE_PROGRESS + Constants.ALARM_MIN_RRATE));

        pressureUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(0);
        });
        pressureLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(1);
        });
        pipUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(2);
        });
        pipLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(3);
        });
        peepUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(4);
        });
        peepLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(5);
        });
        vtUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(6);
        });
        vtLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(7);
        });
        mVolUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(8);
        });
        mVolLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(9);
        });
        rRateUpperButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(10);
        });
        rRateLowerButton.setOnClickListener(v -> {
            model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
            model.setAlarmTabSelectedValue(11);
        });

        ArrayList<Integer> minMaxList = Constants.ALARM_MIN_MAX_LIST;
        model.getAlarmTabSelectedValue().observe(requireActivity(), selectedValue -> {
            buttonList.get(selectedValue).setBackgroundColor(resources.getColor(R.color.orange));
            if (selectedValue == 0) {
                seekBar.setMax(Constants.ALARM_PRESSURE_RANGE);
                seekBar.setProgress(model.getTempHiPressure().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_PRESSURE));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_PRESSURE));
            } else if (selectedValue == 2 || selectedValue == 3) {
                seekBar.setMax(Constants.ALARM_PIP_RANGE);
                seekBar.setProgress(selectedValue == 2 ? model.getTempHiPip().getValue() : model.getTempLoPip().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_PIP));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_PIP));
            } else if (selectedValue == 4 || selectedValue == 5) {
                seekBar.setMax(Constants.ALARM_PEEP_RANGE);
                seekBar.setProgress(selectedValue == 4 ? model.getTempHiPeep().getValue() : model.getTempLoPeep().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_PEEP));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_PEEP));
            } else if (selectedValue == 6 || selectedValue == 7) {
                seekBar.setMax(Constants.ALARM_VT_RANGE);
                seekBar.setProgress(selectedValue == 6 ? model.getTempHiVt().getValue() : model.getTempLoVt().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_VT));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_VT));
            } else if (selectedValue == 8 || selectedValue == 9) {
                seekBar.setMax(Constants.ALARM_MVOL_RANGE);
                seekBar.setProgress(selectedValue == 8 ? model.getTempHiMVolProgress().getValue() : model.getTempLoMVolProgress().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_MVOL));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_MVOL));
            } else if (selectedValue == 10 || selectedValue == 11) {
                seekBar.setMax(Constants.ALARM_RRATE_RANGE);
                seekBar.setProgress(selectedValue == 10 ? model.getTempHiRRate().getValue() : model.getTempLoRRate().getValue());
                minTextView.setText(String.valueOf(Constants.ALARM_MIN_RRATE));
                maxTextView.setText(String.valueOf(Constants.ALARM_MAX_RRATE));
            }
        });
        model.getAlarmTabPrevSelectedValue().observe(requireActivity(), prevSelectedValue -> {
            buttonList.get(prevSelectedValue).setBackgroundColor(resources.getColor(R.color.design_default_color_primary));
        });
        model.getResetDefault().observe(requireActivity(), resetDefault -> {
            if (resetDefault) {
                model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
                model.setAlarmTabSelectedValue(0);
                pressureUpperButton.setBackgroundColor(resources.getColor(R.color.orange));
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                model.getAlarmTabSelectedValue().observe(requireActivity(), selectedValue -> {

                    if (selectedValue == 8 || selectedValue == 9) {
                        buttonList.get(selectedValue).setText(String.valueOf((double) progress / 10f + Constants.ALARM_MIN_MVOL));
                    }
                    else if (selectedValue % 2 == 0)
                        buttonList.get(selectedValue).setText(String.valueOf(progress + minMaxList.get(selectedValue)));
                    else
                        buttonList.get(selectedValue).setText(String.valueOf(progress + minMaxList.get(selectedValue - 1)));

                    if (selectedValue == 0)
                        model.setTempHiPressure(progress);
                    else if (selectedValue == 1)
                        model.setTempLoPressure(progress);
                    else if (selectedValue == 2)
                        model.setTempHiPip(progress);
                    else if (selectedValue == 3)
                        model.setTempLoPip(progress);
                    else if (selectedValue == 4)
                        model.setTempHiPeep(progress);
                    else if (selectedValue == 5)
                        model.setTempLoPeep(progress);
                    else if (selectedValue == 6)
                        model.setTempHiVt(progress);
                    else if (selectedValue == 7)
                        model.setTempLoVt(progress);
                    else if (selectedValue == 8)
                        model.setTempHiMVolProgress(progress);
                    else if (selectedValue == 9)
                        model.setTempLoMVolProgress(progress);
                    else if (selectedValue == 10)
                        model.setTempHiRRate(progress);
                    else if (selectedValue == 11)
                        model.setTempLoRRate(progress);
                });
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        plusButton.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress() + 1));
        minusButton.setOnClickListener(v -> seekBar.setProgress(seekBar.getProgress() - 1));

        model.getSaveChanges().observe(requireActivity(), saveChanges -> {
            if (saveChanges) {
                if (model.getAlarmRangesOkay()) {
                    model.setAlarmHiPressure(model.getTempHiPressure().getValue());
                    model.setAlarmHiPip(model.getTempHiPip().getValue());
                    model.setAlarmLoPip(model.getTempLoPip().getValue());
                    model.setAlarmHiPeep(model.getTempHiPeep().getValue());
                    model.setAlarmLoPeep(model.getTempLoPeep().getValue());
                    model.setAlarmHiVt(model.getTempHiVt().getValue());
                    model.setAlarmLoVt(model.getTempLoVt().getValue());
                    model.setAlarmHiMVolProgress(model.getTempHiMVolProgress().getValue());
                    model.setAlarmLoMVolProgress(model.getTempLoMVolProgress().getValue());
                    model.setAlarmHiMVol(model.getAlarmHiMVolProgress().getValue() / 10f);
                    model.setAlarmLoMVol(model.getAlarmLoMVolProgress().getValue() / 10f);
                    model.setAlarmHiRRate(model.getTempHiRRate().getValue());
                    model.setAlarmLoRRate(model.getTempLoRRate().getValue());
                }
            }
        });

        model.getTempReset().observe(requireActivity(), tempReset -> {
            if (tempReset) {
                model.setTempHiPressure(model.getAlarmHiPressure().getValue());
                model.setTempHiPip(model.getAlarmHiPip().getValue());
                model.setTempLoPip(model.getAlarmLoPip().getValue());
                model.setTempHiPeep(model.getAlarmHiPeep().getValue());
                model.setTempLoPeep(model.getAlarmLoPeep().getValue());
                model.setTempHiVt(model.getAlarmHiVt().getValue());
                model.setTempLoVt(model.getAlarmLoVt().getValue());
                model.setTempHiMVolProgress(model.getAlarmHiMVolProgress().getValue());
                model.setTempLoMVolProgress(model.getAlarmLoMVolProgress().getValue());
                model.setTempHiRRate(model.getAlarmHiRRate().getValue());
                model.setTempLoRRate(model.getTempLoRRate().getValue());

                model.setAlarmTabPrevSelectedValue(model.getAlarmTabSelectedValue().getValue());
                model.setAlarmTabSelectedValue(0);
            }
        });
    }
}