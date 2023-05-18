package com.gerp.usermgmt.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSectionDesignationLogPojo {
    private Long id;

    private Integer sectionDesignationId;


    private String prevEmployeePisCode;

    private String newEmployeePisCode;

    private Boolean isLatest;
}
