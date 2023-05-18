package com.gerp.dartachalani.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DashboardCountPojo {
    private int countManualDarta;
    private int countAutoDarta;
    private int totalDarta;
    private int totalChalani;
    private int totalTippadi;
    private int approvedChalani;
    private int approvedTippadi;
    private int approvedDarta;
    private int pending;
    private int rejected;
    private int tipani_create;
    private int inProgress;
    private int inbox;
    private int total;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<DashboardBarChartPojo> dartaCounts;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private  List<DashboardBarChartPojo> chalaniCounts;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private  List<DashboardBarChartPojo> tippaniCounts;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private DashboardCountPojo darta;
    private DashboardCountPojo tippadi;
    private DashboardCountPojo chalani;
    private String period;
    private DashboardCountPojo test;
}
