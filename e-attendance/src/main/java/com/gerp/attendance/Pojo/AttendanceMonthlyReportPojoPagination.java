package com.gerp.attendance.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AttendanceMonthlyReportPojoPagination {
    int current;
    int pages;
    int size;
    int total;
    List<AttendanceMonthlyReportPojo> records;
    List<KaajSummaryPojo> kaajRecords;
}
