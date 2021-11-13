package com.opencncbender.util;

public enum InputType {

    NUMBER{
        @Override
        public String toString(){
            return "^-?\\d+\\.\\d+";
        }
    },
    POSITIVE_NUMBER{
        @Override
        public String toString(){
            return "\\d+\\.\\d+";
        }
    },
    DOUBLE,
    POSITIVE_DOUBLE,
    ZERO_POSITIVE_DOUBLE,
    PLUS_MINUS_180_DOUBLE



    //public static final String NUMBER = "";
    //public static final String POSITIVE_NUMBER = "\\d*\\.?\\d+$";


}
