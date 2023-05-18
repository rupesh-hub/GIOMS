package com.gerp.attendance.model.leave;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "leave_setup",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name_en","name_np","office_code"}, name = "unique_leavesetup")})
public class LeaveSetup extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_setup_seq_gen")
    @SequenceGenerator(name = "leave_setup_seq_gen", sequenceName = "seq_leave_setup", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
    private String nameNp;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.ALPHA_PATTERN)
    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    private String nameEn;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20) default '00'")
    private String officeCode;

    private Boolean maximumAllowedAccumulation;

    private Boolean unlimitedAllowedAccumulation;

    private Boolean totalAllowedDays;

    private Boolean totalAllowedRepetition;

    private Boolean leaveApprovalDays;

    private Boolean maximumLeaveLimitAtOnce;

    private Boolean gracePeriod;

    private Boolean allowedDaysFy;

    private Boolean totalAllowedRepetitionFy;

    private Boolean documentationSubmissionDay;

    private Boolean minimumYearOfServices;

    private Boolean allowedMonthly;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(30)")
    private String shortNameNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
//    @Pattern(regexp = StringConstants.ALPHA_PATTERN)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(30)")
    private String shortNameEn;

    private int orderValue;

}
