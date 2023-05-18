package com.gerp.usermgmt.model.office;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import com.gerp.usermgmt.model.administrative.AdministrativeBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organization_level")
@EqualsAndHashCode(callSuper = true)
public class OrganizationLevel  extends BaseEmployeeEntity {
    private String code;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation_type_seq_gen")
    @SequenceGenerator(name = "organization_level_seq_gen", sequenceName = "organization_level_seq_gen", initialValue = 100, allocationSize = 1)
    private Integer id;

    @GeneratorType(
            type = OrganizationIdGenerator.class,
            when = GenerationTime.INSERT
    )
    @OneToOne
    @JoinColumn(name = "organisation_type_id", foreignKey = @ForeignKey(name = "fk_office_organisation_type"))
    private OrganisationType organisationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrative_body_id", foreignKey = @ForeignKey(name = "fk_administrative_body_organization_level"))
    private AdministrativeBody administrativeBody;

    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(100)")
    private String shortNameEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_50)
    @Column(name = "hod_title_np", columnDefinition = "VARCHAR(50)")
    private String hodTitleNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "hod_title_en", columnDefinition = "VARCHAR(20)")
    private String hodTitleEn;

    @Column(name = "order_no")
    private Long orderNo;
}
