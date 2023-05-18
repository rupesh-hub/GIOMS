package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyLogPojo;
import com.gerp.attendance.model.dailyLog.DailyLog;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.ApprovalPojo;

import java.util.List;

public interface DailyLogService {
    DailyLog save(DailyLogPojo dailyLogPojo);

    void update(DailyLogPojo dailyLogPojo);

    void softDelete(Long id);

    List<DailyLogPojo> getAllDailyLogs();

    DailyLogPojo getLogById(Long id);

    void updateStatus(ApprovalPojo data);

    List<DailyLogPojo> getDailyLogByPisCode();

    List<DailyLogPojo> getDailyLogByApproverPisCode();

    List<DailyLogPojo> getDailyLogByOfficeCode();

    Page<DailyLogPojo> getDailyLogDetail(GetRowsRequest paginatedRequest);

    Page<DailyLogPojo> filterData(GetRowsRequest paginatedRequest);
}
