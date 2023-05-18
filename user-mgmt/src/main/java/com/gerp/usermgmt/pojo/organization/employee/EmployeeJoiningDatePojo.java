package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeJoiningDatePojo {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joinDateEn;

    private String joinDateNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDateEn;

    private String endDateNp;

    private Boolean isActive;

    private String employeePisCode;
}
