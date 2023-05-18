package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.LoanRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class LoanResponsePojo {

    private Long id;

    private LoanRequestPojo loanRequestPojo;


//    private String name;
//
//    private String loanStatus;
//
//
//    private Double amount;
//
//    private LocalDate date;
//
//    private String remarks;
}
