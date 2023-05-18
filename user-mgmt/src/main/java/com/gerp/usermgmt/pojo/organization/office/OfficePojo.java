package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import com.gerp.shared.utils.StringConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class OfficePojo {
    private String code;
    private String definedCode;
    private Boolean isActiveOffice;
    private Boolean isGiomsActive;
    private Boolean isActive;
    @Pattern(regexp = StringConstants.NEPALI_PATTERN)
    private String nameNp;
    private String nameEn;
    private String phoneNumber;
    private String email;
    private String url;
    private String addressEn;
    private String addressNp;
    private String parentCode;
    private List<OfficePojo> childOffice;
    private OfficePojo parentOffice;
    private List<SectionPojo> section;

    private IdNamePojo district;
    private IdNamePojo officeType;
    private IdNamePojo municipalityVdc;
    private  IdNamePojo Province;
    private  IdNamePojo District;
    private  IdNamePojo organizationLevel;
    private String type;
    private Long organisationTypeId;
    private Integer order;


    private String remarksEn;

    private String remarksNp;

    private String officePrefixEn;

    private String officePrefixNp;

    private String officeSuffixEn;

    private String officeSuffixNp;

    private boolean isTopLevelOffice;
}
