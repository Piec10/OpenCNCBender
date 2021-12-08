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
    private boolean preferredDistanceWindowOpened = false;
    private boolean gCodePreviewWindowOpened = false;

    private GCodePreviewWindowController gCodePreviewWindowController;
    private Stage gCodePreviewWindow = new Stage();



    public MainAppWindow(Stage primaryStage, DataModel dataModel, boolean previewWindowDefaultValue) {

        gCodePreviewWindow.setTitle("G-code preview");
        gCodePreviewWindow.initModality(Modality.WINDOW_MODAL);
        gCodePreviewWindow.setOnHiding(event -> {
            gCodePreviewWindowOpened = false;
        });

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
            mainWindowController.getRibbonPane().setContent(ribbonLoader.load());
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
            sidePanelController.getGCodeTabPane().setContent(gCodeSettingsTabLoader.load());
            //sidePanelController.getGCodeTab().setContent(gCodeSettingsTabLoader.load());
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
                    createSegmentWindow(type);
                    newSegmentWindowOpened = true;
                }
                break;
            case EDIT:
                if(!editSegmentWindowOpened){
                    createSegmentWindow(type);
                    editSegmentWindowOpened = true;
                }
                break;
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

        if(!gCodePreviewWindowOpened) {

            try {
                FXMLLoader gCodePreviewWindowLoader = new FXMLLoader();
                gCodePreviewWindowLoader.setLocation(getClass().getResource("/fxml/GCodePreviewWindow.fxml"));
                AnchorPane pane = gCodePreviewWindowLoader.load();
                gCodePreviewWindowController = gCodePreviewWindowLoader.getController();

                gCodePreviewWindowController.initWindow(instructions, dataModel, gCodePreviewWindow);

                Scene scene = new Scene(pane);
                gCodePreviewWindow.setScene(scene);
                gCodePreviewWindowOpened = true;
                gCodePreviewWindow.showAndWait();

            } catch (Exception e) {

            }
        }
        else{
            gCodePreviewWindowController.setText(instructions);
            gCodePreviewWindow.requestFocus();
        }
    }

    public void openPreferredDistanceWindow() {

        if(!preferredDistanceWindowOpened) {
            try {
                FXMLLoader prefDistWindowLoader = new FXMLLoader();
                prefDistWindowLoader.setLocation(getClass().getResource("/fxml/PreferredDistanceWindow.fxml"));
                TitledPane pane = prefDistWindowLoader.load();
                PreferredDistanceWindowController preferredDistanceWindowController = prefDistWindowLoader.getController();
                preferredDistanceWindowController.initModel(dataModel, this);

                vBox.getChildren().add(pane);
                preferredDistanceWindowOpened = true;
            } catch (Exception e) {

            }
        }
    }

    public void close(PreferredDistanceWindowController preferredDistanceWindowController) {
        vBox.getChildren().remove(preferredDistanceWindowController.getTitledPane());
        preferredDistanceWindowOpened = false;
    }
}
