package com.gerp.shared.enums;

public enum EmployeeUserType {
    PERMANENT("PERMANENT", "स्थायी"),
    CONTRACT("CONTRACT", "करार"),
    TEMPORARY("TEMPORARY", "अस्थायी");

        private String valueEnglish;
        private String nepaliValue;

        EmployeeUserType(String valueEnglish, String valueNepali){
            this.valueEnglish = valueEnglish;
            this.nepaliValue = valueNepali;
        }
}
