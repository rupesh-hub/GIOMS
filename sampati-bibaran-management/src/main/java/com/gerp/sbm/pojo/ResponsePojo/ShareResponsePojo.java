package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.ShareRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


@Getter
@Setter
public class ShareResponsePojo {


    private Long id;

    private ShareRequestPojo shareRequestPojo;

//    private String shareHolderName;
//
//    private String compNameAdr;
//
//
//     private String shareType;
//
//    private BigDecimal shareAmount;
//
//    private Integer shareQuantity;
//
//    private String source;
//
//    private String remarks;
}
