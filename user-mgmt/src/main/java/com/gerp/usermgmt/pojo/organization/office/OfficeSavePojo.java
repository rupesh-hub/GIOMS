package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class OfficeSavePojo {

    private String code;
    private String definedCode;
    private Integer officeTypeId;
    private Boolean isActiveOffice;
    private Boolean isActive;
    private String isGiomsActive;
    private Long organisationTypeId;
    private String nameNp;
    private String nameEn;
    private String districtCode;
    private String municipalityVdcCode;
    private String phoneNumber;
    private String email;
    private String url;

    private String parentCode;
    private Long organizationLevelId;

    private String remarksEn;

    private String remarksNp;

    private String officePrefixEn;

    private String officePrefixNp;

    private String officeSuffixEn;

    private String officeSuffixNp;

    private String addressEn;

    private String addressNp;
}
