package com.gerp.sbm.pojo;

import lombok.Data;

@Data
public class FiscalYearData {

    private Long id;

    private String year;

    private String yearNp;

    private String startDate;

    private String endDate;

    private Boolean isActive;
}
