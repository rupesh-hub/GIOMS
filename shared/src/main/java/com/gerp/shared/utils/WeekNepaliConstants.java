package com.gerp.shared.utils;

import com.gerp.shared.exception.CustomException;

public enum WeekNepaliConstants {

    DAY1("Sun", "आइत"),
    DAY2("Mon", "सोम"),
    DAY3("Tue", "मंगल"),
    DAY4("Wed", "बुध"),
    DAY5("Thu", "बिहि"),
    DAY6("Fri", "शुक्र"),
    DAY7("Sat", "शनि");

    private String engVal;
    private String nepVal;

    WeekNepaliConstants(String engVal, String nepVal) {
        this.engVal = engVal;
        this.nepVal = nepVal;
    }

    public WeekNepaliConstants setDay(Integer ordinal)
    {
        WeekNepaliConstants weekNepaliConstants = WeekNepaliConstants(ordinal);
        this.engVal = weekNepaliConstants.engVal;
        this.nepVal = weekNepaliConstants.nepVal;
        return this;
    }

    public static String getNepVal(Integer ordinal)
    {
        WeekNepaliConstants weekNepaliConstants = WeekNepaliConstants(ordinal);
        return weekNepaliConstants.nepVal;
    }
    public static String getEngVal(Integer ordinal)
    {
        WeekNepaliConstants weekNepaliConstants = WeekNepaliConstants(ordinal);
        return  weekNepaliConstants.engVal;
    }

    public String nepValue() {
        return this.engVal;
    }

    public String engVal() {
        return this.nepVal;
    }



    public static WeekNepaliConstants WeekNepaliConstants(Integer ordinal){
        switch (ordinal) {


            case 1:
                return WeekNepaliConstants.DAY2;
            case 2:
                return WeekNepaliConstants.DAY3;
            case 3:
                return WeekNepaliConstants.DAY4;
            case 4:
                return WeekNepaliConstants.DAY5;
            case 5:
                return WeekNepaliConstants.DAY6;
            case 6:
                return WeekNepaliConstants.DAY7;
            case 7:
                return WeekNepaliConstants.DAY1;
            default:
                throw new CustomException("Invalid Enum value");
        }
    }
}
