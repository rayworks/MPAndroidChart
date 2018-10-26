package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class PentagonRadarChartRenderer extends LineRadarRenderer {
    private final DashPathEffect dashPathEffect;
    protected RadarChart mChart;
    /**
     * paint for drawing the web
     */
    protected Paint mWebPaint;
    protected Paint mHighlightCirclePaint;
    protected Path mDrawDataSetSurfacePathBuffer = new Path();
    protected Path mDrawHighlightCirclePathBuffer = new Path();
    private boolean mDrawWebCenterOutLines = false;
    private Path path = new Path();
    private PointF point = new PointF();

    public PentagonRadarChartRenderer(RadarChart chart, ChartAnimator animator,
                                      ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        dashPathEffect = new DashPathEffect(new float[]{5, 10}, 0);

        mHighlightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        int mostEntries = radarData.getMaxEntryCountSet().getEntryCount();

        for (IRadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible()) {
                drawDataSet(c, set, mostEntries);
            }
        }
    }

    /**
     * Draws the RadarDataSet
     *
     * @param c
     * @param dataSet
     * @param mostEntries the entry count of the dataset with the most entries
     */
    protected void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);
        Path surface = mDrawDataSetSurfacePathBuffer;
        surface.reset();

        boolean hasMovedToPoint = false;

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            mRenderPaint.setColor(dataSet.getColor(j));

            RadarEntry e = dataSet.getEntryForIndex(j);

            Utils.getPosition(
                    center,
                    (e.getY() - mChart.getYChartMin()) * factor * phaseY,
                    sliceangle * j * phaseX + mChart.getRotationAngle(), pOut);

            if (Float.isNaN(pOut.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(pOut.x, pOut.y);
                hasMovedToPoint = true;
            } else {
                surface.lineTo(pOut.x, pOut.y);
            }

            // Next
            // draw the dots for each edge
            drawValueEdgeDots(c, pOut);
        }

        if (dataSet.getEntryCount() > mostEntries) {
            // if this is not the largest set, draw a line to the center before closing
            surface.lineTo(center.x, center.y);
        }

        surface.close();

        if (dataSet.isDrawFilledEnabled()) {

            final Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {

                drawFilledPath(c, surface, drawable);
            } else {

                if (mChart.isDrawGradientArea()) {
                    drawGradientFilledPath(c, surface, mChart.getFilledAreaStartColor(),
                            mChart.getFilledAreaEndColor(), 0, mChart.getHeight());
                } else {
                    drawFilledPath(c, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setStyle(Paint.Style.STROKE);

        // draw the line (only if filled is disabled or alpha is below 255)
        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
            c.drawPath(surface, mRenderPaint);

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    private void drawValueEdgeDots(Canvas c, MPPointF pOut) {
        int preColor = mWebPaint.getColor();
        mWebPaint.setColor(mChart.getEdgeValueCircleColor());
        c.drawCircle(pOut.x, pOut.y, mChart.getEdgeValueRadius(), mWebPaint);
        mWebPaint.setColor(preColor);
    }

    private void drawGradientFilledPath(Canvas c, Path filledPath, int fillColorBeg, int fillColorEnd,
                                        int width, int height) {

        // save
        Paint.Style previous = mRenderPaint.getStyle();
        int previousColor = mRenderPaint.getColor();

        // set
        mRenderPaint.setStyle(Paint.Style.FILL);
        //mRenderPaint.setColor(color);
        LinearGradient shader = new LinearGradient(0, 0, width, height, fillColorBeg, fillColorEnd,
                Shader.TileMode.CLAMP);
        mRenderPaint.setShader(shader);

        c.drawPath(filledPath, mRenderPaint);

        // restore
        mRenderPaint.setColor(previousColor);
        mRenderPaint.setStyle(previous);
    }

    @Override
    public void drawValues(Canvas c) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);
        MPPointF pIcon = MPPointF.getInstance(0, 0);

        float yoffset = Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IRadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!shouldDrawValues(dataSet))
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                RadarEntry entry = dataSet.getEntryForIndex(j);

                Utils.getPosition(
                        center,
                        (entry.getY() - mChart.getYChartMin()) * factor * phaseY,
                        sliceangle * j * phaseX + mChart.getRotationAngle(),
                        pOut);

                if (dataSet.isDrawValuesEnabled()) {
                    drawValue(c,
                            dataSet.getValueFormatter(),
                            entry.getY(),
                            entry,
                            i,
                            pOut.x,
                            pOut.y - yoffset,
                            dataSet.getValueTextColor
                                    (j));
                }

                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {

                    Drawable icon = entry.getIcon();

                    Utils.getPosition(
                            center,
                            (entry.getY()) * factor * phaseY + iconsOffset.y,
                            sliceangle * j * phaseX + mChart.getRotationAngle(),
                            pIcon);

                    //noinspection SuspiciousNameCombination
                    pIcon.y += iconsOffset.x;

                    Utils.drawImage(
                            c,
                            icon,
                            (int) pIcon.x,
                            (int) pIcon.y,
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }
            }

            MPPointF.recycleInstance(iconsOffset);
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
        MPPointF.recycleInstance(pIcon);
    }

    @Override
    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        MPPointF center = mChart.getCenterOffsets();

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        final int xIncrements = 1 + mChart.getSkipWebLineCount();
        int maxEntryCount = mChart.getData().getMaxEntryCountSet().getEntryCount();

        if (mDrawWebCenterOutLines) {
            MPPointF p = MPPointF.getInstance(0, 0);
            for (int i = 0; i < maxEntryCount; i += xIncrements) {

                Utils.getPosition(
                        center,
                        mChart.getYRange() * factor,
                        sliceangle * i + rotationangle,
                        p);

                c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
            }
            MPPointF.recycleInstance(p);
        }

        // draw the inner-web
        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        mWebPaint.setColor(mChart.getWebColorInner());
        mWebPaint.setAlpha(mChart.getWebAlpha());

        int labelCount = mChart.getYAxis().mEntryCount;

        // dash path effect
        mWebPaint.setPathEffect(dashPathEffect);

        float xLeft, yLeft;
        float xRight, yRight;

        int entryCount = mChart.getData().getEntryCount();

        MPPointF p1out = MPPointF.getInstance(0, 0);
        MPPointF p2out = MPPointF.getInstance(0, 0);
        for (int j = 0; j < labelCount; j++) {

            // skip the inner 3 layers
            if (j <= 2) {
                continue;
            }

            // normal path effect
            if (j == labelCount - 1) {
                mWebPaint.setPathEffect(null);
            }

            path.reset();

            int distance = mChart.getDistanceToEdgeCurve();

            for (int i = 0; i < entryCount; i++) {
                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;

                Utils.getPosition(center, r, sliceangle * i + rotationangle, p1out);
                Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle, p2out);

                point.x = p1out.getX();
                point.y = p1out.getY();

                double xDiff = Math.cos(Math.toRadians(36)) * distance;
                double yDiff = Math.sin(Math.toRadians(36)) * distance;

                // Draw the Pentagon :
                // calculate the closing points in pair
                // Left, Right coordinates defined by clockwise
                switch (i) {
                    case 0:
                        // draw a quadratic bezier
                        xLeft = (float) (point.x - xDiff);
                        yLeft = (float) (point.y + yDiff);

                        xRight = (float) (point.x + xDiff);
                        yRight = yLeft;

                        path.moveTo(xLeft, yLeft);
                        path.quadTo(point.x, point.y, xRight, yRight);
                        break;
                    case 1:
                        xLeft = (float) (point.x - xDiff);
                        yLeft = (float) (point.y - yDiff);

                        xRight = (float) (point.x - Math.cos(Math.toRadians(72)) * distance);
                        yRight = (float) (point.y + Math.sin(Math.toRadians(72)) * distance);

                        path.lineTo(xLeft, yLeft);
                        path.quadTo(point.x, point.y, xRight, yRight);

                        break;
                    case 2:
                        float tmp = (float) Math.cos(Math.toRadians(36)) * distance * 2;
                        xLeft = (float) (point.x + tmp * Math.cos(Math.toRadians(36)) - distance);
                        yLeft = (float) (point.y - tmp * Math.sin(Math.toRadians(36)));

                        xRight = point.x - distance;
                        yRight = point.y;

                        path.lineTo(xLeft, yLeft);
                        path.quadTo(point.x, point.y, xRight, yRight);

                        break;
                    case 3:
                        tmp = (float) Math.cos(Math.toRadians(36)) * distance * 2;
                        xRight = (float) (point.x - (tmp * Math.cos(Math.toRadians(36)) - distance));
                        yRight = (float) (point.y - tmp * Math.sin(Math.toRadians(36)));

                        xLeft = point.x + distance;
                        yLeft = point.y;

                        path.lineTo(xLeft, yLeft);
                        path.quadTo(point.x, point.y, xRight, yRight);

                        break;
                    case 4:
                        xLeft = (float) (point.x + Math.cos(Math.toRadians(72)) * distance);
                        yLeft = (float) (point.y + Math.sin(Math.toRadians(72)) * distance);

                        xRight = (float) (point.x + xDiff);
                        yRight = (float) (point.y - yDiff);

                        path.lineTo(xLeft, yLeft);
                        path.quadTo(point.x, point.y, xRight, yRight);
                        break;
                }
            }

            // closing the path
            path.close();

            c.drawPath(path, mWebPaint);

        }
        MPPointF.recycleInstance(p1out);
        MPPointF.recycleInstance(p2out);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        RadarData radarData = mChart.getData();

        for (Highlight high : indices) {

            IRadarDataSet set = radarData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            RadarEntry e = set.getEntryForIndex((int) high.getX());

            if (!isInBoundsX(e, set))
                continue;

            float y = (e.getY() - mChart.getYChartMin());

            Utils.getPosition(center,
                    y * factor * mAnimator.getPhaseY(),
                    sliceangle * high.getX() * mAnimator.getPhaseX() + mChart.getRotationAngle(),
                    pOut);

            high.setDraw(pOut.x, pOut.y);

            // draw the lines
            drawHighlightLines(c, pOut.x, pOut.y, set);

            if (set.isDrawHighlightCircleEnabled()) {

                if (!Float.isNaN(pOut.x) && !Float.isNaN(pOut.y)) {

                    int strokeColor = set.getHighlightCircleStrokeColor();
                    if (strokeColor == ColorTemplate.COLOR_NONE) {
                        strokeColor = set.getColor(0);
                    }

                    if (set.getHighlightCircleStrokeAlpha() < 255) {
                        strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.getHighlightCircleStrokeAlpha());
                    }

                    drawHighlightCircle(c,
                            pOut,
                            set.getHighlightCircleInnerRadius(),
                            set.getHighlightCircleOuterRadius(),
                            set.getHighlightCircleFillColor(),
                            strokeColor,
                            set.getHighlightCircleStrokeWidth());
                }
            }
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    public void drawHighlightCircle(Canvas c,
                                    MPPointF point,
                                    float innerRadius,
                                    float outerRadius,
                                    int fillColor,
                                    int strokeColor,
                                    float strokeWidth) {
        c.save();

        outerRadius = Utils.convertDpToPixel(outerRadius);
        innerRadius = Utils.convertDpToPixel(innerRadius);

        if (fillColor != ColorTemplate.COLOR_NONE) {
            Path p = mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.x, point.y, outerRadius, Path.Direction.CW);
            if (innerRadius > 0.f) {
                p.addCircle(point.x, point.y, innerRadius, Path.Direction.CCW);
            }
            mHighlightCirclePaint.setColor(fillColor);
            mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, mHighlightCirclePaint);
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            mHighlightCirclePaint.setColor(strokeColor);
            mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius, mHighlightCirclePaint);
        }

        c.restore();
    }
}
