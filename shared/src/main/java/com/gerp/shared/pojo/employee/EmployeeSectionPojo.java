package com.gerp.shared.pojo.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeSectionPojo {
    private Long id;
    private String code;
    private String nameNp;
    private String nameEn;
    private String parentCode;
    private String officeCode;
    private ArrayList<EmployeeSectionPojo> subsection;
    private ArrayList<EmployeeMinimalPojo> employees;


    //for seciton response
    private String sectionCode;
    private String sectionNameEn;
    private String sectionNameNp;

    private ArrayList<EmployeeMinimalPojo> employeeList;

    public String getCode() {
        return String.valueOf(id);
    }
}
