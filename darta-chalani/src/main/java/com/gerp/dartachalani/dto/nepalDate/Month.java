package com.gerp.dartachalani.dto.nepalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Month {

    @JsonProperty("month_num")
    public int monthNum;
    @JsonProperty("month_str")
    public String monthStr;
    @JsonProperty("months_ad")
    public List<MonthsAd> monthsAd;
}
