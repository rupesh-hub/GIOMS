package com.gerp.kasamu.constant;

public enum Status {
    PENDING("PENDING", "PENDING"),
    PROGRESS("On Progress", "On Progress"),
    FORWARDED("Forwarded", "Forwarded"),
    CANCELED("CANCELED", "CANCELED"),
    APPROVED("APPROVED", "APPROVED"),
    REJECTED("REJECTED", "REJECTED"),
    COMPLETED("COMPLETED", "COMPLETED"),
    STARTED("STARTED", "STARTED"),
    TODO("TODO","TODO");

    private  String valueEnglish;
    private  String valueNepali;
    Status(String valueEnglish, String valueNepali){
        this.valueEnglish =valueEnglish;
        this.valueNepali = valueNepali;
    }

    public void setValueEnglish(String valueEnglish) {
        this.valueEnglish = valueEnglish;
    }

    public void setValueNepali(String valueNepali) {
        this.valueNepali = valueNepali;
    }

    public String getValueEnglish(){
        return valueEnglish;
    }

    public String getValueNepali(){
        return valueNepali;
    }

}
