package com.opencncbender.controller;

import com.opencncbender.MainAppWindow;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import javafx.fxml.FXML;

public class RibbonController {

    private DataModel dataModel;
    private MainAppWindow mainAppWindow;

    @FXML
    private void handleNewSegment() {

        mainAppWindow.openSegmentWindow(ActionType.NEW);
    }

    @FXML
    private void handleEditSegment() {

        mainAppWindow.openSegmentWindow(ActionType.EDIT);
    }

    @FXML
    private void handleDeleteSegment(){
        dataModel.deleteSelectedStep();
    }

    @FXML
    private void handleAddIntermediatePoints(){
        dataModel.addIntermediatePoints();
    }

    @FXML
    private void handleMoveToTheTop(){
        dataModel.moveSelectedPointsToTheTop();
    }

    @FXML
    private void handleMoveToTheEnd(){
        dataModel.moveSelectedPointsToTheEnd();
    }

    @FXML
    private void handleReverseSelected(){
        dataModel.reverseSelectedPoints();
    }

    @FXML
    private void handleReverseAll(){
        dataModel.reverseAllPoints();
    }

    @FXML
    private void handleDelete(){
        dataModel.deleteSelectedPoints();
    }

    @FXML
    private void handleStartAnimation(){
        dataModel.animationPlay();
    }

    @FXML
    private void handleAnimationNextFrame(){
        dataModel.animationNextFrame();
    }

    public void initModel(DataModel dataModel, MainAppWindow mainAppWindow) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
        this.mainAppWindow = mainAppWindow;
    }
}
