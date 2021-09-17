package com.opencncbender.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Piec on 2020-01-03.
 */
public class GCodeGenerator {

    private MachineGeometry machineGeometry;
    private WireParameters wireParameters;
    private double zFallDistance;
    private double rodSafeAngleOffset;      // angle distance between wire and rod in safe position
    private double wireFeedrate;
    private double angleFeedrate;

    private double rodSafeAngle;            // bending head angle position that is close to straight wire but not touching it
                                            // used for: changing position left/right,
                                            // moving wire forward, rotating wire
    private double rodTouchAngle;           // bending head angle position when touching straight wire
    private double pinTouchPosition;        // bending head Y position when pin is touching straight wire

    private List<String> instructionsList;

    public GCodeGenerator(MachineGeometry machineGeometry, WireParameters wireParameters,
                          double zFallDistance, double rodSafeAngleOffset, double wireFeedrate, double angleFeedrate) {
        this.machineGeometry = machineGeometry;
        this.wireParameters = wireParameters;
        this.zFallDistance = zFallDistance;
        this.rodSafeAngleOffset = rodSafeAngleOffset;
        this.wireFeedrate = wireFeedrate;
        this.angleFeedrate = angleFeedrate;

        calculateRodTouchAngle();
        rodSafeAngle = rodTouchAngle + rodSafeAngleOffset;
        pinTouchPosition = wireParameters.getDiameter() / 2 + machineGeometry.getPinRadius();
    }

    private void calculateRodTouchAngle() {
        double x,y;

        y = machineGeometry.getPinRadius() + machineGeometry.getRodRadius() + wireParameters.getDiameter();
        x = Math.sqrt(machineGeometry.getBendingRadius()*machineGeometry.getBendingRadius()-y*y);
        rodTouchAngle = Math.toDegrees(Math.atan2(y,x));
    }

