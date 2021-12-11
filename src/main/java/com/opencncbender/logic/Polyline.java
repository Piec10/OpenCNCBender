package com.opencncbender.logic;

import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

import static com.opencncbender.util.IsZeroComparator.isZero;

public class Polyline extends MultiselectionEditableObservableList<Point3D> {

    public Polyline() {
    }

    public Polyline(List<Point3D> polyline) {
        list.addAll(polyline);
    }

    public Polyline(BendingSteps bendingSteps) {
        for(int i=0; i< bendingSteps.size(); i++){
            addPointsForBendingStep(bendingSteps.get(i));
        }
    }

    public Polyline(Polyline polyline) {
        list.addAll(polyline.get());
    }

    public void reconstruct(List<Point3D> polyline){
        list.clear();
        list.addAll(polyline);
    }

    public void reconstruct(BendingSteps bendingSteps){
        list.clear();

        for(int i=0; i< bendingSteps.size(); i++){
            addPointsForBendingStep(bendingSteps.get(i));
        }
    }

    private void addPointsForBendingStep(SingleStep newBendingStep) {

        if(list.isEmpty()){
            list.add(new Point3D(0,0,0));
        }
        polylineTranslation(new Vector3D(-newBendingStep.getDistanceX(),0,0));

        if(!isZero(newBendingStep.getAngleB())){
            polylineXAxisRotation(Math.toRadians(newBendingStep.getAngleB()));
        }
        if(!isZero(newBendingStep.getAngleA())){
            polylineZAxisRotation(Math.toRadians(newBendingStep.getAngleA()));
        }
        list.add(new Point3D(0,0,0));
    }

    public String getAsString(){

        StringBuilder outputString = new StringBuilder();

        if(list != null) {
            for (int i = 0; i < list.size(); i++) {

                Point3D currentPoint = list.get(i);
                outputString.append(currentPoint.getX());
                outputString.append(',');
                outputString.append(currentPoint.getY());
                outputString.append(',');
                outputString.append(currentPoint.getZ());
                outputString.append(System.lineSeparator());
            }
            return outputString.toString();
        }
        else return null;
    }

    public void addIntermediatePoints(int firstSelectionIndex, int lastSelectionIndex, double prefferedDistance) {
        if(list != null) {
            List<Point3D> head, middle, modifiedMiddle, tail;
            head = new ArrayList<>(list.subList(0,firstSelectionIndex));
            middle = list.subList(firstSelectionIndex,lastSelectionIndex+1);
            tail = new ArrayList<>(list.subList(lastSelectionIndex+1,list.size()));

            modifiedMiddle = calculateIntermediatePoints(middle,prefferedDistance);

            list.clear();
            list.addAll(head);
            list.addAll(modifiedMiddle);
            list.addAll(tail);
        }
    }

    private List<Point3D> calculateIntermediatePoints(List<Point3D> middle, double preferredDistance) {

        List<Point3D> modifiedMiddle = new ArrayList<>();

        Point3D firstPoint = middle.get(0);
        Point3D secondPoint = middle.get(1);
        Point3D thirdPoint = middle.get(2);

        Vector3D firstPointV = makeVector3D(firstPoint);
        Vector3D secondPointV = makeVector3D(secondPoint);
        Vector3D thirdPointV = makeVector3D(thirdPoint);

        Vector3D arcCenterPointV = calculateArcCenter(firstPointV,secondPointV,thirdPointV);

        modifiedMiddle.add(firstPoint);
        modifiedMiddle.addAll(arcIntermediatePoints(arcCenterPointV,firstPointV,secondPointV,preferredDistance));
        modifiedMiddle.add(secondPoint);

        modifiedMiddle.addAll(arcIntermediatePoints(arcCenterPointV,secondPointV,thirdPointV,preferredDistance));
        modifiedMiddle.add(thirdPoint);

        for(int i=1; i < middle.size()-2; i++){

            firstPoint = middle.get(i);
            secondPoint = middle.get(i+1);
            thirdPoint = middle.get(i+2);

            firstPointV = makeVector3D(firstPoint);
            secondPointV = makeVector3D(secondPoint);
            thirdPointV = makeVector3D(thirdPoint);

            arcCenterPointV = calculateArcCenter(firstPointV,secondPointV,thirdPointV);

            modifiedMiddle.addAll(arcIntermediatePoints(arcCenterPointV,secondPointV,thirdPointV,preferredDistance));
            modifiedMiddle.add(thirdPoint);
        }

        return modifiedMiddle;
    }

