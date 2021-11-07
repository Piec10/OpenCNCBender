package com.opencncbender.util;

import javafx.scene.control.Alert;

public class MultiTextFieldInputManager {

    private boolean wrongInput = false;
    private StringBuilder inputErrorMessage = new StringBuilder();

    public void check(String name, String input, InputType expression){

        if(!input.matches(expression.toString())){
            wrongInput = true;
            inputErrorMessage.append(name);
            inputErrorMessage.append(" value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
    }

    public boolean isInputIncorrect(){
        return wrongInput;
    }

    public Alert getAlert(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input error");
        alert.setHeaderText(inputErrorMessage.toString());
        return alert;
    }
}