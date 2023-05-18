package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.BankDetailsRequestPojo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class BankDetailsResponsePojo {

    private Long id;

    private BankDetailsRequestPojo bankDetailsRequestPojo;

//    private String accountName;
//
//    private String compNameAddr;
//
//    private String accountNum;
//
//    private BigDecimal amount;
//
//    private String source;
//
//    private String remarks;
}
