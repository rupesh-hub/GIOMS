package com.gerp.shared.enums;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public enum EnglishDayType {

    Sunday("Sunday", "sunday"),
    Monday("Monday", "monday"),
    Tuesday("Tuesday","tuesday"),
    Wednesday("Wednesday","wednesday"),
    Thursday("Thursday","thursday"),
    Friday("Friday","friday"),
    Saturday("Saturday","saturday");


    private String key;
    private String value;

    EnglishDayType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getKey(EnglishDayType day) {
        return day.key;
    }

    public String getValue(EnglishDayType day) {
        return day.value;
    }
//    public static List<KeyValuePojo> getEnumList() {
//        return Arrays.stream(TransactionTypeFlag.values())
//                .map(x -> new KeyValuePojo(x.key, x.name()))
//                .collect(Collectors.toList());
//    }
}
