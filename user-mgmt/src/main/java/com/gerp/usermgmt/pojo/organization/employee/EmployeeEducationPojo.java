package com.gerp.usermgmt.pojo.organization.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEducationPojo {

    private Integer id;

    private String educationLevelCode;

    private String facultyCode;

    private String countryCode;

//    private IdNamePojo country;
//
//    private IdNamePojo faculty;
//
//    private IdNamePojo educationLevel;
    //todo modify to object for country facult and educatin level

    private String countryNameEn;
    private String countryCameNp;

    private String facultyNameEn;
    private String facultyNameNp;

    private String educationLevelEn;
    private String educationLevelNp;

    private String institute;

    private String passedYearEn;

    private String passedYearNp;

    private LocalDate admissionDateEn;

    private String admissionDateNp;

    private LocalDate completionDateEn;

    private String completionDateNp;

    private Double percentage;

    private Double grade;

    private String remarks;

}
