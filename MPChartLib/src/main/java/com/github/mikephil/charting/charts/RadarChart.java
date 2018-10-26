package com.github.mikephil.charting.charts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.highlight.RadarHighlighter;
import com.github.mikephil.charting.renderer.RadarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererRadarChart;
import com.github.mikephil.charting.renderer.YAxisRendererRadarChart;
import com.github.mikephil.charting.utils.Utils;

/**
 * Implementation of the RadarChart, a "spidernet"-like chart. It works best
 * when displaying 5-10 entries per DataSet.
 *
 * @author Philipp Jahoda
 */
public class RadarChart extends PieRadarChartBase<RadarData> {

    protected YAxisRendererRadarChart mYAxisRenderer;
    protected XAxisRendererRadarChart mXAxisRenderer;
    protected Drawable[] mEdgeDrawables;
    /**
     * width of the main web lines
     */
    private float mWebLineWidth = 2.5f;
    /**
     * width of the inner web lines
     */
    private float mInnerWebLineWidth = 1.5f;
    /**
     * color for the main web lines
     */
    private int mWebColor = Color.rgb(122, 122, 122);
    /**
     * color for the inner web
     */
    private int mWebColorInner = Color.rgb(122, 122, 122);
    /**
     * transparency the grid is drawn with (0-255)
     */
    private int mWebAlpha = 150;
    /**
     * flag indicating if the web lines should be drawn or not
     */
    private boolean mDrawWeb = true;
    /**
     * modulus that determines how many labels and web-lines are skipped before the next is drawn
     */
    private int mSkipWebLineCount = 0;
    /**
     * the object reprsenting the y-axis labels
     */
    private YAxis mYAxis;
    /**
     * Start color for the data path area
     */
    private int mFilledAreaStartColor;
    /***
     * End color for the data path area
     */
    private int mFilledAreaEndColor;
    /***
     * flag indicating whether path area should be filled with gradient color
     */
    private boolean mDrawGradientArea;
    /***
     * The dot radius for each value
     */
    private int mEdgeValueCircleRadius = 8;
    /**
     * The circle color
     */
    private int mEdgeValueCircleColor = Color.WHITE;
    /**
     * Half of the curve length
     **/
    private int mDistanceToEdgeCurve = 48;
    private boolean mDrawEdgeIcon;
    private int mEdgeIconDashLineColor;

    private int mNumberTextColor = Color.BLACK;
    private float mNumberPixelSize = 16.0f;
    private int mNumberVerticalOffset = 16;
    private boolean mDrawLayerNumber = false;

    public RadarChart(Context context) {
        super(context);
    }

    public RadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /***
     * Gets the text color for layer number
     * @return
     */
    public int getNumberTextColor() {
        return mNumberTextColor;
    }

    /***
     * Sets the text color for layer number
     * @param numberTextColor
     * @return
     */
    public RadarChart setNumberTextColor(int numberTextColor) {
        this.mNumberTextColor = numberTextColor;
        return this;
    }

    /***
     * Whether to draw layer numbers
     * @return
     */
    public boolean isDrawLayerNumber() {
        return mDrawLayerNumber;
    }

    /***
     * Enables drawing layer numbers
     * @param drawLayerNumber
     * @return
     */
    public RadarChart setDrawLayerNumber(boolean drawLayerNumber) {
        this.mDrawLayerNumber = drawLayerNumber;
        return this;
    }

    /***
     * Gets the vertical offset for number
     * @return
     */
    public int getNumberVerticalOffset() {
        return mNumberVerticalOffset;
    }

    /***
     * Sets the vertical offset for number
     * @param numberVerticalOffset
     * @return
     */
    public RadarChart setNumberVerticalOffset(int numberVerticalOffset) {
        this.mNumberVerticalOffset = numberVerticalOffset;
        return this;
    }

    /***
     * Gets the font size of number text
     * @return
     */
    public float getNumberPixelSize() {
        return mNumberPixelSize;
    }

    /***
     * Sets the font size of number text
     * @param numberPixelSize
     * @return
     */
    public RadarChart setNumberPixelSize(float numberPixelSize) {
        this.mNumberPixelSize = numberPixelSize;
        return this;
    }

    public Drawable[] getEdgeDrawables() {
        return mEdgeDrawables;
    }

    /***
     * Sets the drawables for top edges for the whole chart.
     * @param drawables
     * @return
     */
    public RadarChart setEdgeDrawables(Drawable[] drawables) {
        this.mEdgeDrawables = drawables;
        return this;
    }

    /***
     * Whether to draw the edge icons
     * @return
     */
    public boolean isDrawEdgeIcon() {
        return mDrawEdgeIcon;
    }

