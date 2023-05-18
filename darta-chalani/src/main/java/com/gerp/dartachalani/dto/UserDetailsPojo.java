package com.gerp.dartachalani.dto;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsPojo {

    private Long id;
    private String letterType;
    private EmployeeMinimalPojo employeeMinimalPojo;
}
