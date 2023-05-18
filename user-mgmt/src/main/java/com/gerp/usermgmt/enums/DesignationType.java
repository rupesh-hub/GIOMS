package com.gerp.usermgmt.enums;

public enum DesignationType {
    NORMAL_DESIGNATION("NORMAL_DESIGNATION ", "Normal Designation" ,"सामान्य पद"),
    SPECIAL_DESIGNATION("SPECIAL_DESIGNATION", "Special Designation" , "विशेष पद");

    private String key;
    private String valueNepali;
    private String valueEnglish;

    DesignationType(String key, String valueEnglish, String valueNepali) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }
}
