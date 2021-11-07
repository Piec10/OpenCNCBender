package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.Properties;

import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class MachineGeometryTabController {

    private DataModel dataModel;

    @FXML
    private TextField bendingRadiusTF;

    @FXML
    private TextField rodRadiusTF;

    @FXML
    private TextField pinRadiusTF;

    @FXML
    private TextField pinOffsetTF;

    @FXML
    private TextField pinSpacingTF;

    @FXML
    private void initialize(){

    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        bendingRadiusTF.setText(Double.toString(dataModel.getMachineGeometry().getBendingRadius()));
        rodRadiusTF.setText(Double.toString(dataModel.getMachineGeometry().getRodRadius()));
        pinRadiusTF.setText(Double.toString(dataModel.getMachineGeometry().getPinRadius()));
        pinOffsetTF.setText(Double.toString(dataModel.getMachineGeometry().getPinOffset()));
        pinSpacingTF.setText(Double.toString(dataModel.getMachineGeometry().getPinSpacing()));
    }

    public void handleSaveParameters() {

        Double newBendingRadius = null;
        Double newRodRadius = null;
        Double newPinRadius = null;
        Double newPinOffset = null;
        Double newPinSpacing = null;

        boolean wrongInput = false;
        StringBuilder inputErrorMessage = new StringBuilder();

        try{
            newBendingRadius = Double.parseDouble(bendingRadiusTF.getText());
            if(newBendingRadius <= 0){
                wrongInput = true;
                inputErrorMessage.append("Bending radius value must be greater than 0.");
                inputErrorMessage.append(System.lineSeparator());
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Bending radius value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
        try{
            newRodRadius = Double.parseDouble(rodRadiusTF.getText());
            if(newRodRadius <= 0){
                wrongInput = true;
                inputErrorMessage.append("Rod radius value must be greater than 0.");
                inputErrorMessage.append(System.lineSeparator());
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Rod radius value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
        try{
            newPinRadius = Double.parseDouble(pinRadiusTF.getText());
            if(newPinRadius <= 0){
                wrongInput = true;
                inputErrorMessage.append("Pin radius value must be greater than 0.");
                inputErrorMessage.append(System.lineSeparator());
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Pin radius value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
        try{
            newPinOffset = Double.parseDouble(pinOffsetTF.getText());
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Pin offset value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
        try{
            newPinSpacing = Double.parseDouble(pinSpacingTF.getText());
            if(newPinSpacing <= 0){
                wrongInput = true;
                inputErrorMessage.append("Pin spacing value must be greater than 0.");
                inputErrorMessage.append(System.lineSeparator());
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Pin spacing value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }

        if(wrongInput){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText(inputErrorMessage.toString());
            alert.showAndWait();
        }
        else{
            dataModel.getMachineGeometry().setBendingRadius(newBendingRadius);
            dataModel.getMachineGeometry().setRodRadius(newRodRadius);
            dataModel.getMachineGeometry().setPinRadius(newPinRadius);
            dataModel.getMachineGeometry().setPinOffset(newPinOffset);
            dataModel.getMachineGeometry().setPinSpacing(newPinSpacing);

            Properties machineGeometry = dataModel.getMachineGeometry().getMachineGeometryProperties();
            storePropertiesToXML(machineGeometry,"machine_geometry.xml");
        }
    }
}
