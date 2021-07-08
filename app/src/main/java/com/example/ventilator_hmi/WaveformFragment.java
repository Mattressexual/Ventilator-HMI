package com.example.ventilator_hmi;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class WaveformFragment extends Fragment {

    View rootView;
    CustomViewModel model;
    Resources resources;

    LineChart upperChart, lowerChart;
    LineDataSet upperSet, lowerSet;
    ArrayList<Entry> upperValues, lowerValues;
    boolean upperFirstLoop = true, lowerFirstLoop = true;
    int chartInsertIndex = 74, chartLength = 100;
    int upperChartMax = 40, upperChartMin = 0;
    int lowerChartMax = 30, lowerChartMin = -30;

    TextView graphTitleTextView,
            peepTextView, pPlatTextView, mVolTextView, ieRatioTextView, pipTextView, vtiTextView, rRateTextView, fio2TextView;

    public WaveformFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(requireActivity()).get(CustomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_waveform, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resources = getResources();

        graphTitleTextView = rootView.findViewById(R.id.graph_title_textView);
        model.getVentMode().observe(requireActivity(), integer -> graphTitleTextView.setText(integer == 0 ? R.string.pressure_ac : integer == 1 ? R.string.volume_ac : R.string.pressure_support));

        peepTextView = rootView.findViewById(R.id.wave_peep_textView);
        pPlatTextView = rootView.findViewById(R.id.wave_pPlat_textView);
        mVolTextView = rootView.findViewById(R.id.wave_mVol_textView);
        ieRatioTextView = rootView.findViewById(R.id.wave_ieRatio_textView);
        pipTextView = rootView.findViewById(R.id.wave_pip_textView);
        vtiTextView = rootView.findViewById(R.id.wave_vti_textView);
        rRateTextView = rootView.findViewById(R.id.wave_rRate_textView);
        fio2TextView = rootView.findViewById(R.id.wave_fio2_textView);

        model.getPeep().observe(requireActivity(), integer -> peepTextView.setText(String.valueOf(integer + Constants.MIN_PEEP)));
        model.getPip().observe(requireActivity(), integer -> pipTextView.setText(String.valueOf(integer + Constants.MIN_PIP)));
        model.getVt().observe(requireActivity(), integer -> vtiTextView.setText(String.valueOf(integer + Constants.MIN_VT)));
        model.getRRate().observe(requireActivity(), integer -> rRateTextView.setText(String.valueOf(integer + Constants.MIN_RRATE)));
        model.getFio2().observe(requireActivity(), integer -> fio2TextView.setText(String.valueOf(integer + Constants.MIN_FIO2)));

        upperChart = rootView.findViewById(R.id.upper_chart);
        upperChart.getLegend().setEnabled(false);
        upperChart.getAxisRight().setEnabled(false);
        upperChart.getAxisLeft().setDrawZeroLine(true);
        upperChart.getXAxis().setDrawGridLines(false);
        upperChart.getXAxis().setDrawAxisLine(false);
        setYAxis(upperChart.getAxisLeft(), upperChartMin, upperChartMax, 3);
        if (upperFirstLoop) {
            upperValues = new ArrayList<>();
            firstLoop(upperChart, upperSet, upperValues);
            upperFirstLoop = false;
        }

        lowerChart = rootView.findViewById(R.id.lower_chart);
        lowerChart.getLegend().setEnabled(false);
        lowerChart.getXAxis().setDrawGridLines(false);
        lowerChart.getAxisLeft().setDrawZeroLine(true);
        lowerChart.getXAxis().setDrawAxisLine(false);
        lowerChart.getAxisRight().setEnabled(false);
        setYAxis(lowerChart.getAxisLeft(), lowerChartMin, lowerChartMax, 0);

        if (lowerFirstLoop) {
            lowerValues = new ArrayList<>();
            firstLoop(lowerChart, lowerSet, lowerValues);
            lowerFirstLoop = false;
        }

        model.getUpperChartValue().observe(requireActivity(), f -> {
            if (model.getVentRunning().getValue())
                updateChart(upperChart, upperSet, upperValues, model.getUpperChartValue().getValue());
        });

        model.getLowerChartValue().observe(requireActivity(), f -> {
            if (model.getVentRunning().getValue())
                updateChart(lowerChart, lowerSet, lowerValues, model.getLowerChartValue().getValue());
        });

        model.getVentRunning().observe(requireActivity(), ventRunning -> {
            if (!ventRunning) {
                upperValues.clear();
                lowerValues.clear();
                for (int i = 0; i < chartLength; i++) {
                    upperValues.add(new Entry(i, 0));
                    lowerValues.add(new Entry(i, 0));
                }
                upperSet = new LineDataSet(upperValues, "Pressure");
                upperSet.setColor(resources.getColor(R.color.white));
                upperSet.setDrawCircles(false);
                upperSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                ArrayList<ILineDataSet> upperDataSets = new ArrayList<>();
                upperDataSets.add(upperSet);
                LineData upperData = new LineData(upperDataSets);
                upperChart.setData(upperData);
                upperChart.invalidate();
                upperFirstLoop = false;

                lowerSet = new LineDataSet(lowerValues, "Flow");
                lowerSet.setColor(resources.getColor(R.color.white));
                lowerSet.setDrawCircles(false);
                lowerSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(lowerSet);
                LineData lowerData = new LineData(dataSets);
                lowerChart.setData(lowerData);
                lowerChart.invalidate();
                lowerFirstLoop = false;
            }
        });
    }

    void updateChart(LineChart chart, LineDataSet set, ArrayList<Entry> values, float newValue) {
        values.remove(0);
        for (int i = 0; i < chartInsertIndex; i++) {
            Entry entry = values.get(i);
            entry.setX(entry.getX() - 1);
        }
        values.add(chartInsertIndex, new Entry(chartInsertIndex, newValue));
        set = new LineDataSet(values, "");
        set.setColor(resources.getColor(R.color.white));
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    void setYAxis(YAxis yAxis, int min, int max, int labelCount) {
        yAxis.setDrawTopYLabelEntry(true);
        yAxis.setDrawLabels(true);
        yAxis.setLabelCount(labelCount, true);
        yAxis.setDrawAxisLine(false);
        yAxis.setAxisMinimum(min);
        yAxis.setAxisMaximum(max);
    }

    void firstLoop(LineChart chart, LineDataSet set, ArrayList<Entry> values) {
        for (int i = 0; i < 100; i++)
            values.add(new Entry(i, 0f));
        set = new LineDataSet(values, "Flow");
        set.setColor(resources.getColor(R.color.white));
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }
}