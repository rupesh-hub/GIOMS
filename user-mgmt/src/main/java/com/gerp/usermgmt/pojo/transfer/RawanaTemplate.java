package com.gerp.usermgmt.pojo.transfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RawanaTemplate {

    public String departmentName;
    public String patroNo;
    public String date;
    public OfficeDetailRawana officeDetail;
    public EmployeeDetailsPojo employeeDetail;
    public SaruwaDetail sabikDetail;
    public SaruwaDetail saruwaDetail;
    public LeaveTakenPojo leaveTaken;
    public AccumulatedLeavePojo accumulatedLeave;
    public boolean barbujhanathGareko;
    public boolean barbujhanathNagareko;
    public String ramanaDate;
    public String verifyDate;
    public String salary;
    public String increaseSalary;
    public String allowanceDate;
    public String accumulatedSalary;
    public String expenditureInHealth;
    public RamanaDetailLetterPojo ramanaDetail;

    public RawanaTemplate(String departmentName, String patroNo, String date, OfficeDetailRawana officeDetail, EmployeeDetailsPojo employeeDetail, SaruwaDetail sabikDetail, SaruwaDetail saruwaDetail, LeaveTakenPojo leaveTaken, AccumulatedLeavePojo accumulatedLeave, String ramanaDate, String verifyDate, String salary, String increaseSalary, String allowanceDate, String accumulatedSalary, String expenditureInHealth, RamanaDetailLetterPojo ramanaDetail) {
        this.departmentName = departmentName;
        this.patroNo = patroNo;
        this.date = date;
        this.officeDetail = officeDetail;
        this.employeeDetail = employeeDetail;
        this.sabikDetail = sabikDetail;
        this.saruwaDetail = saruwaDetail;
        this.leaveTaken = leaveTaken;
        this.accumulatedLeave = accumulatedLeave;
        this.ramanaDate = ramanaDate;
        this.verifyDate = verifyDate;
        this.salary = salary;
        this.increaseSalary = increaseSalary;
        this.allowanceDate = allowanceDate;
        this.accumulatedSalary = accumulatedSalary;
        this.expenditureInHealth = expenditureInHealth;
        this.ramanaDetail = ramanaDetail;
    }
}
