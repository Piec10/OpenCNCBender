package com.opencncbender.util;

public class IsEqualComparator {

    public static boolean isEqual(double number1, double number2){

        if(Math.abs(number1 - number2) < 0.0001) return true;
        else return false;
    }
}
