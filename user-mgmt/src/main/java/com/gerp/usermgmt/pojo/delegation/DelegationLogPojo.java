package com.gerp.usermgmt.pojo.delegation;

import javax.persistence.Column;
import java.time.LocalDateTime;

public class DelegationLogPojo {

    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;
    private Integer fromSectionId;
    private String fromPisCode;
    private Integer toSectionId;
    private String toPisCode;

    private Boolean isReassignment;

    private Boolean isOfficeHead;

    private String fromDesignationId;

    private String toDesignationId;

    private String fromPositionCode;

    private String toPositionCode;
}
