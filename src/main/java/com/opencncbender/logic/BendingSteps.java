package com.opencncbender.logic;

import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class BendingSteps extends MultiselectionEditableObservableList<SingleStep> {

    public BendingSteps() {
    }

    public BendingSteps(Polyline polyline) {
        generateStepsFromPolyline(polyline);
    }

    public void reconstruct(Polyline polyline){
        list.clear();
        generateStepsFromPolyline(polyline);
    }

    private void generateStepsFromPolyline(Polyline polyline) {

        if(polyline.size() != 0) {
            Point3D currentPoint;
            Vector3D translationToPoint;
            double distanceX, angleA, angleB;
            double lastAngleA;

            Point3D firstPoint = polyline.get(0);

            if (!isPointAtOrigin(firstPoint)) {

                Vector3D translationToFirstPoint = new Vector3D(-firstPoint.getX(), -firstPoint.getY(), -firstPoint.getZ());
                polyline.polylineTranslation(translationToFirstPoint);
                //originTranslation(polyline,translationToFirstPoint);
            }

            Point3D nextPoint = polyline.get(1);
            double rotationAngleXAxis = Math.atan2(nextPoint.getZ(), nextPoint.getY());

            if (!isZero(rotationAngleXAxis)) {

                polyline.polylineXAxisRotation(-rotationAngleXAxis);
                //originRotationXAxis(polyline,-rotationAngleXAxis);
            }

            nextPoint = polyline.get(1);
            double rotationAngleZAxis = Math.atan2(nextPoint.getY(), nextPoint.getX());
            lastAngleA = rotationAngleZAxis;

            if (!isZero(rotationAngleZAxis)) {

                polyline.polylineZAxisRotation(-rotationAngleZAxis);
                //originRotationZAxis(polyline,-rotationAngleZAxis);
            }

            for (int i = 1; i < polyline.size() - 1; i++) {

                currentPoint = polyline.get(i);
                distanceX = currentPoint.getX();

                translationToPoint = new Vector3D(-distanceX, 0, 0);
                polyline.polylineTranslation(translationToPoint);
                //originTranslation(polyline,translationToPoint);

                nextPoint = polyline.get(i + 1);
                rotationAngleXAxis = Math.atan2(nextPoint.getZ(), nextPoint.getY());
                if (isPI(rotationAngleXAxis)) {

                    rotationAngleXAxis = 0.0;
                }

                if (!isZero(rotationAngleXAxis)) {

                /*if((lastAngleA > 0) && (rotationAngleXAxis > 0)){

                    rotationAngleXAxis = rotationAngleXAxis - Math.PI;
                }
                else if((lastAngleA < 0) && (rotationAngleXAxis < 0)){

                    rotationAngleXAxis = rotationAngleXAxis + Math.PI;
                }*/

                    polyline.polylineXAxisRotation(-rotationAngleXAxis);
                    //originRotationXAxis(polyline,-rotationAngleXAxis);
                }

                angleB = Math.toDegrees(rotationAngleXAxis);

                nextPoint = polyline.get(i + 1);
                rotationAngleZAxis = Math.atan2(nextPoint.getY(), nextPoint.getX());
                lastAngleA = rotationAngleZAxis;
                angleA = Math.toDegrees(rotationAngleZAxis);

                polyline.polylineZAxisRotation(-rotationAngleZAxis);
                //originRotationZAxis(polyline,-rotationAngleZAxis);

                //list.add(new SingleStep(Math.abs(distanceX),angleA,angleB));
                list.add(new SingleStep(distanceX, angleA, angleB));
            }
        }
    }

    private boolean isPI(double number) {

        if((Math.PI - Math.abs(number)) < 0.0001) return true;
        else return false;
    }

    private boolean isPointAtOrigin(Point3D point) {

        if((Double.compare(point.getX(),0) == 0) &&
                (Double.compare(point.getY(),0) == 0) &&
                (Double.compare(point.getZ(),0) == 0)){

            return true;
        }
        else return false;
    }

    private boolean isZero(double number){

        if(Math.abs(number) < 0.0001) return true;
        else return false;
    }

    public void add(double lengthX, double angleA, double angleB){
        SingleStep newBendingStep = new SingleStep(lengthX,angleA,angleB);
        list.add(newBendingStep);
    }

    public List<Point3D> getAsPoint3DList() {
        List<Point3D> bendingStepsList = new ArrayList<>();

        for(int i=0; i < list.size(); i++){
            SingleStep currentStep = list.get(i);
            Point3D newPoint = new Point3D(currentStep.getDistanceX(),currentStep.getAngleA(),currentStep.getAngleB());
            bendingStepsList.add(newPoint);
        }
        return bendingStepsList;
    }
}
