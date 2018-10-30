package com.xxmassdeveloper.mpchartexample;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PentagonRadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

public class RoundedRadarChartActivity extends FragmentActivity {

    private int[] resDrawables = new int[]{
            R.drawable.ic_vocabulary_outlines,
            R.drawable.ic_speaking_outlines,
            R.drawable.ic_grammar_outlines,
            R.drawable.ic_expressions_outlines,
            R.drawable.ic_smile};

    private PentagonRadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getRadarChartLayoutResource());

        TextView tv = findViewById(R.id.textView);
        tv.setTextColor(Color.BLACK);

        mChart = findViewById(R.id.radar_chart);

        initializeChart(new float[]{5, 4, 3, 3, 3});
    }

    private void initializeChart(float[] points) {
        if (points == null || points.length != 5)
            throw new IllegalArgumentException("The points param MUST contain 5 elements");

        int startColor = Color.argb(0xE6, 0, 0xBE, 0xFF);
        int endColor = Color.argb(0xE6, 0x14, 0x8C, 0xDC);

        // map the point to the actual capacity value
        float[] capacities = new float[points.length];
        for (int i = 0; i < points.length; i++) {
            float v = points[i];
            if (Float.compare(3.0f, v) >= 0) {
                capacities[i] = 30f;
            } else if (Float.compare(4.0f, v) >= 0) {
                capacities[i] = 60f;
            } else {
                capacities[i] = 90f;
            }
        }
        setData(capacities);

        mChart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            // Texts as the placeholders for displaying icons
            private String[] mActivities = new String[]{"Burger", "Steak", "Salad", "Pasta", "Pizza"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });

        // reset the Label number for "3, 4, 5" points only
        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(3, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Resources resources = getResources();
        // disable the further touch events
        Drawable[] drawables = new Drawable[resDrawables.length];
        for (int i = 0; i < resDrawables.length; i++) {
            drawables[i] = resources.getDrawable(resDrawables[i]);
        }

        mChart.setBackgroundColor(Color.WHITE);
        mChart.setWebColor(Color.BLACK);
        mChart.setWebColorInner(Color.BLACK);
        mChart.setWebAlpha(0xFF);
        mChart.setWebLineWidth(1f);
        mChart.setWebLineWidthInner(1f);

        mChart.setTouchEnabled(false);
        mChart.setEdgeDrawables(drawables)
                .setDrawEdgeIcon(true)
                .setEdgeIconDashLineColor(Color.argb(0x4D, 0, 0, 0))
                .setDrawGradientArea(true)
                .setFilledAreaStartColor(startColor)
                .setFilledAreaEndColor(endColor)
                .setEdgeValueCircleColor(Color.BLACK)
                .setEdgeValueRadius(resources.getDimensionPixelSize(R.dimen.value_dot_radius))
                .setDrawLayerNumber(true)
                .setDistanceToEdgeCurve(20)
                .setNumberTextColor(Color.BLACK)
                .setNumberPixelSize(resources.getDimensionPixelSize(R.dimen.number_font_size))
                .setNumberVerticalOffset((int) resources.getDimension(R.dimen.offset_vertical));
    }

    private int getRadarChartLayoutResource() {
        return R.layout.activity_roundedradarchart_noseekbar;
    }

    private void setData(float[] capacities) {
        // mapped data set from {100.f, 80.f, 60.f, 60.f, 60.f}
        // float[] capacities = new float[]{90, 60, 30, 30, 30};
        int cnt = capacities.length;

        ArrayList<RadarEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their
        // position around the center of the chart.
        for (int i = 0; i < cnt; i++) {
            entries.add(new RadarEntry(capacities[i]));
        }

        RadarDataSet set = new RadarDataSet(entries, "");

        int color = Color.rgb(121, 162, 175);
        int fillColor = Color.rgb(121, 162, 175);
        set.setColor(color);
        set.setFillColor(fillColor);
        set.setDrawFilled(true);
        set.setFillAlpha(180);
        set.setLineWidth(2f);
        set.setDrawHighlightCircleEnabled(true);
        set.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set);

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.BLACK);

        mChart.setData(data);

        // hide the label
        mChart.getLegend().setEnabled(false);

        // hide the description
        mChart.setDescription(null);

        mChart.invalidate();
    }
}
