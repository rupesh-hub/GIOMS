package com.gerp.sbm.pojo.ResponsePojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class CashAndGoldResponsePojo {


    private Long id;

    private Integer quantity;

    private LocalDate receivedDate;

    private Double costPrice;

    private String source;

    private String remarks;

    private String createdBy;
}
