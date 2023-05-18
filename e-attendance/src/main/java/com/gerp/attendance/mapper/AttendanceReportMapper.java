package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.AttendanceAnnualReportPojo;
import com.gerp.attendance.Pojo.AttendanceMonthlyReportPojo;
import com.gerp.attendance.Pojo.EmployeeDetailPojo;
import com.gerp.attendance.Pojo.report.AttendanceReportPojo;
import com.gerp.attendance.Pojo.report.AttendanceStatusPojo;
import com.gerp.attendance.Pojo.report.EmployeeAttendanceMonthlyReportPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface AttendanceReportMapper {

    @Select("select * from _attendance_report_funcs(#{officeCode}, #{fromDate}, #{toDate})")
    List<AttendanceMonthlyReportPojo> getMonthlyReport(@Param("officeCode") String officeCode, @Param("fromDate") Date fromDate,
                                                       @Param("toDate") Date toDate);

    @Select("select * from attendance_annual_report(#{pisCode}, #{filterDate}, #{filterMonth})")
    List<AttendanceAnnualReportPojo> getAnnualReport(@Param("pisCode") String pisCode, @Param("filterDate") Integer filterDate, @Param("filterMonth") Integer filterMonth);


    List<AttendanceStatusPojo> getAttendanceReport(
            @Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,
            @Param("isJoin") Boolean isJoin,
            @Param("isLeft") Boolean isLeft,
            @Param("year") String year,
            @Param("month") Integer month);

    @Select("SELECT case WHEN e.middle_name_en IS NOT NULL THEN concat(e.first_name_en,' ', e.middle_name_en, ' ', e.last_name_en)\n" +
            "    ELSE concat(e.first_name_en,' ', e.last_name_en) end AS employeeNameEn,\n" +
            "    case WHEN e.middle_name_np IS NOT NULL THEN concat(e.first_name_np,' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "         ELSE concat(e.first_name_np,' ', e.last_name_np) end AS employeeNameNp,\n" +
            "    (SELECT fd.name_en FROM functional_designation fd WHERE  fd.code = e.designation_code) designationEn,\n" +
            "    (SELECT fd.name_np FROM functional_designation fd WHERE  fd.code = e.designation_code) designationNp\n" +
            "    FROM employee e WHERE  e.pis_code = #{pisCode}")
    EmployeeDetailPojo getEmployeeDetail(@Param("pisCode") String pisCode);


    Page<AttendanceReportPojo> monthlyAttendanceReport(
            Page<AttendanceReportPojo> page,
            @Param("officeCode") String officeCode,
            @Param("userStatus") Boolean userStatus,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("searchField") Map<String, Object> searchField);
}
