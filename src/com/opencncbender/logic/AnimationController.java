package com.opencncbender.logic;

import com.opencncbender.model.DataModel;
import javafx.geometry.Point3D;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;

public class AnimationController {

    private DataModel dataModel;
    private AnimationTimerExt timer = new AnimationTimerExt(40) {

        @Override
        public void handle() {
            dataModel.updateView3DPointsList(getNextFrame());
        }
    };

    private List<List<Point3D>> frames = new ArrayList<>();
    private int currentFrame = 0;
    private int FPS = 25;
    private double wireFeedrate = 2000;
    private double angleFeedrate = 4000;
    private double distancePerFrame;
    private double anglePerFrame;


    public AnimationController(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public void prepareAnimation(BendingSteps currentBendingSteps) {

        frames.clear();
        Polyline animationPolyline, framePolyline;

        int framesForStep;
        double distanceX, angleA, angleB, angleBPerFrame, sign;
        animationPolyline = new Polyline();
        animationPolyline.add(new Point3D(0,0,0));

        distancePerFrame = wireFeedrate/60/FPS;
        anglePerFrame = angleFeedrate/60/FPS;

        for(int i=0; i<currentBendingSteps.size(); i++){

            distanceX = currentBendingSteps.get(i).getDistanceX();
            angleA = currentBendingSteps.get(i).getAngleA();
            angleB = currentBendingSteps.get(i).getAngleB();

            framesForStep = (int) Math.floor(distanceX/distancePerFrame);
            angleBPerFrame = angleB / framesForStep;

            for(int j=1; j<framesForStep; j++){

                framePolyline = new Polyline(animationPolyline);
                framePolyline.polylineTranslation(new Vector3D(-distancePerFrame*j,0,0));
                framePolyline.polylineXAxisRotation(Math.toRadians(angleBPerFrame*(j)));
                framePolyline.add(new Point3D(0,0,0));
                frames.add(framePolyline.get());
            }
            animationPolyline.polylineTranslation(new Vector3D(-distanceX,0,0));
            animationPolyline.polylineXAxisRotation(Math.toRadians(angleB));
            animationPolyline.add(new Point3D(0,0,0));
            framePolyline = new Polyline(animationPolyline);
            frames.add(framePolyline.get());

            framesForStep = (int) Math.floor(Math.abs(angleA/anglePerFrame));
            if(angleA > 0) sign = 1;
            else sign = -1;

            for(int j=1; j<framesForStep; j++) {
                framePolyline = new Polyline(animationPolyline);
                framePolyline.polylineZAxisRotation(Math.toRadians(sign*anglePerFrame*j));
                frames.add(framePolyline.get());
            }
            animationPolyline.polylineZAxisRotation(Math.toRadians(angleA));
            framePolyline = new Polyline(animationPolyline);
            frames.add(framePolyline.get());
        }
    }

    private void processSingleBendingStep(SingleStep singleBendingStep) {


    }

    public List<Point3D> getFrame(int index) {
        return frames.get(index);
    }

    public List<Point3D> getNextFrame() {
        currentFrame++;
        if(currentFrame==frames.size()){
            currentFrame = 0;
            timer.stop();
            return frames.get(frames.size()-1);
        }
        else return frames.get(currentFrame-1);
    }

    public void start() {
        timer.start();
    }
}