//package com.gerp.attendance.model.leave;
//
//import com.gerp.shared.enums.Status;
//import com.gerp.shared.generic.api.AuditActiveAbstract;
//import com.gerp.shared.utils.StringConstants;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.DynamicUpdate;
//import org.hibernate.annotations.Type;
//
//import javax.persistence.*;
//import javax.validation.constraints.Size;
//
///**
// * @author Sanjeena Basukala
// * @version 1.0.0
// * @since 1.0.0
// */
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@DynamicUpdate
//@Table(name = "leave_request_approval")
//public class LeaveRequestApproval extends AuditActiveAbstract {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_approval_seq_gen")
//    @SequenceGenerator(name = "leave_request_approval_seq_gen", sequenceName = "seq_leave_request_approval", initialValue = 1, allocationSize = 1)
//    private Long id;
//
//    @Enumerated(EnumType.STRING)
//    @Builder.Default
//    private Status status = Status.P;
//
//    @Column(name = "approver_pis_code", columnDefinition = "VARCHAR(20)")
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    private String approverPisCode;
//
//    @Lob
//    @Type(type = "org.hibernate.type.TextType")
//    private String remarks;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "leave_request_detail_id")
//    private LeaveRequestDetail leaveRequestDetail;
//
//}
