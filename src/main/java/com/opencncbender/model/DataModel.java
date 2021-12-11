package com.opencncbender.model;

import com.opencncbender.logic.*;
import com.opencncbender.util.Direction;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.opencncbender.util.IsZeroComparator.isZero;

public class DataModel {

    private final Polyline polyline = new Polyline();
    private final ObservableList<Integer> selectedPointsList = FXCollections.observableArrayList();
    private final BendingSteps bendingSteps = new BendingSteps();
    private final SimpleObjectProperty<SingleStep> selectedStep = new SimpleObjectProperty<>(null);
    private final SimpleIntegerProperty selectedStepIndex = new SimpleIntegerProperty();
    private final ObservableList<Point3D> view3DPointsList = FXCollections.observableArrayList();

    private final SimpleIntegerProperty numberOfPointsSelected = new SimpleIntegerProperty();
    private final SimpleDoubleProperty minDistance = new SimpleDoubleProperty();
    private final SimpleDoubleProperty maxDistance = new SimpleDoubleProperty();
    private final SimpleDoubleProperty preferredDistance = new SimpleDoubleProperty();

    private final SimpleStringProperty currentFilename = new SimpleStringProperty("No file opened");

    private MachineGeometry machineGeometry;
    private WireParameters wireParameters;
    private GCodeGenerator gCodeGenerator;

    private final AnimationController animationController = new AnimationController(this);

    public DataModel(Properties defaultMachineGeometry, Properties defaultWireParameters, Properties defaultGCodeSettings) {
        selectedPointsList.addListener((ListChangeListener<Integer>) change ->{
            updateSelectionInfo();
        });

        this.machineGeometry = new MachineGeometry(defaultMachineGeometry);

        this.wireParameters = new WireParameters(defaultWireParameters);
        this.gCodeGenerator = new GCodeGenerator(defaultGCodeSettings, machineGeometry, wireParameters);

    }

    public MachineGeometry getMachineGeometry() {
        return machineGeometry;
    }

    public WireParameters getWireParameters() {
        return wireParameters;
    }

    public GCodeGenerator getGCodeGenerator(){
        return gCodeGenerator;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public ObservableList<Point3D> getPointsList() {
        return polyline.get();
    }

    public ObservableList<Integer> getSelectedPointsList() {
        return selectedPointsList;
    }

    public BendingSteps getBendingSteps() {
        return bendingSteps;
    }

    public ObservableList<SingleStep> getBendingStepsList() {
        return bendingSteps.get();
    }

    public ObservableList<Point3D> getView3DPointsList() {
        return view3DPointsList;
    }

    public SimpleIntegerProperty numberOfPointsSelectedProperty() {
        return numberOfPointsSelected;
    }

    public SimpleDoubleProperty minDistanceProperty() {
        return minDistance;
    }

    public SimpleDoubleProperty maxDistanceProperty() {
        return maxDistance;
    }

    public SimpleDoubleProperty preferredDistanceProperty() {
        return preferredDistance;
    }

    public SimpleStringProperty currentFilenameProperty() {
        return currentFilename;
    }

    public SimpleObjectProperty<SingleStep> selectedStepProperty() {
        return selectedStep;
    }

    public SimpleIntegerProperty selectedStepIndexProperty() {
        return selectedStepIndex;
    }

    public void setSelectedStepIndex(int selectedStepIndex) {
        this.selectedStepIndex.set(selectedStepIndex);
    }

    public void setSelectedStep(SingleStep selectedStep) {
        this.selectedStep.set(selectedStep);
    }

    private void updateSelectionInfo() {
        if(!selectedPointsList.isEmpty()) {

            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);
            numberOfPointsSelected.set(lastSelectionIndex - firstSelectionIndex + 1);

            if(selectedPointsList.size()>1){
                Point3D firstPoint = polyline.get(firstSelectionIndex);
                Point3D secondPoint = polyline.get(firstSelectionIndex + 1);

                double minDistance = firstPoint.distance(secondPoint);
                double maxDistance = minDistance;
                double currentDistance;

                for (int i = firstSelectionIndex + 1; i < lastSelectionIndex; i++) {
                    firstPoint = polyline.get(i);
                    secondPoint = polyline.get(i+1);
                    currentDistance = firstPoint.distance(secondPoint);

                    if(currentDistance<minDistance){
                        minDistance = currentDistance;
                    }
                    else if(currentDistance > maxDistance){
                        maxDistance = currentDistance;
                    }
                }
                this.minDistance.set(minDistance);
                this.maxDistance.set(maxDistance);

                double roundMinDistance = Math.round(minDistance * 10.0) / 10.0;
                preferredDistance.set(roundMinDistance);
            }
            else{
                minDistance.set(0.0);
                maxDistance.set(0.0);
                preferredDistance.set(0.0);
            }

        }
        else{
            numberOfPointsSelected.set(0);
            minDistance.set(0.0);
            maxDistance.set(0.0);
            preferredDistance.set(0.0);
        }
    }

