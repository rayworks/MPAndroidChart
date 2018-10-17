package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

public class RoundedRadarChartActivity extends RadarChartActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // disable the further touch events
        mChart.setTouchEnabled(false);
        mChart.setDrawGradientArea(true)
                .setFilledAreaStartColor(Color.argb(128, 0, 0, 255))
                .setFilledAreaEndColor(Color.argb(128, 0, 255, 0));
    }

    @Override
    protected int getRadarChartLayoutResource() {
        return R.layout.activity_roundedradarchart_noseekbar;
    }

    @Override
    public void setData() {
        float mult = 80;
        float min = 20;
        int cnt = 5;

        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry(val2));
        }

        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        // hide the label
        mChart.setEdgeValueRadius(6);
        mChart.getLegend().setEnabled(false);

        mChart.invalidate();
    }
}
