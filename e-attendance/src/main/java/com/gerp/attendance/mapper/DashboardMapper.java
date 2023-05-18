package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.AppliedLeavePojo;
import com.gerp.attendance.Pojo.DatesPojo;
import com.gerp.attendance.Pojo.DetailPojo;
import com.gerp.attendance.Pojo.HolidayResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface DashboardMapper {

    AppliedLeavePojo dashboardData(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode);

    List<AppliedLeavePojo> getLeaveData(@Param("officeCode") String officeCode, @Param("pisCode") String pisCode);


    @Select("  select count(*) as employee from employee e                 \n" +
            "       left join users u on u.pis_employee_code = e.pis_code \n" +
            "       left join functional_designation fd on e.designation_code = fd.code\n" +
            "       where e.office_code=  #{officeCode} and u.is_active=true and fd.designation_type='NORMAL_DESIGNATION' ")
    Long getTotalEmployee(@Param("officeCode") String officeCode);

    @Select("select count(*) as employee from employee e \n" +
            "       left join users u on u.pis_employee_code = e.pis_code\n" +
            "       left join functional_designation fd on e.designation_code = fd.code\n" +
            "       where e.office_code= #{officeCode} and u.is_active=true and fd.designation_type='NORMAL_DESIGNATION' ")
    List<String> getListOfEmployee(@Param("officeCode") String officeCode);


    Long getPresentEmployee(@Param("officeCode") String officeCode,
                            @Param("attendanceStatus") List<String> attendanceStatus,
                            @Param("date") LocalDate date);

    Long getAbsentEmployee(@Param("officeCode") String officeCode,
                           @Param("attendanceStatus") List<String> attendanceStatus,
                           @Param("date") LocalDate date);


    Long getEmployeeOnLeave(@Param("officeCode") String officeCode,
                            @Param("attendanceStatus") List<String> attendanceStatus,
                            @Param("date") LocalDate date);

    Long getNotActiveEmployee(@Param("officeCode") String officeCode,
                              @Param("date") LocalDate date);

    @Select("select count(*)\n" +
            "            from kaaj_request  kr\n" +
            "            left join decision_approval da on da.kaaj_request_id=kr.id\n" +
            "            where (#{date} BETWEEN kr.from_date_en and kr.to_date_en)\n" +
            "            and kr.office_code= #{officeCode} and da.status='A' and kr.is_active=true")
    Long getKaajEmployee(@Param("officeCode") String officeCode, @Param("date") LocalDate date);

    @Select("select count(distinct pis_code) from employee_attendance ea where ea.attendance_status=#{code} and ea.date_en=#{date} and ea.office_code=#{officeCode}")
    Long getEmployeeOnKaaj(@Param("code") String code, @Param("date") LocalDate date, @Param("officeCode") String officeCode);

    @Select("select count (*) from decision_approval da where da.code= #{code} and da.status='A' and da.approver_pis_code= #{approvalPisCode} and da.is_active=true")
    Long getApprovals(@Param("code") String code, @Param("approvalPisCode") String approvalPisCode);

    HolidayResponsePojo getUpcomingHoliday(@Param("officeCode") List<String> officeCode, @Param("fiscalYear") String fiscalYear, @Param("date") LocalDate date);

    List<DetailPojo> getLeaveDateWise(@Param("pisCode") String pisCode,
                                      @Param("officeCode") String officeCode,
                                      @Param("year") String year,
                                      @Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate,
                                      @Param("holidayCount") Long holidayCount,
                                      @Param("parentOfficeCodeWithSelf") List<String> parentOfficeCodeWithSelf
    );

    List<Long> getLeavePolicy(@Param("pisCode") String pisCode,
                              @Param("parentOfficeCodeWithSelf") List<String> parentOfficeCodeWithSelf);

    Long getCountKararEmployee(@Param("officeCode") String officeCode, @Param("days") int days);

    List<DatesPojo> getLeaveDuration(@Param("pisCode") String pisCode,
                                     @Param("officeCode") String officeCode,
                                     @Param("year") String year,
                                     @Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate
    );

    Double getTotalDays(@Param("leavePolicyId") Long leavePolicyId,
                        @Param("pisCode") String pisCode,
                        @Param("year") String year);

    Double getLeaveAllowedDays(@Param("leavePolicyId") Long leavePolicyId,
                               @Param("pisCode") String pisCode);

    List<DetailPojo> getByRemainingLeave(@Param("pisCode") String pisCode,
                                         @Param("officeCode") String officeCode,
                                         @Param("fiscalYear") Long fiscalYear,
                                         @Param("parentOfficeCodeWithSelf") List<String> parentOfficeCodeWithSelf
    );
}
