package com.gerp.attendance.model.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.FieldErrorConstant;
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
import java.util.Collection;

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
@Table(name = "shift",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"office_code", "name_en"}, name = "unique_shift_name"),
                @UniqueConstraint(columnNames = {"office_code", "name_np"}, name = "unique_shift_namen")
        })
public class Shift extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shift_seq_gen")
    @SequenceGenerator(name = "shift_seq_gen", sequenceName = "seq_shift", initialValue = 1, allocationSize = 1)
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "office_code", columnDefinition = "VARCHAR(20)")
    private String officeCode;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "name_np", columnDefinition = "VARCHAR(50)")
    private String nameNp;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    private String nameEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    @NotNull
    @Column(name = "fiscal_year")
    private Long fiscalYear;

    @Column(name = "is_default")
    private boolean isDefault;

//    @NotNull
//    @JsonFormat(pattern = "HH:mm:ss")
//    @Column(name = "half_time", columnDefinition = "TIME")
//    private LocalTime halfTime;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", foreignKey = @ForeignKey(name = "FK_ShiftDayConfig_Shift"))
    private Collection<ShiftDayConfig> shiftDayConfigs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", foreignKey = @ForeignKey(name = "FK_ShiftEmployeeGroupConfig_Shift"))
    private Collection<ShiftEmployeeGroupConfig> shiftEmployeeGroupConfigs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", foreignKey = @ForeignKey(name = "FK_EmployeeShiftConfig_Shift"))
    private Collection<ShiftEmployeeConfig> shiftEmployeeConfigs;

}
