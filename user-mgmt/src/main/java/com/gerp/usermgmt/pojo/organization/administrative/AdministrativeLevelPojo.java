package com.gerp.usermgmt.pojo.organization.administrative;

import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AdministrativeLevelPojo {
    private Integer id;
    private String code;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
}
