package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ReceiverType {

    WITHIN_ORGANIZATION("भित्र_संगठनात्मक", "within_organization"),
    INTERNAL_ORGANIZATION("आन्तरिक_अर्गठन", "internal_organization"),
    EXTERNAL_ORGANIZATION("बाह्य_ संगठन", "external_organization");

    private final String valueNepali;
    private final String valueEnglish;

    ReceiverType(String valueNepali, String valueEnglish) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;

    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(ReceiverType.values())
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
