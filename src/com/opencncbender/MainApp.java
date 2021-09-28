package com.opencncbender;

import com.opencncbender.controller.*;
import com.opencncbender.model.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class MainApp extends Application {

    private HBox hBox;

    @Override
    public void start(Stage primaryStage) {

//loading settings files
        Properties defaultMachineGeometry = new Properties();

        try {
            FileInputStream fio = new FileInputStream("machine_geometry.xml");
            defaultMachineGeometry.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("Exception occurred when loading machine_geometry.xml");
            alert.setContentText("Default geometry will be created. Please check it!");
            alert.showAndWait();

            createDefaultMachineGeometry(defaultMachineGeometry);
            storePropertiesToXML(defaultMachineGeometry,"machine_geometry.xml");

            //sidePanelController.getTabPane().getSelectionModel().select(sidePanelController.getMachineTab());
            //TODO: Machine geometry wizard
        }

        Properties defaultWireParameters = new Properties();

        try {
            FileInputStream fio = new FileInputStream("wire_parameters.xml");
            defaultWireParameters.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("Exception occurred when loading wire_parameters.xml");
            alert.setContentText("Default parameters will be created. Please check it!");
            alert.showAndWait();

            createDefaultWireParameters(defaultWireParameters);
            storePropertiesToXML(defaultWireParameters,"wire_parameters.xml");

            //TODO: Wire parameters wizard
        }


        DataModel dataModel = new DataModel(defaultMachineGeometry,defaultWireParameters);


        MainAppWindow mainAppWindow = new MainAppWindow(primaryStage,dataModel);

    }

    private void createDefaultWireParameters(Properties defaultWireParameters) {

        defaultWireParameters.setProperty("wireDiameter","1.6");
        defaultWireParameters.setProperty("overbendAngle","22.0");
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
