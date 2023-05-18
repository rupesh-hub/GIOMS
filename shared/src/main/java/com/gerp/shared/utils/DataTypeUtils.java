package com.gerp.shared.utils;

public class DataTypeUtils {


   public static Boolean isInteger(String val){
        try {
            Integer.parseInt(val);
            return true;
        }catch (Exception ex){
            return false;
        }
    }
}
