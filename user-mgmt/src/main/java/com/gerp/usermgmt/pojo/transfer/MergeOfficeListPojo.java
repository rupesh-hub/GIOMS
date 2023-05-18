package com.gerp.usermgmt.pojo.transfer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergeOfficeListPojo {
    private Integer id;
    private List<DetailPojo> detailPojoSet1;
    private List<DetailPojo> detailPojoSet2;
}
