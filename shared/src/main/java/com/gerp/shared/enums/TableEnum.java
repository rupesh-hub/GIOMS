package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TableEnum {

    LR("Leave Request", "Approval table code for leave request"),
    PR("Post Attendance Request", "Approval table code for Post Attendance"),
    KR("Kaaj Request", "Approval table code for kaaj request"),
    DL("Daily Log", "Approval table code for Daily Log"),
    MA("Manual Attendance", "Approval table code for Manual Attendance"),
    GK("Gayal Katti", "Approval table code for Gayal Katti");

    private final String name;
    private final String desc;

    TableEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(TableEnum.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .name(x.name)
                        .desc(x.desc)
                        .build())
                .collect(Collectors.toList());
    }

    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .name(this.name)
                .desc(this.desc)
                .build();
    }

}
