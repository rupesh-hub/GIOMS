package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class OtherDetailRequestPojo {

    @NotNull
    private String detail;


    @NotNull
    private Double costPrice;

    @NotNull
     private String source;

    @NotNull
    private LocalDate buyDate;

    private String remarks;
    @NotNull
    private String piscode;

}
