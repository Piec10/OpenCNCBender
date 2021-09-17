package com.opencncbender.logic;

/**
 * Created by Piec on 2019-12-21.
 */
public class SingleStep {

    private double distanceX;
    private double angleA;
    private double angleB;

    public SingleStep(double distanceX) {
        this.distanceX = distanceX;
        this.angleA = 0.0;
        this.angleB = 0.0;
    }

    public SingleStep(double distanceX, double angleA) {
        this.distanceX = distanceX;
        this.angleA = angleA;
        this.angleB = 0.0;
    }

    public SingleStep(double distanceX, double angleA, double angleB) {
        this.distanceX = distanceX;
        this.angleA = angleA;
        this.angleB = angleB;
    }

    public double getDistanceX() {
        return distanceX;
    }

    public double getAngleA() {
        return angleA;
    }

    public double getAngleB() {
        return angleB;
    }

    public void setDistanceX(double distanceX) {
        this.distanceX = distanceX;
    }

    public void setAngleA(double angleA) {
        this.angleA = angleA;
    }

    public void setAngleB(double angleB) {
        this.angleB = angleB;
    }
}
