package com.gerp.attendance.model.setup;

import com.gerp.shared.enums.Gender;
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
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicUpdate
@Table(name = "public_holiday",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"office_code", "name_en"}, name = "unique_office_name"),
        @UniqueConstraint(columnNames = {"office_code", "name_np"}, name = "unique_office_namen"),
        @UniqueConstraint(columnNames = {"office_code", "short_name_np"}, name = "unique_office_shortnamenp"),
        @UniqueConstraint(columnNames = {"office_code", "short_name_en"}, name = "unique_office_shortnameen")
})
public class PublicHolidaySetup extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "public_holiday_setup_seq_gen")
    @SequenceGenerator(name = "public_holiday_setup_seq_gen", sequenceName = "seq_public_holiday_setup", initialValue = 1, allocationSize = 1)
    private Integer id;

//    @NotNull(message = FieldErrorConstant.NOT_NULL)
//    @NotBlank(message = FieldErrorConstant.NOT_BLANK)
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
//    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
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
    @Pattern(regexp = StringConstants.ALPHA_PATTERN)
    @Column(name = "name_en", columnDefinition = "VARCHAR(50)")
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(name = "holiday_for")
    private Gender holidayFor;


//    @NotNull
//    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "short_name_np", columnDefinition = "VARCHAR(6)")
    private String shortNameNp;

//    @NotNull
//    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_6)
//    @Pattern(regexp = StringConstants.ALPHA_PATTERN)
    @Column(name = "short_name_en", columnDefinition = "VARCHAR(6)")
    private String shortNameEn;

    public PublicHolidaySetup(Integer id) {
        this.id = id;
    }
}
