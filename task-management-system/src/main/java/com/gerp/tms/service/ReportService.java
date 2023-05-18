package com.gerp.tms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.ProgressUpdatePojo;
import com.gerp.tms.pojo.request.DecisionByOfficeHeadPojo;
import com.gerp.tms.pojo.response.ApprovalReportDetailPojo;
import com.gerp.tms.pojo.response.MonitoringReportsPojo;
import com.gerp.tms.pojo.response.ReportProjectTaskDetailsPojo;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    ReportProjectTaskDetailsPojo getProjectReport(String projectName, LocalDate startDate, LocalDate endDate, int limit, int page, Long taskId);

    Long addProgressMonthly(ProgressUpdatePojo progressUpdatePojo);

    List<MonitoringReportsPojo> getMonitoring(String officeCode);

    Page<ApprovalReportDetailPojo> getApprovalReport(String status, int limit, int page);

    MonitoringReportsPojo getApprovalReportDetails(Long id);

    Long submitTaskProgressMonthly(Long taskId);

    int decisionByOfficeHead(List<DecisionByOfficeHeadPojo> decisionByOfficeHeadPojos);

    void deleteTaskProgressMonthly(int progressId, String month);

}
