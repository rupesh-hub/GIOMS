//package com.gerp.attendance.model.postAttendance;
//
//import com.gerp.shared.enums.Status;
//import com.gerp.shared.generic.api.AuditActiveAbstract;
//import com.gerp.shared.utils.StringConstants;
//import lombok.*;
//import org.hibernate.annotations.DynamicUpdate;
//
//import javax.persistence.*;
//import javax.validation.constraints.Size;
//
//@Entity
//@DynamicUpdate
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Table(name = "post_attendance_request_approval")
//public class PostAttendanceRequestApproval extends AuditActiveAbstract {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_attendance_request_approval_seq_gen")
//    @SequenceGenerator(name = "post_attendance_request_approval_swq_gen", sequenceName = "seq_post_attendance_request_approval", initialValue = 1, allocationSize = 1)
//    private Long id;
//
//    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    @Builder.Default
//    private Status status = Status.P;
//
//    @Column(name = "approver_pis_code", columnDefinition = "VARCHAR(20)")
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    private String approverPisCode;
//
//    @Column(name = "remarks", columnDefinition = "VARCHAR(255)")
//    @Size(max = StringConstants.DEFAULT_MAX_LIMIT)
//    private String remarks;
//
//    @ManyToOne
//    @JoinColumn(name = "post_attendance_request_detail_id")
//    private PostAttendanceRequestDetail postAttendanceRequestDetail;
//
//}
