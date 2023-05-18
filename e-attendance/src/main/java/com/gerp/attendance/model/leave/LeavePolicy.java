package com.gerp.attendance.model.leave;

import com.gerp.shared.enums.Gender;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@RequiredArgsConstructor
@DynamicUpdate
@Table(name = "leave_policy", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"leave_setup_id","office_code"}, name = "unique_leavepolicy"),
})
public class LeavePolicy extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_policy_seq_gen")
    @SequenceGenerator(name = "leave_policy_seq_gen", sequenceName = "seq_leave_policy", initialValue = 1, allocationSize = 1)
    private Long id;

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

    @ManyToOne
    @JoinColumn(name = "leave_setup_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_LeavePolicy_LeaveSetup"))
    private LeaveSetup leaveSetup;

//    @NotNull
//    private Integer totalAllowedAccumulation;
//
//    private Integer totalAllowedAccumulationFy;

    private Integer maxAllowedAccumulation;

    private Integer totalAllowedDays;

    private Integer totalAllowedDaysFy;

    private Integer totalAllowedRepetition;

    private Integer totalAllowedRepetitionFy;

    private Integer gracePeriod;

    private Integer documentSubmissionDays;

    private Integer daysToApprove;

    private Integer maximumLeaveLimitAtOnce;

    private Integer minimumYearOfServices;

    private Integer allowedLeaveMonthly;

    private Boolean permissionForApproval;

    private Boolean countAccumulatedLeave=true;
//    @NotNull
//    @NotBlank
//    @Size(max = StringConstants.DEFAULT_MAX_SIZE_2)
//    @Column(name = "gender", columnDefinition = "VARCHAR(2)")
    @Enumerated(EnumType.STRING)
    private Gender gender;


    private Boolean contractLeave;

//    @NotNull
//    private Boolean countWeekend;

    private Boolean countPublicHoliday;

//    @NotNull
//    private Boolean allowLumpSum;
//
//    @NotNull
//    private Boolean allowDeduction;

    private Boolean allowHalfLeave;

    private Boolean paidLeave;

    private Boolean allowSubstitution;

    private Boolean carryForward;

//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    @JoinColumn(name = "leave_policy_id", foreignKey = @ForeignKey(name = "FK_LeavePolicy_RemainingLeave"))
//    @JsonIgnore
//    private Collection<RemainingLeave> remainingLeaves;

//    @NotNull
//    private Boolean needRecommendation;
//
//    @NotNull
//    private Boolean needApproval;


}
