package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaruwaLetterPojo {
    public String department;
    public String date;
    public String duration;
    public SabikDetail sabikDetail;
    public SabikDetail saruwaDetail;
    public EmployeeDetailsPojo employeeDetail;
}
