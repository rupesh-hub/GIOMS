package com.gerp.attendance.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.shift.ShiftDetailPojo;
import com.gerp.attendance.Pojo.shift.ShiftPojo;
import com.gerp.attendance.model.shift.Shift;
import com.gerp.shared.generic.api.GenericService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface ShiftService extends GenericService<Shift, Integer> {

    Shift create(ShiftPojo data);

    Shift update(ShiftPojo data);

    ShiftPojo findById(Long id);

    List<ShiftPojo> getAllCustomEntity(Long fiscalYear);

    ShiftDetailPojo getShiftByEmployeeCode(String employeeCode);

    Page<ShiftPojo> filterData(GetRowsRequest paginatedRequest);

    List<ShiftPojo> getAllByOfficeCode();

    List<ShiftPojo> getShiftByMonthAndYear(String pisCode, String month, String year);

    List<ShiftPojo> getShiftByDateRange(String pisCode, LocalDate fromDate, LocalDate toDate);

    boolean changeStatus(Long id);

    ShiftPojo getApplicableShiftByEmployeeCodeAndDate(String pisCode, String officeCode, LocalDate now);

    List<Long> getEmployeeShift(String pisCode,String officeCode);

    Set<Long> getEmployeeShifts(String pisCode,String officeCode,LocalDate fromDate,LocalDate toDate,Boolean forDashboard);
}
