package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import static com.opencncbender.util.isParsableToDoubleChecker.isParsableToDouble;
import static com.opencncbender.util.isParsableToDoubleChecker.isParsableToPositiveDouble;

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

        Pattern pattern = Pattern.compile("");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null);

        //wireDiameterTF.setTextFormatter(formatter);
        //overbendAngleTF.setTextFormatter(formatter);

        //wireDiameterTF.textProperty().bind(dataModel.getWireParameters().diameterProperty().asString());
        //overbendAngleTF.textProperty().bind(dataModel.getWireParameters().overbendAngleProperty().asString());



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
        }
    }
}
