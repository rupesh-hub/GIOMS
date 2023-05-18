package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
public class SectionDetailPojo {
    private  String code;
    private String nameNp;
    private String nameEn;
    private String officeCode;
    private String parentCode;
    private String subsection;
    private String employees;


    private String sectionCode;

    private String sectionNameEn;

    private String sectionNameNp;

    private List<EmployeeMinimalPojo> employeeList;
}
