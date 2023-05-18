package com.gerp.usermgmt.pojo.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.enums.PositionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class SearchPojo {

    private String pisCode;
    private String name;
    private Long organisationTypeId;
    private String designationType;
    private PositionType positionType;
    private boolean giomsActive = false;

    private String officeCode;

    private String districtCode;

    private String provinceCode;

    private String order;

    private String municipalityCode;

    private Long sectionId;

    private String serviceCode;

    private String designationCode;

    private String positionCode;

    private String organizationLevelCode;

    private String specialDesignation;

    private String moduleName;
}
