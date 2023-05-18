package com.gerp.sbm.pojo.RequestPojo;

import com.sun.istack.NotNull;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ValuableItemsRequestPojo {

    @NotNull
    private Integer quantity;

    @NotNull
    private LocalDate receivedDate;

    @NotNull
    private Double costPrice;

    @NotNull
    private String source;

    private String remarks;

    @NotNull
    private String piscode;


    @NotNull
    private String details;

}
