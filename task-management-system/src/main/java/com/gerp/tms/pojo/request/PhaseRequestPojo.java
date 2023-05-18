package com.gerp.tms.pojo.request;

import com.gerp.shared.utils.FieldErrorConstant;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseRequestPojo {

    private Long id ;

    @NotNull
    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.ALPHA_NUMERIC)
    private String phaseName;

    @Size(max = StringConstants.DEFAULT_MAX_SIZE_30)
    @Pattern(regexp = StringConstants.NEPALI_PATTERN, message = FieldErrorConstant.PATTERN)
    private String phaseNameNp;
}
