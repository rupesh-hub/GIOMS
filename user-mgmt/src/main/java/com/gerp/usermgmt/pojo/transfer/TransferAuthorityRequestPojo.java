package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TransferAuthorityRequestPojo{
    private Set<String>  transferType;
    private Integer id;
    private String serviceCode;
    private Set<String> groupCode;
    private Set<String> offices;
    private Set<String> positions;
}