    /***
     * Enables drawing the edge icons or not.
     * @param willDraw
     * @return
     */
    public RadarChart setDrawEdgeIcon(boolean willDraw) {
        this.mDrawEdgeIcon = willDraw;
        return this;
    }

    /***
     * Gets the color for dash line which join all the icons.
     * @return
     */
    public int getEdgeIconDashLineColor() {
        return mEdgeIconDashLineColor;
    }

    /***
     * Sets the color for dash line which join all the icons.
     * @param lineColor
     * @return
     */
    public RadarChart setEdgeIconDashLineColor(int lineColor) {
        this.mEdgeIconDashLineColor = lineColor;
        return this;
    }

    @Override
    protected void init() {
        super.init();

        mYAxis = new YAxis(AxisDependency.LEFT);

        mWebLineWidth = Utils.convertDpToPixel(1.5f);
        mInnerWebLineWidth = Utils.convertDpToPixel(0.75f);

        mRenderer = new RadarChartRenderer(this, mAnimator, mViewPortHandler);
        mYAxisRenderer = new YAxisRendererRadarChart(mViewPortHandler, mYAxis, this);
        mXAxisRenderer = new XAxisRendererRadarChart(mViewPortHandler, mXAxis, this);

        mHighlighter = new RadarHighlighter(this);
    }

    @Override
    protected void calcMinMax() {
        super.calcMinMax();

        mYAxis.calculate(mData.getYMin(AxisDependency.LEFT), mData.getYMax(AxisDependency.LEFT));
        mXAxis.calculate(0, mData.getMaxEntryCountSet().getEntryCount());
    }

    @Override
    public void notifyDataSetChanged() {
        if (mData == null)
            return;

        calcMinMax();

        mYAxisRenderer.computeAxis(mYAxis.mAxisMinimum, mYAxis.mAxisMaximum, mYAxis.isInverted());
        mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);

        if (mLegend != null && !mLegend.isLegendCustom())
            mLegendRenderer.computeLegend(mData);

        calculateOffsets();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mData == null)
            return;

