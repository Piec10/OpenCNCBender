package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MachineParametersTabController {

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

        MultiTextFieldInputManager TFManager = new MultiTextFieldInputManager();

        TFManager.check("Bending radius", bendingRadiusTF.getText(), InputType.POSITIVE_NUMBER);
        TFManager.check("Rod radius", rodRadiusTF.getText(), InputType.POSITIVE_NUMBER);
        TFManager.check("Pin radius", pinRadiusTF.getText(), InputType.POSITIVE_NUMBER);
        TFManager.check("Pin offset", pinOffsetTF.getText(), InputType.NUMBER);
        TFManager.check("Pin spacing", pinSpacingTF.getText(), InputType.POSITIVE_NUMBER);

        if (TFManager.isInputIncorrect()) {
            TFManager.getAlert().showAndWait();
        } else {

            double newBendingRadius = Double.parseDouble(bendingRadiusTF.getText());
            double newRodRadius = Double.parseDouble(rodRadiusTF.getText());
            double newPinRadius = Double.parseDouble(pinRadiusTF.getText());
            double newPinOffset = Double.parseDouble(pinOffsetTF.getText());
            double newPinSpacing = Double.parseDouble(pinSpacingTF.getText());

            dataModel.getMachineGeometry().setBendingRadius(newBendingRadius);
            dataModel.getMachineGeometry().setRodRadius(newRodRadius);
            dataModel.getMachineGeometry().setPinRadius(newPinRadius);
            dataModel.getMachineGeometry().setPinOffset(newPinOffset);
            dataModel.getMachineGeometry().setPinSpacing(newPinSpacing);
        }
    }
}
