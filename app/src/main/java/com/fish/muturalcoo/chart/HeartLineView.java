package com.fish.muturalcoo.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fish.muturalcoo.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by allen on 2018/5/24.
 */

public class HeartLineView extends View {

    private Paint mPaint;

    //线的颜色，柱子右边显示**人的颜色
    private int lineColor, dataFontColor;
    //X轴距离左边和右边的距离，Y轴距离上边和下边的距离
    private float xLeftSpace, xRightSpace,
            yTopSpace, yBottomSpace;
    //XY轴刻度的大小：X轴刻度的高，Y轴刻度的宽
    private float xDividerHeight, yDividerWidth;
    //XY轴刻度颜色
    private int xDividerColor, yDividerColor;

    //x轴下面的字体距离X轴的距离，Y轴左边的字体距离Y轴的距离
    private float txtXSpace, txtYSpace;

    //每个柱子的高度
    private float pillarHeight;
    //柱子距离Y轴刻度的距离
    private float pillarMarginY = 10;

    private float FULL_AMOUNT = 7000;

    private List<Integer> dataList = new ArrayList<>();
    private float xyFontSize;
    private Paint dashPaint;
    private Paint paintCurve;

    public HeartLineView(Context context) {
        this(context, null);
    }

    public HeartLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initXmlAttrs(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(2);

        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setStrokeWidth(3);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{5, 5}, 0));


        paintCurve = new Paint();
        paintCurve.setStyle(Paint.Style.STROKE);
        paintCurve.setDither(true);
        paintCurve.setAntiAlias(true);
        paintCurve.setStrokeWidth(3);
        PathEffect pathEffect = new CornerPathEffect(25);
        paintCurve.setPathEffect(pathEffect);


    }

    private void initXmlAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.hpillar);
        if (typedArray == null) return;
        lineColor = typedArray.getColor(R.styleable.hpillar_line_color_h, Color.BLACK);
        dataFontColor = typedArray.getColor(R.styleable.hpillar_data_font_color_h, Color.BLACK);
        xLeftSpace = typedArray.getDimension(R.styleable.hpillar_x_left_space_h, 50);
        xRightSpace = typedArray.getDimension(R.styleable.hpillar_x_right_space_h, 80);
        yTopSpace = typedArray.getDimension(R.styleable.hpillar_y_top_space_h, 30);
        yBottomSpace = typedArray.getDimension(R.styleable.hpillar_y_bottom_space_h, 100);
        yDividerWidth = typedArray.getDimension(R.styleable.hpillar_y_divider_width, 14);
        xDividerHeight = typedArray.getDimension(R.styleable.hpillar_x_divider_height, 14);
        xDividerColor = typedArray.getColor(R.styleable.hpillar_x_divider_color, Color.BLACK);
        yDividerColor = typedArray.getColor(R.styleable.hpillar_y_divider_color, Color.BLACK);
        xyFontSize = typedArray.getDimension(R.styleable.hpillar_xy_font_size_h, 30);
        txtXSpace = typedArray.getDimension(R.styleable.hpillar_txt_x_space, 20);
        txtYSpace = typedArray.getDimension(R.styleable.hpillar_txt_y_space, 15);
        pillarHeight = typedArray.getDimension(R.styleable.hpillar_pillar_height, 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataList.size() == 0) return;
        /**
         * 绘制ＸＹ轴
         */
        initPaintColor(lineColor);
        float xLineTop = getHeight() - yBottomSpace;//view的高度 - Y轴距离view顶部的距离
        //两点确定一线
        canvas.drawLine(
                xLeftSpace, xLineTop,
                getWidth() - xRightSpace, xLineTop,
                mPaint
        );
        //绘制Y

        canvas.drawLine(
                xLeftSpace, yTopSpace,
                xLeftSpace, getHeight() - yBottomSpace,
                mPaint
        );


        /**
         * 绘制Y轴刻度 和 左边的文字
         */
        //Y轴每个刻度的高度：获取Y轴真真实高度，然后除以6获取每个刻度的高度
        float spaceVertical = (getHeight() - yTopSpace - yBottomSpace) / 6;
        //Y轴刻度左右的X坐标
        float yScaleLeftX = xLeftSpace - yDividerWidth / 2;
        float yScaleRightX = xLeftSpace + yDividerWidth / 2;
        float[] pts = {
                yScaleLeftX, yTopSpace, yScaleRightX, yTopSpace,
                yScaleLeftX, yTopSpace + spaceVertical, yScaleRightX, yTopSpace + spaceVertical,
                yScaleLeftX, yTopSpace + spaceVertical * 2, yScaleRightX, yTopSpace + spaceVertical * 2,
                yScaleLeftX, yTopSpace + spaceVertical * 3, yScaleRightX, yTopSpace + spaceVertical * 3,
                yScaleLeftX, yTopSpace + spaceVertical * 4, yScaleRightX, yTopSpace + spaceVertical * 4,
        };
