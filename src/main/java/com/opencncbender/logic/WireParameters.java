package com.opencncbender.logic;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.Properties;

/**
 * Created by Piec on 2019-12-21.
 */
public class WireParameters {

    private DoubleProperty diameter = new SimpleDoubleProperty();

    CompensationValues overbendAngleValues = new CompensationValues();

    public WireParameters(double diameter) {
        this.diameter.set(diameter);
    }

    public WireParameters(Properties defaultWireParameters) {

        this.diameter.set(Double.parseDouble(defaultWireParameters.getProperty("wireDiameter","1.0")));

        int i=0;
        while(true){

            StringBuilder compValueKey = new StringBuilder();
            compValueKey.append("comp");
            compValueKey.append(i);

            String values = defaultWireParameters.getProperty(compValueKey.toString());
            if(values == null) break;

            String [] splittedValues = values.split(":");

            try{
                double value = Double.parseDouble(splittedValues[0]);
                double compensationValue = Double.parseDouble(splittedValues[1]);
                overbendAngleValues.setCompensationValue(value,compensationValue);
            }
            catch (Exception e){
                //omit wrong values
            }
            i++;
        }
    }

    public double getDiameter() {
        return diameter.get();
    }

    public double getOverbendAngle(double inputValue) {
        return overbendAngleValues.getCompensationValue(inputValue);
    }

    public DoubleProperty diameterProperty() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter.set(diameter);
    }

    public CompensationValues getOverbendAngleValues() {
        return overbendAngleValues;
    }

    public Properties getWireProperties() {

        Properties properties = new Properties();

        properties.setProperty("wireDiameter",Double.toString(diameter.get()));

        if(overbendAngleValues.getCompensationValueList().size() != 0){

            for(int i=0; i<overbendAngleValues.getCompensationValueList().size(); i++){

                CompensationValuePair compensationValuePair =  overbendAngleValues.getCompensationValuePair(i);
                StringBuilder key = new StringBuilder();
                StringBuilder valuePair = new StringBuilder();

                key.append("comp");
                key.append(i);
                valuePair.append(compensationValuePair.getValue());
                valuePair.append(":");
                valuePair.append(compensationValuePair.getCompensationValue());

                properties.setProperty(key.toString(),valuePair.toString());
            }
        }
        return properties;
    }
}
