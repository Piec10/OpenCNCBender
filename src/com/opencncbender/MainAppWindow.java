package com.opencncbender;

import com.opencncbender.controller.*;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainAppWindow {

    private final VBox vBox = new VBox();
    private DataModel dataModel;

    private boolean newSegmentWindowOpened = false;
    private boolean editSegmentWindowOpened = false;

    public MainAppWindow(Stage primaryStage, DataModel dataModel) {

        this.dataModel = dataModel;

        primaryStage.setTitle("Open CNC Bender");
        primaryStage.setMaximized(true);

        BorderPane rootLayout = null;

        MainWindowController mainWindowController = null;
        RibbonController ribbonController = null;
        SidePanelController sidePanelController = null;
        BendingStepsTableTabController bendingStepsTableTabController = null;
        PointsTabController pointsTabController = null;
        MachineParametersTabController machineParametersTabController = null;
        WireParametersTabController wireParametersTabController = null;
        View3DWindowController view3DWindowController = null;

        try {
            FXMLLoader mainWindowLoader = new FXMLLoader();
            mainWindowLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/MainWindow.fxml"));
            rootLayout = mainWindowLoader.load();
            mainWindowController = mainWindowLoader.getController();

            FXMLLoader ribbonLoader = new FXMLLoader();
            ribbonLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/Ribbon.fxml"));
            mainWindowController.getvBox().getChildren().add(ribbonLoader.load());
            //ribbonLoader.load();
            ribbonController = ribbonLoader.getController();

            FXMLLoader sidePanelLoader = new FXMLLoader();
            sidePanelLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/SidePanel.fxml"));

            //hBox = new HBox();
            //hBox.getChildren().add(sidePanelLoader.load());
            //rootLayout.setLeft(hBox);
            rootLayout.setLeft(sidePanelLoader.load());
            sidePanelController = sidePanelLoader.getController();
            //ribbonController.setHBox(hBox);


            FXMLLoader bendingStepsTableTabLoader = new FXMLLoader();
            bendingStepsTableTabLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/BendingStepsTableTab.fxml"));
            sidePanelController.getBendingStepsTab().setContent(bendingStepsTableTabLoader.load());
            bendingStepsTableTabController = bendingStepsTableTabLoader.getController();

            FXMLLoader pointsTabLoader = new FXMLLoader();
            pointsTabLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/PointsTab.fxml"));
            sidePanelController.getPointsTab().setContent(pointsTabLoader.load());
            pointsTabController = pointsTabLoader.getController();

            FXMLLoader machineParametersTabLoader = new FXMLLoader();
            machineParametersTabLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/MachineParametersTab.fxml"));
            sidePanelController.getMachineTab().setContent(machineParametersTabLoader.load());
            machineParametersTabController = machineParametersTabLoader.getController();

            FXMLLoader wireParametersTabLoader = new FXMLLoader();
            wireParametersTabLoader.setLocation(MainApp.class.getResource("/com/opencncbender/resources/fxml/WireParametersTab.fxml"));
            sidePanelController.getWireTab().setContent(wireParametersTabLoader.load());
            wireParametersTabController = wireParametersTabLoader.getController();

            view3DWindowController = new View3DWindowController();

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(vBox);
            anchorPane.setManaged(false);

            StackPane stackPane = view3DWindowController.initialize();

            stackPane.getChildren().add(anchorPane);

            rootLayout.setCenter(stackPane);
            //ribbonController.setVBox(vBox);


        } catch (Exception e) {

        }
        Scene mainScene = new Scene(rootLayout);
        primaryStage.setScene(mainScene);
        primaryStage.show();


        mainWindowController.initModel(dataModel);
        ribbonController.initModel(dataModel, this);
        sidePanelController.initModel(dataModel);
        bendingStepsTableTabController.initModel(dataModel);
        pointsTabController.initModel(dataModel);
        machineParametersTabController.initModel(dataModel);
        wireParametersTabController.initModel(dataModel);
        view3DWindowController.initModel(dataModel);
    }

    public void openSegmentWindow(ActionType type) {

        switch (type){
            case NEW:
                if(!newSegmentWindowOpened){
                    vBox.getChildren().clear();
                    editSegmentWindowOpened = false;
                    createSegmentWindow(type);
                    newSegmentWindowOpened = true;
                }
                break;
            case EDIT:
                if(!editSegmentWindowOpened){
                vBox.getChildren().clear();
                newSegmentWindowOpened = false;
                createSegmentWindow(type);
                editSegmentWindowOpened = true;
            }
        }
    }

    private void createSegmentWindow(ActionType type) {

        try {
            FXMLLoader segmentEditWindowLoader = new FXMLLoader();
            segmentEditWindowLoader.setLocation(getClass().getClassLoader().getResource("com/opencncbender/resources/fxml/SegmentEditWindow.fxml"));
            TitledPane pane = segmentEditWindowLoader.load();
            SegmentEditWindowController segmentEditWindowController = segmentEditWindowLoader.getController();
            segmentEditWindowController.initModel(dataModel, type, this);

            vBox.getChildren().add(pane);
        }
        catch(Exception e){

        }
    }

    public void close(SegmentEditWindowController segmentEditWindowController) {
        vBox.getChildren().remove(segmentEditWindowController.getTitledPane());
        switch (segmentEditWindowController.getType()){
            case NEW:
                newSegmentWindowOpened = false;
                break;
            case EDIT:
                editSegmentWindowOpened = false;
                break;
        }
    }
}
