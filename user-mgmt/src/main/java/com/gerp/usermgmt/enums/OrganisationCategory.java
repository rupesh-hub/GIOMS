package com.gerp.usermgmt.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OrganisationCategory {
    GOVERNMENT_OFFICE("GOVERNMENT_OFFICE" , "Government Office", "सरकारी कार्यालय\n"), SANSTHAN("SANSTHAN" ,"Sansthan", "संस्थान");

    private String key;
    private String valueNepali;
    private String valueEnglish;

    OrganisationCategory(String key, String valueEnglish, String valueNepali) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

}
