package com.opencncbender.controller;

import com.opencncbender.MainAppWindow;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.ActionType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RibbonController {

    private DataModel dataModel;
    private HBox hBox;
    private VBox vBox;
    private Accordion editWindowPanel;
    private MainAppWindow mainAppWindow;

    @FXML
    private void handleNewSegment() throws IOException {

        mainAppWindow.openSegmentWindow(ActionType.NEW);

        /*FXMLLoader segmentEditWindowLoader = new FXMLLoader();
        segmentEditWindowLoader.setLocation(getClass().getClassLoader().getResource("com/opencncbender/resources/fxml/SegmentEditWindow.fxml"));
        TitledPane pane = segmentEditWindowLoader.load();
        SegmentEditWindowController segmentEditWindowController = segmentEditWindowLoader.getController();
        segmentEditWindowController.initModel(dataModel, ActionType.NEW);*/

        //editWindowPanel.getPanes().add(pane);
        //vBox.getChildren().add(pane);
        //System.out.println(vBox.getHeight());
        //System.out.println(vBox.getWidth());
        //hBox.getChildren().add(anchorPane);
        //Scene scene = new Scene(anchorPane);
        //Stage stage = new Stage();
        //stage.setTitle("New segment");

        //stage.setAlwaysOnTop(true);
        //stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setScene(scene);
        //stage.showAndWait();

    }

    @FXML
    private void handleEditSegment() throws IOException {

        mainAppWindow.openSegmentWindow(ActionType.EDIT);
        /*FXMLLoader segmentEditWindowLoader = new FXMLLoader();
        segmentEditWindowLoader.setLocation(getClass().getClassLoader().getResource("com/opencncbender/resources/fxml/SegmentEditWindow.fxml"));
        AnchorPane anchorPane = segmentEditWindowLoader.load();
        SegmentEditWindowController segmentEditWindowController = segmentEditWindowLoader.getController();
        segmentEditWindowController.initModel(dataModel, ActionType.EDIT);*/


        //Scene scene = new Scene(anchorPane);
        //Stage stage = new Stage();
        //stage.setTitle("Edit segment");

        //stage.setAlwaysOnTop(true);
        //stage.initModality(Modality.APPLICATION_MODAL);
        //stage.setScene(scene);
        //stage.showAndWait();

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


    public void setHBox(HBox hBox) {
        this.hBox = hBox;
    }

    public void setVBox(VBox vBox) {
        this.vBox = vBox;
    }

    public void setAccordion(Accordion editWindowPanel) {
        this.editWindowPanel = editWindowPanel;
    }
}
