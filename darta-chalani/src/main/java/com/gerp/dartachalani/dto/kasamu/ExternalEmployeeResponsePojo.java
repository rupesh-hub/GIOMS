package com.gerp.dartachalani.dto.kasamu;

import com.gerp.dartachalani.dto.DesignationPojo;
import com.gerp.dartachalani.dto.PositionPojo;
import lombok.Data;

@Data
public class ExternalEmployeeResponsePojo {
    private String pisCode;
    private String name;
    private DesignationPojo designation;
    private PositionPojo position;
    private ServicePojo service;
    private ServicePojo group;
    private ServicePojo subGroup;
    private String serviceName;
    private String serviceCode;
    private String groupCode;
    private String subGroupCode;
    private String currentOfficeName;
    private String designationCode;
    private String positionCode;
}
