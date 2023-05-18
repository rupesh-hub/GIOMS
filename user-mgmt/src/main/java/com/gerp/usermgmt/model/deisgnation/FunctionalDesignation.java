package com.gerp.usermgmt.model.deisgnation;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.config.generator.DesignationCodeGenerator;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import com.gerp.usermgmt.enums.DesignationType;
import com.gerp.usermgmt.model.office.OrganisationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "functional_designation", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"}, name = "unique_functional_designation_code"),
})
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FunctionalDesignation extends BaseEmployeeEntity {
//
//    @Column(name = "hierarchy", nullable = false)
//    private Integer hierarchy;

    private Integer id;

    @GeneratorType(
            type = OrganizationIdGenerator.class,
            when = GenerationTime.INSERT
    )
    @OneToOne
    @JoinColumn(name = "organisation_type_id", foreignKey = @ForeignKey(name = "fk_designation_organisation_type"))
    private OrganisationType organisationType;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "salutation", columnDefinition = "varchar(100)")
    private String salutation;

//    @Id
//    @GeneratedValue(generator = "designation_code_seq")
//    @GenericGenerator(name = "designation_code_seq", strategy = "com.gerp.usermgmt.config.generator.DesignationCodeGenerator")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "designation_code_seq")
    @GenericGenerator(name = "designation_code_seq",
    strategy = "com.gerp.usermgmt.config.generator.DesignationCodeGenerator",
    parameters = {
            @org.hibernate.annotations.Parameter(name = DesignationCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = DesignationCodeGenerator.INITIAL_PARAM, value = "1111100400"),
    })
    private String code;

    @Column(name = "is_category")
    private Boolean isCategory;

    @Column(name = "order_no")
    private Long orderNo;

    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(100)")
    private String shortNameEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;

    @Column(name = "has_sub_class")
    private Boolean hasSubClass;

    @Enumerated(EnumType.STRING)
    private DesignationType designationType;

    @Column(name = "manually_created")
    private Boolean manuallyCreated = Boolean.TRUE;
    @Override
    public Serializable getId() {
        return code;
    }

}
