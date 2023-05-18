package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
public class SectionMinimalPojo {
    @JsonIgnore
    private Long id;
    private  String code;
    @JsonIgnore
    private String nameNp;
    @JsonIgnore
    private String nameEn;
    @JsonIgnore
    private String officeCode;
    @JsonIgnore
    private String parentCode;
    @JsonIgnore
    private String subsection;
    @JsonIgnore
    private String employees;


    private String sectionCode;

    private String sectionNameEn;

    private String sectionNameNp;

    private ArrayList<EmployeeMinimalDetailsPojo> employeeList;
}

