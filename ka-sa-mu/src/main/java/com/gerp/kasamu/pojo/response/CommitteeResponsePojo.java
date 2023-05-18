package com.gerp.kasamu.pojo.response;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommitteeResponsePojo {

    private Long id;
    private String pisCode;
    private String officeCode;
    private Boolean status;
    private EmployeeMinimalPojo employeeDetails;

}
