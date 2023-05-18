package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.FixedAssetRequestPojo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class FixedAssetResponsePojo {

    private Long id;

    private FixedAssetRequestPojo fixedAssetRequestPojo;

//    @NotNull
//    private String ownerName;
//
//    @NotNull
//    private String districtCode;
//
//
//    @NotNull
//    private String fixedAssetType;
//
//
//    @NotNull
//    private String localLevelType;
//
//
//    @NotNull
//   private String localLevel;
//
//    @NotNull
//    private Integer wardNo;
//
//
//    @NotNull
//    private Integer kittaNo;
//
//    @NotNull
//   private Double costPrice;
//
//    @NotNull
//    private Double area;
//
//    @NotNull
//    private String source;
//
//    private String remarks;
}
