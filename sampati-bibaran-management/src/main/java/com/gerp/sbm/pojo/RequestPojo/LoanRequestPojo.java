package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class LoanRequestPojo  {


    @NotNull
    private String name;

    @NotNull
    private String loanStatus;


    @NotNull
    private Double amount;

    @NotNull
    private LocalDate date;

    private String remarks;

    @NotNull
    private String piscode;

}
