package com.opencncbender.util;

public class isParsableToDoubleChecker {

    public static boolean isParsableToDouble(String input){

        if(input.matches("^-?\\d*\\.?\\d+$")){
            return true;
        }
        else return false;
    }

    public static boolean isParsableToPositiveDouble(String input){

        if(input.matches("\\d*\\.?\\d+$")){
            return true;
        }
        else return false;
    }
}
