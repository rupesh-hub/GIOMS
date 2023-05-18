package com.gerp.usermgmt.pojo.organization.office;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.usermgmt.enums.OfficeSetupStatusEnum;
import com.gerp.usermgmt.enums.OfficeSetupStepEnum;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfficeSetupPojo {

    private Long id;

    private OfficeSetupStepEnum officeSetupStep;

    private OfficeSetupStatusEnum officeSetupStatus;

    private Long orderNo;

    private String officeCode;

}
