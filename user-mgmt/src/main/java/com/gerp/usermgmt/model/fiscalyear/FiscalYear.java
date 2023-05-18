package com.gerp.usermgmt.model.fiscalyear;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fiscal_year")
@EqualsAndHashCode(callSuper = true)
public class FiscalYear extends AuditActiveAbstract {

    @Id
    private String code;

    @NotNull
    @NotBlank
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_8)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    private String year;

    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    @Column(name = "year_np", unique = true, length = StringConstants.DEFAULT_MAX_SIZE_20)
    private String yearNp;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate endDate;

    @Override
    public Serializable getId() {
        return code;
    }
}
