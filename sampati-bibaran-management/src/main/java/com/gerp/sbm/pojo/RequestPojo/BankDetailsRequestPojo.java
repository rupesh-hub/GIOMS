package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BankDetailsRequestPojo {

    @NotNull
    private String accountName;

    @NotNull
    private String compNameAddr;

    @NotNull
    private String accountNum;

    @NotNull
    private Double amount;

    @NotNull
    private String source;

    private String remarks;

    @NotNull
    private String piscode;

}
