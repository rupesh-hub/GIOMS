package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.shared.enums.BloodGroup;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.enums.MaritalStatus;
import com.gerp.shared.pojo.json.GenderKeyValueOptionSerializer;
import com.gerp.usermgmt.annotations.DobValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KararEmployeePojo {
    private String pisCode;

    private String employeeCode;

    private String firstNameEn;

    private String middleNameEn;

    private String lastNameEn;

    private String firstNameNp;

    private String middleNameNp;

    private String lastNameNp;

    private MaritalStatus maritalStatus;

    @DobValidation
    private LocalDate birthDateAd;

    private String birthDateBs;

    private String pan;

    private BloodGroup bloodGroup;


    @JsonSerialize(using = GenderKeyValueOptionSerializer.class, as = Enum.class)
    private Gender gender;

    private String emailAddress;
    private String mobileNumber;
    private String fax;
    private String telephoneNo;
    private String extensionNo;


    private Boolean isActive = Boolean.TRUE;
    private LocalDate currentPositionAppDateAd;
    private String currentPositionAppDateBs;

    private String citizenshipNumber;
    private String sectionId;
    private BigInteger serviceGroupId;
//    private ReligionPojo religion;
    private String officeCode;

    private String designationCode;
    private String positionCode;
    private String serviceCode;
    private String employeeServiceStatusCode;
    private String sectionSubsectionId;

    private String fatherFnameEn;

    private String fatherFnameNp;

    private String fatherMnameEn;

    private String fatherMnameNp;

    private String fatherLnameEn;

    private String fatherLnameNp;

    private String fatherFullNameEn;

    private String fatherFullNameNp;

    private String motherFnameEn;

    private String motherFnameNp;

    private String motherMnameEn;

    private String motherMnameNp;

    private String motherLnameEn;

    private String motherLnameNp;

    private String motherFullNameEn;


    private String motherFullNameNp;

    private String grandFatherFnameEn;

    private String grandFatherFnameNp;

    private String grandFatherMnameEn;

    private String grandFatherMnameNp;

    private String grandFatherLnameEn;

    private String grandFatherLnameNp;

    private String grandFatherFullNameEn;

    private String grandFatherFullNameNp;


    private String spouseFnameEn;

    private String spouseFnameNp;

    private String spouseMnameEn;

    private String spouseMnameNp;

    private String spouseLnameEn;

    private String spouseLnameNp;

    private String spouseFullNameEn;

    private String spouseFullNameNp;

    private List<EmployeeRelationPojo> employeeRelations;

    private List<EmployeeEducationPojo> employeeEducations;

    private String officeTelephone;


//    private Province permanentProvinceId;

//    private Province temporaryProvince;

    private String permanentDistrictCode;

    private String temporaryDistrictCode;

    private String permanentMunicipalityVdcCode;
    private String temporaryMunicipalityVdcCode;

    private String temporaryWardNo;
    private String permanentWardNo;

    private String temporaryHouseNo;

    private String permanentHouseNo;

    private String temporaryStreet;

    private String permanentStreet;

    private String religionCode;

    private List<EmployeeJoiningDatePojo> employeeJoiningDates;

    private LocalDate curOfficeJoinDtEn;

    private String curOfficeJoinDtNp;

    private Long organisationTypeId;

    private String password;

    private String reenterPassword;

}
