package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DonorAgentPojo   {

    private Integer id;
    private String donorAgentUcd;
    private String donorAgentNameE;
    private String donorAgentNameN;
    private DonorSubGroupPojo donorSubGroup;
}
