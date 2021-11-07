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
    }



    //public static final String NUMBER = "";
    //public static final String POSITIVE_NUMBER = "\\d*\\.?\\d+$";


}
