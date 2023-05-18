package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeNamePojo {
    private String pisCode;
    private String employeeNameEn;
    private String employeeNameNp;
    @JsonIgnore
    private String officeCode;
    @JsonIgnore
    private IdNamePojo coreDesignation;
    @JsonIgnore
    private IdNamePojo functionalDesignation;
}
