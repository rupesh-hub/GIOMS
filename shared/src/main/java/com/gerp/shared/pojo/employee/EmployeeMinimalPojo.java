package com.gerp.shared.pojo.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeMinimalPojo {
    private String pisCode;
    private String employeeCode;
    private String email;
    private String citizenshipNumber;
    private String employeeNameEn;
    private String employeeServiceStatusCode;
    private String employeeNameNp;
    private List<String> userType;
    private String sectionCode;
    private Long sectionId;

    private String officeCode;
    @JsonIgnore
    private IdNamePojo coreDesignation;

    private IdNamePojo functionalDesignation;
    private IdNamePojo employeeOffice;

    private IdNamePojo position;
    private IdNamePojo section;
    private Integer days;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate joiningDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate curOfficeJoinDtEn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate curOfficeJoinDtNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String profilePic;

    private MultipartFile file;
}
