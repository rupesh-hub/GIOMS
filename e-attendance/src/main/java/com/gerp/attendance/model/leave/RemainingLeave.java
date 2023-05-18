package com.gerp.attendance.model.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Table(name = "remaining_leave")
public class RemainingLeave  extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remaining_leave_seq_gen")
    @SequenceGenerator(name = "remaining_leave_seq_gen", sequenceName = "seq_remaining_leave", initialValue = 1, allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @Column(precision=6, scale=1)
    private Double leaveTaken;

    @Column(precision=6, scale=1)
    private Double accumulatedLeave;

    @Column(precision=6, scale=1)
    private Double homeLeaveAdditional;

    @Column(precision=6, scale=1)
    private Double leaveTakenFy;

    private Integer repetition;

    @Column(precision=6, scale=1)
    private Double accumulatedLeaveFy;

    private Integer fiscalYear;

    private String officeCode;

    @Column(precision=6, scale=1)
    private Double remainingLeave;

    private Integer travelDays;

    private Double preAccumulatedLeave;
    private Integer preAdditionalDay;
    private Double preExtraAccumulatedLeave;
    private Double adjustHomeLeave;

    @Column(precision=6, scale=1)
    private Double monthlyLeaveTaken;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_10)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "year", columnDefinition = "VARCHAR(10)")
    private String year;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "leave_policy_id")
//    private LeavePolicy leavePolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_policy_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_RemainingLeave_LeavePolicy"))
    private LeavePolicy leavePolicy;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate uptoDate;
//adjust date for homeLeave
    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate adjustUpdateDate;

}
