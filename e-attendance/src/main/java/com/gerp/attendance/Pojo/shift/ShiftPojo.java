package com.gerp.attendance.Pojo.shift;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShiftPojo {
    private Integer id;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_20)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    private String nameEn;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    private String nameNp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fromDateEn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate toDateEn;

    private String fromDateNp;
    private String toDateNp;

    private List<ShiftDayPojo> days;

    @NotNull
    private Long fiscalYear;

//    @NotNull
    private Boolean isDefault = true;

    private Boolean isActive;

    private MappingCount employeeGroup;

}
