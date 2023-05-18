package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.VehicleRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Getter
@Setter
public class VehicleResponsePojo {

    private Long id;

    private VehicleRequestPojo vehicleRequestPojo;

//    @NotNull
//   private String ownerName;
//
//    @NotNull
//    private String vehicleType;
//
//    @NotNull
//    private String vehicleNum;
//
//    @NotNull
//    private Double costPrice;
//
//    @NotNull
//   private String source;
//
//    private LocalDate boughtDate;
//
//    private String remarks;
}
