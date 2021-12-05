package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import javax.script.Bindings;
import javax.swing.event.ChangeListener;

public class SidePanelController {

    private IntegerProperty width = new SimpleIntegerProperty();

    //private SimpleBooleanProperty isExpanded = new SimpleBooleanProperty(false);
    private boolean isExpanded = true;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab bendingStepsTab;

    @FXML
    private Tab pointsTab;

    @FXML
    private Tab machineTab;

    @FXML
    private Tab wireTab;

    @FXML
    private Tab gCodeTab;

    @FXML
    private ScrollPane gCodeTabPane;

    @FXML
    private Button toggleExpandButton;

    @FXML
    private void initialize(){

        tabPane.setOnMouseClicked(mouseEvent -> {
            if(!isExpanded) handleToggleExpanded();
        });

    }

    @FXML
    private void handleToggleExpanded(){
        if(isExpanded){

            rootPane.setMaxWidth(33);
            toggleExpandButton.setText(">");
            isExpanded = false;
        }
        else{
            rootPane.setMaxWidth(400);
            toggleExpandButton.setText("<");
            isExpanded = true;
        }
    }

    public void initModel(DataModel dataModel) {

    }

    public Tab getBendingStepsTab() {
        return bendingStepsTab;
    }

    public Tab getPointsTab() {
        return pointsTab;
    }

    public Tab getMachineTab() {
        return machineTab;
    }

    public Tab getWireTab() {
        return wireTab;
    }

    public Tab getGCodeTab() {
        return gCodeTab;
    }

    public ScrollPane getGCodeTabPane() {
        return gCodeTabPane;
    }

    public TabPane getTabPane() {
        return tabPane;
    }
}
