package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import com.gerp.attendance.model.leave.RemainingLeave;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface RemainingLeaveService extends GenericService<RemainingLeave, Long> {

    RemainingLeave save(RemainingLeavePojo remainingLeavePojo);
    RemainingLeave saveRemainingLeave(RemainingLeavePojo remainingLeavePojo);
    RemainingLeave updateRemainingLeave(RemainingLeavePojo remainingLeavePojo);
    Long updateRemainingLeaveNew(RemainingLeavePojo remainingLeavePojo);
    RemainingLeave update(RemainingLeaveResponsePoio remainingLeaveResponsePoio);
     ArrayList<RemainingLeaveMinimalPojo>getAllRemainingLeave();
    RemainingLeaveResponsePoio getRemainingLeaveById(Long id,String year);
    List<RemainingLeaveMinimalPojo> getByOfficeCode(String name, String year);
    List<RemainingLeaveByOfficeCodePojo> remainingLeaveByOfficeCode(String name, String year);
    Page<EmployeeAttendanceMonthlyReportPojo> filterRemainingLeave(GetRowsRequest paginatedRequest);
    ArrayList<RemainingLeaveMinimalPojo> getLeaveOfPisCode(String pisCode,String year);
    List<RemainingLeaveRequestPojo> getRemainingLeave(String pisCode,String year);
    RemainingLeaveForLeaveRequestPojo getByPisCode(Long leavePolicyId,String pisCode,String type,String year,String fromDate,String toDate);
    String readTransformedExcel(RemainingLeaveDocRequestPojo remainingRequestPojo) throws IOException;
    List<EmployeeLeaveTakenPojo> employeeLeaveTaken(String name);
    void remainingLeave();
    void updateRemainingLeaveDaily();
    void updateRemainingLeaveDailyByPisCode(String pisCode);
    void deleteRemainingLeave(Long id);
    String updateKarar(String pisCode, LocalDate fromDate, LocalDate toDate);
    Integer unInformLeaveCount();
    HomeLeavePojo getHomeLeave(String pisCode);

}
