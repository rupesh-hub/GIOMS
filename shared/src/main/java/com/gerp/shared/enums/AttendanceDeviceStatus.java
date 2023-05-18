package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.pojo.KeyValuePojo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum AttendanceDeviceStatus {
    SAD("SaveAsDraft","SaveAsDraft",-1),
    NW("New","New",0),
    A("Approved","Approved",1),
    M("Modified","Modified",2),
    D("Deleted","Deleted",3),
    AM("ApprovedModified","ApprovedModified",4),
    NA("NonApproval","NonApproval",5);

    private final String valueEnglish;
    private final String valueNepali;
    private final Integer value;

    AttendanceDeviceStatus(String valueEnglish, String valueNepali, Integer value) {
        this.valueEnglish = valueEnglish;
        this.valueNepali = valueNepali;
        this.value=value;
    }

    public static List<KeyValuePojo> getEnumList() {
        return Arrays.stream(AttendanceDeviceStatus.values())
                .map(x -> KeyValuePojo.builder()
                        .key(x.toString())
                        .valueEnglish(x.valueEnglish)
                        .valueNepali(x.valueNepali)
                        .value(x.value)
                        .build())
                .collect(Collectors.toList());
    }

    public KeyValuePojo getEnum() {
        return KeyValuePojo.builder()
                .key(this.toString())
                .valueEnglish(this.valueEnglish)
                .valueNepali(this.valueNepali)
                .value(this.value)
                .build();
    }


}
