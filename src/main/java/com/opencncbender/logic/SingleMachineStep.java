package com.opencncbender.logic;

/**
 * Created by Piec on 2020-01-03.
 */
public class SingleMachineStep {

    private double wireDistance;
    private double machineAngle;
    private byte machineAngleDirection;     // indicates direction of bend with given parameters
                                            // if 0 or 1: clockwise/anticlockwise movement of bending head with
                                            // simultaneous wire feed to create arc
                                            // if -1: wire movement, no bending, machineAngle indicates rotation of wire

    public SingleMachineStep(double wireDistance) { // simple wire feed
        this.wireDistance = wireDistance;
        this.machineAngle = 0;
        this.machineAngleDirection = -1;
    }

    public SingleMachineStep(double wireDistance, double machineAngle) {    // wire feed with rotation
        this.wireDistance = wireDistance;
        this.machineAngle = machineAngle;
        this.machineAngleDirection = -1;
    }

    public SingleMachineStep(double wireDistance, double machineAngle, byte machineAngleDirection) {    // bending
        this.wireDistance = wireDistance;
        this.machineAngle = machineAngle;
        this.machineAngleDirection = machineAngleDirection;
    }

    public double getWireDistance() {
        return wireDistance;
    }

    public double getMachineAngle() {
        return machineAngle;
    }

    public byte getMachineAngleDirection() {
        return machineAngleDirection;
    }
}
