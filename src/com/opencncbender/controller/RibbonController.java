package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import javafx.fxml.FXML;

public class RibbonController {

    private DataModel dataModel;

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

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

    }


}
