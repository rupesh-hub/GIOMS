package com.gerp.attendance.Pojo.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * @author bibek
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportPojo {

    private Long fiscalYear;

    private String year;

    private String pisCode;

    private String officeCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private String checkIn;

    private String checkOut;

    private Status approvalStatus;

    private Map<String, Object> searchField;

    private String sectionCode;

    private String sectionPisCode;

    private Boolean forLeave= false;

    private Boolean forKaaj= false;
}
