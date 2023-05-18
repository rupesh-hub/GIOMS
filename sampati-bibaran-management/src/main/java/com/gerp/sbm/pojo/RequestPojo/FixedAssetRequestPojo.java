package com.gerp.sbm.pojo.RequestPojo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class FixedAssetRequestPojo {

    @NotNull
    private String ownerName;

    @NotNull
    private String districtCode;


    @NotNull
    private String fixedAssetType;


//    @NotNull
//    private String localLevelType;


    @NotNull
    private String localLevel;

    @NotNull
    private Integer wardNo;


    @NotNull
    private Integer kittaNo;

    @NotNull
   private Double costPrice;

    @NotNull
    private Double area;

    @NotNull
    private String source;

    private String remarks;

    @NotNull
    private String piscode;

}
