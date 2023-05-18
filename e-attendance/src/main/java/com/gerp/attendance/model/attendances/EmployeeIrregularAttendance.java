package com.gerp.attendance.model.attendances;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "employee_irregular_attendance")
public class EmployeeIrregularAttendance extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_irregular_attendance_seq_gen")
    @SequenceGenerator(name = "employee_irregular_attendance_seq_gen", sequenceName = "seq_employee_irregular_attendance", initialValue = 1, allocationSize = 1)
    private Long id;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "pis_code", columnDefinition = "VARCHAR(30)")
    private String pisCode;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    private Integer shiftId;

    private int irregularDaysCount;

    private String month;

    private LocalDate latestUpdateDate;
}
