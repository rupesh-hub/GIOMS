package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HeaderOfficeDetailsPojo {
    private String officeNameEn;
    private String officeNameNp;
    private String ministryEn;
    private String ministryNp;
    private List<AccountPojo> accountPojoList;

}
