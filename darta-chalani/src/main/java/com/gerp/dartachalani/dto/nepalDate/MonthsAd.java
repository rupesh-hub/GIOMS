package com.gerp.dartachalani.dto.nepalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthsAd {
    @JsonProperty("month_str")
    public String monthStr;
    @JsonProperty("month_num")
    public int monthNum;
}
