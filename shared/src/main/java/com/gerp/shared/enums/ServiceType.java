package com.gerp.shared.enums;

public enum ServiceType {
    SERVICE("Service", "सेवा"),
    GROUP("Group", "समूह"),
    SUBGROUP("Subgroup", "उपसमूह");

    private final String valueEnglish;
    private final String valueNepali;

    ServiceType(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }
}
