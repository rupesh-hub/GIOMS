package com.gerp.usermgmt.model.orgtransfer;

import com.gerp.shared.enums.OrgTransferType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.enums.TransferStatus;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@DynamicUpdate
@Data
@Table(name = "org_transfer_history")
public class OrgTransferHistory extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "transfer_history_seq", sequenceName = "transfer_history_seq", allocationSize = 1)
    @GeneratedValue(generator = "transfer_history_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transfer_status",columnDefinition = "VARCHAR(10)", nullable = false)
    private TransferStatus transferStatus;

    private Long orgTransferRequestId;

    @Column(name = "from_office_code",columnDefinition = "VARCHAR(10)")
    private String fromOfficeCode;

    @Column(name = "from_designation_code",columnDefinition = "VARCHAR(10)")
    private String fromDesignationCode;

    @Column(name = "target_office_code",columnDefinition = "VARCHAR(10)")
    private String targetOfficeCode;

    @Column(name = "to_designation_code",columnDefinition = "VARCHAR(10)")
    private String toDesignationCode;

    private LocalDate officeJoiningDateEn;

    @Column(name = "prev_office_join_date_en")
    private LocalDate prevOfficeJoinDateEn;

    @Column(name = "prev_office_join_date_np")
    private LocalDate prevOfficeJoinDateNp;

    @Column(name = "last_modified_date_np",columnDefinition = "VARCHAR(10)")
    private String lastModifiedDateNp;

    @Column(name = "last_modified_date_en",columnDefinition = "VARCHAR(10)")
    private String lastModifiedDateEN;


    @Column(name = "approved_date_np",columnDefinition = "VARCHAR(10)")
    private String approvedDateNp;

    @Column(name = "approved_date_en")
    private LocalDate approvedDateEn;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "requested_date_np")
    private String requestedDateNp;

    @Column(name = "requested_date_en", updatable = false)
    private LocalDate requestedDateEn;


    @Column(name = "approver_code",columnDefinition = "VARCHAR(10)")
    private String approverCode;

    @Column(name = "pis_code",columnDefinition = "VARCHAR(10)")
    private String pisCode;

    @Column(name = "from_section_id")
    private Long fromSectionId;

    @Enumerated(EnumType.STRING)
    private OrgTransferType transferType;

    @Column(name = "expected_join_date_en")
    private LocalDate expectedJoinDateEn;

    @Column(name = "expected_join_date_np")
    private String expectedJoinDateNp;

    private String remark;
    private Boolean acknowledged;
}
