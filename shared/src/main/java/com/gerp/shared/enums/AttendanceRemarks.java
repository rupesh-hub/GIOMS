package com.gerp.shared.enums;

import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AttendanceRemarks {

    IR("अनियमित चेक इन", "Irregular"),
    R("नियमित उपस्थिति", "Regular");

    private final String valueNepali;
    private final String valueEnglish;

    AttendanceRemarks(String valueNepali, String valueEnglish) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;

    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(AttendanceRemarks.values())
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
}
