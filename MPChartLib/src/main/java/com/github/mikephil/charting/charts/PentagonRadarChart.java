package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.renderer.PentagonRadarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererRadarChartExt;

import java.util.List;

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

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        if (mData == null)
            return;

        // draw the value zone first
        mRenderer.drawData(canvas);

        if (mXAxis.isEnabled())
            mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);

        mXAxisRenderer.renderAxisLabels(canvas);

        if (mDrawWeb)
            mRenderer.drawExtras(canvas);

        if (mYAxis.isEnabled() && mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        if (mYAxis.isEnabled() && !mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        mYAxisRenderer.renderAxisLabels(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);

        // draw the value dots at last
        List<IRadarDataSet> dataSets = getData().getDataSets();
        for (IRadarDataSet set : dataSets) {
            if (set.isVisible()) {
                ((PentagonRadarChartRenderer) mRenderer).drawValueDots(canvas, set);
            }
        }

    }
}
