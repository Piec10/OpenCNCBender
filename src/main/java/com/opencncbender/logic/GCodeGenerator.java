package com.opencncbender.logic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

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

    private StringProperty startingGCode = new SimpleStringProperty("");
    private StringProperty endingGCode = new SimpleStringProperty("");
    private StringProperty openClampGCode = new SimpleStringProperty("");
    private StringProperty closeClampGCode = new SimpleStringProperty("");

    private BooleanProperty wireClamp = new SimpleBooleanProperty(false);

    private List<String> instructionsList;

    public GCodeGenerator(MachineGeometry machineGeometry, WireParameters wireParameters,
                          double zFallDistance, double rodSafeAngleOffset, double wireFeedrate, double angleFeedrate) {
        this.machineGeometry = machineGeometry;
        this.wireParameters = wireParameters;
        this.zFallDistance = zFallDistance;
        this.rodSafeAngleOffset = rodSafeAngleOffset;
        this.wireFeedrate = wireFeedrate;
        this.angleFeedrate = angleFeedrate;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("G90");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("G0 Y0 A0 B0");
        startingGCode.set(stringBuilder.toString());

        stringBuilder = new StringBuilder();
        stringBuilder.append("G0 X0 B0");
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("G0 Z" + -zFallDistance);
        endingGCode.set(stringBuilder.toString());

    }

    public GCodeGenerator(Properties defaultGCodeSettings, MachineGeometry machineGeometry, WireParameters wireParameters, double zFallDistance, double rodSafeAngleOffset, double wireFeedrate, double angleFeedrate) {
        this.machineGeometry = machineGeometry;
        this.wireParameters = wireParameters;
        this.zFallDistance = zFallDistance;
        this.rodSafeAngleOffset = rodSafeAngleOffset;
        this.wireFeedrate = wireFeedrate;
        this.angleFeedrate = angleFeedrate;

        startingGCode.set(defaultGCodeSettings.getProperty("startingGCode",""));
        endingGCode.set(defaultGCodeSettings.getProperty("endingGCode",""));
        openClampGCode.set(defaultGCodeSettings.getProperty("openClampGCode",""));
        closeClampGCode.set(defaultGCodeSettings.getProperty("closeClampGCode",""));
    }

    public GCodeGenerator(Properties defaultGCodeSettings, MachineGeometry machineGeometry, WireParameters wireParameters) {
        this.machineGeometry = machineGeometry;
        this.wireParameters = wireParameters;

        zFallDistance = Double.parseDouble(defaultGCodeSettings.getProperty("zFallDistance","5.0"));
        rodSafeAngleOffset = Double.parseDouble(defaultGCodeSettings.getProperty("safeAngleOffset","5.0"));
        wireFeedrate = Double.parseDouble(defaultGCodeSettings.getProperty("wireFeedrate","10000.0"));
        angleFeedrate = Double.parseDouble(defaultGCodeSettings.getProperty("angleFeedrate","18000.0"));
        startingGCode.set(defaultGCodeSettings.getProperty("startingGCode",""));
        endingGCode.set(defaultGCodeSettings.getProperty("endingGCode",""));
        openClampGCode.set(defaultGCodeSettings.getProperty("openClampGCode",""));
        closeClampGCode.set(defaultGCodeSettings.getProperty("closeClampGCode",""));
        wireClamp.set(Boolean.parseBoolean(defaultGCodeSettings.getProperty("wireClamp","false")));
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

        calculateRodTouchAngle();
        rodSafeAngle = rodTouchAngle + rodSafeAngleOffset;
        pinTouchPosition = wireParameters.getDiameter() / 2 + machineGeometry.getPinRadius();

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

        if(startingGCode.get() != "") {
            instructionsList.add(startingGCode.get());
        }
        if(isWireClamp()){
            instructionsList.add(openClampGCode.get());
        }

        instructionsList.add("G0 X" + totalWireLength);
        instructionsList.add("M0");
        if(isWireClamp()){
            instructionsList.add(closeClampGCode.get());
        }

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
        if(endingGCode.get() != "") {
            instructionsList.add(endingGCode.get());
        }
        if(isWireClamp()){
            instructionsList.add(openClampGCode.get());
        }

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

    public String getStartingGCode() {
        return startingGCode.get();
    }

    public StringProperty startingGCodeProperty() {
        return startingGCode;
    }

    public String getEndingGCode() {
        return endingGCode.get();
    }

    public StringProperty endingGCodeProperty() {
        return endingGCode;
    }

    public void setStartingGCode(String startingGCode) {
        this.startingGCode.set(startingGCode);
    }

    public void setEndingGCode(String endingGCode) {
        this.endingGCode.set(endingGCode);
    }

    public void setOpenClampGCode(String openClampGCode) {
        this.openClampGCode.set(openClampGCode);
    }

    public void setCloseClampGCcode(String closeClampGCcode) {
        this.closeClampGCode.set(closeClampGCcode);
    }

    public double getzFallDistance() {
        return zFallDistance;
    }

    public double getRodSafeAngleOffset() {
        return rodSafeAngleOffset;
    }

    public double getWireFeedrate() {
        return wireFeedrate;
    }

    public double getAngleFeedrate() {
        return angleFeedrate;
    }

    public void setzFallDistance(double zFallDistance) {
        this.zFallDistance = zFallDistance;
    }

    public void setRodSafeAngleOffset(double rodSafeAngleOffset) {
        this.rodSafeAngleOffset = rodSafeAngleOffset;
    }

    public void setWireFeedrate(double wireFeedrate) {
        this.wireFeedrate = wireFeedrate;
    }

    public void setAngleFeedrate(double angleFeedrate) {
        this.angleFeedrate = angleFeedrate;
    }

    public String getOpenClampGCode() {
        return openClampGCode.get();
    }

    public StringProperty openClampGCodeProperty() {
        return openClampGCode;
    }

    public String getCloseClampGCode() {
        return closeClampGCode.get();
    }

    public StringProperty closeClampGCodeProperty() {
        return closeClampGCode;
    }

    public void setCloseClampGCode(String closeClampGCode) {
        this.closeClampGCode.set(closeClampGCode);
    }

    public boolean isWireClamp() {
        return wireClamp.get();
    }

    public BooleanProperty wireClampProperty() {
        return wireClamp;
    }

    public void setWireClamp(boolean wireClamp) {
        this.wireClamp.set(wireClamp);
    }

    public Properties getGCodeProperties() {

        Properties properties = new Properties();

        properties.setProperty("startingGCode",startingGCode.get());
        properties.setProperty("endingGCode",endingGCode.get());
        properties.setProperty("openClampGCode",openClampGCode.get());
        properties.setProperty("closeClampGCode",closeClampGCode.get());
        properties.setProperty("zFallDistance",Double.toString(zFallDistance));
        properties.setProperty("safeAngleOffset",Double.toString(rodSafeAngleOffset));
        properties.setProperty("wireFeedrate",Double.toString(wireFeedrate));
        properties.setProperty("angleFeedrate",Double.toString(angleFeedrate));
        properties.setProperty("wireClamp",String.valueOf(wireClamp.get()));

        return properties;
    }
}
