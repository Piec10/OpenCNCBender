package com.opencncbender;

import com.opencncbender.controller.*;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MainAppWindow {

    private final VBox vBox = new VBox();
    private DataModel dataModel;

    private boolean newSegmentWindowOpened = false;
    private boolean editSegmentWindowOpened = false;


    public MainAppWindow(Stage primaryStage, DataModel dataModel, boolean previewWindowDefaultValue) {

        this.dataModel = dataModel;

        primaryStage.setTitle("Open CNC Bender");
        primaryStage.setMaximized(true);

        BorderPane rootLayout = null;

        MainWindowController mainWindowController = null;
        RibbonController ribbonController = null;
        SidePanelController sidePanelController = null;
        BendingStepsTableTabController bendingStepsTableTabController = null;
        PointsTabController pointsTabController = null;
        MachineGeometryTabController machineGeometryTabController = null;
        WireParametersTabController wireParametersTabController = null;
        GCodeSettingsTabController gCodeSettingsTabController = null;
        View3DWindowController view3DWindowController = null;

        try {
            FXMLLoader mainWindowLoader = new FXMLLoader();
            mainWindowLoader.setLocation(getClass().getResource("/fxml/MainWindow.fxml"));
            rootLayout = mainWindowLoader.load();
            mainWindowController = mainWindowLoader.getController();

            FXMLLoader ribbonLoader = new FXMLLoader();
            ribbonLoader.setLocation(getClass().getResource("/fxml/Ribbon.fxml"));
            mainWindowController.getvBox().getChildren().add(ribbonLoader.load());
            ribbonController = ribbonLoader.getController();

            FXMLLoader sidePanelLoader = new FXMLLoader();
            sidePanelLoader.setLocation(getClass().getResource("/fxml/SidePanel.fxml"));
            rootLayout.setLeft(sidePanelLoader.load());
            sidePanelController = sidePanelLoader.getController();

            FXMLLoader bendingStepsTableTabLoader = new FXMLLoader();
            bendingStepsTableTabLoader.setLocation(getClass().getResource("/fxml/BendingStepsTableTab.fxml"));
            sidePanelController.getBendingStepsTab().setContent(bendingStepsTableTabLoader.load());
            bendingStepsTableTabController = bendingStepsTableTabLoader.getController();

            FXMLLoader pointsTabLoader = new FXMLLoader();
            pointsTabLoader.setLocation(getClass().getResource("/fxml/PointsTab.fxml"));
            sidePanelController.getPointsTab().setContent(pointsTabLoader.load());
            pointsTabController = pointsTabLoader.getController();

            FXMLLoader machineParametersTabLoader = new FXMLLoader();
            machineParametersTabLoader.setLocation(getClass().getResource("/fxml/MachineParametersTab.fxml"));
            sidePanelController.getMachineTab().setContent(machineParametersTabLoader.load());
            machineGeometryTabController = machineParametersTabLoader.getController();

            FXMLLoader wireParametersTabLoader = new FXMLLoader();
            wireParametersTabLoader.setLocation(getClass().getResource("/fxml/WireParametersTab.fxml"));
            sidePanelController.getWireTab().setContent(wireParametersTabLoader.load());
            wireParametersTabController = wireParametersTabLoader.getController();

            FXMLLoader gCodeSettingsTabLoader = new FXMLLoader();
            gCodeSettingsTabLoader.setLocation(getClass().getResource("/fxml/GCodeSettingsTab.fxml"));
            sidePanelController.getgCodeTab().setContent(gCodeSettingsTabLoader.load());
            gCodeSettingsTabController = gCodeSettingsTabLoader.getController();


            view3DWindowController = new View3DWindowController();

            AnchorPane anchorPane = new AnchorPane();
            anchorPane.getChildren().add(vBox);
            anchorPane.setManaged(false);

            StackPane stackPane = view3DWindowController.initialize();

            stackPane.getChildren().add(anchorPane);

            rootLayout.setCenter(stackPane);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Scene mainScene = new Scene(rootLayout);
        primaryStage.setScene(mainScene);
        primaryStage.show();


        mainWindowController.initModel(dataModel, this, previewWindowDefaultValue);
        ribbonController.initModel(dataModel, this);
        sidePanelController.initModel(dataModel);
        bendingStepsTableTabController.initModel(dataModel);
        pointsTabController.initModel(dataModel);
        machineGeometryTabController.initModel(dataModel);
        wireParametersTabController.initModel(dataModel);
        gCodeSettingsTabController.initModel(dataModel, mainWindowController);
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
            segmentEditWindowLoader.setLocation(getClass().getResource("/fxml/SegmentEditWindow.fxml"));
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

    public void openGCodePreviewWindow(String instructions) {

        try {
            FXMLLoader gCodePreviewWindowLoader = new FXMLLoader();
            gCodePreviewWindowLoader.setLocation(getClass().getResource("/fxml/GCodePreviewWindow.fxml"));
            AnchorPane pane = gCodePreviewWindowLoader.load();
            GCodePreviewWindowController gCodePreviewWindowController = gCodePreviewWindowLoader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("G-code preview");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            gCodePreviewWindowController.initWindow(instructions, dataModel, dialogStage);

            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();
        }
        catch (Exception e){

        }
    }
}
