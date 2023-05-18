package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeOtherDetailsPojo extends EmployeeDetailsPojo{
    public String organizationName;
    public String birthDate;
    public String joinDate;
    public String district;

    public EmployeeOtherDetailsPojo(String employeeName, String employeeCode, String organizationName, String birthDate, String joinDate, String district) {
        super(employeeName, employeeCode);
        this.organizationName = organizationName;
        this.birthDate = birthDate;
        this.joinDate = joinDate;
        this.district = district;
    }
}
