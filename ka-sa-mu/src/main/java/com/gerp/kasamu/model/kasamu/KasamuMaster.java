package com.gerp.kasamu.model.kasamu;

import com.gerp.kasamu.converter.AttributeEncryptor;
import com.gerp.kasamu.model.committee.Committee;
import com.gerp.kasamu.model.committee.CommitteeIndicator;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@DynamicUpdate
@Entity
@Getter
@Setter
public class KasamuMaster extends AuditActiveAbstract {

    @Id
    @GeneratedValue(generator = "kasamu_master_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "kasamu_master_seq_gen", sequenceName = "seq_kasamu_master", initialValue = 1, allocationSize = 1)
    private Long id;


    @Column(columnDefinition = "VARCHAR(20)")
    private String valuationPeriod;


    @Column(columnDefinition = "TEXT")
    @Convert(converter = AttributeEncryptor.class)
    private String kasamuEnDetails;

//    @Column(columnDefinition = "VARCHAR(20)")
//    private String officeCode;


    @Column(columnDefinition = "VARCHAR(20)")
    private String employeePisCode;


    @Column(columnDefinition = "VARCHAR(20)")
    private String supervisorPisCode;


    @Column(columnDefinition = "VARCHAR(10)")
    private String evaluatorPisCode;


//    @Column(unique = true)
//    private Long regdNum;

    private LocalDate subDate;
//
//
//    @Column(columnDefinition = "VARCHAR(20)")
//    private String currentOfficeCode;

    @Column(columnDefinition = "VARCHAR(10)")
    private String fiscalYear;

//    private LocalDate superReceivedDate;
//
//    private LocalDate superSubmittedDate;
//
//    private LocalDate reviewerSubDate;


//    private Boolean submittedStatus;

//    @Column(columnDefinition = "TEXT")
////    @Convert(converter = AttributeEncryptor.class)
//    private String valRemarksBySupervisor;
//
//    @Column(columnDefinition = "TEXT")
////    @Convert(converter = AttributeEncryptor.class)
//    private String valRemarksByEvaluator;
//
//    @Column(columnDefinition = "TEXT")
////    @Convert(converter = AttributeEncryptor.class)
//    private String valRemarksByEmployee;
//
//    private LocalDate evalSubDate;
//
//    private LocalDate evalCommitteeSubDate;

    private boolean reviewedBySuper;

    private boolean reviewedByPurnarawal;

    private boolean reviewedByCommittee ;


//    @Convert(converter = AttributeEncryptor.class)
//    private String empShreni;
//
    private String status;


    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", referencedColumnName = "id",foreignKey = @ForeignKey(name = "FK_kasamu_master_evaluator"))
    private List<KasamuEvaluator> kasamuEvaluatorList;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", foreignKey = @ForeignKey(name = "FK_kasamu_master_detail"))
    private List<KasamuDetail> kasamuDetailList;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", foreignKey = @ForeignKey(name = "FK_kasamu_master_non_gazetted"))
    private List<KasamuForNoGazetted> kasamuForNoGazettedList;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", foreignKey = @ForeignKey(name = "FK_kasamu_master_committee_indicator"))
    private List<CommitteeIndicator> committeeIndicatorList;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", foreignKey = @ForeignKey(name = "FK_kasamu_master_committee"))
    private List<Committee> committeeList;

    @OneToMany(cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
    @JoinColumn(name = "kasamu_master_id", foreignKey = @ForeignKey(name = "FK_kasamu_master_transfer_office"))
    private List<TransferOffice> transferOffices;


}
