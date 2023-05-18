package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum  DashboardParamStatus {
    LR("Leave Request", "Leave Request"),
    KR("Kaaj Reqeuest", "Kaaj Request"),
    DR("Darta", "Darta"),
    CH("Chalani", "Chalani"),
    TI("Tippani", "Tippani");

    private final String nameEn;
    private final String nameNp;

    DashboardParamStatus(String nameEn, String nameNp) {
        this.nameEn = nameEn;
        this.nameNp = nameNp;
    }


    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(DashboardParamStatus.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .valueEnglish(x.nameEn)
                        .valueNepali(x.nameNp)
                        .build())
                .collect(Collectors.toList());
    }


    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .valueEnglish(this.nameEn)
                .valueNepali(this.nameNp)
                .build();
    }
}