    public List<String> generateInstructionsList (Steps<SingleMachineStep> machineSteps){

        instructionsList = new ArrayList<>();

        double totalWireLength = 0;
        double totalWireAngle = 0;

        String strTotalWireLength;
        String strTotalWireAngle;

        for(int i=0; i < machineSteps.size(); i++){

            totalWireLength = totalWireLength + machineSteps.get(i).getWireDistance();
            //System.out.println(machineSteps.get(i).getWireDistance());
        }

        byte machineAngleDirection;
        byte firstBendDirection = 0;
        for(int i=0; i < machineSteps.size(); i++){

            machineAngleDirection = machineSteps.get(i).getMachineAngleDirection();
            if(machineAngleDirection != -1){

                firstBendDirection = machineAngleDirection;
                break;
            }
        }

        totalWireLength = totalWireLength + 2;
        totalWireLength = Math.floor(totalWireLength);

        instructionsList.add("G90");
        instructionsList.add("G0 X" + totalWireLength + " Y0 A0 B0");
        instructionsList.add("M0");

        safePositionChangeDir(firstBendDirection);

        byte currentBendDir, lastBendDir;
        double currentWirePosition = totalWireLength;
        double bendAngle, touchAngle;

        String strCurrentWirePosition, strBendAngle, strTouchAngle;

        SingleMachineStep currentStep;

        currentBendDir = firstBendDirection;

        for(int i=0; i < machineSteps.size(); i++){

            currentStep = machineSteps.get(i);

            currentWirePosition = currentWirePosition - currentStep.getWireDistance();
            machineAngleDirection = currentStep.getMachineAngleDirection();

            if(machineAngleDirection != -1){

                bendAngle = currentStep.getMachineAngle();
                if(!isZero(bendAngle)){
                    lastBendDir = currentBendDir;
                    currentBendDir = machineAngleDirection;

                    if(currentBendDir != lastBendDir){
                        safePositionChangeDir(currentBendDir);
                    }


                    touchAngle = rodTouchAngle;
                    if(currentBendDir == 1){
                        touchAngle = -touchAngle;
                    }

                    strTouchAngle = String.format(Locale.ROOT,"%.03f", touchAngle);
                    strCurrentWirePosition = String.format(Locale.ROOT,"%.03f", currentWirePosition);
                    strBendAngle = String.format(Locale.ROOT,"%.03f", bendAngle);

                    instructionsList.add("G0 A" + strTouchAngle);
                    instructionsList.add("G1 F" + wireFeedrate + " X" + strCurrentWirePosition + " A" + strBendAngle);
                    //instructionsList.addStep("M0");
                    safePositionSameDir(currentBendDir);
                }
            }
            else{
                totalWireAngle = totalWireAngle + currentStep.getMachineAngle();

                strCurrentWirePosition = String.format(Locale.ROOT,"%.03f", currentWirePosition);
                strTotalWireAngle = String.format(Locale.ROOT,"%.03f", totalWireAngle);
                instructionsList.add("G0 X" + strCurrentWirePosition + " B" + strTotalWireAngle);
                //instructionsList.addStep("M0");
            }
        }

        instructionsList.add("G0 X0 B0");

        /*byte currentBendDir, lastBendDir;
        double currentWirePosition = totalWireLength;
        SingleMachineStep currentStep;

        currentStep = machineSteps.get(0);
        currentWirePosition = currentWirePosition - currentStep.getWireDistance();
        instructionsList.addStep("G0 X" + currentWirePosition);

        currentStep = machineSteps.get(1);
        currentBendDir = currentStep.getMachineAngleDirection();
        safePositionChangeDir(currentBendDir);

        double bendAngle = currentStep.getMachineAngle();
        double touchAngle = rodTouchAngle;
        if(currentBendDir == 1){
            touchAngle = -touchAngle;
        }
        currentWirePosition = currentWirePosition - currentStep.getWireDistance();

        instructionsList.addStep("G0 A" + touchAngle);
        instructionsList.addStep("G1 F" + angleFeedrate + " X" + currentWirePosition + " A" + bendAngle);
        safePositionSameDir(currentBendDir);


        currentStep = machineSteps.get(2);
        currentWirePosition = currentWirePosition - currentStep.getWireDistance();
        totalWireAngle = totalWireAngle + currentStep.getMachineAngle();
        instructionsList.addStep("G0 X" + currentWirePosition + " B" + totalWireAngle);

        currentStep = machineSteps.get(3);
        currentBendDir = currentStep.getMachineAngleDirection();
        safePositionChangeDir(currentBendDir);*/

        /*if(currentBendDir == 1){

            instructionsList.addStep("G0 Y" + -machineGeometry.getBendingRadius()/2 + " A-90");
            instructionsList.addStep("G0 Z0");
            instructionsList.addStep("G0 Y" + -pinTouchPosition);
            instructionsList.addStep("G0 A" + -rodTouchAngle);
        }
        else{

            instructionsList.addStep("G0 Y" + machineGeometry.getBendingRadius()/2 + " A90");
            instructionsList.addStep("G0 Z0");
            instructionsList.addStep("G0 Y" + pinTouchPosition);
            instructionsList.addStep("G0 A" + rodTouchAngle);
        }*/


        //currentWirePosition = currentWirePosition - currentStep.getWireDistance();




        //instructionsList.addStep("G1 F" + wireFeedrate + " X" + currentWirePosition + " A" + );

        /*byte currentBendDir, lastBendDir;
        double totalWireLength = 0;
        SingleMachineStep singleStep;

        singleStep = machineSteps.get(0);
        currentBendDir = singleStep.getMachineAngleDirection();
        safePositionSameDir(currentBendDir);

        for(int i=0; i < machineSteps.size(); i++){

            singleStep = machineSteps.get(i);
            lastBendDir = currentBendDir;
            currentBendDir = singleStep.getMachineAngleDirection();

            totalWireLength += singleStep.getDistanceX();
            feedWire(totalWireLength);

            if(currentBendDir != lastBendDir){
                safePositionChangeDir(currentBendDir);
            }

            if(singleStep.getAngleA() != 0) {
                bend(singleStep.getAngleA());
                safePositionSameDir(currentBendDir);
            }
        }

        cutWire();*/

        return instructionsList;
    }

    /*private void cutWire() {

        instructionsList.addStep("M702 T1 Z0");
        instructionsList.addStep("G92 E0");
        instructionsList.addStep("G0 F" + wireFeedrate + " E" + distanceAfterCut);
        instructionsList.addStep("G92 E0");

    }*/

    private void bend(double angleA) {

        instructionsList.add("G0 F" + angleFeedrate + " X" + angleA);
    }

    private void safePositionSameDir(byte dir) {

        double writeAngle = rodSafeAngle;

        if(dir == 1) writeAngle = -rodSafeAngle;

        String strWriteAngle = String.format(Locale.ROOT,"%.03f", writeAngle);


        instructionsList.add("G0 A" + strWriteAngle);
    }

    private void safePositionChangeDir(byte dir) {

        double safeAngle = rodSafeAngle;
        double safePosition = machineGeometry.getBendingRadius()/2;
        double touchPosition = pinTouchPosition;

        if(dir == 1){
            safeAngle = -safeAngle;
            safePosition = -safePosition;
            touchPosition = -touchPosition;
        }

        String strSafeAngle = String.format(Locale.ROOT,"%.03f", safeAngle);
        String strSafePosition = String.format(Locale.ROOT,"%.03f", safePosition);
        String strTouchPosition = String.format(Locale.ROOT,"%.03f", touchPosition);

        instructionsList.add("G0 Z" + -zFallDistance);
        instructionsList.add("G0 Y" + strSafePosition + " A" + strSafeAngle);
        instructionsList.add("G0 Z0");
        instructionsList.add("G0 Y" + strTouchPosition);
    }

    private boolean isZero(double number){

        if(Math.abs(number) < 0.0001) return true;
        else return false;
    }

}
