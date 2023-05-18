package com.gerp.usermgmt.model.employee;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.model.deisgnation.FunctionalDesignation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "section_designation",    uniqueConstraints=
@UniqueConstraint(columnNames={"section_subsection_id", "employee_pis_code" ,"is_active"}))
public class SectionDesignation extends AuditActiveAbstract{

    public SectionDesignation(SectionDesignation designation){
        this(designation.getId(), designation.getFunctionalDesignation(), designation.getSectionSubsection(),designation.getEmployee(),
                designation.getCurrentPositionAppDateAd(),designation.getCurrentPositionAppDateBs(),designation.getPosition(),
                designation.getService() ,designation.getOrderNo(), designation.isOnTransferProcess(), designation.getDisabled());
    }
    @Id
    @SequenceGenerator(name = "section_designation_seq", sequenceName = "section_designation_seq", allocationSize = 1)
    @GeneratedValue(generator = "section_designation_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_designation_code", foreignKey = @ForeignKey(name = "FK_func_designation_section"))
    private FunctionalDesignation functionalDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_subsection_id", foreignKey = @ForeignKey(name = "FK_section_subsection_sec_designation"))
    private SectionSubsection sectionSubsection;

    @OneToOne()
    @JoinColumn(name = "employee_pis_code", foreignKey = @ForeignKey(name = "FK_section_designation_employee"))
    private Employee employee;

    @Column(name = "current_position_app_date_ad")
    private LocalDate currentPositionAppDateAd;

    @Column(name = "current_position_app_date_bs", length = StringConstants.DEFAULT_MAX_SIZE_100)
    private String currentPositionAppDateBs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", foreignKey = @ForeignKey(name = "FK_position_section_designation"))
    private Position position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", foreignKey = @ForeignKey(name = "FK_service_section_designation"))
    private Service service;

    @Column(name = "order_no")
    private Integer orderNo;

    @Column(name = "is_on_transfer_process" , columnDefinition = "boolean default false")
    private boolean isOnTransferProcess = Boolean.FALSE;

    @Column(name = "disabled")
    private Boolean disabled = Boolean.FALSE;
}