    public void openBendFile(File selectedFile) {

        selectedPointsList.clear();

        bendingSteps.reconstruct(BendFileReader.readFile(selectedFile.getPath()));
        currentFilename.set(selectedFile.getName());
        updatePolyline();
    }

    public void openXYZFile(File selectedFile) {

        selectedPointsList.clear();

        Polyline loadedPolyline = new Polyline(XyzFileReader.readPolyline(selectedFile.getPath()));

        currentFilename.set(selectedFile.getName());
        bendingSteps.reconstruct(loadedPolyline);
        updatePolyline();
    }

    private void updatePolyline() {
        polyline.reconstruct(bendingSteps);
        updateView3DPointsList();
    }

    private void updateView3DPointsList() {
        view3DPointsList.clear();
        view3DPointsList.addAll(polyline.get());
    }

    public void updateView3DPointsList(List<Point3D> loadedPolyline) {
        view3DPointsList.clear();
        view3DPointsList.addAll(loadedPolyline);
    }

    public void changeSelectionOfPoint(String resultID) {
        int selectedID = Integer.parseInt(resultID);
        if(selectedPointsList.isEmpty()){
            selectedPointsList.add(selectedID);
        }
        else {
            int currentID;
            int index = 0;
            boolean isLastElement = true;

            while(index<selectedPointsList.size()){

                currentID = selectedPointsList.get(index);

                if(currentID>=selectedID){
                    if(currentID==selectedID){
                        selectedPointsList.remove(index);
                    }
                    else{
                        selectedPointsList.add(index,selectedID);
                    }
                    isLastElement = false;
                    break;
                }
                index++;
            }
            if(isLastElement){
                selectedPointsList.add(selectedID);
            }
        }
    }

    public void changeSelectionOfPoints(List<Integer> selectedIndices) {
        selectedPointsList.clear();
        selectedPointsList.addAll(selectedIndices);
    }

