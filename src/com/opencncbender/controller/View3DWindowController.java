package com.opencncbender.controller;


import com.opencncbender.model.DataModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.input.PickResult;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.List;


public class View3DWindowController {

    private DataModel dataModel;

    private Group view3DGroup = new Group();
    private SubScene subScene;
    private PerspectiveCamera camera = new PerspectiveCamera(true);
    
    private final ObservableList<Sphere> sphereList = FXCollections.observableArrayList();
    private final ObservableList<Cylinder> connectionsList = FXCollections.observableArrayList();

    private Point3D boundaryMinPoint, boundaryMaxPoint;

    final PhongMaterial unselectedColor = new PhongMaterial(Color.WHITE);
    final PhongMaterial selectedColor = new PhongMaterial(Color.RED);
    final PhongMaterial xAxisColor = new PhongMaterial(Color.RED);
    final PhongMaterial yAxisColor = new PhongMaterial(Color.GREEN);
    final PhongMaterial zAxisColor = new PhongMaterial(Color.BLUE);


    final double defaultViewAngleX = 120;
    final double defaultViewAngleY = 45;
    final double defaultViewDistanceZ = 0;
    final double defaultPanDistanceX = 0;
    final double defaultPanDistanceY = 0;

    //Tracks drag starting point for x and y
    private double anchorX, anchorY;
    //Keep track of current angle and pan distance for x and y
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private double anchorPanDistanceX = 0;
    private double anchorPanDistanceY = 0;
    //We will update these after drag. Using JavaFX property to bind with object
    private final DoubleProperty angleX = new SimpleDoubleProperty(defaultViewAngleX);
    private final DoubleProperty angleY = new SimpleDoubleProperty(defaultViewAngleY);
    private final DoubleProperty distanceZ = new SimpleDoubleProperty(defaultViewDistanceZ);
    private final DoubleProperty panDistanceX = new SimpleDoubleProperty(defaultPanDistanceX);
    private final DoubleProperty panDistanceY = new SimpleDoubleProperty(defaultPanDistanceY);
    private VBox vBox;


    public View3DWindowController() {
    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        dataModel.getView3DPointsList().addListener((ListChangeListener<Point3D>) change ->{
            generatePointList(dataModel.getView3DPointsList());
        });

        dataModel.getSelectedPointsList().addListener((ListChangeListener<Integer>) change -> {
            selectPoints(dataModel.getSelectedPointsList());
        });
    }

    public StackPane initialize() {
        
        Bindings.bindContent(view3DGroup.getChildren(),sphereList);
        Bindings.bindContent(view3DGroup.getChildren(),connectionsList);

        Group originCS = createOriginCS();
        view3DGroup.getChildren().add(originCS);

        //PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.translateXProperty().set(0);
        camera.translateYProperty().set(0);
        //camera.translateYProperty().set(-20);
        camera.translateZProperty().set(-300);
        camera.setNearClip(0.01);
        camera.setFarClip(2000);

        /*pCamera.translateXProperty().set(0);
        pCamera.translateYProperty().set(0);
        //camera.translateYProperty().set(-20);
        pCamera.translateZProperty().set(-300);
        pCamera.setNearClip(0.01);
        pCamera.setFarClip(2000);*/

        StackPane view3DPane = new StackPane();


        subScene = new SubScene(view3DGroup,-1,-1,true, SceneAntialiasing.BALANCED);
        subScene.widthProperty().bind(view3DPane.widthProperty());
        subScene.heightProperty().bind(view3DPane.heightProperty());
        subScene.setManaged(false);
        subScene.setCamera(camera);
        subScene.setFill(Color.LIGHTGRAY);
        initMouseControl(view3DGroup,subScene);

        view3DPane.getChildren().add(subScene);

        vBox = new VBox();
        vBox.setSpacing(1);
        vBox.setPrefWidth(70);
        vBox.setManaged(false);


        Button isometricViewButton = new Button("Isometric");
        isometricViewButton.setOnAction(actionEvent -> setIsometricView());
        isometricViewButton.setMinWidth(vBox.getPrefWidth());
        
        Button topViewButton = new Button("Top");
        topViewButton.setOnAction(actionEvent -> setTopView());
        topViewButton.setMinWidth(vBox.getPrefWidth());
        
        Button frontViewButton = new Button("Front");
        frontViewButton.setOnAction(actionEvent -> setFrontView());
        frontViewButton.setMinWidth(vBox.getPrefWidth());

        //vBox.getChildren().addAll(isometricViewButton,topViewButton,frontViewButton);

        view3DPane.getChildren().add(vBox);

        return view3DPane;
    }

    private Group createOriginCS() {
        Point3D origin = new Point3D(0,0,0);
        Point3D xAxisPoint = new Point3D(10,0,0);
        Point3D yAxisPoint = new Point3D(0,10,0);
        Point3D zAxisPoint = new Point3D(0,0,10);

        Cylinder xAxis = createLine(origin,xAxisPoint,0.1);
        Cylinder yAxis = createLine(origin,yAxisPoint,0.1);
        Cylinder zAxis = createLine(origin,zAxisPoint,0.1);

        xAxis.setMaterial(xAxisColor);
        yAxis.setMaterial(yAxisColor);
        zAxis.setMaterial(zAxisColor);

        Group originCS = new Group();
        originCS.getChildren().add(xAxis);
        originCS.getChildren().add(yAxis);
        originCS.getChildren().add(zAxis);

        return originCS;
    }

