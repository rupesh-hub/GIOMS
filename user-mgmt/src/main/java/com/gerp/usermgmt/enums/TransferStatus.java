package com.gerp.usermgmt.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TransferStatus {

    P("Pending", "पेन्डिङ"),
    C("Canceled", "रद्द"),
    A("Approved", "स्वीकृत"),
    R("Rejected", "अस्वीकृत"),
    AK("ACKNOWLEDGED", "स्वीकृति");

    private final String valueEnglish;
    private final String valueNepali;

    TransferStatus(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(TransferStatus.values())
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
}
