package com.opencncbender;

import com.opencncbender.controller.*;
import com.opencncbender.model.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {


        //building dependencies

        //loading views
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
            rootLayout.setLeft(sidePanelLoader.load());
            sidePanelController = sidePanelLoader.getController();

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
            rootLayout.setCenter(view3DWindowController.initialize());
        } catch (Exception e) {

        }
        Scene mainScene = new Scene(rootLayout);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        //loading settings files
        Properties defaultMachineGeometry = new Properties();

        try {
            FileInputStream fio = new FileInputStream("machine_geometry.xml");
            defaultMachineGeometry.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("Exception occured when loading machine_geometry.xml");
            alert.setContentText("Default geometry will be created. Please check it!");
            alert.showAndWait();

            createDefaultMachineGeometry(defaultMachineGeometry);


            sidePanelController.getTabPane().getSelectionModel().select(sidePanelController.getMachineTab());
            //TODO: Machine geometry wizard
        }


        DataModel dataModel = new DataModel(defaultMachineGeometry);
        mainWindowController.initModel(dataModel);
        ribbonController.initModel(dataModel);
        sidePanelController.initModel(dataModel);
        bendingStepsTableTabController.initModel(dataModel);
        pointsTabController.initModel(dataModel);
        machineParametersTabController.initModel(dataModel);
        wireParametersTabController.initModel(dataModel);
        view3DWindowController.initModel(dataModel);



        //mainWindowController.getSplitPane().setDividerPosition(0,0);
    }

    private void createDefaultMachineGeometry(Properties defaultMachineGeometry) {

        defaultMachineGeometry.setProperty("bendingRadius","7.5");
        defaultMachineGeometry.setProperty("rodRadius","2.75");
        defaultMachineGeometry.setProperty("pinRadius","2.75");
        defaultMachineGeometry.setProperty("pinOffset","0.0");
        defaultMachineGeometry.setProperty("pinSpacing","0.0");
    }

    public static void main(String[] args) { launch(args); }
}
