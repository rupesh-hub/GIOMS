package com.gerp.shared.enums;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public enum NepaliDay {

    आइतबार("आइतबार", "आइतबार"),
    सोमबार("सोमबार", "सोमबार"),
    मंगलबार("मंगलबार", "मंगलबार"),
    बुधबार("बुधबार", "बुधबार"),
    बिहिबार("बिहिबार", "बिहिबार"),
    शुक्रबार("शुक्रबार", "शुक्रबार"),
    शनिबार("शनिबार", "शनिबार");

    private String key;
    private String value;

    NepaliDay(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKey(NepaliDay nepaliDay) {
        return nepaliDay.key;
    }

    public String getValue(NepaliDay nepaliDay) {
        return nepaliDay.value;
    }
}