//        if (mYAxis.isEnabled())
//            mYAxisRenderer.computeAxis(mYAxis.mAxisMinimum, mYAxis.mAxisMaximum, mYAxis.isInverted());

        if (mXAxis.isEnabled())
            mXAxisRenderer.computeAxis(mXAxis.mAxisMinimum, mXAxis.mAxisMaximum, false);

        mXAxisRenderer.renderAxisLabels(canvas);

        if (mDrawWeb)
            mRenderer.drawExtras(canvas);

        if (mYAxis.isEnabled() && mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        mRenderer.drawData(canvas);

        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);

        if (mYAxis.isEnabled() && !mYAxis.isDrawLimitLinesBehindDataEnabled())
            mYAxisRenderer.renderLimitLines(canvas);

        mYAxisRenderer.renderAxisLabels(canvas);

        mRenderer.drawValues(canvas);

        mLegendRenderer.renderLegend(canvas);

        drawDescription(canvas);

        drawMarkers(canvas);
    }

    /**
     * Returns the factor that is needed to transform values into pixels.
     *
     * @return
     */
    public float getFactor() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f) / mYAxis.mAxisRange;
    }

    /**
     * Returns the angle that each slice in the radar chart occupies.
     *
     * @return
     */
    public float getSliceAngle() {
        return 360f / (float) mData.getMaxEntryCountSet().getEntryCount();
    }

    @Override
    public int getIndexForAngle(float angle) {

        // take the current angle of the chart into consideration
        float a = Utils.getNormalizedAngle(angle - getRotationAngle());

        float sliceangle = getSliceAngle();

        int max = mData.getMaxEntryCountSet().getEntryCount();

        int index = 0;

        for (int i = 0; i < max; i++) {

            float referenceAngle = sliceangle * (i + 1) - sliceangle / 2f;

            if (referenceAngle > a) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * Returns the object that represents all y-labels of the RadarChart.
     *
     * @return
     */
    public YAxis getYAxis() {
        return mYAxis;
    }

    public float getWebLineWidth() {
        return mWebLineWidth;
    }

    /**
     * Sets the width of the web lines that come from the center.
     *
     * @param width
     */
    public void setWebLineWidth(float width) {
        mWebLineWidth = Utils.convertDpToPixel(width);
    }

    public float getWebLineWidthInner() {
        return mInnerWebLineWidth;
    }

    /**
     * Sets the width of the web lines that are in between the lines coming from
     * the center.
     *
     * @param width
     */
    public void setWebLineWidthInner(float width) {
        mInnerWebLineWidth = Utils.convertDpToPixel(width);
    }

    /**
     * Returns the alpha value for all web lines.
     *
     * @return
     */
    public int getWebAlpha() {
        return mWebAlpha;
    }

    /**
     * Sets the transparency (alpha) value for all web lines, default: 150, 255
     * = 100% opaque, 0 = 100% transparent
     *
     * @param alpha
     */
    public void setWebAlpha(int alpha) {
        mWebAlpha = alpha;
    }

    public int getWebColor() {
        return mWebColor;
    }

    /**
     * Sets the color for the web lines that come from the center. Don't forget
     * to use getResources().getColor(...) when loading a color from the
     * resources. Default: Color.rgb(122, 122, 122)
     *
     * @param color
     */
    public void setWebColor(int color) {
        mWebColor = color;
    }

    public int getWebColorInner() {
        return mWebColorInner;
    }

    /**
     * Sets the color for the web lines in between the lines that come from the
     * center. Don't forget to use getResources().getColor(...) when loading a
     * color from the resources. Default: Color.rgb(122, 122, 122)
     *
     * @param color
     */
    public void setWebColorInner(int color) {
        mWebColorInner = color;
    }

    /**
     * If set to true, drawing the web is enabled, if set to false, drawing the
     * whole web is disabled. Default: true
     *
     * @param enabled
     */
    public void setDrawWeb(boolean enabled) {
        mDrawWeb = enabled;
    }

    /**
     * Returns the modulus that is used for skipping web-lines.
     *
     * @return
     */
    public int getSkipWebLineCount() {
        return mSkipWebLineCount;
    }

    /**
     * Sets the number of web-lines that should be skipped on chart web before the
     * next one is drawn. This targets the lines that come from the center of the RadarChart.
     *
     * @param count if count = 1 -> 1 line is skipped in between
     */
    public void setSkipWebLineCount(int count) {

        mSkipWebLineCount = Math.max(0, count);
    }

    @Override
    protected float getRequiredLegendOffset() {
        return mLegendRenderer.getLabelPaint().getTextSize() * 4.f;
    }

    @Override
    protected float getRequiredBaseOffset() {
        return mXAxis.isEnabled() && mXAxis.isDrawLabelsEnabled() ?
                mXAxis.mLabelRotatedWidth :
                Utils.convertDpToPixel(10f);
    }

    @Override
    public float getRadius() {
        RectF content = mViewPortHandler.getContentRect();
        return Math.min(content.width() / 2f, content.height() / 2f);
    }

    /**
     * Returns the maximum value this chart can display on it's y-axis.
     */
    public float getYChartMax() {
        return mYAxis.mAxisMaximum;
    }

    /**
     * Returns the minimum value this chart can display on it's y-axis.
     */
    public float getYChartMin() {
        return mYAxis.mAxisMinimum;
    }

    /**
     * Returns the range of y-values this chart can display.
     *
     * @return
     */
    public float getYRange() {
        return mYAxis.mAxisRange;
    }


    public int getFilledAreaStartColor() {
        return mFilledAreaStartColor;
    }

    public RadarChart setFilledAreaStartColor(int color) {
        this.mFilledAreaStartColor = color;
        return this;
    }

    public int getFilledAreaEndColor() {
        return mFilledAreaEndColor;
    }

    public RadarChart setFilledAreaEndColor(int color) {
        this.mFilledAreaEndColor = color;
        return this;
    }

    public boolean isDrawGradientArea() {
        return mDrawGradientArea;
    }

    /***
     * Enable drawing the gradient area for the value shape or not.
     * @param enabled
     * @return
     */
    public RadarChart setDrawGradientArea(boolean enabled) {
        this.mDrawGradientArea = enabled;
        return this;
    }

    /***
     * Gets the circle color for the value dot.
     * @return
     */
    public int getEdgeValueCircleColor() {
        return mEdgeValueCircleColor;
    }

    /***
     * Sets the circle color for each value dot.
     * @param circleColor
     * @return
     */
    public RadarChart setEdgeValueCircleColor(int circleColor) {
        this.mEdgeValueCircleColor = circleColor;
        return this;
    }

    /***
     * Gets the circle radius for each value dot.
     * @return
     */
    public int getEdgeValueRadius() {
        return mEdgeValueCircleRadius;
    }

    /***
     * Sets the circle radius for each value dot.
     *
     * @param radius
     * @return
     */
    public RadarChart setEdgeValueRadius(int radius) {
        this.mEdgeValueCircleRadius = radius;
        return this;
    }

    /***
     * Gets the distance value for bezier curve.
     * @return
     */
    public int getDistanceToEdgeCurve() {
        return mDistanceToEdgeCurve;
    }

    /***
     * Sets the distance value for bezier curve.
     * @param distance
     * @return
     */
    public RadarChart setDistanceToEdgeCurve(int distance) {
        this.mDistanceToEdgeCurve = distance;
        return this;
    }
}
