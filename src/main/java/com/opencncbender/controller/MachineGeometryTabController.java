package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
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
    private TextField bAngleCompValueTF;

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
        bAngleCompValueTF.setText(Double.toString(dataModel.getMachineGeometry().getbAngleCompValue()));
    }

    public void handleSaveParameters() {

        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Bending radius", bendingRadiusTF.getText(), InputType.POSITIVE_DOUBLE);
        manager.check("Rod radius", rodRadiusTF.getText(), InputType.POSITIVE_DOUBLE);
        manager.check("Pin radius", pinRadiusTF.getText(), InputType.POSITIVE_DOUBLE);
        manager.check("Pin offset", pinOffsetTF.getText(), InputType.DOUBLE);
        manager.check("Pin spacing", pinSpacingTF.getText(), InputType.ZERO_POSITIVE_DOUBLE);
        manager.check("B compensation angle", bAngleCompValueTF.getText(), InputType.DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            dataModel.getMachineGeometry().setBendingRadius(manager.getParsedValue());
            dataModel.getMachineGeometry().setRodRadius(manager.getParsedValue());
            dataModel.getMachineGeometry().setPinRadius(manager.getParsedValue());
            dataModel.getMachineGeometry().setPinOffset(manager.getParsedValue());
            dataModel.getMachineGeometry().setPinSpacing(manager.getParsedValue());
            dataModel.getMachineGeometry().setbAngleCompValue(manager.getParsedValue());

            Properties machineGeometry = dataModel.getMachineGeometry().getMachineGeometryProperties();
            storePropertiesToXML(machineGeometry,"machine_geometry.xml");
        }
    }
}
