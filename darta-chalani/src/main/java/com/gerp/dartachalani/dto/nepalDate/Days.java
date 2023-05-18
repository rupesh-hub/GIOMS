package com.gerp.dartachalani.dto.nepalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Days {
    private String name;
    @JsonProperty("day_bs")
    private Integer dayBs;

    @JsonProperty("day_ad")
    private Integer dayAd;
    private String month;
    private String year;
    private Boolean today;

    @JsonProperty("name_bs")
    private String nameBs;
    @JsonProperty("name_ad")
    private String nameAd;
    @JsonProperty("day_name_en")
    private String dayNameEn;
    @JsonProperty("day_name_np")
    private String dayNameNp;

    @JsonProperty("month_bs")
    private Integer monthBs;

    @JsonProperty("month_ad")
    private Integer monthAd;

    @JsonProperty("year_bs")
    private Integer yearBs;

    @JsonProperty("year_ad")
    private Integer yearAd;
}
