package com.example.lab21.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DynamicCurveView extends View {

    private final List<Float> dataPoints = new ArrayList<>();
    private static final int MAX_POINTS = 100;

    private Paint gridPaint;
    private Paint linePaint;
    private Paint fillPaint;
    private Paint textPaint;

    public DynamicCurveView(Context context) {
        super(context);
        init();
    }

    public DynamicCurveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(2f);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#3F51B5")); // Primary color
        linePaint.setStrokeWidth(6f);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#757575"));
        textPaint.setTextSize(32f);
    }

    public void appendValue(float value) {
        if (dataPoints.size() >= MAX_POINTS) {
            dataPoints.remove(0);
        }
        dataPoints.add(value);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int paddingLeft = 50;
        int paddingBottom = 50;
        int paddingTop = 50;
        int paddingRight = 30;

        // Draw axes
        canvas.drawLine(paddingLeft, height - paddingBottom, width - paddingRight, height - paddingBottom, gridPaint); // X
        canvas.drawLine(paddingLeft, paddingTop, paddingLeft, height - paddingBottom, gridPaint); // Y

        if (dataPoints.size() < 2) {
            canvas.drawText("En attente des données...", width / 3f, height / 2f, textPaint);
            return;
        }

        float minVal = Float.MAX_VALUE;
        float maxVal = -Float.MAX_VALUE;

        for (float v : dataPoints) {
            if (v < minVal) minVal = v;
            if (v > maxVal) maxVal = v;
        }

        if (maxVal == minVal) {
            maxVal = minVal + 1;
        }
        
        // Add a bit of margin to Y axis
        float range = maxVal - minVal;
        maxVal += range * 0.1f;
        minVal -= range * 0.1f;

        Path linePath = new Path();
        Path fillPath = new Path();
        
        float drawWidth = width - paddingLeft - paddingRight;
        float drawHeight = height - paddingTop - paddingBottom;

        for (int i = 0; i < dataPoints.size(); i++) {
            float x = paddingLeft + i * (drawWidth / (MAX_POINTS - 1));
            float normalizedY = (dataPoints.get(i) - minVal) / (maxVal - minVal);
            float y = height - paddingBottom - (normalizedY * drawHeight);

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, height - paddingBottom);
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
            
            if (i == dataPoints.size() - 1) {
                fillPath.lineTo(x, height - paddingBottom);
                fillPath.close();
            }
        }

        // Setup gradient for fill
        LinearGradient gradient = new LinearGradient(
                0, paddingTop, 0, height - paddingBottom,
                Color.parseColor("#403F51B5"), Color.TRANSPARENT, Shader.TileMode.CLAMP);
        fillPaint.setShader(gradient);

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);

        canvas.drawText(String.format("Min: %.1f", minVal + range * 0.1f), paddingLeft + 10, paddingTop, textPaint);
        canvas.drawText(String.format("Max: %.1f", maxVal - range * 0.1f), width - 200, paddingTop, textPaint);
    }
}