    private List<Point3D> arcIntermediatePoints(Vector3D arcCenterPointV, Vector3D firstPointV, Vector3D secondPointV, double preferredDistance){

        List<Point3D> intermediatePoints = new ArrayList<>();

        Vector3D centerToFirstVector = calculateVector(arcCenterPointV,firstPointV);
        Vector3D centerToSecondVector = calculateVector(arcCenterPointV,secondPointV);

        double arcRadius = arcCenterPointV.distance(firstPointV);
        double arcAngle = Vector3D.angle(centerToFirstVector,centerToSecondVector);
        double arcLength = Math.toDegrees(arcAngle)/360 * 2 * arcRadius * Math.PI;
        int numberOfDivisions = (int) Math.round(arcLength / preferredDistance);
        double dividedAngle = arcAngle / numberOfDivisions;

        Plane commonPlane = new Plane(arcCenterPointV,firstPointV,secondPointV, 0.0001);
        Rotation rotation = new Rotation(commonPlane.getNormal(),dividedAngle);

        Vector3D divisionVector;
        divisionVector = rotation.applyTo(centerToFirstVector);
        Vector3D newPointV;

        for(int i=1; i<numberOfDivisions; i++){
            newPointV = arcCenterPointV.add(divisionVector);
            Point3D newPoint = makePoint3D(newPointV);
            intermediatePoints.add(newPoint);
            divisionVector = rotation.applyTo(divisionVector);
        }

        return intermediatePoints;
    }

    private Vector3D calculateVector(Vector3D firstPointV, Vector3D secondPointV) {
        return new Vector3D(secondPointV.getX()-firstPointV.getX(),secondPointV.getY()-firstPointV.getY(),secondPointV.getZ()-firstPointV.getZ());
    }

    private Vector3D makeVector3D(Point3D point) {
        return new Vector3D(point.getX(),point.getY(),point.getZ());
    }

    private Point3D makePoint3D(Vector3D point){
        return new Point3D(point.getX(),point.getY(),point.getZ());
    }

    private Vector3D calculateArcCenter(Vector3D firstPointV, Vector3D secondPointV, Vector3D thirdPointV){

        Plane commonPlane = new Plane(firstPointV,secondPointV,thirdPointV, 0.0001);

        Point3D firstPoint = makePoint3D(firstPointV);
        Point3D secondPoint = makePoint3D(secondPointV);
        Point3D thirdPoint = makePoint3D(thirdPointV);

        Point3D firstMiddlePoint = firstPoint.midpoint(secondPoint);
        Point3D secondMiddlePoint = secondPoint.midpoint(thirdPoint);
        Vector3D firstMiddlePointV = makeVector3D(firstMiddlePoint);
        Vector3D secondMiddlePointV = makeVector3D(secondMiddlePoint);

        Vector3D firstVector = calculateVector(firstPointV,secondPointV);
        Vector3D secondVector = calculateVector(secondPointV,thirdPointV);

        Plane firstCrossingPlane = new Plane(firstMiddlePointV,firstVector,0.0001);
        Plane secondCrossingPlane = new Plane(secondMiddlePointV,secondVector,0.0001);

        Vector3D arcCenterPointV = Plane.intersection(commonPlane,firstCrossingPlane,secondCrossingPlane);

        return arcCenterPointV;
    }

    public void moveToOrigin(){

        if(!list.isEmpty()) {
            Point3D firstPoint = list.get(0);

            if (!isPointAtOrigin(firstPoint)) {
                Vector3D translationToOrigin = new Vector3D(-firstPoint.getX(), -firstPoint.getY(), -firstPoint.getZ());
                polylineTranslation(translationToOrigin);
            }
        }
    }

    public void polylineTranslation(Vector3D translation) {

        Point3D oldPoint, newPoint;

        for(int i=0; i < list.size(); i++){

            oldPoint = list.get(i);
            newPoint = new Point3D(oldPoint.getX() + translation.getX(),
                    oldPoint.getY() + translation.getY(),
                    oldPoint.getZ() + translation.getZ());
            list.set(i,newPoint);
        }
    }

    public void polylineZAxisRotation(double angle) {

        Point3D oldPoint, newPoint;
        double x,y,z;

        for(int i=0; i < list.size(); i++){

            oldPoint = list.get(i);
            x = Math.cos(angle)*oldPoint.getX() - Math.sin(angle)*oldPoint.getY();
            y = Math.sin(angle)*oldPoint.getX() + Math.cos(angle)*oldPoint.getY();
            z = oldPoint.getZ();

            newPoint = new Point3D(x,y,z);
            list.set(i,newPoint);
        }
    }

    public void polylineXAxisRotation(double angle) {

        Point3D oldPoint, newPoint;
        double x,y,z;

        for(int i=0; i < list.size(); i++){

            oldPoint = list.get(i);
            x = oldPoint.getX();
            y = Math.cos(angle)*oldPoint.getY() - Math.sin(angle)*oldPoint.getZ();
            z = Math.sin(angle)*oldPoint.getY() + Math.cos(angle)*oldPoint.getZ();

            newPoint = new Point3D(x,y,z);
            list.set(i,newPoint);
        }
    }

    private boolean isPointAtOrigin(Point3D point) {

        if((Double.compare(point.getX(),0) == 0) &&
                (Double.compare(point.getY(),0) == 0) &&
                (Double.compare(point.getZ(),0) == 0)){

            return true;
        }
        else return false;
    }
}
