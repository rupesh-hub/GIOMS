package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class CashAndGoldRequestPojo {


    private Long id;

    @NotNull
    private Integer quantity;

    @NotNull
    private LocalDate receivedDate;

    @NotNull
    private Double costPrice;

    @NotNull
    private String source;

    private String remarks;

    private Long masterId;
}
