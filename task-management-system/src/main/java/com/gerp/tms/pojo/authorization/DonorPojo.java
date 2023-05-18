package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DonorPojo  {
    private Integer id;
    private String donorNameN;
    private String donorNameE;
    private String donorCode;
    private DonorAgentPojo donorAgent;
}
