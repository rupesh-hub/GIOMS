package com.gerp.attendance.model.attendances;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.enums.AttendanceRemarks;
import com.gerp.shared.enums.AttendanceStatus;
import com.gerp.shared.enums.Day;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "employee_attendance")
public class EmployeeAttendance extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_attendance_seq_gen")
    @SequenceGenerator(name = "employee_attendance_seq_gen", sequenceName = "seq_employee_attendance", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
//    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "date_en")
    private LocalDate dateEn;

    private String dateNp;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkin", columnDefinition = "TIME")
    private LocalTime checkIn;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "checkout", columnDefinition = "TIME")
    private LocalTime checkOut;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "shift_checkin", columnDefinition = "TIME")
    private LocalTime shiftCheckIn;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "half_time", columnDefinition = "TIME")
    private LocalTime halfTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Column(name = "shift_checkout", columnDefinition = "TIME")
    private LocalTime shiftCheckOut;

    private Integer shiftId;

    @EnumValue
    private Day day;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status")
    private AttendanceStatus attendanceStatus;

    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    @Builder.Default
    private Boolean isDevice = false;

    @Builder.Default
    private Boolean isHoliday =false;

    @Column(columnDefinition = "TEXT")
    private String lateRemarks;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_remarks")
    private AttendanceRemarks attendanceRemarks;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "manual_attendance_id")
//    private ManualAttendance manualAttendance;

//    @ManyToOne
//    @JoinColumn(name = "attendance_device_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_attendance_type__employee_attendace"))
//    private AttendanceStatus attendanceStatus;
}
