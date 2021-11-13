package com.opencncbender.controller;

import com.opencncbender.MainAppWindow;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;


public class SegmentEditWindowController {

    private DataModel dataModel;

    @FXML
    TitledPane titledPane;

    @FXML
    private Button applyButton;

    @FXML
    private Button okButton;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField distanceXTF;

    @FXML
    private TextField angleATF;

    @FXML
    private TextField angleBTF;

    @FXML
    private HBox spacer;

    @FXML
    private HBox title;

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

                titleLabel.setText("New segment");
                applyButton.setText("Add");

                applyButton.setOnAction(actionEvent -> {
                    manageSegment();
                    distanceXTF.requestFocus();
                });

                okButton.setOnAction(actionEvent -> {
                    manageSegment();
                    closeWindow();
                });
                break;
            case EDIT:

                titleLabel.setText("Edit segment");
                applyButton.setText("Save");

                applyButton.setOnAction(actionEvent -> {
                    manageSegment();
                });

                okButton.setOnAction(actionEvent -> {
                    manageSegment();
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

                        distanceXTF.setText("0");
                        angleATF.setText("0");
                        angleBTF.setText("0");
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
        title.minWidthProperty().bind(titledPane.widthProperty());
        spacer.setMaxWidth(Double.MAX_VALUE);
    }

    private void manageSegment(){

        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Distance X", distanceXTF.getText(), InputType.POSITIVE_DOUBLE);
        manager.check("Angle A", angleATF.getText(), InputType.PLUS_MINUS_180_DOUBLE);
        manager.check("Angle B", angleBTF.getText(), InputType.PLUS_MINUS_180_DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            switch (type) {
                case NEW:
                    dataModel.addManualStep(manager.getParsedValue(), manager.getParsedValue(), manager.getParsedValue());
                    break;
                case EDIT:
                    dataModel.editSegment(manager.getParsedValue(), manager.getParsedValue(), manager.getParsedValue());
                    break;
            }
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
