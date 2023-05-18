package com.gerp.shared.enums;

public enum MaritalStatus {
    MARRIED("Married", "Married", "विवाहित "), UNMARRIED("UnMarried", "UnMarried", "अविवाहित")
    , UNKNOWN("NOT AVAILABLE", "NOT AVAILABLE", "उपलब्ध नभएको");

    private final String key;
    private final String valueNepali;
    private final String valueEnglish;

    MaritalStatus(String key, String valueNepali, String valueEnglish) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

   public static MaritalStatus getEnumFromCode(String code){
        switch (code) {
            case "01":
                return MaritalStatus.MARRIED;
            case "0":
                return MaritalStatus.UNKNOWN;
            case "02":
                return MaritalStatus.UNMARRIED;
            default:
                return null;

        }
    }
}
