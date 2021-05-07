package org.vaadin.example;

public class CentralTrend {
    private int mode;
    private double median;
    private double average;

    public CentralTrend() {

    }

    public CentralTrend(int mode, double median, double average) {
        this.mode = mode;
        this.median = median;
        this.average = average;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public double getMedian() {
        return median;
    }

    public void setMedian(double median) {
        this.median = median;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
