package com.opencncbender.controller;

import com.opencncbender.logic.SingleStep;
import com.opencncbender.model.DataModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

import java.util.Locale;

public class BendingStepsTableTabController {

    private DataModel dataModel;

    @FXML
    private TableView<SingleStep> manualStepsTableView;

    @FXML
    private TableColumn<SingleStep, String> LengthXColumn;

    @FXML
    private TableColumn<SingleStep, String> AngleAColumn;

    @FXML
    private TableColumn<SingleStep, String> AngleBColumn;

    @FXML
    private void initialize(){

        LengthXColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getDistanceX())));
        AngleAColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getAngleA())));
        AngleBColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getAngleB())));
        LengthXColumn.setReorderable(false);
        AngleAColumn.setReorderable(false);
        AngleBColumn.setReorderable(false);

        manualStepsTableView.setPlaceholder(new Label(""));
    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        manualStepsTableView.setItems(dataModel.getBendingStepsList());
        manualStepsTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldStep, newStep) -> {
            dataModel.setSelectedStep(newStep);
            dataModel.setSelectedStepIndex(manualStepsTableView.getSelectionModel().selectedIndexProperty().get());
        });

        manualStepsTableView.setRowFactory(tableView -> {
            final TableRow<SingleStep> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)  ) {
                    tableView.getSelectionModel().clearSelection(index);
                    event.consume();
                }
            });
            return row;
        });

        /*dataModel.selectedStepProperty().addListener((obs, oldStep, newStep) -> {
            if (newStep == null) {
                manualStepsTableView.getSelectionModel().clearSelection();
            } else {
                manualStepsTableView.getSelectionModel().select(newStep);
            }
        });*/

    }
}
