package com.gerp.shared.enums;

import com.gerp.shared.pojo.KeyValuePojo;

import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.Optional;

public enum OrderBy {
    PRIVACY(1, "letter_privacy"),
    PRIORITY(2, "letter_priority"),
    SUBJECT(3, "subject"),
    STATUS(4, "status"),
    CREATED_DATE(5, "created_date"),
    TIPPANI_NO(6, "tippani_no"),
    REGISTRATION_NO(7, "registration_no"),
    DISPATCH_NO(8, "dispatch_no"),
    DISPATCH_DATE_EN(9, "dispatch_date_en"),
    ENTRY_TYPE(10, "entry_type");

    int colNum;
    String colVal;

    OrderBy(int colNum, String colVal) {
        this.colNum = colNum;
        this.colVal = colVal;
    }

    public static String getVal(int colNum) {
        return Arrays.stream(OrderBy.values()).filter(val -> val.colNum == colNum).findFirst().get().colVal;
    }
}
