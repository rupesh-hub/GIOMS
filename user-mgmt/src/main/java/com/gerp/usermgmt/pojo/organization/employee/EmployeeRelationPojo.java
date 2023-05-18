package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeRelationPojo {
    private String relation;

    private String firstNameEn;

    private String firstNameNp;

    private String middleNameNp;

    private String middleNameEn;

    private String lastNameEn;

    private String lastNameNp;
}
