package com.gerp.usermgmt.pojo.transfer;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendaceDetailPojo {

    private Long leaveTakenDays;

    private Long totalAllowedDays;

    private String nameNp;

    private String nameEn;
}