    public void addIntermediatePoints() {
        if ((numberOfPointsSelected.get() >= 3) && (preferredDistance.get()>0)) {
            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);
            double prefDist = preferredDistance.get();

            selectedPointsList.clear();
            polyline.addIntermediatePoints(firstSelectionIndex,lastSelectionIndex,prefDist);
            bendingSteps.reconstruct(polyline);
            updateView3DPointsList();
        }
    }

    public void closeFile() {
        selectedPointsList.clear();
        bendingSteps.clear();
        polyline.clear();
        view3DPointsList.clear();
        currentFilename.set("No file opened");
    }

    public String getGCodeInstructions() {
        if(polyline.get() != null) {

            MachineStepsCalculator machineStepsCalculator = new MachineStepsCalculator(machineGeometry,wireParameters);

            Steps<SingleMachineStep> machineSteps;
            machineSteps = machineStepsCalculator.calculateMachineSteps(bendingSteps);
            List<String> instructions = gCodeGenerator.generateInstructionsList(machineSteps);

            StringBuilder outputString = new StringBuilder();

            for (int i = 0; i < instructions.size(); i++) {

                outputString.append(instructions.get(i));
                outputString.append(System.lineSeparator());
            }
            return outputString.toString();
        }
        else return null;
    }

    public void moveSelectedPointsToTheTop() {
        if(numberOfPointsSelected.get()>0){
            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);

            selectedPointsList.clear();
            polyline.moveSelectedToTheStart(firstSelectionIndex,lastSelectionIndex);
            bendingSteps.reconstruct(polyline);
            updateView3DPointsList();
        }
    }

    public void moveSelectedPointsToTheEnd() {
        if(numberOfPointsSelected.get()>0){
            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);

            selectedPointsList.clear();
            polyline.moveSelectedToTheEnd(firstSelectionIndex,lastSelectionIndex);
            bendingSteps.reconstruct(polyline);
            updateView3DPointsList();
        }
    }

    public void reverseSelectedPoints() {
        if(numberOfPointsSelected.get()>0){
            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);

            selectedPointsList.clear();
            polyline.reverseSelected(firstSelectionIndex,lastSelectionIndex);
            bendingSteps.reconstruct(polyline);
            updateView3DPointsList();
        }
    }

    public void reverseAllPoints() {
        selectedPointsList.clear();
        polyline.reversePolyline();
        bendingSteps.reconstruct(polyline);
        updateView3DPointsList();
    }

    public void deleteSelectedPoints() {
        if(numberOfPointsSelected.get()>0){
            int firstSelectionIndex = selectedPointsList.get(0);
            int lastSelectionIndex = selectedPointsList.get(selectedPointsList.size()-1);

            selectedPointsList.clear();
            polyline.deleteSelected(firstSelectionIndex,lastSelectionIndex);
            bendingSteps.reconstruct(polyline);
            updateView3DPointsList();
        }
    }

    public void addManualStep(double lengthX, double angleA, double angleB) {
        if(!isZero(lengthX)) {

            bendingSteps.add(lengthX,angleA,angleB);
            updatePolyline();
        }
    }

    public void deleteSelectedStep() {
        if(selectedStepProperty().get() != null){
            bendingSteps.remove(selectedStepIndex.get());
            updatePolyline();
        }
    }

    public void animationPlay() {
        animationController.prepareAnimation(bendingSteps);
        animationController.start();
    }

    public void animationNextFrame() {
        updateView3DPointsList(animationController.getNextFrame());
    }

    public void editSegment(Double distanceX, Double angleA, Double angleB) {

        SingleStep editedStep = new SingleStep(distanceX,angleA,angleB);

        bendingSteps.set(selectedStepIndex.get(),editedStep);
        updatePolyline();
    }

    public void alternateAngles(Direction direction) {
        if(selectedStepProperty().get() != null){

            double oldAngleA = selectedStep.get().getAngleA();
            double oldAngleB = selectedStep.get().getAngleB();

            double newAngleA = oldAngleA * -1;
            double newAngleB = 0.0;

            switch (direction){
                case CLOCKWISE:
                    newAngleB = oldAngleB + 180;
                    if(newAngleB >= 360){
                        newAngleB = newAngleB - 360;
                    }
                    break;
                case COUNTERCLOCKWISE:
                    newAngleB = oldAngleB - 180;
                    if(newAngleB <= -360){
                        newAngleB = newAngleB + 360;
                    }
                    break;
            }

            SingleStep alteredStep = new SingleStep(selectedStep.get().getDistanceX(), newAngleA, newAngleB);
            int index = selectedStepIndex.get() + 1;

            bendingSteps.set(selectedStepIndex.get(),alteredStep);
            bendingSteps.changeAngleAsign(index);

            updatePolyline();
        }
    }
}
