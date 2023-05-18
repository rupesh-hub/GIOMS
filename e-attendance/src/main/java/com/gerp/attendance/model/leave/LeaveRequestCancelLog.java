package com.gerp.attendance.model.leave;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

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
@Table(name = "leave_request_cancel_log")
public class LeaveRequestCancelLog extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_cancel_log_seq_gen")
    @SequenceGenerator(name = "leave_request_cancel_log_seq_gen", sequenceName = "seq_leave_request_cancel_log", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(precision=6, scale=1)
    private Double actualDays;

    private Integer travelDays;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate toDateEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_request_detail_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_LeaveRequestCancelLog_LeaveRequestDetail"))
    private LeaveRequestDetail leaveRequestDetail;

}