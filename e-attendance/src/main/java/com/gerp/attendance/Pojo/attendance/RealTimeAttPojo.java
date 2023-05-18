package com.gerp.attendance.Pojo.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RealTimeAttPojo {

    @JsonProperty("UserPin")
    private Long deviceId;
    @JsonProperty("VerifyType")
    private Long verifyType;
    @JsonProperty("VerifyTime")
    private String checkTime;
    @JsonProperty("DeviceSn")
    private String deviceSn;
    @JsonProperty("BranchCode")
    private String officeCode;
    @JsonProperty("Temperature")
    private Float temperature;

    private String signature;
}
