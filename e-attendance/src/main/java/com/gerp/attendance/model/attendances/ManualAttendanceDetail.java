package com.gerp.attendance.model.attendances;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "manual_attendance_detail")
public class ManualAttendanceDetail extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "manual_attendance_detail_seq_gen")
    @SequenceGenerator(name = "manual_attendance_detail_seq_gen", sequenceName = "seq_manual_attendance_detail", initialValue = 1, allocationSize = 1)
    private Long id;


    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;


    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkin_time", columnDefinition = "TIME")
    private LocalTime checkInTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkout_time", columnDefinition = "TIME")
    private LocalTime checkOutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_attendance_id")
    private ManualAttendance manualAttendance;

    private String remarks;

    @Column(name = "group_order")
    private Integer groupOrder;

    @Column(name = "from_date_en")
    private LocalDate fromDateEn;
    private String fromDateNp;

    @Column(name = "to_date_en")
    private LocalDate toDateEn;
    private String toDateNp;

}
