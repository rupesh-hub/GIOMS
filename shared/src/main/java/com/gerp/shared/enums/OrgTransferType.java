package com.gerp.shared.enums;

public enum OrgTransferType {
    SARUWA("सरुवा","Saruwa"), KAAJ_SARUWA("काज सरुवा","Kaaj Saruwa")
    , KAAJ_FIRTA("काज फिर्ता","Kaaj Firta");

    private final String valueNepali;
    private final String valueEnglish;


    OrgTransferType(String valueNepali, String valueEnglish) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }
}
