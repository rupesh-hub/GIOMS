package com.gerp.attendance.model.setup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
@Builder
@DynamicUpdate
@Table(name = "periodic_holiday")
public class PeriodicHoliday extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodic_holiday_seq_gen")
    @SequenceGenerator(name = "periodic_holiday_seq_gen", sequenceName = "seq_periodic_holiday", initialValue = 1, allocationSize = 1)
    private Long id;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "from_date_en")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "to_date_en")
    private LocalDate toDateEn;

    @NotNull(message = FieldErrorConstant.NOT_NULL)
    @NotBlank(message = FieldErrorConstant.NOT_BLANK)
    @Column(name = "from_date_np", columnDefinition = "VARCHAR(20)")
    private String fromDateNp;

    @NotNull(message = FieldErrorConstant.NOT_NULL)
    @NotBlank(message = FieldErrorConstant.NOT_BLANK)
    @Column(name = "to_date_np", columnDefinition = "VARCHAR(20)")
    private String toDateNp;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    @Column(name = "fiscal_year_code", columnDefinition = "VARCHAR(6)")
    private String fiscalYearCode;

    @NotNull(message = FieldErrorConstant.NOT_NULL)
    @ManyToOne
    @JoinColumn(name = "public_holiday_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_PeriodicHoliday_PublicHoliday"))
    private PublicHolidaySetup publicHoliday;

    private Boolean isSpecificHoliday=false;

    private Boolean isCurrentYear;


    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "year_en")
    private LocalDate yearEn;


    @Column(name = "year_np", columnDefinition = "VARCHAR(20)")
    private String yearNp;

}
