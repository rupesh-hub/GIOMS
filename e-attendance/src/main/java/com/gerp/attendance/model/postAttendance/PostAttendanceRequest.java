package com.gerp.attendance.model.postAttendance;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "post_attendance_request")
public class PostAttendanceRequest extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_attendance_request_seq_gen")
    @SequenceGenerator(name = "post_attendance_request_seq_gen", sequenceName = "seq_post_attendance_request", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String officeCode;

    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    private String pisCode;

//    @Column(name = "approval_employee_id")
//    private String approvalEmployeeId;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    private String fiscalYearCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attendance_request_id", foreignKey = @ForeignKey(name = "fk_post_attendance_request_detail"))
    private Collection<PostAttendanceRequestDetail> postAttendanceRequestDetails;

}
