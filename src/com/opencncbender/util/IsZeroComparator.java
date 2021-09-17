package com.opencncbender.util;

public class IsZeroComparator {

    public static boolean isZero(double number){

        if(Math.abs(number) < 0.0001) return true;
        else return false;
    }
}
