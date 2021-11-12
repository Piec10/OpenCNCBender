package com.opencncbender;

import com.opencncbender.model.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        AnchorPane splashScreenPane = null;
        try {
            FXMLLoader splashScreenLoader = new FXMLLoader();
            splashScreenLoader.setLocation(getClass().getResource("/fxml/SplashScreen.fxml"));
            splashScreenPane = splashScreenLoader.load();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //Stage dialogStage = new Stage();
        Scene splashScreenScene = new Scene(splashScreenPane);
        //dialogStage.setScene(splashScreenScene);
        //dialogStage.showAndWait();
        //primaryStage.setScene(splashScreenScene);

        //primaryStage.show();



        //loading settings files
        Properties defaultMachineGeometry = new Properties();
        try {
            FileInputStream fio = new FileInputStream("machine_geometry.xml");
            defaultMachineGeometry.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("No machine geometry settings found.");
            alert.setContentText("Default geometry will be created. Please check it!");
            alert.showAndWait();

            createDefaultMachineGeometry(defaultMachineGeometry);
            storePropertiesToXML(defaultMachineGeometry,"machine_geometry.xml");

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
            alert.setHeaderText("No wire parameters found.");
            alert.setContentText("Default parameters will be created. Please check it!");
            alert.showAndWait();

            createDefaultWireParameters(defaultWireParameters);
            storePropertiesToXML(defaultWireParameters,"wire_parameters.xml");

            //TODO: Wire parameters wizard
        }

        Properties defaultGCodeSettings = new Properties();
        try {
            FileInputStream fio = new FileInputStream("gcode_settings.xml");
            defaultGCodeSettings.loadFromXML(fio);
            fio.close();
        } catch (IOException e) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("No G-code settings found.");
            alert.setContentText("Default settings will be created. Please check it!");
            alert.showAndWait();

            createDefaultGCodeSettings(defaultGCodeSettings);
            storePropertiesToXML(defaultGCodeSettings,"gcode_settings.xml");

            //TODO: Wire parameters wizard
        }

        DataModel dataModel = new DataModel(defaultMachineGeometry,defaultWireParameters,defaultGCodeSettings,this);

        boolean previewWindowDefaultValue;
        try{
            previewWindowDefaultValue = Boolean.parseBoolean(defaultGCodeSettings.getProperty("showPreviewWindow"));
        }
        catch (Exception e){
            previewWindowDefaultValue = false;
        }

        //PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
        //pauseTransition.play();
        /*try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //primaryStage.close();

        //dialogStage.close();
        new MainAppWindow(primaryStage, dataModel, previewWindowDefaultValue);


    }

    private void createDefaultGCodeSettings(Properties defaultGCodeSettings) {

        defaultGCodeSettings.setProperty("showPreviewWindow","false");
    }

    private void createDefaultWireParameters(Properties defaultWireParameters) {

        defaultWireParameters.setProperty("wireDiameter","1.6");
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
