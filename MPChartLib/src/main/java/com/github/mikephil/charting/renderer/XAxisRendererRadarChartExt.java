package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class XAxisRendererRadarChartExt extends XAxisRendererRadarChart {
    private final PointF point;
    private Path path;
    private Paint paintDash;


    public XAxisRendererRadarChartExt(ViewPortHandler viewPortHandler, XAxis xAxis, RadarChart chart) {
        super(viewPortHandler, xAxis, chart);

        paintDash = new Paint();
        paintDash.setAntiAlias(true);
        paintDash.setStyle(Paint.Style.STROKE);

        paintDash.setPathEffect(new DashPathEffect(new float[]{5, 10}, 0));

        point = new PointF();
    }

    @Override
    public void renderAxisLabels(Canvas c) {
        // super.renderAxisLabels(c);
        if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled())
            return;

        final float labelRotationAngleDegrees = mXAxis.getLabelRotationAngle();
        final MPPointF drawLabelAnchor = MPPointF.getInstance(0.5f, 0.25f);

        mAxisLabelPaint.setTypeface(mXAxis.getTypeface());
        mAxisLabelPaint.setTextSize(mXAxis.getTextSize());
        mAxisLabelPaint.setColor(mXAxis.getTextColor());

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        float xLeft, yLeft;
        float xRight, yRight;
        float startX = .0f, startY = .0f;
        int distance = mChart.getDistanceToEdgeCurve();

        Drawable[] drawables = mChart.getEdgeDrawables();
        for (int i = 0; i < mChart.getData().getMaxEntryCountSet().getEntryCount(); i++) {

            String label = mXAxis.getValueFormatter().getFormattedValue(i, mXAxis);

            float angle = (sliceangle * i + mChart.getRotationAngle()) % 360f;

            Utils.getPosition(center, mChart.getYRange() * factor
                    + mXAxis.mLabelRotatedWidth / 2f, angle, pOut);

            // TODO: remove the duplicate logic as we have it in "PentagonRadarChartRenderer"
            if (mChart.isDrawEdgeIcon() && drawables != null && drawables.length > i) {
                paintDash.setColor(mChart.getEdgeIconDashLineColor());

                if (path == null) {
                    path = new Path();
                }

                Drawable drawable = drawables[i];
                int intrinsicWidth = drawable.getIntrinsicWidth();
                int intrinsicHeight = drawable.getIntrinsicHeight();

                Utils.drawImage(c, drawable, (int) pOut.x, (int) pOut.y,
                        intrinsicWidth, intrinsicHeight);


                point.x = pOut.getX();
                point.y = pOut.getY();

                double xDiff = Math.cos(Math.toRadians(36)) * distance;
                double yDiff = Math.sin(Math.toRadians(36)) * distance;

                // icons are concatenate with a dash line
                switch (i) {
                    case 0:
                        startX = xLeft = (float) (point.x - xDiff);
                        startY = yLeft = (float) (point.y + yDiff);

                        xRight = (float) (point.x + xDiff);
                        yRight = yLeft;

                        path.moveTo(xRight, yRight);
                        break;
                    case 1:
                        xLeft = (float) (point.x - xDiff);
                        yLeft = (float) (point.y - yDiff);

                        xRight = (float) (point.x - Math.cos(Math.toRadians(72)) * distance);
                        yRight = (float) (point.y + Math.sin(Math.toRadians(72)) * distance);

                        path.lineTo(xLeft, yLeft);
                        path.moveTo(xRight, yRight);
                        break;
                    case 2:
                        float tmp = (float) Math.cos(Math.toRadians(36)) * distance * 2;
                        xLeft = (float) (point.x + tmp * Math.cos(Math.toRadians(36)) - distance);
                        yLeft = (float) (point.y - tmp * Math.sin(Math.toRadians(36)));

                        xRight = point.x - distance;
                        yRight = point.y;

                        path.lineTo(xLeft, yLeft);
                        path.moveTo(xRight, yRight);
                        break;
                    case 3:
                        tmp = (float) Math.cos(Math.toRadians(36)) * distance * 2;
                        xRight = (float) (point.x - (tmp * Math.cos(Math.toRadians(36)) - distance));
                        yRight = (float) (point.y - tmp * Math.sin(Math.toRadians(36)));

                        xLeft = point.x + distance;
                        yLeft = point.y;

                        path.lineTo(xLeft, yLeft);
                        path.moveTo(xRight, yRight);
                        break;
                    case 4:
                        xLeft = (float) (point.x + Math.cos(Math.toRadians(72)) * distance);
                        yLeft = (float) (point.y + Math.sin(Math.toRadians(72)) * distance);

                        xRight = (float) (point.x + xDiff);
                        yRight = (float) (point.y - yDiff);

                        path.lineTo(xLeft, yLeft);

                        path.moveTo(xRight, yRight);
                        path.lineTo(startX, startY);

                        break;
                }
            } else {
                drawLabel(c, label, pOut.x, pOut.y - mXAxis.mLabelRotatedHeight / 2.f,
                        drawLabelAnchor, labelRotationAngleDegrees);
            }
        }

        if (path != null) {
            c.drawPath(path, paintDash);
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
        MPPointF.recycleInstance(drawLabelAnchor);
    }
}
