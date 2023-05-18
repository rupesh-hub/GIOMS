package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Getter
@Setter
public class ShareRequestPojo  {

    @NotNull
    private String shareHolderName;

    @NotNull
    private String compNameAdr;

    @NotNull
     private String shareType;

    @NotNull
    private BigDecimal shareAmount;

    @NotNull
    private Integer shareQuantity;

    @NotNull
    private String source;

    private String remarks;

    @NotNull
    private String piscode;

}
