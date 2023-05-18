package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum NepaliMonth {
    Baisakh("Baisakh", "वैशाख"),
    Jestha("Jestha", "जेठ"),
    Ashar("Ashar","असार"),
    Shrawan("Shrawan","साउन"),
    Bhadra("Bhadra","भदौ"),
    Ashoj("Ashoj","असोज"),
    Kartik("Kartik","कात्तिक"),
    Mangsir("Mangsir","मंसिर"),
    Poush("Poush","पौष"),
    Magh("Magh","माघ"),
    Falgun("Falgun","फागुन"),
    Chaitra("Chaitra","चैत");

    private final String valueEnglish;
    private final String valueNepali;

    NepaliMonth(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(NepaliMonth.values())
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
