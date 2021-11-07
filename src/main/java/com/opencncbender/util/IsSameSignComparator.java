package com.opencncbender.util;

public class IsSameSignComparator {

    public static boolean isSameSign(double number1, double number2){

        return ((number1 > 0) == (number2 > 0));
    }
}
