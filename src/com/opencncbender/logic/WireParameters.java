package com.opencncbender.logic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Piec on 2019-12-21.
 */
public class WireParameters {

    private DoubleProperty diameter = new SimpleDoubleProperty();
    private DoubleProperty overbendAngle = new SimpleDoubleProperty();

    public WireParameters(double diameter) {
        this.diameter.set(diameter);
        this.overbendAngle.set(0.0);
    }

    public WireParameters(double diameter, double overbendAngle) {
        this.diameter.set(diameter);
        this.overbendAngle.set(overbendAngle);
    }

    public double getDiameter() {
        return diameter.get();
    }

    public double getOverbendAngle() {
        return overbendAngle.get();
    }

    public DoubleProperty diameterProperty() {
        return diameter;
    }

    public DoubleProperty overbendAngleProperty() {
        return overbendAngle;
    }

    public void setDiameter(double diameter) {
        this.diameter.set(diameter);
    }

    public void setOverbendAngle(double overbendAngle) {
        this.overbendAngle.set(overbendAngle);
    }
}
