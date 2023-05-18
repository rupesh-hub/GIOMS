package com.gerp.usermgmt.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.usermgmt.enums.DesignationAssignFlag;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePojo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionDesignationLogResponsePojo {
    private Long id;
    private EmployeePojo employee;
    private EmployeePojo assignerEmployee;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastEditedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    private Boolean isAssigned;

    private DesignationAssignFlag designationAssignFlag;

    public void setDesignationAssignFlag(String value) {
        this.designationAssignFlag = DesignationAssignFlag.valueOf(value);
    }
}
