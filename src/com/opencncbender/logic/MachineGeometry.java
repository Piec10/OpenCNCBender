package com.opencncbender.logic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Piec on 2019-12-21.
 */
public class MachineGeometry {

    private DoubleProperty bendingRadius = new SimpleDoubleProperty();
    private DoubleProperty rodRadius = new SimpleDoubleProperty();
    private DoubleProperty pinRadius = new SimpleDoubleProperty();
    private DoubleProperty pinOffset = new SimpleDoubleProperty();   //offset from rod center of rotation(0,0) in carthesian XY
    private DoubleProperty pinSpacing = new SimpleDoubleProperty();  //symmetric along X axis, when wire goes between two fixed pins
                                    // if 0 then pinOffset must be also 0, for machines witch single pin that moves left/right

    //private double bendingRadius;
    //private double rodRadius;
    //private double pinRadius;
    //private double pinOffset;       //offset from rod center of rotation(0,0) in carthesian XY
    //private double pinSpacing;      //symmetric along X axis, when wire goes between two fixed pins
                                    // if 0 then pinOffset must be also 0, for machines witch single pin that moves left/right


    public MachineGeometry(double bendingRadius, double rodRadius, double pinRadius, double pinOffset, double pinSpacing) {
        this.bendingRadius.set(bendingRadius);
        this.rodRadius.set(rodRadius);
        this.pinRadius.set(pinRadius);
        this.pinOffset.set(pinOffset);
        this.pinSpacing.set(pinSpacing);
    }

    public double getBendingRadius() {
        return bendingRadius.get();
    }

    public double getRodRadius() {
        return rodRadius.get();
    }

    public double getPinRadius() {
        return pinRadius.get();
    }

    public double getPinOffset() {
        return pinOffset.get();
    }

    public double getPinSpacing() {
        return pinSpacing.get();
    }

    public DoubleProperty bendingRadiusProperty() {
        return bendingRadius;
    }

    public DoubleProperty rodRadiusProperty() {
        return rodRadius;
    }

    public DoubleProperty pinRadiusProperty() {
        return pinRadius;
    }

    public DoubleProperty pinOffsetProperty() {
        return pinOffset;
    }

    public DoubleProperty pinSpacingProperty() {
        return pinSpacing;
    }

    public void setBendingRadius(double bendingRadius) {
        this.bendingRadius.set(bendingRadius);
    }

    public void setRodRadius(double rodRadius) {
        this.rodRadius.set(rodRadius);
    }

    public void setPinRadius(double pinRadius) {
        this.pinRadius.set(pinRadius);
    }

    public void setPinOffset(double pinOffset) {
        this.pinOffset.set(pinOffset);
    }

    public void setPinSpacing(double pinSpacing) {
        this.pinSpacing.set(pinSpacing);
    }
}