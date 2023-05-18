package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestResultPojo implements Serializable {

    @JsonProperty("MachineNumber")
    private Long machineNumber;
    @JsonProperty("IndRegID")
    private Long indRegID;
    @JsonProperty("DateTimeRecord")
    private String dateTimeRecord;
    @JsonProperty("DeviceIP")
    private String deviceIP;
    @JsonProperty("Mode")
    private String Mode;

    @JsonProperty("DateOnlyRecord")
    private String dateOnlyRecord;

    @JsonProperty("TimeOnlyRecord")
    private String timeOnlyRecord;

}
