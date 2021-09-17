package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.TextFileWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Paths;

public class MainWindowController {

    private DataModel dataModel;

    @FXML
    private VBox vBox;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label infoLabel;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Pane pane;

    @FXML
    private void initialize() {
        infoLabel.setText("CNCBender by @mateusz_piecka");
    }

    public void initModel(DataModel dataModel) {
        if (this.dataModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.dataModel = dataModel;
    }

    @FXML
    private void handleOpenXYZFile(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XYZ", "*.xyz"));
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));

        File selectedFile = fileChooser.showOpenDialog(menuBar.getScene().getWindow());
        if (selectedFile != null) {
            dataModel.openXYZFile(selectedFile);
        }
    }

    @FXML
    private void handleSaveXYZFile(){
        String polylineAsString;
        polylineAsString = dataModel.getPolyline().getAsString();

        if(polylineAsString != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XYZ", "*.xyz"));
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            fileChooser.setInitialDirectory(new File(currentPath));
            String initialFileName = TextFileWriter.changeExtension(dataModel.currentFilenameProperty().get(), "modified.xyz");
            fileChooser.setInitialFileName(initialFileName);

            File selectedFile = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

            if (selectedFile != null) {
                TextFileWriter.saveTextFile(selectedFile, polylineAsString);
            }
        }
    }

    @FXML
    private void handleCloseFile(){
        dataModel.closeFile();
    }

    @FXML
    private void handleExportGCodeFile(){
        String instructions;
        instructions = dataModel.getGCodeInstructions();

        if(instructions != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GCODE", "*.gcode"));
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            fileChooser.setInitialDirectory(new File(currentPath));
            String initialFileName = TextFileWriter.changeExtension(dataModel.currentFilenameProperty().get(), "gcode");
            fileChooser.setInitialFileName(initialFileName);

            File selectedFile = fileChooser.showSaveDialog(menuBar.getScene().getWindow());

            if (selectedFile != null) {
                TextFileWriter.saveTextFile(selectedFile, instructions);
            }
        }
    }

    @FXML
    private void handleExit(){
        Platform.exit();
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    public VBox getvBox() {
        return vBox;
    }

    public Pane getPane() {
        return pane;
    }
}
