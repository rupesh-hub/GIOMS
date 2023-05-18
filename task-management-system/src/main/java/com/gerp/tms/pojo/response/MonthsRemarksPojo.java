package com.gerp.tms.pojo.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthsRemarksPojo {
    private int id;
    private String remarks;
    private String status;
    private Double budget;
    private MonthsResponsePojo month;
}
