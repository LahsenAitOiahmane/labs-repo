package com.example.lab21.utils;

import java.util.LinkedList;
import java.util.Queue;

public class MathFilter {

    private final Queue<Float> slidingWindow = new LinkedList<>();
    private final int windowSize;
    private final float alpha;
    private final float[] lowPassValues = new float[3];

    public MathFilter(int windowSize, float alpha) {
        this.windowSize = windowSize;
        this.alpha = alpha;
    }

    public void applyLowPass(float x, float y, float z) {
        lowPassValues[0] = alpha * lowPassValues[0] + (1 - alpha) * x;
        lowPassValues[1] = alpha * lowPassValues[1] + (1 - alpha) * y;
        lowPassValues[2] = alpha * lowPassValues[2] + (1 - alpha) * z;
    }

    public float[] getGravityEstimate() {
        return lowPassValues;
    }

    public float getLinearMagnitude(float x, float y, float z) {
        float linearX = x - lowPassValues[0];
        float linearY = y - lowPassValues[1];
        float linearZ = z - lowPassValues[2];
        return (float) Math.sqrt(linearX * linearX + linearY * linearY + linearZ * linearZ);
    }

    public void addValue(float val) {
        if (slidingWindow.size() >= windowSize) {
            slidingWindow.poll();
        }
        slidingWindow.add(val);
    }

    public boolean isCalibrated() {
        return slidingWindow.size() >= windowSize;
    }

    public float getMean() {
        float sum = 0f;
        for (float v : slidingWindow) sum += v;
        return slidingWindow.isEmpty() ? 0 : sum / slidingWindow.size();
    }

    public float getMax() {
        float max = 0f;
        for (float v : slidingWindow) {
            if (v > max) max = v;
        }
        return max;
    }

    public float getStandardDeviation() {
        float mean = getMean();
        float variance = 0f;
        for (float v : slidingWindow) {
            variance += (v - mean) * (v - mean);
        }
        return slidingWindow.isEmpty() ? 0 : (float) Math.sqrt(variance / slidingWindow.size());
    }
}
