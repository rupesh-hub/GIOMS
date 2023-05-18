package com.gerp.shared.enums;

import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public enum LetterPrivacy {

    NM("Normal", "सामान्य"),
    CN("Confidential", "गोप्य"),
    HC("Highly Confidential", "उच्च गोपनियता");

    private String valueEnglish;
    private String valueNepali;

    LetterPrivacy(String valueEnglish, String valueNepali) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(LetterPrivacy.values())
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
