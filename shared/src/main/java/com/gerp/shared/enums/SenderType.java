package com.gerp.shared.enums;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public enum SenderType {

    IN("Individual", ""),
    OG("Organization", ""),
    OT("Other Organization", "");

    private String valueEnglish;
    private String valueNepali;

    SenderType(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }
}
