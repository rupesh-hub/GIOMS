package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class DashboardBarChartPojo {
    private String name;
    private int count;
    private LocalDate currentDate;
    public DashboardBarChartPojo(String name, int count) {
        this.name = name;
        this.count = count;
    }
}
