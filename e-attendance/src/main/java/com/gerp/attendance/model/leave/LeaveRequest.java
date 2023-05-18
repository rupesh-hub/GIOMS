package com.gerp.attendance.model.leave;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gerp.attendance.model.approval.DecisionApproval;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "leave_request")
public class LeaveRequest extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_seq_gen")
    @SequenceGenerator(name = "leave_request_seq_gen", sequenceName = "seq_leave_request", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    private Integer fiscalYear;

    @Builder.Default
    private Boolean appliedForOthers = Boolean.FALSE;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "emp_pis_code", columnDefinition = "VARCHAR(30)")
    private String empPisCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "year", columnDefinition = "VARCHAR(10)")
    private String year;

    @NotNull
    private Boolean isHoliday;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_id", foreignKey = @ForeignKey(name = "FK_LeaveRequest_LeaveRequestDetail"))
    @JsonIgnore
    private Collection<LeaveRequestDetail> leaveRequestDetails;

    @Column(name = "leave_request_signature")
    @Type(type = "org.hibernate.type.TextType")
    private String leaveRequesterSignature;

    @Column(name = "leave_request_hash_content")
    @Type(type = "org.hibernate.type.TextType")
    private String leaveRequesterHashContent;

    @Column(name = "content")
    @Type(type = "org.hibernate.type.TextType")
    private String content;

}

