package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeMinimalDetailsPojo {

    private String pisCode;
    private String employeeCode;
    private String employeeNameEn;
    private String employeeNameNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String designationEn;
    private String designationNp;
    private IdNamePojo functionalDesignation;
    private Boolean isActive;


}
