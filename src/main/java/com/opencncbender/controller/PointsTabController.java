package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.util.Locale;

public class PointsTabController {

    private DataModel dataModel;
    private boolean selectionSelfUpdate = false;

    @FXML
    private TableView<Point3D> pointsTableView;

    @FXML
    private TableColumn<Point3D, String> xColumn;

    @FXML
    private TableColumn<Point3D, String> yColumn;

    @FXML
    private TableColumn<Point3D, String> zColumn;

    @FXML
    private Label filenameLabel;

    @FXML
    private Label pointsQuantityLabel;

    @FXML
    private Label minDistanceLabel;

    @FXML
    private Label maxDistanceLabel;

    @FXML
    private TextField preferredDistanceTF;

    @FXML
    private void initialize() {

        pointsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        xColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getX())));
        yColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getY())));
        zColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getZ())));

        xColumn.setReorderable(false);
        yColumn.setReorderable(false);
        zColumn.setReorderable(false);

        pointsTableView.setPlaceholder(new Label(""));
    }
    public void initModel(DataModel dataModel) {

        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        pointsTableView.setItems(dataModel.getPointsList());

        pointsTableView.setRowFactory(tableView -> {
            final TableRow<Point3D> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)  ) {
                    tableView.getSelectionModel().clearSelection(index);
                    event.consume();
                }
            });
            row.addEventFilter(MouseEvent.MOUSE_RELEASED, event-> {
                selectionSelfUpdate = true;
                dataModel.changeSelectionOfPoints(pointsTableView.getSelectionModel().getSelectedIndices());
                selectionSelfUpdate = false;
            });
            return row;
        });

        pointsTableView.setOnKeyPressed(keyEvent -> {
            selectionSelfUpdate = true;
            dataModel.changeSelectionOfPoints(pointsTableView.getSelectionModel().getSelectedIndices());
            selectionSelfUpdate = false;
        });

        dataModel.getSelectedPointsList().addListener((ListChangeListener<Integer>) change ->{
            if(!selectionSelfUpdate) {
                updatePointsSelection();
            }
        });

        pointsQuantityLabel.textProperty().bind(dataModel.numberOfPointsSelectedProperty().asString());
        minDistanceLabel.textProperty().bind(dataModel.minDistanceProperty().asString(Locale.ROOT,"%.3f"));
        maxDistanceLabel.textProperty().bind(dataModel.maxDistanceProperty().asString(Locale.ROOT,"%.3f"));

    }

    private void updatePointsSelection() {

        pointsTableView.getSelectionModel().clearSelection();

        if(!dataModel.getSelectedPointsList().isEmpty()){
            for(int i=0; i<dataModel.getSelectedPointsList().size();i++){
                pointsTableView.getSelectionModel().select(dataModel.getSelectedPointsList().get(i));
            }
        }
    }
}
