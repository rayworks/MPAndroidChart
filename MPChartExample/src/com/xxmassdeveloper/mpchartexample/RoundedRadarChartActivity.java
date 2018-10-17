package com.xxmassdeveloper.mpchartexample;

import android.os.Bundle;

public class RoundedRadarChartActivity extends RadarChartActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // disable the further touch events
        mChart.setTouchEnabled(false);
    }

    @Override
    protected int getRadarChartLayoutResource() {
        return R.layout.activity_roundedradarchart_noseekbar;
    }

    @Override
    public void setData() {
        super.setData();
    }
}
