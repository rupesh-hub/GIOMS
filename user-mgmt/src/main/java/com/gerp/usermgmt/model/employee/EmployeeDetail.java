package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.shared.enums.BloodGroup;
import com.gerp.shared.enums.Gender;
import com.gerp.shared.enums.MaritalStatus;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.annotations.DobValidation;
import com.gerp.usermgmt.model.address.District;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import com.gerp.usermgmt.model.address.Province;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;


@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class EmployeeDetail {

    @Column(name = "birth_date_ad")
    @DobValidation
    private LocalDate birthDateAd;

    @Column(name = "birth_date_bs", length = StringConstants.DEFAULT_MAX_SIZE_100)
    private String birthDateBs;

    @Column(name = "current_position_app_date_ad")
    private LocalDate currentPositionAppDateAd = LocalDate.now();

    @Column(name = "current_position_app_date_bs", length = StringConstants.DEFAULT_MAX_SIZE_100)
    private String currentPositionAppDateBs;

    @Column(name = "emergency_phone_number", length = StringConstants.DEFAULT_MAX_SIZE_50)
    private String emergencyPhoneNumber;

    @Column(name = "home_phone_number", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String homePhoneNumber;

    @Column(name = "office_phone_number", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String officePhoneNumber;

    @Column(name = "mobile_number", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String mobileNumber;

    @Column(name = "email_address", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String emailAddress;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String temporaryWardNo;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String permanentWardNo;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String temporaryHouseNo;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String permanentHouseNo;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String temporaryStreet;

    @Column(length = StringConstants.DEFAULT_MAX_SIZE_30)
    private String permanentStreet;


    @Column(name = "father_fname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherFnameEn;

    @Column(name = "father_fname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherFnameNp;

    @Column(name = "father_mname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherMnameEn;

    @Column(name = "father_mname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherMnameNp;

    @Column(name = "father_lname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherLnameEn;

    @Column(name = "father_lname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherLnameNp;

    @Column(name = "father_full_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherFullNameEn;

    @Column(name = "father_full_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String fatherFullNameNp;


    @Column(name = "mother_fname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherFnameEn;

    @Column(name = "mother_fname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherFnameNp;

    @Column(name = "mother_mname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherMnameEn;

    @Column(name = "mother_mname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherMnameNp;

    @Column(name = "mother_lname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherLnameEn;

    @Column(name = "mother_lname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherLnameNp;

    @Column(name = "mother_full_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherFullNameEn;


    @Column(name = "mother_full_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String motherFullNameNp;

    @Column(name = "grand_father_fname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherFnameEn;

    @Column(name = "grand_father_fname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherFnameNp;

    @Column(name = "grand_father_mname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherMnameEn;

    @Column(name = "grand_father_mname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherMnameNp;

    @Column(name = "grand_father_lname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherLnameEn;

    @Column(name = "grand_father_lname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherLnameNp;

    @Column(name = "grand_father_full_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherFullNameEn;

    @Column(name = "grand_father_full_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String grandFatherFullNameNp;


    @Column(name = "spouse_fname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseFnameEn;

    @Column(name = "spouse_fname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseFnameNp;

    @Column(name = "spouse_mname_en", length = StringConstants.DEFAULT_MAX_SIZE_20)
    private String spouseMnameEn;

    @Column(name = "spouse_mname_np", length = StringConstants.DEFAULT_MAX_SIZE_50)
    private String spouseMnameNp;

    @Column(name = "spouse_lname_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseLnameEn;

    @Column(name = "spouse_lname_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseLnameNp;

    @Column(name = "spouse_full_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseFullNameEn;

    @Column(name = "spouse_full_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String spouseFullNameNp;

    @Column(name = "blood_group")
    private BloodGroup bloodGroup;

    @Column(name = "marital_status")
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "profile_pic", columnDefinition = "text")
    private String profilePic;


    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "per_province_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_employee_province_permanent"))
    private Province permanentProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "temp_province_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_employee_province_temporary"))
    private Province temporaryProvince;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "per_district_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_district_permanent"))
    private District permanentDistrict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "temp_district_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_district_temporary"))
    private District temporaryDistrict;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "per_municipality_vdc", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_municipality_permanent"))
    private MunicipalityVdc permanentMunicipalityVdc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "temp_municipality_vdc", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_municipality_temporary"))
    private MunicipalityVdc temporaryMunicipalityVdc;

    private String fax;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "telephone_no", columnDefinition = "VARCHAR(30)")
    private String telephoneNo;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "office_telephone", columnDefinition = "VARCHAR(20)")
    private String officeTelephone;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "extension_no", columnDefinition = "VARCHAR(20)")
    private String extensionNo;

    @Column(name = "cur_office_join_dt_en")
    private LocalDate curOfficeJoinDtEn;

    @Column(name = "cur_office_join_dt_np",columnDefinition = "VARCHAR(10)")
    private String curOfficeJoinDtNp;

    @OneToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "employee_pis_code", foreignKey = @ForeignKey(name = "fk_employee_joining_date"))
    private List<EmployeeJoiningDate> employeeJoiningDates;

}
