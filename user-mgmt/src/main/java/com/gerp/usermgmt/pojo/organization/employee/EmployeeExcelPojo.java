package com.gerp.usermgmt.pojo.organization.employee;

import com.gerp.shared.enums.Gender;
import com.poiji.annotation.ExcelCellName;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@NoArgsConstructor
@Data
public class EmployeeExcelPojo {

    @NotNull
    @ExcelCellName(value = "officeCode")
    private String officeCode;

    @ExcelCellName(value = "pisCode")
    private String pisCode;
    
    @ExcelCellName(value = "pisCode")
    private String employeeCode;

    @ExcelCellName(value = "firstNameEn")
    private String firstNameEn;

    @ExcelCellName(value = "middleNameEn")
    private String middleNameEn;

    @ExcelCellName(value = "lastNameEn")
    private String lastNameEn;

    @ExcelCellName(value = "firstNameNp")
    private String firstNameNp;

    @ExcelCellName(value = "middleNameNp")
    private String middleNameNp;

    @ExcelCellName(value = "lastNameNp")
    private String lastNameNp;

    @ExcelCellName(value = "mobileNumber")
    private String mobileNumber;

    @ExcelCellName(value = "gender")
    private Gender gender;

    @ExcelCellName(value = "emailAddress")
    private String emailAddress;

    @ExcelCellName(value = "designationCode")
    private String designationCode;

    @ExcelCellName(value = "positionCode")
    private String positionCode;

    @ExcelCellName(value = "serviceCode")
    private String serviceCode;

    @ExcelCellName(value = "subGroupCode")
    private String subGroupCode;

    @ExcelCellName(value = "groupCode")
    private String groupCode;

    @ExcelCellName(value = "employeeServiceStatusCode")
    private String employeeServiceStatusCode;

    @ExcelCellName(value = "curOfficeJoinDtEn")
    private LocalDate curOfficeJoinDtEn;

    @ExcelCellName(value = "curOfficeJoinDtNp")
    private String curOfficeJoinDtNp;

    @ExcelCellName(value = "currentPositionAppDateAd")
    private LocalDate currentPositionAppDateAd;

    @ExcelCellName(value = "currentPositionAppDateBs")
    private String currentPositionAppDateBs;

    private void setServiceCode(String serviceCode){
        if(serviceCode != null) {
            this.serviceCode= serviceCode;
        }
        if(this.groupCode != null) {
            this.serviceCode = groupCode;
        }
        if(this.subGroupCode != null) {
            this.serviceCode = this.subGroupCode;
        }

    }
}
