package com.gerp.tms.constant;

import com.sun.xml.bind.v2.TODO;

public enum Status {
    PENDING("PENDING", "PENDING"),
    PROGRESS("On Progress", "On Progress"),
    FORWARDED("Forwarded", "Forwarded"),
    CANCELED("CANCELED", "CANCELED"),
    APPROVED("APPROVED", "APPROVED"),
    REJECTED("REJECTED", "REJECTED"),
    COMPLETED("COMPLETED", "कार्य सम्पन्न भएको"),
    STARTED("STARTED", "कार्य सुरु भएको"),
    TODO("TODO","बाँकि कार्य");

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
