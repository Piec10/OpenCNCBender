package com.opencncbender.util;

import javafx.scene.control.Alert;
import java.util.LinkedList;
import java.util.Queue;

public class MultiTextFieldInputManager {

    private boolean wrongInput = false;
    private StringBuilder inputErrorMessage = new StringBuilder();
    private Queue<Double> parsedValues = new LinkedList<>();

    public void check(String name, String input, InputType inputType){

        Double checkedValue = null;

        try{
            checkedValue = Double.parseDouble(input);
            switch (inputType){
                case POSITIVE_DOUBLE:
                    if(checkedValue <= 0){
                        wrongInput = true;
                        inputErrorMessage.append(name);
                        inputErrorMessage.append(" value must be greater than 0.");
                        inputErrorMessage.append(System.lineSeparator());
                    }
                    break;
                case ZERO_POSITIVE_DOUBLE:
                    if(checkedValue < 0){
                        wrongInput = true;
                        inputErrorMessage.append(name);
                        inputErrorMessage.append(" value must be equal or greater than 0.");
                        inputErrorMessage.append(System.lineSeparator());
                    }
                    break;
                case PLUS_MINUS_180_DOUBLE:
                    if((checkedValue < -180)||(checkedValue > 180)){
                        wrongInput = true;
                        inputErrorMessage.append(name);
                        inputErrorMessage.append(" value must be between -180 and 180. ");
                        inputErrorMessage.append(System.lineSeparator());
                    }
                    break;
            }
        }
        catch (Exception e){
            wrongInput = true;
            inputErrorMessage.append(name);
            inputErrorMessage.append(" value is incorrect.");
            inputErrorMessage.append(System.lineSeparator());
        }
        finally {
            parsedValues.add(checkedValue);
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

    public Double getParsedValue(){
        return parsedValues.poll();
    }
}