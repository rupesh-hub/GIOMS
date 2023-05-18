package com.gerp.usermgmt.pojo.organization.employee;

import com.gerp.shared.utils.StringConstants;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class ReligionPojo {
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
    private String code;
    private Integer id;
}
