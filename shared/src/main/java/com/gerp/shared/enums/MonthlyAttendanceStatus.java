package com.gerp.shared.enums;

import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum  MonthlyAttendanceStatus {

    KAAJ("काज", "Kaaj","का"),
    ATTENDANCE_AND_KAAJ("हजिर भई काजमा गएको ", "Attendance and Kaaj","हा.का"),
    LEAVE("विदा", "Leave","बि"),
    UNINOFRMED_LEAVE_ABSENT("अनुपस्ठित", "Uninformed leave","अनु"),
    WEEKEND("सप्ताहन्त विदा", "Weekend","सा.बि."),
    PUBLIC_HOLIDAY("सार्वजनिक विदा", "Public Holiday","सा.बि."),
    ATTENDANCE("उपस्थिति", "Attendance","उ"),
    HALF_LEAVE("आधा दिन बिदामा उपस्थिति","Half Leave","उ"),
    BAATO_MYAAD("बाटो म्याद ", "Bato Myaad", "बा.म्या"),
    ATTENDANCES("उपस्थिति","Attendance","उ");


    private final String valueNepali;
    private final String valueEnglish;
    private final String valueNepaliShort;

    MonthlyAttendanceStatus(String valueNepali, String valueEnglish,String valueNepaliShort) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
        this.valueNepaliShort = valueNepaliShort;

    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(MonthlyAttendanceStatus.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .valueEnglish(x.valueEnglish)
                        .valueNepali(x.valueNepali)
                        .valueNepaliShort(x.valueNepaliShort)
                        .build())
                .collect(Collectors.toList());
    }

    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .valueEnglish(this.valueEnglish)
                .valueNepali(this.valueNepali)
                .valueNepaliShort(this.valueNepaliShort)
                .build();
    }
}
