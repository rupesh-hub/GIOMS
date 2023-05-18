package com.gerp.tms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.response.ApprovalReportDetailPojo;
import com.gerp.tms.pojo.response.MonitoringReportsPojo;
import com.gerp.tms.pojo.response.ReportProjectTaskDetailsPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Mapper
public interface ReportMapper {
    ReportProjectTaskDetailsPojo getProjectTaskDetail(@Param("projectName") String projectName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("pisCode") String pisCode, @Param("taskId") Long taskId);

    List<MonitoringReportsPojo> getMonitoring(String officeCode);

    Page<ApprovalReportDetailPojo> getToBeApprovalTask(Page<ApprovalReportDetailPojo> pagination, @Param("status") String status, @Param("pisCode") String pisCode);

    MonitoringReportsPojo getApprovalDetails(@Param("id") Long id);
}
