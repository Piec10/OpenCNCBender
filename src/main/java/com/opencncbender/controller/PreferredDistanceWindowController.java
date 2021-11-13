package com.opencncbender.controller;

import com.opencncbender.MainAppWindow;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;

public class PreferredDistanceWindowController {

    private DataModel dataModel;

    private MainAppWindow mainAppWindow;

    @FXML
    TitledPane titledPane;

    @FXML
    private HBox spacer;

    @FXML
    private HBox title;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField prefDistTF;

    @FXML
    private Button okButton;

    @FXML
    private Button addButton;

    @FXML
    private void handleClose(){
        closeWindow();
    }

    @FXML
    private void handleOK(){
        addIntermediatePoints();
        closeWindow();
    }

    @FXML
    private void handleAdd(){
        addIntermediatePoints();
    }

    public void initModel(DataModel dataModel, MainAppWindow mainAppWindow) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
        this.mainAppWindow = mainAppWindow;

        titleLabel.setText("Add intermediate points");
        title.minWidthProperty().bind(titledPane.widthProperty());
        spacer.setMaxWidth(Double.MAX_VALUE);

        if(dataModel.numberOfPointsSelectedProperty().get()<3){
            okButton.setDisable(true);
            addButton.setDisable(true);
        }
        else{
            okButton.setDisable(false);
            addButton.setDisable(false);
        }
        prefDistTF.setText(Double.toString(dataModel.preferredDistanceProperty().get()));

        dataModel.preferredDistanceProperty().addListener((obs, oldDistance, newDistance) -> {
            prefDistTF.setText(Double.toString(dataModel.preferredDistanceProperty().get()));
        });

        dataModel.numberOfPointsSelectedProperty().addListener((obs, oldNumb, newNumb) -> {
            if(dataModel.numberOfPointsSelectedProperty().get()<3){
                okButton.setDisable(true);
                addButton.setDisable(true);
            }
            else{
                okButton.setDisable(false);
                addButton.setDisable(false);
            }
        });
    }

    private void closeWindow() {
        mainAppWindow.close(this);
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }

    private void addIntermediatePoints(){

        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Distance between points", prefDistTF.getText(), InputType.POSITIVE_DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            dataModel.preferredDistanceProperty().set(manager.getParsedValue());
            dataModel.addIntermediatePoints();
        }
    }
}
