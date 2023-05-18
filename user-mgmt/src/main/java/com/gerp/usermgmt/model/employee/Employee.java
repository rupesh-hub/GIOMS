package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.config.generator.EmployeePisCodeGenerator;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import com.gerp.usermgmt.model.office.Office;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        indexes = {
                @Index(columnList = "pis_code", name = "pis_code_index"),
                @Index(columnList = "employee_code", name = "employee_code_index"),
        },
        name = "employee")
public class Employee extends EmployeeDetail implements BaseEntity{

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "employee_seq")
//    @GeneratorType(type = EmployeePisCodeGenerator.class, when = GenerationTime.INSERT)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @GenericGenerator(
            name = "employee_seq", strategy = "com.gerp.usermgmt.config.generator.EmployeePisCodeGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = EmployeePisCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = EmployeePisCodeGenerator.VALUE_PREFIX_PARAMETER, value = "K_"),
            @org.hibernate.annotations.Parameter(name = EmployeePisCodeGenerator.NUMBER_FORMAT_PARAMETER, value = "%03d"),

    })
    @Column(name = "pis_code", updatable = false)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC, message = "Employee Pis code must be Alpha numeric")
    private String pisCode;

    @Column(name = "employee_code", unique = true)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC, message = "Employee code must be Alpha numeric")
    private String employeeCode;

    @Column(name = "defined_code" , columnDefinition = "varchar(100)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    private String definedCode;

    @Column(name = "ref_emp_code" , columnDefinition = "varchar(100)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    private String refEmployeeCode;

 @ManyToOne()
 @JoinColumn(name = "employee_service_status_code", foreignKey = @ForeignKey(name = "fk_employee_service_status_employee"))
 private EmployeeServiceStatus employeeServiceStatus;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "app_service_status_code", foreignKey = @ForeignKey(name = "fk_employee_service_status_employee_app"))
 private EmployeeServiceStatus appEmployeeServiceStatus;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "position_code", foreignKey = @ForeignKey(name = "fk_employee_position"))
 private Position position;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "app_position_code", foreignKey = @ForeignKey(name = "fk_employee_position_app"))
 private Position appPosition;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "app_designation_code", foreignKey = @ForeignKey(name = "fk_employee_designation_app"))
 private FunctionalDesignation appDesignation;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "designation_code", foreignKey = @ForeignKey(name = "fk_employee_designation"))
 private FunctionalDesignation designation;

 @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
 @JoinColumn(name = "employee_pis_code",
         foreignKey = @ForeignKey(name = "fk_employee_employee_relation"))
 private List<EmployeeRelation> employeeRelations;

// @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
// @JoinColumn(name = "employee_pis_code",
//         foreignKey = @ForeignKey(name = "fk_employee_employee_education"))
// private List<EmployeeEducation> employeeEducations;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_pis_code",foreignKey = @ForeignKey(name = "fk_employee_employee_education"))
    private List<EmployeeEducation> employeeEducations;



 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "office_code", foreignKey = @ForeignKey(name = "fk_office_employee"), nullable = false)
 private Office office;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_code", foreignKey = @ForeignKey(name = "fk_service_employee"))
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_service_code", foreignKey = @ForeignKey(name = "fk_service_employee_app"))
    private Service appService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_subsection_id", foreignKey = @ForeignKey(name = "fk_self_section_subsection"))
    private SectionSubsection sectionSubsection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "religion_code", foreignKey = @ForeignKey(name = "fk_religion_employee"))
    private Religion religion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_service_type_code", foreignKey = @ForeignKey(name = "fk_employee_service_type_employee"))
    private EmployeeServiceType employeeServiceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_service_type_code", foreignKey = @ForeignKey(name = "fk_employee_service_type_employee_app"))
    private EmployeeServiceType appEmployeeServiceType;


    @Column(name = "citizenship_number", length = StringConstants.DEFAULT_MAX_SIZE_50)
    private String citizenshipNumber;

    @Column(name = "pan", length = StringConstants.DEFAULT_MAX_SIZE_100)
    private String pan;

//    @Column(name = "full_name_en", length = StringConstants.DEFAULT_MAX_SIZE_30)
//    private String fullNameEn;
//
//    @Column(name = "full_name_np", length = StringConstants.DEFAULT_MAX_SIZE_50)
//    private String fullNameNp;

    @Column(name = "first_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String firstNameEn;

    @Column(name = "middle_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String middleNameEn;

    @Column(name = "last_name_en", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String lastNameEn;

    @Column(name = "first_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String firstNameNp;

    @Column(name = "middle_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String middleNameNp;

    @Column(name = "last_name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String lastNameNp;

    @Column(name = "approved_date_np", columnDefinition = "VARCHAR(50)")
    private String approvedDateNp;

    private Boolean approved;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "approved_date_En")
    private LocalDate approvedDateEN;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
    @Column(name = "approved_by" , columnDefinition = "varchar(200)")
    private String approvedBy;

    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    private Boolean giomsEmployee = Boolean.TRUE;

    @Column(name = "order_no")
    private Integer orderNo;

    @Transient
    private Long organisationTypeId;

    @Override
    public Serializable getId() {
        return pisCode;
    }

    public Employee(String pisCode, Long sectionId) {
        this.pisCode = pisCode;
        this.setSectionSubsection(new SectionSubsection(sectionId));
    }

    public Employee(String pisCode) {
        this.pisCode = pisCode;
    }
}
