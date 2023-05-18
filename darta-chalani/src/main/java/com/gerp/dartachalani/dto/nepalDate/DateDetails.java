package com.gerp.dartachalani.dto.nepalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class DateDetails {

    @JsonProperty("start_weekday")
    public int startWeekday;
    public Month month;
    public String year;

    @JsonProperty("day_names")
    public List<String> dayNames;
    public List<List<Days>> days;
}
