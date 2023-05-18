package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.kaaj.report.KaajReportData;
import com.gerp.attendance.Pojo.report.ReportPojo;
import com.gerp.attendance.model.kaaj.KaajRequest;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface KaajRequestService extends GenericService<KaajRequest, Long> {

    KaajRequest save(KaajRequestPojo kaajRequestPojo);

    Page<KaajHistoryPojo> getKaajHistoryByPisCode(final GetRowsRequest rowsRequest);

    KaajRequest saveKaajBulk(KaajRequestPojo kaajRequestPojo);

    KaajRequest update(KaajRequestPojo kaajRequestPojo);

    List<KaajRequestCustomPojo> getAllKaajRequest();

    List<KaajRequestCustomPojo> getKaajRequestByPisCode();

    KaajRequestCustomPojo getKaajRequestById(Long id);

//    KaajRequest saveKaaj(ManualAttendancePojo manualAttendancePojo);

    void deleteKajRequest(Long id);

    void updateStatus(ApprovalPojo data) throws ParseException;

    List<KaajRequestCustomPojo> getKaajByApproverPisCode();

    ArrayList<KaajRequestMinimalPojo>getKaajByMonthAndYear(String pisCode, String month, String year);

    ArrayList<KaajRequestMinimalPojo>getKaajByDateRange(String pisCode, LocalDate from, LocalDate to);

    Page<KaajResponsePojo> filterData(GetRowsRequest paginatedRequest);

    AttendanceMonthlyReportPojoPagination filterKaajSummary(GetRowsRequest paginatedRequest) throws ParseException;

    void filterExcelReport(ReportPojo reportPojo, HttpServletResponse response);

    List<EmployeeOnKaajPojo> getEmployeeOnKaaj();

    KaajReportData getPaperReportData(Long kaajRequestId);
}
