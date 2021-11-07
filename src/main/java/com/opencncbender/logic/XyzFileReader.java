package com.opencncbender.logic;

import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piec on 2021-01-24.
 */
public class XyzFileReader {

    public XyzFileReader() {

    }

    /*public Steps readFile(String filepath){

        ArrayList<Point3D> polyline;
        polyline = readPolyline(filepath);

        Steps<SingleStep> bendingSteps = generateStepsFromPolyline(polyline);
        return bendingSteps;
    }*/

    public BendingSteps generateStepsFromPolyline(List<Point3D> polyline) {
        BendingSteps bendingSteps = new BendingSteps();

        Point3D currentPoint;
        Vector3D translationToPoint;
        double distanceX, angleA, angleB;
        double lastAngleA;

        Point3D firstPoint = polyline.get(0);

        if(!isPointAtOrigin(firstPoint)){

            Vector3D translationToFirstPoint = new Vector3D(-firstPoint.getX(),-firstPoint.getY(),-firstPoint.getZ());
            originTranslation(polyline,translationToFirstPoint);
        }

        Point3D nextPoint = polyline.get(1);
        double rotationAngleXAxis = Math.atan2(nextPoint.getZ(),nextPoint.getY());

        if(!isZero(rotationAngleXAxis)){

            originRotationXAxis(polyline,-rotationAngleXAxis);
        }

        nextPoint = polyline.get(1);
        double rotationAngleZAxis = Math.atan2(nextPoint.getY(),nextPoint.getX());
        lastAngleA = rotationAngleZAxis;

        if(!isZero(rotationAngleZAxis)){

            originRotationZAxis(polyline,-rotationAngleZAxis);
        }

        for(int i=1; i < polyline.size()-1; i++){

            currentPoint = polyline.get(i);
            distanceX = currentPoint.getX();

            translationToPoint = new Vector3D(-distanceX,0,0);
            originTranslation(polyline,translationToPoint);

            nextPoint = polyline.get(i+1);
            rotationAngleXAxis = Math.atan2(nextPoint.getZ(),nextPoint.getY());
            if(isPI(rotationAngleXAxis)){

                rotationAngleXAxis = 0.0;
            }

            if(!isZero(rotationAngleXAxis)){

                if((lastAngleA > 0) && (rotationAngleXAxis > 0)){

                    rotationAngleXAxis = rotationAngleXAxis - Math.PI;
                }
                else if((lastAngleA < 0) && (rotationAngleXAxis < 0)){

                    rotationAngleXAxis = rotationAngleXAxis + Math.PI;
                }

                originRotationXAxis(polyline,-rotationAngleXAxis);
            }

            angleB = Math.toDegrees(rotationAngleXAxis);

            nextPoint = polyline.get(i+1);
            rotationAngleZAxis = Math.atan2(nextPoint.getY(),nextPoint.getX());
            lastAngleA = rotationAngleZAxis;
            angleA = Math.toDegrees(rotationAngleZAxis);

            originRotationZAxis(polyline,-rotationAngleZAxis);

            bendingSteps.add(new SingleStep(distanceX,angleA,angleB));
        }

        return bendingSteps;
    }

    private boolean isPI(double number) {

        if((Math.PI - Math.abs(number)) < 0.0001) return true;
        else return false;
    }

    public static List<Point3D> readPolyline(String filepath){

        ArrayList<Point3D> polyline = new ArrayList();

        try(BufferedReader inputFile = new BufferedReader(new FileReader(filepath))) {

            String line;
            Point3D currentPoint;
            Point3D lastPoint = new Point3D(0,0,0);

            while((line = inputFile.readLine()) != null){

                String[] pointCoord = line.split(",");

                currentPoint = new Point3D(Double.parseDouble(pointCoord[0]),
                        Double.parseDouble(pointCoord[1]),
                        Double.parseDouble(pointCoord[2]));

                if(polyline.size() == 0){

                    polyline.add(currentPoint);
                }
                else if(pointIsNotEqual(currentPoint,lastPoint)){

                    polyline.add(currentPoint);
                }

                lastPoint = currentPoint;
            }

        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return polyline;
    }

    private static boolean pointIsNotEqual(Point3D currentPoint, Point3D lastPoint){

        if((Double.compare(currentPoint.getX(),lastPoint.getX()) == 0) &&
                (Double.compare(currentPoint.getY(),lastPoint.getY()) == 0) &&
                (Double.compare(currentPoint.getZ(),lastPoint.getZ()) == 0)){

            return false;
        }
        else return true;
    }

    private boolean isZero(double number){

        if(Math.abs(number) < 0.0001) return true;
        else return false;
    }

    private void originRotationZAxis(List<Point3D> polyline, double rotationAngleZAxis) {

        Point3D oldPoint, newPoint;
        double x,y,z;

        for(int i=0; i < polyline.size(); i++){

            oldPoint = polyline.get(i);
            x = Math.cos(rotationAngleZAxis)*oldPoint.getX() - Math.sin(rotationAngleZAxis)*oldPoint.getY();
            y = Math.sin(rotationAngleZAxis)*oldPoint.getX() + Math.cos(rotationAngleZAxis)*oldPoint.getY();
            z = oldPoint.getZ();

            newPoint = new Point3D(x,y,z);
            polyline.set(i,newPoint);
        }
    }

    private void originRotationXAxis(List<Point3D> polyline, double rotationAngleXAxis) {

        Point3D oldPoint, newPoint;
        double x,y,z;

        for(int i=0; i < polyline.size(); i++){

            oldPoint = polyline.get(i);
            x = oldPoint.getX();
            y = Math.cos(rotationAngleXAxis)*oldPoint.getY() - Math.sin(rotationAngleXAxis)*oldPoint.getZ();
            z = Math.sin(rotationAngleXAxis)*oldPoint.getY() + Math.cos(rotationAngleXAxis)*oldPoint.getZ();

            newPoint = new Point3D(x,y,z);
            polyline.set(i,newPoint);
        }
    }

    private void originTranslation(List<Point3D> polyline, Vector3D translationToFirstPoint) {

        Point3D oldPoint, newPoint;

        for(int i=0; i < polyline.size(); i++){

            oldPoint = polyline.get(i);
            newPoint = new Point3D(oldPoint.getX() + translationToFirstPoint.getX(),
                    oldPoint.getY() + translationToFirstPoint.getY(),
                    oldPoint.getZ() + translationToFirstPoint.getZ());
            polyline.set(i,newPoint);
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
