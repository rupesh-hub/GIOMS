package com.gerp.usermgmt.model.employee;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.gerp.shared.enums.PositionType;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.base.BaseEmployeeEntity;
import com.gerp.usermgmt.config.generator.DelegationGenerator;
import com.gerp.usermgmt.config.generator.OrganizationIdGenerator;
import com.gerp.usermgmt.config.generator.PositionCodeGenerator;
import com.gerp.usermgmt.model.office.OrganisationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "position")
public class Position extends BaseEmployeeEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "position_code_seq")
    @GenericGenerator(name = "position_code_seq",
    strategy = "com.gerp.usermgmt.config.generator.PositionCodeGenerator",
    parameters = {
            @org.hibernate.annotations.Parameter(name = PositionCodeGenerator.INCREMENT_PARAM, value = "1"),
            @org.hibernate.annotations.Parameter(name = PositionCodeGenerator.INITIAL_PARAM, value = "2500")
    })
    private String code;


    @Column(name = "defined_code" , columnDefinition = "varchar(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String definedCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(100)")
    private String shortNameEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_100)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(100)")
    private String shortNameNp;


    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Position> positions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_position_code", foreignKey =
    @ForeignKey(name = "fk_self_position"))
    private Position parent;

    @Column(name = "order_no")
    private Long orderNo;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    private String source;

    @GeneratorType(
            type = OrganizationIdGenerator.class,
            when = GenerationTime.INSERT
    )
    @OneToOne
    @JoinColumn(name = "organisation_type_id", foreignKey = @ForeignKey(name = "fk_position_organisation_type"))
    private OrganisationType organisationType;


    @Enumerated(EnumType.STRING)
    @Column(name = "position_type")
    private PositionType positionType;

    @Override
    public Serializable getId() {
        return code;
    }
}
