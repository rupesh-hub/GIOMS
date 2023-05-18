package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrganizationLevelPojo {
    private String code;
    private Integer id;

    private String nameNp;

    private String nameEn;

    private Long orderNo;

    private Long organisationTypeId;
}
