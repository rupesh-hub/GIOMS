package com.gerp.usermgmt.pojo.organization.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gerp.shared.enums.BloodGroup;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.pojo.json.BloodGroupKeyValueOptionSerializer;
import com.gerp.shared.pojo.json.GenderKeyValueOptionSerializer;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import com.gerp.shared.utils.StringDataUtils;
import com.gerp.usermgmt.model.address.Province;
import com.gerp.usermgmt.pojo.RoleDetailPojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeePojo {
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC, message = FieldErrorConstant.INVALID)
    private String pisCode;
    private String employeeCode;
    private String sectionDesignationId;
    private Long contactId;
    private String positionCode;
    private String officeCode;
    private String nameNp;
    private String nameEn;
    private String errorMessage;

    private Boolean isFavourite;

    private String firstNameEn;

    private String middleNameEn;

    private String lastNameEn;

    private String firstNameNp;

    private String middleNameNp;

    private String lastNameNp;

    private String religionCode;

    private String maritalStatus;

    private LocalDate birthDateAd;

    private String birthDateBs;

    private String pan;

    @JsonSerialize(using = BloodGroupKeyValueOptionSerializer.class, as = Enum.class)
    private BloodGroup bloodGroup;


    @JsonSerialize(using = GenderKeyValueOptionSerializer.class, as = Enum.class)
    private Gender gender;

    private String emailAddress;
    private String mobileNumber;
    private String fax;
    private String telephoneNo;
    private String extensionNo;

    private List<RoleDetailPojo>roleList;

    private Boolean isActive;
    private LocalDate currentPositionAppDateAd;
    private String currentPositionAppDateBs;

    private String citizenshipNumber;
    private String sectionId;
    private Integer coreDesignationId;
    private BigInteger serviceGroupId;
    private String functionalDesignationCode;
    private String functionalDesignationType;
    private ReligionPojo religionPojo;
    private IdNamePojo office;

    private IdNamePojo functionalDesignation;
    private IdNamePojo position;
    private IdNamePojo serviceGroup;
    private ServicePojo group;
    private ServicePojo service;
    private ServicePojo subGroup;
    private IdNamePojo serviceStatus;
    private IdNamePojo coreDesignation;
    private IdNamePojo section;

    private IdNamePojo sectionSubsection;

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

    private Boolean userAlreadyExists;

    private Province permanentProvince;

    private Province temporaryProvince;

    private IdNamePojo permanentDistrict;

    private IdNamePojo temporaryDistrict;

    private IdNamePojo permanentMunicipalityVdc;

    private String employeeType;

    private IdNamePojo temporaryMunicipalityVdc;

    private String temporaryWardNo;
    private String permanentWardNo;

    private String temporaryHouseNo;

    private String permanentHouseNo;

    List<EmployeeJoiningDatePojo> employeeJoiningDates;

    private LocalDate curOfficeJoinDtEn;

    private String curOfficeJoinDtNp;

    private Integer orderNo;

    private Boolean isTriggeredUp;

    private String shiftNameEn;

    private String shiftNameNp;


    private String shiftCheckInTime;

    private String shiftCheckOutTime;

    private String profilePic;


    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Kathmandu")
    private Timestamp adminRoleCreatedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Kathmandu")
    private Timestamp adminRoleUpdatedDate;

    private String createDateNp;
    private String updateDateNp;




//    @JsonSetter("bloodGroup")
//    public void setBloodGroup1(BloodGroup bloodGroup) {
//        this.bloodGroup = bloodGroup;
//    }

    public void setBloodGroup(String val){
        if(val != null) {
            if (StringDataUtils.isNumeric(val)) {
                this.bloodGroup = BloodGroup.getEnumFromOrdinal(Integer.valueOf(val));
            } else {
                this.bloodGroup = BloodGroup.valueOf(val);
            }
        }
    }

}
