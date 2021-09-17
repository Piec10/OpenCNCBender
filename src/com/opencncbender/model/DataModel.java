package com.opencncbender.model;

import com.opencncbender.logic.*;
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

    private final AnimationController animationController = new AnimationController(this);

    public DataModel(Properties defaultMachineGeometry) {
        selectedPointsList.addListener((ListChangeListener<Integer>) change ->{
            updateSelectionInfo();
        });

        this.machineGeometry = new MachineGeometry(Double.parseDouble(defaultMachineGeometry.getProperty("bendingRadius")),
                Double.parseDouble(defaultMachineGeometry.getProperty("rodRadius")),
                Double.parseDouble(defaultMachineGeometry.getProperty("pinRadius")),
                Double.parseDouble(defaultMachineGeometry.getProperty("pinOffset")),
                Double.parseDouble(defaultMachineGeometry.getProperty("pinSpacing")));

        this.wireParameters = new WireParameters(1.6, 22);

    }

    public MachineGeometry getMachineGeometry() {
        return machineGeometry;
    }

    public WireParameters getWireParameters() {
        return wireParameters;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public ObservableList<Point3D> getPointsList() {
        return polyline.get();
        //return pointsList;
    }

    public ObservableList<Integer> getSelectedPointsList() {
        return selectedPointsList;
    }

    public ObservableList<SingleStep> getBendingStepsList() {
        return bendingSteps.get();
        //return bendingStepsList;
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

    public void openXYZFile(File selectedFile) {

        selectedPointsList.clear();

        Polyline loadedPolyline = new Polyline(XyzFileReader.readPolyline(selectedFile.getPath()));

        currentFilename.set(selectedFile.getName());
        bendingSteps.reconstruct(loadedPolyline);
        updatePolyline();
    }

    /*private void updateObservableLists() {
        pointsList.clear();
        pointsList.addAll(polyline.get());
        bendingStepsList.clear();
        bendingStepsList.addAll(bendingSteps.get());
        view3DPointsList.clear();
        view3DPointsList.addAll(polyline.get());
    }*/


    private void updatePolyline() {
        polyline.reconstruct(bendingSteps);
        updateView3DPointsList();
    }

    /*private void updateBendingStepsList(BendingSteps bendingSteps) {
        bendingStepsList.clear();
        bendingStepsList.addAll(bendingSteps.getAsPoint3DList());
    }*/

    /*private void updatePointsList(Polyline loadedPolyline) {
        pointsList.clear();
        pointsList.addAll(loadedPolyline.get());
    }*/

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
            GCodeGenerator gCodeGenerator = new GCodeGenerator(machineGeometry, wireParameters,
                    5,
                    5,
                    10000,
                    18000);

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

    public void setMachineGeometryProperties(Properties defaultMachineGeometry) {
        machineGeometry.setBendingRadius(Double.parseDouble(defaultMachineGeometry.getProperty("bendingRadius")));
        machineGeometry.setPinRadius(Double.parseDouble(defaultMachineGeometry.getProperty("pinRadius")));
        machineGeometry.setRodRadius(Double.parseDouble(defaultMachineGeometry.getProperty("rodRadius")));
        machineGeometry.setPinSpacing(Double.parseDouble(defaultMachineGeometry.getProperty("pinSpacing")));
        machineGeometry.setPinOffset(Double.parseDouble(defaultMachineGeometry.getProperty("pinOffset")));
    }
}
