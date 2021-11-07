package com.opencncbender.logic;

import java.util.Objects;

public class CompensationValuePair implements Comparable<CompensationValuePair>{

    private double value;
    private double compensationValue;

    public CompensationValuePair(double value, double compensationValue) {
        this.value = value;
        this.compensationValue = compensationValue;
    }

    public double getValue() {
        return value;
    }

    public double getCompensationValue() {
        return compensationValue;
    }

    @Override
    public int compareTo(CompensationValuePair o) {

        return Double.compare(value,o.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompensationValuePair)) return false;
        CompensationValuePair that = (CompensationValuePair) o;
        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
