package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Status {

    D("Draft", "ड्राफ्ट"),
    P("Pending", "पेन्डिङ"),
    OP("On Progress", "प्रगतिमा"),
    F("Forwarded", "सिफारिस गरिएको"),
    C("Canceled", "रद्द"),
    A("Approved", "स्वीकृत"),
    R("Rejected", "अस्वीकृत"),
    RV("Reverted", "फिर्ता भएको"),
    IP("In Progress", "प्रगति हुदैछ"),
    FN("Finalized", "अन्तिम"),
    SG("Suggestion", "राय"),
    INACTIVE("Inactive", "निष्क्रिय");


    private final String valueEnglish;
    private final String valueNepali;

    Status(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(Status.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .valueEnglish(x.valueEnglish)
                        .valueNepali(x.valueNepali)
                        .build())
                .collect(Collectors.toList());
    }

    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .valueEnglish(this.valueEnglish)
                .valueNepali(this.valueNepali)
                .build();
    }

    public String getValueEnglish() {
        return valueEnglish;
    }

    public String getValueNepali() {
        return valueNepali;
    }

    public boolean validateUpdate() {
        return this.equals(Status.C);
    }

    public boolean validateApprove() {
        return this.equals(Status.A) || this.equals(Status.F) || this.equals(Status.R);
    }

    public boolean validateReview() {
        return this.equals(Status.F);
    }

    public boolean validateRevert() {
        return this.equals(Status.RV);
    }

}
