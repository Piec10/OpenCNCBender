package com.opencncbender.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;

import static com.opencncbender.util.IsEqualComparator.isEqual;
import static com.opencncbender.util.IsSameSignComparator.isSameSign;
import static com.opencncbender.util.IsZeroComparator.isZero;

public class CompensationValues {

    private ObservableList<CompensationValuePair> compensationValuePairs = FXCollections.observableArrayList();

    public void setCompensationValue(CompensationValuePair valuePair){

        for(int i=0; i<compensationValuePairs.size(); i++){
            if(compensationValuePairs.get(i).equals(valuePair)) {
                compensationValuePairs.remove(i);
                break;
            }
        }
        compensationValuePairs.add(valuePair);
        Collections.sort(compensationValuePairs);
    }

    public void setCompensationValue(double inputValue, double compensationValue){

        CompensationValuePair newPair =  new CompensationValuePair(inputValue, compensationValue);

        setCompensationValue(newPair);
    }

    public CompensationValuePair getCompensationValuePair(int index){

        return compensationValuePairs.get(index);
    }

    public double getCompensationValue(double inputValue){

        if(isZero(inputValue)){
            return 0.0;
        }

        for(int i=0; i<compensationValuePairs.size(); i++){

            if(isEqual(inputValue,compensationValuePairs.get(i).getValue())){
                return compensationValuePairs.get(i).getCompensationValue();
            }
            else if(inputValue < compensationValuePairs.get(i).getValue()){

                if(i == 0){
                    return calculateForFirstElement(inputValue);
                }
                else{
                    return calculateForElement(i,inputValue);
                }
            }
        }
        return calculateForLastElement(inputValue);
    }

    private double calculateForElement(int index, double inputValue) {

        CompensationValuePair pair1, pair2;
        pair1 = compensationValuePairs.get(index-1);
        pair2 = compensationValuePairs.get(index);

        if((isSameSign(inputValue,pair1.getValue()))&&(isSameSign(inputValue,pair2.getValue()))){

            return interpolatedCompensationValue(pair1,pair2,inputValue);
        }
        else if(isSameSign(inputValue,pair1.getValue())){
            if(index>=2){
                pair2 = compensationValuePairs.get(index-2);
                return interpolatedCompensationValue(pair1,pair2,inputValue);
            }
            else return 0.0;
        }
        else{
            if(index<compensationValuePairs.size()){
                pair1 = compensationValuePairs.get(index+1);
                return interpolatedCompensationValue(pair1,pair2,inputValue);
            }
            else return 0.0;
        }
    }

    private double calculateForFirstElement(double inputValue) {

        if(compensationValuePairs.size() >= 2){
            CompensationValuePair pair1, pair2;
            pair1 = compensationValuePairs.get(0);
            pair2 = compensationValuePairs.get(1);

            if((isSameSign(inputValue,pair1.getValue()))&&(isSameSign(inputValue,pair2.getValue()))){

                return interpolatedCompensationValue(pair1,pair2,inputValue);
            }
            else return 0.0;
        }
        else return 0.0;
    }

    private double calculateForLastElement(double inputValue) {

        if(compensationValuePairs.size() >= 2){
            CompensationValuePair pair1, pair2;
            pair1 = compensationValuePairs.get(compensationValuePairs.size()-2);
            pair2 = compensationValuePairs.get(compensationValuePairs.size()-1);

            if((isSameSign(inputValue,pair1.getValue()))&&(isSameSign(inputValue,pair2.getValue()))){

                return interpolatedCompensationValue(pair1,pair2,inputValue);
            }
            else return 0.0;
        }
        else return 0.0;
    }

    private double interpolatedCompensationValue(CompensationValuePair pair1, CompensationValuePair pair2, double inputValue) {

        double a, b, x1, x2, y1, y2;
        x1 = pair1.getValue();
        y1 = pair1.getCompensationValue();
        x2 = pair2.getValue();
        y2 = pair2.getCompensationValue();
        a = (y1 - y2)/(x1 - x2);
        b = y1 - a * x1;

        return (inputValue * a + b);
    }

    public ObservableList<CompensationValuePair> getCompensationValueList() {
        return compensationValuePairs;
    }

    public void deleteCompensationValue(int index) {
        compensationValuePairs.remove(index);
    }
}