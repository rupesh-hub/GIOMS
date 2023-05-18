package com.gerp.tms.pojo.authorization;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PayingOfficePojo {
    private Integer id;
    private String poUcd;
    private String poDescE;
    private String poDescN;
    private String poAddressE;
    private String poAddressN;
    private String ministryCd;
    private String pisMinistryCd;
    private String pisOfficeCode;
}
