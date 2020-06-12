package com.kinecab.demo.util;

public class Utils {
    public static String trimUp(String string){
        String stringTrim = string.trim();
        return stringTrim.substring(0,1).toUpperCase() + stringTrim.substring(1).toLowerCase();
    }
}
