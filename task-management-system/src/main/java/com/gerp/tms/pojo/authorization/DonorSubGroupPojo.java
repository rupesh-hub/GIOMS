package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DonorSubGroupPojo  {

    private Integer id;
    private String donorSubGroupUcd;
    private String donorSubGroupNameE;
    private String donorSubGroupNameN;
    private DonorGroupPojo donorGroup;

}
