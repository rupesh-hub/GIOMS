package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.AttendanceAnnualReportMainPojo;
import com.gerp.attendance.Pojo.AttendanceMonthlyReportPojoPagination;
import com.gerp.attendance.Pojo.report.AttendanceReportPojo;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.Date;
import java.util.List;

public interface AttendanceReportService {
    AttendanceMonthlyReportPojoPagination getAttendanceReportMonthly(Date fromDate, Date toDate, Integer page, Integer limit);
    AttendanceAnnualReportMainPojo getAttendanceReportAnnual(Integer filterDate, String pisCode) throws Exception;

   AttendanceReportPojo getAttendanceReportGeneric(final String pisCode, final String year);

    Page<AttendanceReportPojo> getAttendanceReportMonthlyGeneric(GetRowsRequest paginatedRequest);
}
