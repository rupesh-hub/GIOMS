package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.attendance.AttendanceSearchPojo;
import com.gerp.attendance.Pojo.report.*;
import com.gerp.attendance.Pojo.shift.AttendanceShiftPojo;
import com.gerp.attendance.Pojo.shift.ShiftResponsePojo;
import com.gerp.attendance.model.attendances.EmployeeAttendance;
import com.gerp.shared.enums.AttendanceStatus;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface EmployeeAttendanceMapper {

    @Select("SELECT id,checkin,date_en,date_np,checkout, pis_code,attendance_status FROM employee_attendance")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "checkIn", column = "checkin"),
            @Result(property = "checkOut", column = "checkout"),
            @Result(property = "pisCode", column = "pis_code"),
            @Result(property = "dateEn", column = "date_en"),
            @Result(property = "dateNp", column = "date_np"),
            @Result(property = "attendanceStatus", column = "attendance_status")
    })
    ArrayList<EmployeeAttendancePojo> getAllEmployeeAttendance();

    @Select("select * from kaaj_request_lower_paginated(#{fromDate},#{toDate},#{offset},#{limit},#{lowerOfficeList})")
    List<MasterDashboardPojo> getMasterDashboardPaginated(@Param("fromDate") String fromDate,
                                                 @Param("toDate") String toDate,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit,
                                                          @Param("lowerOfficeList") String lowerOfficeList);


    @Select("select device_id from piscode_device_id_mapper where pis_code=#{pisCode} limit 1")
    Long getMappedDeviceId(@Param("pisCode") String  pisCode);

    @Select("select dnf.old_office_code from device_num_officecode dnf where dnf.new_office_code=#{officeCode}")
    List<String> getMappedDeviceOffice(@Param("officeCode") String  officeCode);


    @Select("select * from kaaj_request_dates(#{fromDate},#{toDate})")
    List<MasterDashboardPojo> getMasterDashboard(@Param("fromDate") String fromDate,
                                                 @Param("toDate") String toDate);

    @Select("select * from kaaj_request_lower(#{fromDate},#{toDate},#{lowerOfficeList})")
    List<MasterDashboardPojo> getMasterCount(@Param("fromDate") String fromDate,
                                                 @Param("toDate") String toDate,
                                             @Param("lowerOfficeList") String lowerOfficeList);

    @Select("select * from kaaj_request_lower_test_leave(#{fromDate},#{toDate},#{limit},#{offset},#{lowerOfficeList}, #{type})")
    List<MasterDashboardPojo> getMasterCountLeave(@Param("fromDate") String fromDate,
                                             @Param("toDate") String toDate,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit,
                                             @Param("lowerOfficeList") String lowerOfficeList,
                                                  @Param("type") String type
                                                  );

    @Select("select * from kaaj_request_lower_test_kaaj(#{fromDate},#{toDate},#{limit},#{offset},#{lowerOfficeList}, #{type})")
    List<MasterDashboardPojo> getMasterCountKaaj(@Param("fromDate") String fromDate,
                                                  @Param("toDate") String toDate,
                                                  @Param("offset") Integer offset,
                                                  @Param("limit") Integer limit,
                                                  @Param("lowerOfficeList") String lowerOfficeList,
                                                 @Param("type") String type);

    @Select("select * from kaaj_request_lower_test_leave_excel(#{fromDate},#{toDate},#{lowerOfficeList}, #{type})")
    List<MasterDashboardPojo> getMasterCountLeaveExcel(@Param("fromDate") String fromDate,
                                                  @Param("toDate") String toDate,
                                                  @Param("lowerOfficeList") String lowerOfficeList,
                                                       @Param("type") String type);

    @Select("select * from kaaj_request_lower_test_kaaj_excel(#{fromDate},#{toDate},#{lowerOfficeList}, #{type})")
    List<MasterDashboardPojo> getMasterCountKaajExcel(@Param("fromDate") String fromDate,
                                                 @Param("toDate") String toDate,
                                                 @Param("lowerOfficeList") String lowerOfficeList,
                                                      @Param("type") String type);

    @Select("select * from kaaj_leave(#{fromDate},#{toDate},#{officeCode})")
    MasterDashboardPojo getLeaveKaajCount(@Param("fromDate") String fromDate,
                                                 @Param("toDate") String toDate,
                                          @Param("officeCode") String officeCode);


    DateListingPojo getStartEndDate(@Param("year") Integer year);

    @Select("select * from employee_attendance_schedular()")
    Boolean updateAttendanceSchedular();

    EmployeeAttendanceResponsePojo getEmpAttByPisCode(@Param("pisCode") String pisCode,
                                                      @Param("officeCode") String officeCode,
                                                      @Param("attendanceStatus") List<String> attendanceStatus);

    Page<LateEmployeePojo> getAllLateAttendance(Page<LateEmployeePojo> page,
                                                     @Param("officeCode") String officeCode,
                                                @Param("userStatus") Boolean userStatus,
                                                @Param("searchField") Map<String, Object> searchField);

    List<LateEmployeePojo> getAllLateAttendanceCheckInByMonth(
            @Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,
            @Param("day") LocalDate day,
            @Param("month") String month,
            @Param("configDay") int configDay,
            @Param("maximumLateCheckin") LocalTime maximumLateCheckin);


     List<String> getEmployeeAbsentDataInMonth(@Param("pisCode") String pisCode, @Param("month") String month);

    List<LateEmployeePojo> getAllLateAttendanceCheckOutByMonth(
            @Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,
            @Param("day") LocalDate today,
            @Param("month") String month,
            @Param("configDay") int configDay,
            @Param("maximumEarlyCheckout") LocalTime maximumEarlyCheckout);

    @Select("select distinct ea.date_en  from employee_attendance ea where ea.is_holiday= false and ea.date_en " +
            "< current_date  order by date_en desc limit 1;")
    LocalDate getActiveDay();

    List<LateEmployeePojo> getAllLateAttendanceCheckInByMonthOfOffice(@Param("officeCode") String officeCode, @Param("date") LocalDate date, @Param("month") int monthId, @Param("allowedLimit")int allowedLimit);

    List<LateEmployeePojo> getAllLateAttendanceCheckOutByMonthOfOffice(@Param("officeCode") String officeCode, @Param("date") LocalDate date, @Param("month") int monthId, @Param("allowedLimit")int allowedLimit);

    void updateEmployeeCancel(@Param("pisCode") String pisCode,@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    void updateEmployeeCancelFuture(@Param("pisCode") String pisCode,@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    List<LateEmployeePojo> getAllExcelLateAttendance(@Param("officeCode") String officeCode,
                                                      @Param("searchField") Map<String, Object> searchField);

    @Select("SELECT id,checkin,date_en,date_np,checkout, pis_code,attendance_status FROM employee_attendance WHERE date_en = #{date}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "checkIn", column = "checkin"),
            @Result(property = "checkOut", column = "checkout"),
            @Result(property = "pisCode", column = "pis_code"),
            @Result(property = "dateEn", column = "date_en"),
            @Result(property = "dateNp", column = "date_np"),
            @Result(property = "attendanceStatus", column = "attendance_status")
    })
    ArrayList<EmployeeAttendancePojo> getEmpAttByDate(LocalDate date);

    @Select("select ea.checkin from employee_attendance ea where ea.date_en =#{checkTime} and ea.pis_code =#{pisCode}")
    LocalTime getcheckin(@Param("checkTime") LocalDate  checkTime, @Param("pisCode") String  pisCode);

    @Select("select ea.checkout from employee_attendance ea where ea.date_en =#{checkTime} and ea.pis_code =#{pisCode}")
    LocalTime getcheckout(@Param("checkTime") LocalDate  checkTime, @Param("pisCode") String  pisCode);

    @Select("select * from employee_attendance ea where ea.date_en =#{checkTime} and ea.pis_code =#{pisCode}")
    EmployeeAttendance getByPisCode(@Param("checkTime") LocalDate  checkTime, @Param("pisCode") String  pisCode);

    ShiftResponsePojo getShiftDetail(@Param("day") Long day, @Param("shiftId") List<Long> shiftId,@Param("checkin") LocalTime  checkin);


    @Select("<script>" +
            "SELECT id,checkin as checkIn,date_en,date_np,checkout as checkOut, pis_code,attendance_status FROM employee_attendance" +
            "<where>" +
            "<if test=\"map != null\"> " +
            "<if test=\"map.date_en != null and map.date_en != ''\"> " +
            "<bind name=\"date_en_p\" value=\"'%' + map.date_en + '%'\" /> " +
            "and TO_CHAR(date_en, 'YYYY-MM-DD') like #{date_en_p} " +
            "</if> " +
            "</if> " +
            "</where>" +
            "</script>")
    Page<EmployeeAttendancePojo> findAllByDate(Page page, @Param("map") Map<String, Object> map);

//    @Select("select * from employee_attendance ea where ea.pis_code=#{pisCode} and ea.date_en= #{dateEn} and ea.shift_checkin= #{shiftCheckin} and ea.shift_checkout= #{shiftCheckout}")
//    @Select("select *\n" +
//            "from employee_attendance\n" +
//            "where pis_code = #{pisCode}\n" +
//            "  and date_en = #{dateEn}\n" +
//            "order by shift_checkout")
//    List<EmployeeAttendance> getEmployeeData(@Param("pisCode") String pisCode, @Param("dateEn") LocalDate dateEn);

    EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> filterData(EmployeeAttendancePage<EmployeeAttendanceReportDataPojo> page,
//                                                                        @Param("fiscalYear") String fiscalYear,
                                                                        @Param("pisCode") String pisCode,
                                                                        @Param("device") String device,
                                                                        @Param("manualAttendance") String manualAttendance,
                                                                        @Param("fromDate") LocalDate fromDate,
                                                                        @Param("toDate") LocalDate toDate,
                                                                        @Param("userStatus") Boolean userStatus,
                                                                        @Param("searchField") Map<String, Object> searchField);

    Page<EmployeeAttendanceReportDataPojo> filterDataEmployee(Page<EmployeeAttendanceReportDataPojo> page,
                                                                        @Param("pisCode") String pisCode,
                                                                        @Param("searchField") Map<String, Object> searchField);

    Page<EmployeeAttendanceReportDataPojo> filterEmployee(Page<EmployeeAttendanceReportDataPojo> page,
                                                              @Param("pisCode") String pisCode,
                                                              @Param("searchField") Map<String, Object> searchField);

    List<EmployeeAttendanceMonthlyReportPojo> filterExcelDataPaginatedMonthly(@Param("officeCode") String officeCode,
                                                                            @Param("searchField") Map<String, Object> searchField);

//    Page<EmployeeAttendancePojo>getfilterMyAttendance(Page<EmployeeAttendancePojo> page,
//                                                      @Param("officeCode") String officeCode,
//                                                      @Param("pisCode") String pisCode,
//                                                      @Param("deviceId") String deviceId,
//                                                      @Param("deviceOffice") List<String> deviceOffice,
//                                                      @Param("searchField") Map<String, Object> searchField );

    Page<EmployeeAttendancePojo> filterMyAttendance(Page<EmployeeAttendancePojo> page,
                                                      @Param("officeCode") String officeCode,
                                                      @Param("pisCode") String pisCode,
                                                      @Param("searchField") Map<String, Object> searchField );


    EmployeeAttendanceTotalSum getSumForFilter(
//                                               @Param("fiscalYear") String fiscalYear,
                                               @Param("pisCode") String pisCode,
                                               @Param("device") String device,
                                               @Param("manualAttendance") String manualAttendance,
                                               @Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate,
                                               @Param("searchField") Map<String, Object> searchField);



   EmployeeAttendanceSummaryDataPojo getSummaryData(@Param("fiscalYear") String fiscalYear,
                                                    @Param("pisCode") String pisCode,
                                                    @Param("shiftId") Set<Long> shiftId,
                                                    @Param("officeCode") List<String> officeCode,
                                                    @Param("fromDateEn") LocalDate fromDateEn,
                                                    @Param("toDateEn") LocalDate toDateEn,
                                                    @Param("searchField") Map<String, Object> searchField
                                                    );


    Long getSummaryLeave(@Param("fiscalYear") String fiscalYear,
                                                      @Param("year") String year,
                                                     @Param("pisCode") String pisCode,
                                                     @Param("officeCode") List<String> officeCode,
                                                     @Param("fromDateEn") LocalDate fromDateEn,
                                                     @Param("toDateEn") LocalDate toDateEn,
                                                     @Param("searchField") Map<String, Object> searchField
    );

//   @Select("select * from employee_attendance where pis_code = #{pisCode} and date_en = #{dateEn} limit 1 ")
//   EmployeeAttendance getByDateAndPisCode(@Param("dateEn") LocalDate dateEn, @Param("pisCode") String pisCode);


   List<EmployeeAttendanceReportDataPojo> filterData(@Param("pisCode") String pisCode,
                                                                        @Param("device") String device,
                                                                        @Param("manualAttendance") String manualAttendance,
                                                                        @Param("fromDate") LocalDate fromDate,
                                                                        @Param("toDate") LocalDate toDate,
                                                     @Param("searchField") Map<String, Object> searchField);

    EmployeeAttendanceTotalSum getSumForFilter(
            @Param("pisCode") String pisCode,
            @Param("device") String device,
            @Param("manualAttendance") String manualAttendance,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    Page<DailyInformationPojo> getDailyInformation(Page<DailyInformationPojo> page,
                                                   @Param("fiscalYear") String fiscalYear,
                                                   @Param("officeCode") String officeCode,
                                                   @Param("date") LocalDate date,
                                                   @Param("userStatus") Boolean userStatus,
                                                   @Param("searchField") Map<String, Object> searchField);

    Page<DailyInformationPojo> getAbsentData(Page<DailyInformationPojo> page,
                                                   @Param("fiscalYear") String fiscalYear,
                                                   @Param("officeCode") String officeCode,
                                                   @Param("dateEn") LocalDate date,
                                             @Param("userStatus") Boolean userStatus,
                                             @Param("searchField") Map<String, Object> searchField);

    Page<DailyInformationPojo> getPresentData(Page<DailyInformationPojo> page,
                                             @Param("fiscalYear") String fiscalYear,
                                             @Param("officeCode") String officeCode,
                                             @Param("dateEn") LocalDate date,
                                              @Param("userStatus") Boolean userStatus,
                                             @Param("searchField") Map<String, Object> searchField);


    Page<DailyInformationPojo> getDailyInformationForReport(Page<DailyInformationPojo> page,
                                                   @Param("fiscalYear") String fiscalYear,
                                                   @Param("officeCode") String officeCode,
                                                   @Param("date") LocalDate date,
                                                            @Param("userStatus") Boolean userStatus,
                                                   @Param("searchField") Map<String, Object> searchField);

   List<DailyInformationPojo> getFilterDailyInformation(@Param("fiscalYear") String fiscalYear,
                                                        @Param("officeCode") String officeCode,
                                                        @Param("date") LocalDate date,
                                                        @Param("searchField") Map<String, Object> searchField);


    Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedMonthlyCheck(Page<EmployeeAttendanceMonthlyReportPojo> page,
                                                                         @Param("officeCode") String officeCode,
                                                                         @Param("userStatus") Boolean userStatus,
                                                                         @Param("fromDate") LocalDate fromDate,
                                                                         @Param("toDate") LocalDate toDate,
                                                                         @Param("searchField") Map<String, Object> searchField);

    Page<EmployeeAttendanceMonthlyReportPojo> filterDataPaginatedMonthly(Page<EmployeeAttendanceMonthlyReportPojo> page,
                                                                         @Param("officeCode") String officeCode,
                                                                         @Param("userStatus") Boolean userStatus,
                                                                         @Param("fromDate") LocalDate fromDate,
                                                                         @Param("toDate") LocalDate toDate,
                                                                         @Param("searchField") Map<String, Object> searchField);

    List<EmployeeAttendanceMonthlyReportPojo> monthlyAttendanceData(@Param("officeCode") String officeCode,
                                                                         @Param("pisCode") String pisCode);

    List<MonthDataPojo> getMonthAttendanceData(
            @Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,
            @Param("isJoin") Boolean isJoin,
            @Param("isLeft") Boolean isLeft,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    DesignationDataPojo getDesignationData(
            @Param("pisCode") String pisCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    List<MonthDataPojo> getDetailMonthAttendanceData(
            @Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,
            @Param("isJoin") Boolean isJoin,
            @Param("isLeft") Boolean isLeft,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    List<MonthDataLeavePojo> getMonthLeaveData(@Param("pisCode") String pisCode,
                                               @Param("fromDate") LocalDate fromDate,
                                               @Param("toDate") LocalDate toDate);


    List<MonthlyDailyLog> getDailyLogData(@Param("pisCode") String pisCode,
                                          @Param("isJoin") Boolean isJoin,
                                          @Param("isLeft") Boolean isLeft,
                                             @Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate);

    List<MonthDataKaajPojo> getMonthKaajData(@Param("pisCode") String pisCode,
                                             @Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate);


//    List<AttendanceShiftPojo> getEmployeeShift(@Param("officeCode") String officeCode,
//                                               @Param("deviceId") String deviceId,
//                                         @Param("pisCode") String pisCode,
//                                               @Param("deviceOfficeCode") List<String> deviceOfficeCode,
//                                         @Param("currentDate") LocalDate currentDate);

    List<AttendanceShiftPojo> getEmployeeShift(@Param("officeCode") String officeCode,
                                               @Param("pisCode") String pisCode,
                                               @Param("currentDate") LocalDate currentDate);


    void updateLateStatus(@Param("empId") Long empId,
                          @Param("officeAllowedLimit") Integer officeAllowedLimit,
                          @Param("officeLateLimit") LocalTime officeLateLimit,
                          @Param("officeEarlyLimit") LocalTime officeEarlyLimit,
                          @Param("month") String month);


    void updatepublicHoliday(@Param("fromDate") LocalDate fromDate,
                              @Param("toDate") LocalDate toDate);

    void updateHoliday(@Param("fromDate") LocalDate fromDate,
                             @Param("toDate") LocalDate toDate);

    List<EmployeePojo> getAllEmployee(@Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate);

    String checkForKaaj(@Param("dateEn") LocalDate dateEn,
                        @Param("pisCode") String pisCode,
                        @Param("officeCode") String officeCode);

    void updateStatus(@Param("id") Long id,
                      @Param("attendanceStatus") String attendanceStatus);

    DatesPojo getStartAndEndDate(@Param("month")Integer month, @Param("year") Integer year);



    List<EmployeeAttendance> getEmployeeAttendance(@Param("fromDate") LocalDate fromDate,
                                             @Param("toDate") LocalDate toDate,
                                             @Param("attendanceStatus") AttendanceStatus attendanceStatus);

    @Select("select pis_code from piscode_device_id_mapper where device_id= #{deviceID} and office_code=#{officeCode}")
    String getPisCodeByDeviceIdAndOfficeCode(@Param("deviceID") Long deviceID, @Param("officeCode") String officeCode);

    @Select("select count(*) from employee where pis_code = #{pisCode}")
    int checkPisCode(String pisCode);

    @Select("select gender from employee where pis_code = #{pisCode} and office_code= #{officeCode}")
    String getEmployeeGender(@Param("pisCode") String pisCode,@Param("officeCode") String officeCode);

    ArrayList<EmployeeMinimalDetailsPojo> sortEmployee(@Param("pisCode") List<String> pisCode,
                                                       @Param("searchField") Map<String, Object> searchField);


    List<EmployeeAttendanceNewMonthlyPojo> getMonthlyAttendance(@Param("pisCode") String pisCode,
            @Param("officeCode") String officeCode,@Param("fiscalYearCode") String fiscalYearCode,@Param("fromDate") LocalDate fromDate,
                                                                     @Param("toDate") LocalDate toDate);

    @Select("select count(*) from employee where pis_code = #{pisCode} and office_code = #{officeCode}")
    int checkPisCodeByOffice(@Param("pisCode")String pisCode, @Param("officeCode") String officeCode);

    DatesPojo getMonthStartAndEndDate(@Param("month")Integer month, @Param("year") Integer year);

    DashboardPendingCount getTotalPendingCount(@Param("pisCode") String pisCode);

    @Select("select  min(eng_date) as minDate, max(eng_date) as maxDate\n" +
            "from date_list\n" +
            "where nepali_year=#{year}::INTEGER and nepali_month=#{monthId}\n")
     DateListPojo findMonths(@Param("monthId") int monthId,@Param("year") String year);


    DashboardCountPojo CountEmployeeLateArrivedByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate now, @Param("maximum_early_checkout") LocalTime maximumEarlyCheckout, @Param("maximum_late_checkin") LocalTime maximumLateCheckin,@Param("pisCode") String pisCode);



    List<EmployeeAttendancePojo> filterEmployeeAttendance(@Param("attendanceSearchPojo")AttendanceSearchPojo attendanceSearchPojo, @Param("officeCode")String officeCode);

    List<EmployeeAttendancePojo> filterEmployeeList(@Param("officeCode")String officeCode);

    DashboardCountPojo TotalLateCheckInTime(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("pisCode") String pisCode,
                                 @Param("maximum_late_checkin") LocalTime maximumLateCheckin,@Param("maximum_early_checkout") LocalTime maximumEarlyCheckout);

    Double getAbsentCount(@Param("startDate") LocalDate startDate,@Param("endDate") LocalDate endDate,@Param("pisCode") String pisCode);

    @Select("select * from kaaj_request_lower_paginated_total(#{fromDate}, #{toDate}, #{officeList})")
    MasterDashboardTotalPojo getMasterDashboardTotal(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("officeList") String officeList);


    @Select("select * from holiday_return_data(#{date})")
    Boolean holidayReturnData(@Param("date") String date);
}
