package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.config.generator.EmployeePisCodeGenerator;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import com.gerp.usermgmt.config.generator.ServiceCodeGeneratorNew;
import com.gerp.usermgmt.model.office.OrganisationType;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Builder
public class Service extends BaseEmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "service_code_seq")
    @GenericGenerator(
            name = "service_code_seq", strategy = "com.gerp.usermgmt.config.generator.ServiceCodeGeneratorNew", parameters = {
            @org.hibernate.annotations.Parameter(name = ServiceCodeGeneratorNew.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = ServiceCodeGeneratorNew.INITIAL_PARAM, value = "1000100")
            })
private String code;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Service> services = new HashSet<>();

    @ManyToOne()
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "parent_code", foreignKey = @ForeignKey(name = "fk_self_service"))
    private Service parent;

    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "service_type_code" , columnDefinition = "varchar(20)")
    private String serviceTypeCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(100)")
    private String shortNameEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;

    @Column(name = "order_no")
    private Long orderNo;

    @GeneratorType(
            type = OrganizationIdGenerator.class,
            when = GenerationTime.INSERT
    )
    @OneToOne
    @JoinColumn(name = "organisation_type_id", foreignKey = @ForeignKey(name = "fk_service_group_organisation_type"))
    private OrganisationType organisationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;


    public Service(String serviceCode) {
        this.code = serviceCode;
    }

    @Override
    public Serializable getId() {
        return code;
    }
}
