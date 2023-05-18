package com.gerp.shared.enums;

import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LeaveTypeEnum {

    MA("म्यानुअल उपस्थिति", "Manual Attendance", "उ"),
    KAAJ("काज", "Kaaj", "काज"),
    GAYAL_KATTI("गयलकट्टी", "Gayal Katti", "ग"),
    LEAVE("विदा", "Leave", "बि"),
    BAATO_MYAAD("बाटो म्याद ", "Bato Myaad", "बा.म्या"),
    UNINOFRMED_LEAVE_ABSENT("अनुपस्थित ", "Uninformed leave", "अनु"),
    WEEKEND("सप्ताहन्त", "Weekend", "सा.बि."),
//    PA("पोष्ट उपस्थिति", "Post Attendance"),
    PUBLIC_HOLIDAY("सार्वजनिक विदा", "Public Holiday", "सा.बि."),
    ATTENDANCE_AND_KAAJ("हजिर भई काजमा गएको ", "Attendance and Kaaj","हा.का"),
    ATTENDANCE_ON_HOLIDAY("विदाको दिनमा उपस्थित", "Attendance_on_holiday","बि.उ"),
    DEVICE("उपस्थित", "Device Attendance", "उ");


    private final String valueNepali;
    private final String valueEnglish;
    private final String valueNepaliShort;

    LeaveTypeEnum(String valueNepali, String valueEnglish, String valueNepaliShort) {
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
        this.valueNepaliShort = valueNepaliShort;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(LeaveTypeEnum.values())
                .filter(y->(!y.valueEnglish.equalsIgnoreCase("Gayal Katti")
                            && !y.valueEnglish.equalsIgnoreCase("Bato Myaad")
                            && !y.valueEnglish.equalsIgnoreCase("Attendance and Kaaj")))
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

    public Boolean getIsPresent() {
        switch (this){
            case DEVICE:
            case MA:
            case KAAJ:
                return true;
            default:
                return false;
        }
    }
}
