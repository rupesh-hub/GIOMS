package com.gerp.usermgmt.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Gender {
    M("MALE", "पुरुष", "Male"),
    F("FEMALE", "महिला", "Female"),
    O("OTHER", "तेस्रो लिंगी", "Other");

    private String key;
    private String valueNepali;
    private String valueEnglish;

    Gender(String key, String valueNepali, String valueEnglish) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

    public String getKey() {
        return key;
    }

    public String getValueNepali() {
        return valueNepali;
    }

    public String getValueEnglish() {
        return valueEnglish;
    }

    public static List<KeyValuePojo> getGenderList() {
        List<KeyValuePojo> genderList = new ArrayList<>();
        genderList = Arrays.stream(Gender.values()).map(gender ->
                new KeyValuePojo(gender.key, gender.valueNepali, gender.valueEnglish))
                .collect(Collectors.toList());
        return genderList;
    }
}
