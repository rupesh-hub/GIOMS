package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@DynamicUpdate
@Getter
@Setter
@Table(name = "transfer_history")
public class TransferHistory extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "transfer_history_seq", sequenceName = "transfer_history_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_history_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Column(name = "transfer_type",columnDefinition = "VARCHAR(10)", nullable = false)
    private String transferType;

    @Column(name = "from_office_code",columnDefinition = "VARCHAR(10)")
    private String fromOfficeCode;
    @Column(name = "from_section_code")
    private Long fromSectionCode;
    @Column(name = "from_service_code",columnDefinition = "VARCHAR(10)")
    private String fromServiceCode;
    @Column(name = "from_position_code",columnDefinition = "VARCHAR(10)")
    private String fromPositionCode;
    @Column(name = "from_designation_code",columnDefinition = "VARCHAR(10)")
    private String fromDesignationCode;

    @Column(name = "to_office_code",columnDefinition = "VARCHAR(10)")
    private String toOfficeCode;
    @Column(name = "to_service_code",columnDefinition = "VARCHAR(10)")
    private String toServiceCode;
    @Column(name = "to_position_code",columnDefinition = "VARCHAR(10)")
    private String toPositionCode;
    @Column(name = "to_designation_code",columnDefinition = "VARCHAR(10)")
    private String toDesignationCode;
    @Column(name = "to_section_code")
    private Long toSectionCode;
    @Column(name = "expected_departure_date_np",columnDefinition = "VARCHAR(10)")
    private String expectedDepartureDateNp;
    @Column(name = "expected_departure_date_en")
    private LocalDate expectedDepartureDateEn;

//    @Column(name = "office_joining_date_np",columnDefinition = "VARCHAR(10)")
//    private String officeJoiningDateNp;
//    @Column(name = "office_joining_date_en")
//    private LocalDate officeJoiningDateEn;

    @Column(columnDefinition = "VARCHAR(10)")
    private String approved;

    @Column(name = "approved_date_np",columnDefinition = "VARCHAR(10)")
    private String approvedDateNp;
    @Column(name = "approved_date_en")
    private LocalDate approvedDateEn;


    @Column(name = "approver_code",columnDefinition = "VARCHAR(10)")
    private String approverCode;

    @Column(name = "pis_code",columnDefinition = "VARCHAR(10)")
    private String pisCode;

    @Column(name = "is_rawana_generated")
    private Boolean isRawanaGenerated;

    @OneToMany(fetch = FetchType.LAZY , cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_history_id", foreignKey = @ForeignKey(name = "fk_transfer_history_remarks"))
    private List<TransferRemarks> transfer_remarks;

    @OneToMany(fetch = FetchType.LAZY,targetEntity = TransferDocuments.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "transfer_history_id",referencedColumnName = "id",foreignKey =@ForeignKey(name= "FK_transfer_history_related_documents"))
    private List<TransferDocuments> relatedDocumentsList;

}

