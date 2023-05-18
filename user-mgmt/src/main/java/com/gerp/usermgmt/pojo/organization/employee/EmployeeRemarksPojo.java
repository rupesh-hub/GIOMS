package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.EmployeeStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeeRemarksPojo {

    private Long id;

    private String remarks;

    private String pisCode;


    private EmployeeStatus employeeStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate actionDate;
}
