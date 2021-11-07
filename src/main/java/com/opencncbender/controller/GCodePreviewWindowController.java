package com.opencncbender.controller;

import com.opencncbender.model.DataModel;
import com.opencncbender.util.TextFileWriter;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class GCodePreviewWindowController {

    Stage dialogStage;
    DataModel dataModel;

    @FXML
    private TextArea previewTA;

    @FXML
    private void handleSave(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GCODE", "*.gcode"));
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));
        String initialFileName = TextFileWriter.changeExtension(dataModel.currentFilenameProperty().get(), "gcode");
        fileChooser.setInitialFileName(initialFileName);

        File selectedFile = fileChooser.showSaveDialog(dialogStage);

        if (selectedFile != null) {
            TextFileWriter.saveTextFile(selectedFile, previewTA.getText());
        }
    }

    @FXML
    private void handleClose(){
        dialogStage.close();
    }

    public void setText(String instructions) {
        previewTA.setText(instructions);
    }

    public void initWindow(String instructions, DataModel dataModel, Stage dialogStage) {

        previewTA.setText(instructions);
        this.dataModel = dataModel;
        this.dialogStage = dialogStage;
    }
}
