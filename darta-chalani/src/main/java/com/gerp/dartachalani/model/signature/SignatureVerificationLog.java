package com.gerp.dartachalani.model.signature;

import com.gerp.shared.enums.Status;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "signature_verification_log")
public class SignatureVerificationLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "memo_id")
    private Long memoId;

    @Column(name = "memo_content_id")
    private Long memoContentId;

    @Column(name = "dispatch_id")
    private Long dispatchId;

    @Column(name = "dispatch_review_id")
    private Long dispatchReviewId;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "signature_type")
    private SignatureType signatureType;

    @Column(name = "signature_by")
    private String signatureBy;

    @Column(name = "individual_status")
    private String individualStatus;

}
