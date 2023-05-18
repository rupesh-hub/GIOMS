package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.AgricultureDetailRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class AgricultureDetailResponsePojo {

    private Long id;

    private AgricultureDetailRequestPojo agricultureDetailRequestPojo;
//    private String agricultureDetail;
//
//    private Integer quantity;
//
//    private Double costPrice;
//
//    private String source;
//
//    private LocalDate buyDate;
//
//    private String remarks;
}
