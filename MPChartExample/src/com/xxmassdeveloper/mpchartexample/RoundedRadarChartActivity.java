package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;

public class RoundedRadarChartActivity extends RadarChartActivity {

    private int[] resDrawables = new int[]{
            R.drawable.ic_vocabulary_outlines,
            R.drawable.ic_speaking_outlines,
            R.drawable.ic_grammar_outlines,
            R.drawable.ic_expressions_outlines,
            R.drawable.ic_smile};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int startColor = Color.argb(0xCF, 0, 0xBE, 0xFF);
        int endColor = Color.argb(0xCF, 0x14, 0x8C, 0xDC);

        // disable the further touch events
        Drawable[] drawables = new Drawable[resDrawables.length];
        for (int i = 0; i < resDrawables.length; i++) {
            drawables[i] = getResources().getDrawable(resDrawables[i]);

            // apply the tint color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawables[i].setTint(Color.WHITE);
            }
        }

        mChart.setTouchEnabled(false);
        mChart.setEdgeDrawables(drawables)
                .setDrawEdgeIcon(true)
                .setEdgeIconDashLineColor(Color.argb(0x4D, 0, 0, 0))
                .setDrawGradientArea(true)
                .setFilledAreaStartColor(startColor)
                .setFilledAreaEndColor(endColor)
                .setEdgeValueCircleColor(Color.WHITE)
                .setEdgeValueRadius(8);
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

        int color = Color.rgb(121, 162, 175);
        int fillColor = Color.rgb(121, 162, 175);
        set2.setColor(color);
        set2.setFillColor(fillColor);
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
