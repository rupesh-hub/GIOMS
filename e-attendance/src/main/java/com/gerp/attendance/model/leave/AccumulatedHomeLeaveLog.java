package com.gerp.attendance.model.leave;

import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
@Table(name = "accumulated_home_leave_log")
public class AccumulatedHomeLeaveLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accumulated_home_leave_log_seq_gen")
    @SequenceGenerator(name = "accumulated_home_leave_log_seq_gen", sequenceName = "seq_accumulated_home_leave_log", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(precision=6, scale=1)
    private Double accumulatedLeave;

    @Column(precision=6, scale=1)
    private Double accumulatedLeaveFy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remaining_leave_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_AccumulatedHomeLeaveLog_RemainingLeave"))
    private RemainingLeave remainingLeave;

}