//        yScaleLeftX, yTopSpace + spaceVertical * 5, yScaleRightX, yTopSpace + spaceVertical * 5

        //绘制一组线：pts中得数据四个为一组,同样代表起点终点坐标
//        canvas.drawLines(pts, mPaint);
        //绘制Y轴刻度左边文字
        Rect yTxtRect;
        mPaint.setColor(dataFontColor);
        mPaint.setTextSize(xyFontSize);
        int[] ys = {40, 80, 120, 160, 200, 240};
        for (int i = 0; i < ys.length; i++) {
            String number = ys[(ys.length - 1 - i)] + "";
            yTxtRect = new Rect();
            //为了让六个月份居中，测量最大数
            mPaint.getTextBounds("12", 0, 2, yTxtRect);
            //设置居中
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(
                    number,
                    xLeftSpace - yTxtRect.width() / 2 - txtYSpace,
                    yTopSpace + (spaceVertical * (i + 1)) + yTxtRect.height() / 2,
                    mPaint
            );

        }

        /**
         * 绘制X轴刻度 和 X轴下面的文字
         */
//        dataList
        int maxValue = Collections.max(dataList);
        int average = maxValue / 4;
        FULL_AMOUNT = average * 5;
        //获取x轴刻度间距  view宽度 - x轴左边距 - X轴右边距 - Y轴刻度宽度一半 - 柱子距离刻度的距离
        float xSpaceVertical = (getWidth() - xLeftSpace - xRightSpace - yDividerWidth / 2 - pillarMarginY) / 6;
        //X刻度上下Y坐标
        float xScaleTopY = getHeight() - yBottomSpace - xDividerHeight / 2;
//        float xScaleBottomY = getHeight() - yBottomSpace + xDividerHeight / 2;
        float xScaleBottomY = getHeight() - yBottomSpace;
        float[] xptsx = {
                xLeftSpace, yTopSpace, xLeftSpace, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical, yTopSpace, xLeftSpace + xSpaceVertical + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical * 2, yTopSpace, xLeftSpace + xSpaceVertical * 2 + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical * 3, yTopSpace, xLeftSpace + xSpaceVertical * 3 + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical * 4, yTopSpace, xLeftSpace + xSpaceVertical * 4 + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical * 5, yTopSpace, xLeftSpace + xSpaceVertical * 5 + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace,
                xLeftSpace + yDividerWidth / 2 + pillarMarginY + xSpaceVertical * 6, yTopSpace, xLeftSpace + xSpaceVertical * 6 + yDividerWidth / 2 + pillarMarginY, getHeight() - yBottomSpace

        };
        initPaintColor(xDividerColor);
        dashPaint.setColor(xDividerColor);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawLines(xptsx, dashPaint);

        //绘制X轴刻度下面的文字
        initPaintColor(dataFontColor);
        mPaint.setTextSize(xyFontSize);
        int[] xs = {1, 5, 10, 15, 20, 25, 30};
        Rect xNumberBound;
        for (int i = 0; i < xs.length; i++) {

            String number = xs[i] + "";
            xNumberBound = new Rect();
            mPaint.getTextBounds(number, 0, number.length(), xNumberBound);
            canvas.drawText(
                    number,
                    xLeftSpace + yDividerWidth + xSpaceVertical * (i),
                    getHeight() - yBottomSpace + xNumberBound.height() + txtXSpace,
                    mPaint
            );
        }

        //获取X轴的刻度
        float fullWidth = getWidth() - xLeftSpace - xRightSpace - yDividerWidth / 2 - pillarMarginY;
        float perX = xSpaceVertical / 5;
        float perY = spaceVertical / 40;
        Path path = new Path();
        for (int x = 0; x < dataList.size(); x++) {
            if (x == 0) {

                path.moveTo(xLeftSpace, xLineTop - (dataList.get(x) - 40) * perY);
            } else {
                path.lineTo(xLeftSpace + x * perX, xLineTop - (dataList.get(x) - 40) * perY);
            }

            if ((x == dataList.size() - 1)) {
                path.lineTo(getWidth()-xRightSpace, xLineTop - (dataList.get(x) - 40) * perY);
            }
//            柱子Y轴上坐标：view高度 - Y轴距离view下边的距离 - Y轴刻度高度*(x+1) - 柱子高度一半
        }
        paintCurve.setStrokeWidth(8);
        paintCurve.setColor(getResources().getColor(R.color.color_FFEF00 ));
        canvas.drawPath(path, paintCurve);


    }

    private void initPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setData(List<Integer> data) {
        dataList.clear();
        dataList.addAll(data);
        invalidate();
    }


}
