package com.gerp.shared.enums;

public enum EmployeeStatus {
    RETIREMENT("Retired", "अवकाश ग्रहण"),
    DEATH("मृत्यु भएको", "Death"),
    TERMINATED("Terminated", "गयलकट्टी"),

    SARUWA("Transfer", "सरुवा"),

    SUSPENDED("Suspended", "निलम्बित"),

    RESIGNATION("Resignation", "राजिनामा"),

    OTHER("Other", "अन्य");



    private final String valueEnglish;
    private final String valueNepali;


    EmployeeStatus(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }
}
