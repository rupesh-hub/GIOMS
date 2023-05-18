package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class TransferAuthorityResponsePojo {
     private Integer id;
    private DetailPojo service;
    private List<DetailPojo> group;
    private List<DetailPojo> type;
    private List<DetailPojo> positions;
    private List<DetailPojo> offices;
    private DetailPojo district;
}
