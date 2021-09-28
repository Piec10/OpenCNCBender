package com.opencncbender.controller;

import com.opencncbender.MainAppWindow;
import com.opencncbender.logic.SingleStep;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import javax.swing.event.ChangeListener;

public class SegmentEditWindowController {

    private DataModel dataModel;

    @FXML
    TitledPane titledPane;

    @FXML
    private Button applyButton;

    @FXML
    private Button okButton;

    @FXML
    private TextField distanceXTF;

    @FXML
    private TextField angleATF;

    @FXML
    private TextField angleBTF;
    private MainAppWindow mainAppWindow;
    private ActionType type;

    @FXML
    private void handleClose(){

        closeWindow();
    }

    public void initModel(DataModel dataModel, ActionType type, MainAppWindow mainAppWindow) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
        this.type = type;
        this.mainAppWindow = mainAppWindow;

        switch (type){
            case NEW:

                titledPane.setText("New segment");
                applyButton.setText("Add");

                applyButton.setOnAction(actionEvent -> {
                    addNewSegment();
                    distanceXTF.requestFocus();
                });

                okButton.setOnAction(actionEvent -> {
                    addNewSegment();
                    closeWindow();
                });
                break;
            case EDIT:

                titledPane.setText("Edit segment");
                applyButton.setText("Save");

                applyButton.setOnAction(actionEvent -> {
                    editSegment();
                });

                okButton.setOnAction(actionEvent -> {
                    editSegment();
                    closeWindow();
                });

                if(dataModel.selectedStepProperty().get() == null) {
                    okButton.setDisable(true);
                    applyButton.setDisable(true);
                }
                else{
                    okButton.setDisable(false);
                    applyButton.setDisable(false);

                    distanceXTF.setText(String.valueOf(dataModel.selectedStepProperty().get().getDistanceX()));
                    angleATF.setText(String.valueOf(dataModel.selectedStepProperty().get().getAngleA()));
                    angleBTF.setText(String.valueOf(dataModel.selectedStepProperty().get().getAngleB()));
                }

                dataModel.selectedStepProperty().addListener((obs, oldStep, newStep) -> {
                    if (newStep == null) {
                        okButton.setDisable(true);
                        applyButton.setDisable(true);
                    } else {
                        okButton.setDisable(false);
                        applyButton.setDisable(false);

                        distanceXTF.setText(String.valueOf(dataModel.selectedStepProperty().get().getDistanceX()));
                        angleATF.setText(String.valueOf(dataModel.selectedStepProperty().get().getAngleA()));
                        angleBTF.setText(String.valueOf(dataModel.selectedStepProperty().get().getAngleB()));
                    }
                });
                break;
        }
    }

    private void editSegment() {
        Double distanceX = null;
        Double angleA = null;
        Double angleB = null;

        boolean wrongInput = false;
        StringBuilder inputErrorMessage = new StringBuilder();

        try {
            distanceX = Double.parseDouble(distanceXTF.getText());

            if(distanceX <= 0){
                wrongInput = true;
                inputErrorMessage.append("Distance X value must be greater than 0.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Distance X value is incorrect.");
        }

        try {
            angleA = Double.parseDouble(angleATF.getText());

            if((angleA <= -180)&&(angleA >= 180)){
                wrongInput = true;
                inputErrorMessage.append("Angle A value must be between -180 and 180.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Angle A value is incorrect.");
        }

        try {
            angleB = Double.parseDouble(angleBTF.getText());

            if((angleB <= -180)&&(angleB >= 180)){
                wrongInput = true;
                inputErrorMessage.append("Angle B value must be between -180 and 180.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Angle B value is incorrect.");
        }

        if(wrongInput){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText(inputErrorMessage.toString());
            alert.showAndWait();
        }
        else{
            dataModel.editSegment(distanceX,angleA,angleB);
        }
    }

    private void addNewSegment() {

        Double distanceX = null;
        Double angleA = null;
        Double angleB = null;

        boolean wrongInput = false;
        StringBuilder inputErrorMessage = new StringBuilder();

        try {
            distanceX = Double.parseDouble(distanceXTF.getText());

            if(distanceX <= 0){
                wrongInput = true;
                inputErrorMessage.append("Distance X value must be greater than 0.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Distance X value is incorrect.");
        }

        try {
            angleA = Double.parseDouble(angleATF.getText());

            if((angleA <= -180)&&(angleA >= 180)){
                wrongInput = true;
                inputErrorMessage.append("Angle A value must be between -180 and 180.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Angle A value is incorrect.");
        }

        try {
            angleB = Double.parseDouble(angleBTF.getText());

            if((angleB <= -180)&&(angleB >= 180)){
                wrongInput = true;
                inputErrorMessage.append("Angle B value must be between -180 and 180.");
            }
        }
        catch(Exception e){
            wrongInput = true;
            inputErrorMessage.append("Angle B value is incorrect.");
        }

        if(wrongInput){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error");
            alert.setHeaderText(inputErrorMessage.toString());
            alert.showAndWait();
        }
        else{
            dataModel.addManualStep(distanceX,angleA,angleB);
        }
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }

    public ActionType getType() {
        return type;
    }

    private void closeWindow() {

        mainAppWindow.close(this);
    }
}
