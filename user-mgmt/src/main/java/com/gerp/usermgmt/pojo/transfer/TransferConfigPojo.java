package com.gerp.usermgmt.pojo.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferConfigPojo {
    private Integer id;
    private String officeCode;
    private Boolean isSaruwa;
    private String ministerCode;
    private DetailPojo office;
    private DetailPojo ministry;
}
