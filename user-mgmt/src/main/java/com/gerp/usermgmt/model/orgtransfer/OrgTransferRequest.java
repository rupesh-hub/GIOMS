package com.gerp.usermgmt.model.orgtransfer;

import com.gerp.shared.enums.OrgTransferType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.enums.TransferStatus;
import com.gerp.usermgmt.model.office.Office;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@DynamicUpdate
@Data
@Table(name = "org_transfer_request")
public class OrgTransferRequest extends AuditActiveAbstract {
    @Id
    @SequenceGenerator(name = "org_transfer_request_seq", sequenceName = "org_transfer_request_seq", allocationSize = 1)
    @GeneratedValue(generator = "org_transfer_request_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransferStatus transferStatus = TransferStatus.P;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_15)
    @Column(columnDefinition = "VARCHAR(15)",name = "employee_pis_code")
    private String employeePisCode;

    @Column(name = "from_section_id")
    private Long fromSectionId;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_15)
    private String transferRequestFrom;
//
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_15)
//    private String transferRequestTo;

    @OneToOne
    private Office targetOffice;

    @OneToOne
    private Office requestedOffice;

    @Column(name = "approved_date_en", updatable = false)
    private LocalDate approvedDateEn;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "approved_date_np")
    private String approvedDateNp;

    @Size(max =StringConstants.DEFAULT_MAX_SIZE_10)
    @Column(columnDefinition = "VARCHAR(10)",name = "requested_date_np")
    private String requestedDateNp;

    @Column(name = "requested_date_en", updatable = false)
    private LocalDate requestedDateEn;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Enumerated(EnumType.STRING)
    private OrgTransferType transferType;

    @Column(name = "expected_join_date_en")
    private LocalDate expectedJoinDateEn;

    @Column(name = "expected_join_date_np")
    private String expectedJoinDateNp;

    private Boolean acknowledged = Boolean.FALSE;

}
