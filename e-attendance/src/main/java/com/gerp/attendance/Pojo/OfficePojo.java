package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficePojo {

    private String code;
    private Boolean isActiveOffice;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
    private String phoneNumber;
    private String email;
    private String addressEn;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String addressNp;
    private String parentCode;
    private List<OfficePojo> childOffice;
    private OfficePojo parentOffice;

    private IdNamePojo district;
    private  IdNamePojo Province;
    private  IdNamePojo District;
    private  IdNamePojo organizationLevel;
}