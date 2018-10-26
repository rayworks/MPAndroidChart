package com.github.mikephil.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import com.github.mikephil.charting.renderer.PentagonRadarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererRadarChartExt;

public class PentagonRadarChart extends RadarChart {
    public PentagonRadarChart(Context context) {
        this(context, null);
    }

    public PentagonRadarChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PentagonRadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        // reattach the custom render
        mRenderer = new PentagonRadarChartRenderer(this, mAnimator, mViewPortHandler);
        mXAxisRenderer = new XAxisRendererRadarChartExt(getViewPortHandler(), mXAxis, this);
    }
}
