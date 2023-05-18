package com.gerp.dartachalani.dto.kasamu;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.ServiceType;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServicePojo {
    private String code;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
    private String parentCode;
    private ServiceType serviceType;

    private ServicePojo parent;

}