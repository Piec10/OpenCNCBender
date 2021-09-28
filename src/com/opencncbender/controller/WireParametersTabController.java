package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.util.Properties;

import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class WireParametersTabController {

    private DataModel dataModel;

    @FXML
    private TextField wireDiameterTF;

    @FXML
    private TextField overbendAngleTF;

    @FXML
    private void initialize(){

    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        wireDiameterTF.setText(Double.toString(dataModel.getWireParameters().getDiameter()));
        overbendAngleTF.setText(Double.toString(dataModel.getWireParameters().getOverbendAngle()));
    }

    public void handleSaveParameters(){

        MultiTextFieldInputManager TFManager = new MultiTextFieldInputManager();

        TFManager.check("Wire diameter", wireDiameterTF.getText(), InputType.POSITIVE_NUMBER);
        TFManager.check("Overbend angle", overbendAngleTF.getText(), InputType.POSITIVE_NUMBER);

        if(TFManager.isInputIncorrect()){
            TFManager.getAlert().showAndWait();
        }
        else{

            double newWireDiameter, newOverbendAngle;
            newWireDiameter = Double.parseDouble(wireDiameterTF.getText());
            newOverbendAngle = Double.parseDouble(overbendAngleTF.getText());

            dataModel.getWireParameters().setDiameter(newWireDiameter);
            dataModel.getWireParameters().setOverbendAngle(newOverbendAngle);

            Properties wireParameters = new Properties();
            wireParameters.setProperty("wireDiameter",Double.toString(newWireDiameter));
            wireParameters.setProperty("overbendAngle",Double.toString(newOverbendAngle));

            storePropertiesToXML(wireParameters,"wire_parameters.xml");
        }
    }
}
