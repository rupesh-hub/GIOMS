package com.gerp.usermgmt.model.delegation;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.usermgmt.util.DelegationAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "delegation")
public class Delegation extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delegation_seq_gen")
    @SequenceGenerator(name = "delegation_seq_gen", sequenceName = "seq_delegation", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name ="effective_date")
    private LocalDateTime effectiveDate;
    @Column(name ="expire_date")
    private LocalDateTime expireDate;
    @Column(name ="form_section_id")
    private Integer fromSectionId;
    @Column(name ="form_piscode",columnDefinition = "VARCHAR(20)")
    private String fromPisCode;
    @Column(name ="to_section_id")
    private Integer toSectionId;
    @Column(name ="to_piscode",columnDefinition = "VARCHAR(20)")
    private String toPisCode;

    @Column(name ="is_reassignment")
    private Boolean isReassignment;

    @Column(name= "is_office_head")
    private Boolean isOfficeHead;

    @Column(name="form_designation_id")
    private String fromDesignationId;

    @Column(name="to_designation_id")
    private String toDesignationId;

    @Column(name = "form_position_code",columnDefinition = "VARCHAR(10)")
    private String fromPositionCode;

    @Column(name = "to_position_code",columnDefinition = "VARCHAR(10)")
    private String toPositionCode;

    @Column(name = "is_abort")
    private Boolean isAbort;

}
