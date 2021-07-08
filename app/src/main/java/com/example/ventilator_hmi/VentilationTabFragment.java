package com.example.ventilator_hmi;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class VentilationTabFragment extends Fragment {

    CustomViewModel model;
    View rootView;
    TabLayout tabLayout;
    Button button1, peepButton, button3, iTimeButton, iPauseButton, fio2Button, pTriggerButton, plusButton, minusButton;
    TextView
            bt1_typeTextView, bt1_unitTextView,
            bt3_typeTextView, button3_unitTextView,
            bt1_valueTextView, peepValueTextView, bt3_valueTextView, iTimeValueTextView, iPauseValueTextView, fio2ValueTextView, pTriggerValueTextView,
            rangeMinTextView, rangeMaxTextView;
    SeekBar seekBar;

    Resources resources;

    boolean changeProgress = false;

    public VentilationTabFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return rootView = inflater.inflate(R.layout.fragment_ventilation_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resources = getResources();

        button1 = rootView.findViewById(R.id.mode_pip_button);
        peepButton = rootView.findViewById(R.id.mode_peep_button);
        button3 = rootView.findViewById(R.id.mode_r_rate_button);
        iTimeButton = rootView.findViewById(R.id.mode_i_time_button);
        iPauseButton = rootView.findViewById(R.id.mode_i_pause_button);
        fio2Button = rootView.findViewById(R.id.mode_fio2_button);
        pTriggerButton = rootView.findViewById(R.id.mode_p_trigger_button);

        bt1_typeTextView = rootView.findViewById(R.id.mode_bt1_type_textView);
        bt1_unitTextView = rootView.findViewById(R.id.mode_bt1_unit_textView);
        bt3_typeTextView = rootView.findViewById(R.id.mode_bt3_type_textView);
        button3_unitTextView = rootView.findViewById(R.id.mode_bt3_unit_textView);

        bt1_valueTextView = rootView.findViewById(R.id.button1_val_textView);
        peepValueTextView = rootView.findViewById(R.id.peep_val_textView);
        bt3_valueTextView = rootView.findViewById(R.id.button3_val_textView);
        iTimeValueTextView = rootView.findViewById(R.id.iTime_val_textView);
        iPauseValueTextView = rootView.findViewById(R.id.iPause_val_textView);
        fio2ValueTextView = rootView.findViewById(R.id.fio2_val_textView);
        pTriggerValueTextView = rootView.findViewById(R.id.pTrigger_val_textView);
        seekBar = rootView.findViewById(R.id.seekBar);
        rangeMinTextView = rootView.findViewById(R.id.seekBar_min_textView);
        rangeMaxTextView = rootView.findViewById(R.id.seekBar_max_textView);
        plusButton = rootView.findViewById(R.id.plus_button);
        minusButton = rootView.findViewById(R.id.minus_button);

        tabLayout = rootView.findViewById(R.id.mode_tabLayout);
        seekBar.setMax(Constants.PIP_RANGE);
        seekBar.setProgress(model.getPip().getValue());

        bt1_valueTextView.setText(String.valueOf(model.getPip().getValue() + Constants.MIN_PIP));
        peepValueTextView.setText(String.valueOf(model.getPeep().getValue() + Constants.MIN_PEEP));
        bt3_valueTextView.setText(String.valueOf(model.getRRate().getValue() + Constants.MIN_RRATE));
        iTimeValueTextView.setText(String.valueOf((model.getITimeProgress().getValue() + Constants.MIN_ITIME) / 10.0));
        iPauseValueTextView.setText(String.valueOf(model.getIPause().getValue() + Constants.MIN_IPAUSE));
        fio2ValueTextView.setText(String.valueOf(model.getFio2().getValue() + Constants.MIN_FIO2));
        pTriggerValueTextView.setText(String.valueOf(model.getPTrigger().getValue() + Constants.MIN_PTRIGGER));

        button1.setOnClickListener(v -> {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(0);
        });
        peepButton.setOnClickListener(v ->  {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(1);
        });
        button3.setOnClickListener(v ->  {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(2);
        });
        iTimeButton.setOnClickListener(v ->  {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(3);
        });
        iPauseButton.setOnClickListener(v ->  {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(4);
        });
        fio2Button.setOnClickListener(v -> {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(5);
        });
        pTriggerButton.setOnClickListener(v -> {
            model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
            model.setVentTabSelectedValue(6);
        });

        ArrayList<Button> buttonList = new ArrayList<>();
        buttonList.add(button1);
        buttonList.add(peepButton);
        buttonList.add(button3);
        buttonList.add(iTimeButton);
        buttonList.add(iPauseButton);
        buttonList.add(fio2Button);
        buttonList.add(pTriggerButton);

        model.getTempVentMode().observe(requireActivity(), tempVentMode -> {
            int selectedValue = model.getVentTabSelectedValue().getValue();
            if (tempVentMode == 0) {
                bt1_typeTextView.setText(R.string.pip);
                bt1_unitTextView.setText(R.string.cmh20);
                bt1_valueTextView.setText(String.valueOf(model.getTempPip().getValue() + Constants.MIN_PIP));
                bt3_typeTextView.setText(R.string.r_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempRRate().getValue() + Constants.MIN_RRATE));
                if (selectedValue == 0) {
                    seekBar.setMax(Constants.PIP_RANGE);
                    seekBar.setProgress(model.getTempPip().getValue());
                    rangeMinTextView.setText(Constants.MIN_PIP_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_PIP_TEXT);
                } else if (selectedValue == 2) {
                    seekBar.setMax(Constants.RRATE_RANGE);
                    seekBar.setProgress(model.getTempRRate().getValue());
                    rangeMinTextView.setText(Constants.MIN_RRATE_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_RRATE_TEXT);
                }
            } else if (tempVentMode == 1) {
                bt1_typeTextView.setText(R.string.vt);
                bt1_unitTextView.setText(R.string.ml);
                bt1_valueTextView.setText(String.valueOf(model.getTempVt().getValue() + Constants.MIN_VT));
                bt3_typeTextView.setText(R.string.r_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempRRate().getValue() + Constants.MIN_RRATE));
                if (selectedValue == 0) {
                    seekBar.setMax(Constants.VT_RANGE);
                    seekBar.setProgress(model.getTempVt().getValue());
                    rangeMinTextView.setText(Constants.MIN_VT_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_VT_TEXT);
                } else if (selectedValue == 2) {
                    seekBar.setMax(Constants.RRATE_RANGE);
                    seekBar.setProgress(model.getTempRRate().getValue());
                    rangeMinTextView.setText(Constants.MIN_RRATE_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_RRATE_TEXT);
                }
            } else if (tempVentMode == 2) {
                bt1_typeTextView.setText(R.string.p_support);
                bt1_unitTextView.setText(R.string.cmh20);
                bt1_valueTextView.setText(String.valueOf(model.getTempPSupport().getValue() + Constants.MIN_PSUPPORT));
                bt3_typeTextView.setText(R.string.backup_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempBackupRate().getValue() + Constants.MIN_BACKUPRATE));
                if (selectedValue == 0) {
                    seekBar.setMax(Constants.PSUPPORT_RANGE);
                    seekBar.setProgress(model.getTempPSupport().getValue());
                    rangeMinTextView.setText(Constants.MIN_PSUPPORT_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_PSUPPORT_TEXT);
                } else if (selectedValue == 2) {
                    seekBar.setMax(Constants.BACKUP_RANGE);
                    seekBar.setProgress(model.getTempBackupRate().getValue());
                    rangeMinTextView.setText(Constants.MIN_BACKUPRATE_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_BACKUPRATE_TEXT);
                }
            }
        });

        model.getVentTabSelectedValue().observe(requireActivity(), selectedValue -> {
            buttonList.get(selectedValue).setBackgroundColor(resources.getColor(R.color.orange));
            if (selectedValue == 0) {
                int ventMode = model.getTempVentMode().getValue();
                if (ventMode == 0) {
                    seekBar.setMax(Constants.PIP_RANGE);
                    seekBar.setProgress(model.getTempPip().getValue());
                    rangeMinTextView.setText(Constants.MIN_PIP_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_PIP_TEXT);
                } else if (ventMode == 1) {
                    seekBar.setMax(Constants.VT_RANGE);
                    seekBar.setProgress(model.getTempVt().getValue());
                    rangeMinTextView.setText(Constants.MIN_VT_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_VT_TEXT);
                } else if (ventMode == 2) {
                    seekBar.setMax(Constants.PSUPPORT_RANGE);
                    seekBar.setProgress(model.getTempPSupport().getValue());
                    rangeMinTextView.setText(Constants.MIN_PSUPPORT_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_PSUPPORT_TEXT);
                }
            } else if (selectedValue == 1) {
                seekBar.setMax(Constants.PEEP_RANGE);
                seekBar.setProgress(model.getTempPeep().getValue());
                rangeMinTextView.setText(Constants.MIN_PEEP_TEXT);
                rangeMaxTextView.setText(Constants.MAX_PEEP_TEXT);
            } else if (selectedValue == 2) {
                int ventMode = model.getTempVentMode().getValue();
                if (ventMode == 2) {
                    seekBar.setMax(Constants.BACKUP_RANGE);
                    seekBar.setProgress(model.getTempBackupRate().getValue());
                    rangeMinTextView.setText(Constants.MIN_BACKUPRATE_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_BACKUPRATE_TEXT);
                } else {
                    seekBar.setMax(Constants.RRATE_RANGE);
                    seekBar.setProgress(model.getTempRRate().getValue());
                    rangeMinTextView.setText(Constants.MIN_RRATE_TEXT);
                    rangeMaxTextView.setText(Constants.MAX_RRATE_TEXT);
                }
            } else if (selectedValue == 3) {
                seekBar.setMax(Constants.ITIME_RANGE);
                seekBar.setProgress(model.getTempITimeProgress().getValue());
                rangeMinTextView.setText(Constants.MIN_ITIME_TEXT);
                rangeMaxTextView.setText(Constants.MAX_ITIME_TEXT);
            } else if (selectedValue == 4) {
                seekBar.setMax(Constants.IPAUSE_RANGE);
                seekBar.setProgress(model.getTempIPause().getValue());
                rangeMinTextView.setText(Constants.MIN_IPAUSE_TEXT);
                rangeMaxTextView.setText(Constants.MAX_IPAUSE_TEXT);
            } else if (selectedValue == 5) {
                seekBar.setMax(Constants.FIO2_RANGE);
                seekBar.setProgress(model.getTempFio2().getValue());
                rangeMinTextView.setText(Constants.MIN_FIO2_TEXT);
                rangeMaxTextView.setText(Constants.MAX_FIO2_TEXT);
            } else if (selectedValue == 6) {
                seekBar.setMax(Constants.PTRIGGER_RANGE);
                seekBar.setProgress(model.getTempPTrigger().getValue());
                rangeMinTextView.setText(Constants.MIN_PTRIGGER_TEXT);
                rangeMaxTextView.setText(Constants.MAX_PTRIGGER_TEXT);
            }
        });
        model.getVentTabPrevSelectedValue().observe(requireActivity(), prevSelectedValue -> {
            buttonList.get(prevSelectedValue).setBackgroundColor(resources.getColor(R.color.design_default_color_primary));
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                model.getVentTabSelectedValue().observe(requireActivity(), selectedValue -> {
                    if (changeProgress) {
                        if (selectedValue == 0) {
                            int ventMode = model.getTempVentMode().getValue();
                            if (ventMode == 0) {
                                model.setTempPip(progress);
                                bt1_valueTextView.setText(String.valueOf(progress + Constants.MIN_PIP));
                            } else if (ventMode == 1) {
                                model.setTempVt(progress);
                                bt1_valueTextView.setText(String.valueOf(progress + Constants.MIN_VT));
                            } else if (ventMode == 2) {
                                model.setTempPSupport(progress);
                                bt1_valueTextView.setText(String.valueOf(progress + Constants.MIN_PSUPPORT));
                            }
                        } else if (selectedValue == 1) {
                            model.setTempPeep(progress);
                            peepValueTextView.setText(String.valueOf(progress + Constants.MIN_PEEP));
                        } else if (selectedValue == 2) {
                            int ventMode = model.getTempVentMode().getValue();
                            if (ventMode == 2) {
                                model.setTempBackupRate(progress);
                                bt3_valueTextView.setText(String.valueOf(progress + Constants.MIN_BACKUPRATE));
                            } else {
                                model.setTempRRate(progress);
                                bt3_valueTextView.setText(String.valueOf(progress + Constants.MIN_RRATE));
                            }
                        } else if (selectedValue == 3) {
                            model.setTempITime(progress);
                            iTimeValueTextView.setText(String.valueOf(((double) progress + Constants.MIN_ITIME) / 10f));
                        } else if (selectedValue == 4) {
                            model.setTempIPause(progress);
                            iPauseValueTextView.setText(String.valueOf(progress + Constants.MIN_IPAUSE));
                        } else if (selectedValue == 5) {
                            model.setTempFio2(progress);
                            fio2ValueTextView.setText(String.valueOf(progress + Constants.MIN_FIO2));
                        } else if (selectedValue == 6) {
                            model.setTempPTrigger(progress);
                            pTriggerValueTextView.setText(String.valueOf(progress + Constants.MIN_PTRIGGER));
                        }
                    }
                });
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { changeProgress = false; }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { changeProgress = true; }
        });

        plusButton.setOnClickListener(v -> {
            changeProgress = true;
            seekBar.setProgress(seekBar.getProgress() + 1);
            changeProgress = false;
        });
        minusButton.setOnClickListener(v -> {
            changeProgress = true;
            seekBar.setProgress(seekBar.getProgress() - 1);
            changeProgress = false;
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { model.setTempVentMode(tab.getPosition()); }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        model.getTempVentMode().observe(requireActivity(), tempVentMode -> {
            int selectedValue = model.getVentTabSelectedValue().getValue();
            if (selectedValue == 0) {
                seekBar.setMax(tempVentMode == 0 ? Constants.PIP_RANGE : tempVentMode == 1 ? Constants.VT_RANGE : Constants.PSUPPORT_RANGE);
                seekBar.setProgress(
                        tempVentMode == 0 ? model.getTempPip().getValue()
                                : tempVentMode == 1 ? model.getTempVt().getValue()
                                : model.getTempPSupport().getValue());
            } else if (selectedValue == 2) {
                seekBar.setMax(tempVentMode == 2 ? Constants.BACKUP_RANGE : Constants.RRATE_RANGE);
                seekBar.setProgress(tempVentMode == 2 ? model.getTempBackupRate().getValue() : model.getTempRRate().getValue());
            }

            if (tempVentMode == 0) {
                bt1_typeTextView.setText(R.string.pip);
                bt1_unitTextView.setText(R.string.cmh20);
                bt1_valueTextView.setText(String.valueOf(model.getTempPip().getValue() + Constants.MIN_PIP));
                bt3_typeTextView.setText(R.string.r_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempRRate().getValue() + Constants.MIN_RRATE));
            } else if (tempVentMode == 1) {
                bt1_typeTextView.setText(R.string.vt);
                bt1_unitTextView.setText(R.string.ml);
                bt1_valueTextView.setText(String.valueOf(model.getTempVt().getValue() + Constants.MIN_VT));
                bt3_typeTextView.setText(R.string.r_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempRRate().getValue() + Constants.MIN_RRATE));
            } else if (tempVentMode == 2) {
                bt1_typeTextView.setText(R.string.p_support);
                bt1_unitTextView.setText(R.string.cmh20);
                bt1_valueTextView.setText(String.valueOf(model.getTempPSupport().getValue() + Constants.MIN_PSUPPORT));
                bt3_typeTextView.setText(R.string.backup_rate);
                bt3_valueTextView.setText(String.valueOf(model.getTempBackupRate().getValue() + Constants.MIN_BACKUPRATE));
            }
        });

        model.getResetDefault().observe(requireActivity(), resetDefault -> {
            if (resetDefault) {
                model.setVentTabPrevSelectedValue(model.getVentTabSelectedValue().getValue());
                model.setVentTabSelectedValue(0);
                tabLayout.selectTab(tabLayout.getTabAt(0));
                button1.setBackgroundColor(resources.getColor(R.color.orange));

                model.setResetDefault(false);
            }
        });

        model.getSaveChanges().observe(requireActivity(), saveChanges -> {
            if (saveChanges) {
                model.setVentMode(model.getTempVentMode().getValue());
                model.setPip(model.getTempPip().getValue());
                model.setVt(model.getTempVt().getValue());
                model.setPSupport(model.getTempPSupport().getValue());
                model.setPeep(model.getTempPeep().getValue());
                model.setRRate(model.getTempRRate().getValue());
                model.setBackupRate(model.getTempBackupRate().getValue());
                model.setITime((double) model.getTempITimeProgress().getValue() / 10f);
                model.setIPause(model.getTempIPause().getValue());
                model.setFio2(model.getTempFio2().getValue());
                model.setPTrigger(model.getTempPTrigger().getValue());
                model.setSaveChanges(false);
            }
        });
    }
}