    private void initMouseControl(Group group, SubScene scene) {
        Rotate xRotate;
        Rotate yRotate;
        Translate zTranslate;
        Translate panTranslate;
        group.getTransforms().addAll(
                zTranslate = new Translate(0,0,0),
                panTranslate = new Translate(0,0,0),
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Z_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);
        zTranslate.zProperty().bind(distanceZ);
        panTranslate.xProperty().bind(panDistanceX);
        panTranslate.yProperty().bind(panDistanceY);

        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
            anchorPanDistanceX = panDistanceX.get();
            anchorPanDistanceY = panDistanceY.get();
        });

        scene.setOnMouseDragged(event -> {
            if(event.isPrimaryButtonDown()) {
                angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
                angleY.set(anchorAngleY + anchorX - event.getSceneX());
            }
            else if(event.isSecondaryButtonDown()) {
                panDistanceX.set(anchorPanDistanceX - (anchorX - event.getSceneX()));
                panDistanceY.set(anchorPanDistanceY - (anchorY - event.getSceneY()));
            }
        });

        scene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            distanceZ.set(distanceZ.get() + delta);
        });

        scene.setOnMouseReleased(event -> {
            PickResult pickResult = event.getPickResult();
            if(pickResult != null && pickResult.getIntersectedNode() instanceof Sphere) {

                String resultID = pickResult.getIntersectedNode().getId();
                dataModel.changeSelectionOfPoint(resultID);

                //viewController.changeSelectionOfPoint(resultID);
                //polylineTabController.changeSelectionOfPoint(resultID);
            }
        });
    }

    public void generatePointList(List<Point3D> polyline){

        sphereList.clear();
        connectionsList.clear();

        if(!polyline.isEmpty()) {
            for (int i = 0; i < polyline.size(); i++) {
                Sphere sphere = new Sphere(0.8);
                sphere.translateXProperty().set(polyline.get(i).getX());
                sphere.translateYProperty().set(polyline.get(i).getY());
                sphere.translateZProperty().set(polyline.get(i).getZ());
                sphere.setId(Integer.toString(i));
                sphereList.add(sphere);
            }
            for (int i = 0; i < polyline.size() - 1; i++) {
                Cylinder line = createLine(polyline.get(i), polyline.get(i + 1), 0.25);
                connectionsList.add(line);
            }
            //calculateBoundaryPoints(polyline);
            //drawBoundary();
        }
    }

    private Cylinder createLine(Point3D origin, Point3D target, double thickness) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(thickness, height);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    private void drawBoundary() {

        double width,height,depth;
        width = boundaryMaxPoint.getX() - boundaryMinPoint.getX();
        height = boundaryMaxPoint.getY() - boundaryMinPoint.getY();
        depth = boundaryMaxPoint.getZ() - boundaryMinPoint.getZ();

        /*boundaryBox = new Box(width,height,depth);
        boundaryBox.translateXProperty().set(boundaryMinPoint.getX() + width/2);
        boundaryBox.translateYProperty().set(boundaryMinPoint.getY() + height/2);
        boundaryBox.translateZProperty().set(boundaryMinPoint.getZ() + depth/2);
        boundaryBox.drawModeProperty().set(DrawMode.LINE);
        */
    }

    private void calculateBoundaryPoints(List<Point3D> polyline) {
        double minX, minY, minZ, maxX, maxY, maxZ;
        Point3D currentPoint = polyline.get(0);
        minX = maxX = currentPoint.getX();
        minY = maxY = currentPoint.getY();
        minZ = maxZ = currentPoint.getZ();

        for(int i=1; i < polyline.size(); i++){
            currentPoint = polyline.get(i);

            if(currentPoint.getX() < minX){
                minX = currentPoint.getX();
            }
            else if(currentPoint.getX() > maxX){
                maxX = currentPoint.getX();
            }

            if(currentPoint.getY() < minY){
                minY = currentPoint.getY();
            }
            else if(currentPoint.getY() > maxY){
                maxY = currentPoint.getY();
            }

            if(currentPoint.getZ() < minZ){
                minZ = currentPoint.getZ();
            }
            else if(currentPoint.getZ() > maxZ){
                maxZ = currentPoint.getZ();
            }
        }

        boundaryMinPoint = new Point3D(minX, minY, minZ);
        boundaryMaxPoint = new Point3D(maxX, maxY, maxZ);
    }



    public void selectPoints(List<Integer> selectedPoints) {

        deselectAll();

        if(!selectedPoints.isEmpty()) {

            int firstSelectionIndex = selectedPoints.get(0);
            int lastSelectionIndex = selectedPoints.get(selectedPoints.size() - 1);
            for (int i = firstSelectionIndex; i <= lastSelectionIndex; i++) {
                selectPoint(i);
            }
        }
    }

    private void selectPoint(int i) {
        sphereList.get(i).setMaterial(selectedColor);
    }

    private void deselectAll() {
        for(Sphere sphere : sphereList){
            sphere.setMaterial(unselectedColor);
        }
    }

    public void setIsometricView(){
        distanceZ.set(defaultViewDistanceZ);
        angleX.set(defaultViewAngleX);
        angleY.set(defaultViewAngleY);
        panDistanceX.set(defaultPanDistanceX);
        panDistanceY.set(defaultPanDistanceY);
    }

    private void setFrontView() {
        angleX.set(90);
        angleY.set(0);
    }

    private void setTopView() {
        angleX.set(180);
        angleY.set(0);
    }

    public void clear() {
        sphereList.clear();
        connectionsList.clear();
    }

    public VBox getVBox() {
        return vBox;
    }
}
