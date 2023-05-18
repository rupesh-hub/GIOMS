package com.gerp.usermgmt.model.office;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import com.gerp.usermgmt.model.address.Country;
import com.gerp.usermgmt.model.address.District;
import com.gerp.usermgmt.model.address.MunicipalityVdc;
import com.gerp.usermgmt.model.address.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "office")
@EqualsAndHashCode(callSuper = true)
public class Office extends BaseEmployeeEntity {

    @Id
    @GeneratedValue(generator = "office_code_seq", strategy = GenerationType.SEQUENCE)
    @GenericGenerator(name = "office_code_seq", strategy = "com.gerp.usermgmt.config.generator.OfficeCodeGeneration")
    private String code;

    @OneToOne
    private OfficeType officeType;

    @GeneratorType(
            type = OrganizationIdGenerator.class,
            when = GenerationTime.INSERT
    )
    @OneToOne
    @JoinColumn(name = "organisation_type_id", foreignKey = @ForeignKey(name = "fk_office_organisation_type"))
    private OrganisationType organisationType;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "defined_code" , columnDefinition = "varchar(20)", nullable = true)
    private String definedCode;

    @Column(name = "office_name_code", columnDefinition = "VARCHAR(20)")
    private String officeNameCode;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Office> offices = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code", foreignKey = @ForeignKey(name = "fk_self_office"))
    private Office parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_level_id", foreignKey = @ForeignKey(name = "fk_organization_level_office"))
    private OrganizationLevel organizationLevel;


    @Size(max = StringConstants.PHONE_MAX_SIZE)
    @Column(name = "phone_number", columnDefinition = "varchar")
    private String phoneNumber;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    private String url;

    @Column(name = "email")
    private String email;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
    @Column(name = "address_en")
    private String addressEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_200)
    @Column(name = "address_np")
    private String addressNp;

    @Lob
    @Column(name = "remarks_en" , columnDefinition = "text")
    @Type(type = "org.hibernate.type.TextType")
    private String remarksEn;

    @Lob
    @Column(name = "remarks_np" , columnDefinition = "text")
    @Type(type = "org.hibernate.type.TextType")
    private String remarksNp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_category_code", foreignKey = @ForeignKey(name = "fk_office_category_office"))
    private OfficeCategory officeCategory;

    @Column(name = "office_prefix_en", columnDefinition = "VARCHAR(70)")
    private String officePrefixEn;


    @Column(name = "office_prefix_np", columnDefinition = "VARCHAR(70)")
    private String officePrefixNp;


    @Column(name = "office_suffix_en")
    private String officeSuffixEn;

    @Column(name = "office_suffix_np")
    private String officeSuffixNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "telephone_no", columnDefinition = "VARCHAR(20)")
    private String telephoneNo;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "fax_no", columnDefinition = "VARCHAR(20)")
    private String faxNo;


    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "office_dissolve_date")
    private LocalDate officeDissolveDate;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "office_create_dt")
    private LocalDate officeCreateDt;

    @Column(name = "allow_over_staffing")
    private Boolean allowOverStaffing = false;

    @Column(name = "is_gioms_active", columnDefinition = "boolean default false")
    private Boolean isGiomsActive;

    private Boolean disabled = Boolean.FALSE;

    private Boolean isHeadOffice;



    @Column(name = "office_serial_no", columnDefinition = "varchar(50)")
    private String officeSerialNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "province_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_employee_province"))
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "district_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_district"))
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "municipality_vdc_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_municipality_vdc_code"))
    private MunicipalityVdc municipalityVdc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "country_code", referencedColumnName = "code", foreignKey = @ForeignKey(name = "fk_employee_country"))
    private Country country;

    @Column(name = "set_up_completed", columnDefinition = "boolean default false")
    private Boolean setupCompleted;

    public Office(String code) {
        this.code = code;
        this.definedCode = code;
    }

    @Override
    public Serializable getId() {
        return this.code;
    }
}
