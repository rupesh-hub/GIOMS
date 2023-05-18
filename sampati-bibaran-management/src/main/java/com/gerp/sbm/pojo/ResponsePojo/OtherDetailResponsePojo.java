package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.OtherDetailRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class OtherDetailResponsePojo {

     private Long id;

     private OtherDetailRequestPojo otherDetailRequestPojo;

//    private String detail;
//
//
//    private Double costPrice;
//
//     private String source;
//
//    private LocalDate buyDate;
//
//    private String remarks;
}
