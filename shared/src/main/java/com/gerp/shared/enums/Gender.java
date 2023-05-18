package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Gender {

    A("सबै", "All", true),
    M("पुरुष", "Male", false),
    F("महिला", "Female", false),
    O( "तेस्रो लिंगी", "Other", false);

    private final String valueNepali;
    private final String valueEnglish;
    private final boolean withAll;

    Gender(String valueNepali, String valueEnglish, boolean withAll) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
        this.withAll = withAll;
    }

    public static List<KeyValuePojo> getEnumListWithAll() {
        return Arrays.stream(Gender.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .valueEnglish(x.valueEnglish)
                        .valueNepali(x.valueNepali)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(Gender.values())
                .filter(x->!x.equals(Gender.A))
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
