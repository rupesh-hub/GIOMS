package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class JoiningDatePojo {
    private String employeePisCode;
    private String officeCode;
    private LocalDate joiningDateEn;
    private String joiningDateNp;
}
