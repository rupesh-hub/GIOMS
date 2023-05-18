package com.gerp.shared.enums;

public enum EmployeeType {
    PERMANENT_EMPLOYEE("Permanent Employee", "स्थायी कर्मचारी", "Permanent Employee "),
    TEMPORARY_EMPLOYEE("Temporary Employee", "अस्थायी कर्मचारी", "Temporary Employee");

    private final String key;
    private final String valueNepali;
    private final String valueEnglish;

    EmployeeType(String key, String valueNepali, String valueEnglish) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

}
