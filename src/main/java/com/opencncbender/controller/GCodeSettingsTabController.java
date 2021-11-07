package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.Properties;

import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class GCodeSettingsTabController {

    private DataModel dataModel;

    @FXML
    private TextField zFallDistanceTF;

    @FXML
    private TextField safeAngleOffsetTF;

    @FXML
    private TextField wireFeedrateTF;

    @FXML
    private TextField angleFeedrateTF;

    @FXML
    private TextArea startingGCodeTA;

    @FXML
    private TextArea endingGCodeTA;

    @FXML
    private CheckBox showPreviewCB;

    public void initModel(DataModel dataModel, MainWindowController mainWindowController) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
        showPreviewCB.setSelected(mainWindowController.showPreviewWindowProperty().get());
        mainWindowController.showPreviewWindowProperty().bindBidirectional(showPreviewCB.selectedProperty());

        startingGCodeTA.setText(dataModel.getGCodeGenerator().getStartingGCode());
        endingGCodeTA.setText(dataModel.getGCodeGenerator().getEndingGCode());

        zFallDistanceTF.setText(Double.toString(dataModel.getGCodeGenerator().getzFallDistance()));
        safeAngleOffsetTF.setText(Double.toString(dataModel.getGCodeGenerator().getRodSafeAngleOffset()));
        wireFeedrateTF.setText(Double.toString(dataModel.getGCodeGenerator().getWireFeedrate()));
        angleFeedrateTF.setText(Double.toString(dataModel.getGCodeGenerator().getAngleFeedrate()));
    }

    public void handleSaveSettings(){

        dataModel.getGCodeGenerator().setStartingGCode(startingGCodeTA.getText());
        dataModel.getGCodeGenerator().setEndingGCode(endingGCodeTA.getText());

        Properties gCodeProperties = dataModel.getGCodeGenerator().getGCodeProperties();
        gCodeProperties.setProperty("showPreviewWindow", String.valueOf(showPreviewCB.selectedProperty().get()));
        storePropertiesToXML(gCodeProperties,"gcode_settings.xml");
    }
}
