package com.gerp.usermgmt.pojo.MasterDashboardPojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class MasterDetailPojo {

    private String officeCode;

    private String officeNameEn;

    private String officeNameNp;

    private Long leaveCount;

    private Long kaajCount;

    private Long manualCount;

    private Long autoCount;

    private Long totalDarta;

    private Long chalaniCount;

    private Long tippaniCount;

    private String orderStatus;
}
