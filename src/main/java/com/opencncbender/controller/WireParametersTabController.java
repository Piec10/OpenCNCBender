package com.opencncbender.controller;

import com.opencncbender.logic.CompensationValuePair;
import com.opencncbender.model.DataModel;
import com.opencncbender.util.InputType;
import com.opencncbender.util.MultiTextFieldInputManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.util.Locale;
import java.util.Properties;

import static com.opencncbender.util.PropertiesXMLWriter.storePropertiesToXML;

public class WireParametersTabController {

    private DataModel dataModel;

    @FXML
    private TableView<CompensationValuePair> compensationValuesTableView;

    @FXML
    private TableColumn<CompensationValuePair, String> angleColumn;

    @FXML
    private TableColumn<CompensationValuePair, String> compensationAngleColumn;

    @FXML
    private TextField wireDiameterTF;

    @FXML
    private TextField newAngleTF;

    @FXML
    private TextField newCompAngleTF;

    @FXML
    private Button deleteButton;

    @FXML
    private Button addOrChangeButton;

    @FXML
    private void initialize(){

        angleColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getValue())));
        compensationAngleColumn.setCellValueFactory(param -> new SimpleStringProperty(String.format(Locale.ROOT,"%.3f",param.getValue().getCompensationValue())));
        angleColumn.setReorderable(false);
        compensationAngleColumn.setReorderable(false);
        compensationValuesTableView.setPlaceholder(new Label(""));

        compensationValuesTableView.setRowFactory(tableView -> {
            final TableRow<CompensationValuePair> row = new TableRow<>();
            row.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                final int index = row.getIndex();
                if (index >= 0 && index < tableView.getItems().size() && tableView.getSelectionModel().isSelected(index)) {
                    tableView.getSelectionModel().clearSelection(index);
                    event.consume();
                }
            });
            return row;
        });

        compensationValuesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldStep, newStep) -> {
            if(newStep == null){
                deleteButton.setDisable(true);
                newAngleTF.setText("");
                newCompAngleTF.setText("");
                addOrChangeButton.setText("Add");
                newAngleTF.setDisable(false);
            }
            else {
                deleteButton.setDisable(false);
                newAngleTF.setText(Double.toString(newStep.getValue()));
                newCompAngleTF.setText(Double.toString(newStep.getCompensationValue()));
                addOrChangeButton.setText("Change");
                newAngleTF.setDisable(true);
            }
        });
    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;

        wireDiameterTF.setText(Double.toString(dataModel.getWireParameters().getDiameter()));
        compensationValuesTableView.setItems(dataModel.getWireParameters().getOverbendAngleValues().getCompensationValueList());
    }

    public void handleDelete(){
        dataModel.getWireParameters().getOverbendAngleValues().deleteCompensationValue(compensationValuesTableView.getSelectionModel().selectedIndexProperty().get());
        compensationValuesTableView.getSelectionModel().clearSelection();
    }

    public void handleAddOrChange(){

        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Angle", newAngleTF.getText(),InputType.PLUS_MINUS_180_DOUBLE);
        manager.check("Compensation angle",newCompAngleTF.getText(),InputType.PLUS_MINUS_180_DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            dataModel.getWireParameters().getOverbendAngleValues().setCompensationValue(manager.getParsedValue(), manager.getParsedValue());
            compensationValuesTableView.refresh();
            compensationValuesTableView.getSelectionModel().clearSelection();
        }
    }

    public void handleSaveParameters(){

        MultiTextFieldInputManager manager = new MultiTextFieldInputManager();

        manager.check("Wire diameter", wireDiameterTF.getText(), InputType.POSITIVE_DOUBLE);

        if(manager.isInputIncorrect()){
            manager.getAlert().showAndWait();
        }
        else{
            dataModel.getWireParameters().setDiameter(manager.getParsedValue());
            Properties wireProperties = dataModel.getWireParameters().getWireProperties();
            storePropertiesToXML(wireProperties,"wire_parameters.xml");
        }
    }
}
