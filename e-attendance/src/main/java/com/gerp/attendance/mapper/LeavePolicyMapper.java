package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.*;
import com.gerp.shared.enums.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mapper
public interface LeavePolicyMapper {

    @Select("select lp.id,\n" +
            "       lp.total_allowed_days_fy as allowed_days_fy,\n" +
            "       lp.max_allowed_accumulation,\n" +
            "       lp.total_allowed_repetition_fy,\n" +
            "       ls.id      as leave_setup_id,\n" +
            "       ls.name_en as leaveNameEn,\n" +
            "       ls.name_np as leaveNameNp\n" +
            "from leave_policy lp\n" +
            "         left join leave_setup ls on ls.id = lp.leave_setup_id where lp.is_active=true")
    ArrayList<LeavePolicyResponsePojo> getAllLeavePolicy();


    ArrayList<LeavePolicyResponsePojo> getAllLeavePolicyByOffice(@Param("officeCode") List<String> officeCode);

    ArrayList<LeavePolicyResponsePojo> getAllKararEmployee(@Param("officeCode") List<String> officeCode);

    Long checkForParentLeavePolicy(@Param("officeCode") List<String> officeCode, @Param("leaveSetupId") Long leaveSetupId);

    ArrayList<LeavePolicyResponsePojo> getAllLeavePolicyDetail(@Param("officeCode") List<String> officeCode, @Param("pisCode") String pisCode, @Param("year") String year);

    @Select("WITH recursive q AS (select code, parent_code, 0 as level\n" +
            "from office o\n" +
            "where o.code = #{officeCode}\n" +
            "UNION ALL\n" +
            "select z.code, z.parent_code, q.level + 1\n" +
            "from office z\n" +
            "         join q on z.code = q.parent_code)\n" +
            "select lp.id from q inner join\n" +
            "    leave_policy lp on lp.office_code=q.code\n" +
            "where lp.leave_setup_id=#{leaveSetupId}\n" +
            "and lp.is_active=true\n" +
            "order by level limit 1;")
    Long getByLeaveSetup(@Param("officeCode") String officeCode, @Param("leaveSetupId") Long leaveSetupId);

    @Select("select ls.name_en from leave_policy lp left join leave_setup ls on ls.id = lp.leave_setup_id where lp.id= #{id}")
    String findLeaveName(Long id);

    @Select("select fy.code from fiscal_year fy where #{date} between fy.start_date and fy.end_date and is_active is true")
    String getFiscalCode(@Param("date") LocalDate date);

    @Update("update remaining_leave set accumulated_leave = remaining_leave.accumulated_leave - #{remainder},\n" +
            "pre_accumulated_leave  = case when (remaining_leave.pre_accumulated_leave = null or remaining_leave.pre_accumulated_leave = 0)\n" +
            "then remaining_leave.accumulated_leave - #{remainder}\n" +
            "else remaining_leave.pre_accumulated_leave - #{remainder} end\n" +
            "where remaining_leave.year=#{year}\n" +
            "and remaining_leave.pis_code=#{pisCode}\n" +
            "and remaining_leave.leave_policy_id=#{leavePolicyId}\n" +
            "and remaining_leave.is_active= true")
    void updateCurrentYearHome(@Param("year") String year, @Param("leavePolicyId") Long leavePolicyId, @Param("pisCode") String pisCode, @Param("remainder") double remainder);


    Boolean checkForGender(@Param("policyId") Long policyId, @Param("pisCode") String pisCode);

    int getDeductHoliday(@Param("fromDate") LocalDate fromDate,
                         @Param("toDate") LocalDate toDate,
                         @Param("officeCode") List<String> officeCode,
                         @Param("pisCode") String pisCode,
                         @Param("shiftId") Set<Long> shiftId,
                         @Param("fiscalYear") String fiscalYear);


    LeavePolicyPojo getPolicyByNameAndEmpCode(@Param("days") Integer days, @Param("pisCode") String pisCode, @Param("id") Long id);

    Boolean getRepetitionValidation(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    Double getMaxDays(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    Long getMaximumDays(@Param("policyId") Long policyId);

    Long checkForTravelDays(@Param("year") String year, @Param("policyId") Long policyId, @Param("pisCode") String pisCode);

    Double getHomeLeaveAccumulatedLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("fiscalYear") Integer fiscalYear,
                                        @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    Boolean checkForTotalAllowed(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("fromDateEn") LocalDate fromDateEn, @Param("toDateEn") LocalDate toDateEn, @Param("maxDays") Double maxDays, @Param("year") String year, @Param("days") Double days,
                                 @Param("leaveId") Long leaveId);

    Long getPreviousLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId);

    Double getNewAccumulatedLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    PrevLeavePolicyPojo getPrevAccumulatedLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    Boolean validateLeave(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    Boolean checkForMinimumService(@Param("pisCode") String pisCode, @Param("currentDate") String currentDate, @Param("minimumYearService") Long minimumYearService);

    Long getTotalAllowedDays(@Param("officeCode") List<String> officeCode, @Param("pisCode") String pisCode);


    Double getTotalDays(@Param("pisCode") String pisCode,
                        @Param("officeCode") String officeCode,
                        @Param("toDate") LocalDate toDate,
                        @Param("allowedForMonth") Integer allowedForMonth,
                        @Param("totalDays") Double totalDays,
                        @Param("empJoinDate") LocalDate empJoinDate,
                        @Param("newEmployee") Boolean newEmployee,
                        @Param("forTransfer") String forTransfer
    );


    Double getMonthlyAllowedDays(@Param("pisCode") String pisCode,
                                 @Param("officeCode") String officeCode,
                                 @Param("toDate") LocalDate toDate,
                                 @Param("allowedForMonth") Integer allowedForMonth,
                                 @Param("totalDays") Double totalDays,
                                 @Param("empJoinDate") LocalDate empJoinDate,
                                 @Param("newEmployee") Boolean newEmployee,
                                 @Param("forTransfer") String forTransfer
    );

    MonthDetailPojo getMaxMonth(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("year") Integer year);

    Double getMonthlyTotalDays(@Param("pisCode") String pisCode,
                               @Param("officeCode") String officeCode,
                               @Param("toDate") LocalDate toDate,
                               @Param("countPrevious") Boolean countPrevious,
                               @Param("allowedForMonth") Integer allowedForMonth,
                               @Param("empJoinDate") LocalDate empJoinDate,
                               @Param("fiscalYear") String fiscalYear
    );

    MonthStatusPojo getAccurateMonthDate(@Param("month") Integer month,
                                         @Param("fromDate") LocalDate fromDate,
                                         @Param("toDate") LocalDate toDate,
                                         @Param("pisCode") String pisCode,
                                         @Param("officeCode") String officeCode,
                                         @Param("nepaliDate") LocalDate nepaliDate,
                                         @Param("year") Integer year);

    Integer getMonthInKarar(@Param("fromDate") LocalDate fromDate,
                            @Param("toDate") LocalDate toDate);


    Double getTotalMonthlyDays(@Param("pisCode") String pisCode,
                               @Param("officeCode") String officeCode,
                               @Param("policyId") Long policyId,
                               @Param("year") String year,
                               @Param("leaveId") Long leaveId
    );


    Double getHomeLeaveAllowedDays(@Param("pisCode") String pisCode,
                                   @Param("officeCode") String officeCode,
                                   @Param("empJoinDate") LocalDate empJoinDate,
                                   @Param("maximumAllocatedDays") Integer maximumAllocatedDays,
                                   @Param("accumulatedDays") Double accumulatedDays,
                                   @Param("leaveStatus") List<Status> leaveStatus,
                                   @Param("leaveId") Long leaveId);

    Double getHomeLeave(@Param("pisCode") String pisCode,
                        @Param("officeCode") String officeCode,
                        @Param("empJoinDate") LocalDate empJoinDate,
                        @Param("latestDate") LocalDate latestDate,
                        @Param("year") String year,
                        @Param("homeLeaveAdditional") Integer homeLeaveAdditional);

    HomeLeavePojo getHomeLeaveWithAdditional(@Param("pisCode") String pisCode,
                                             @Param("officeCode") String officeCode,
                                             @Param("empJoinDate") LocalDate empJoinDate,
                                             @Param("latestDate") LocalDate latestDate,
                                             @Param("year") String year,
                                             @Param("homeLeaveAdditional") Integer homeLeaveAdditional);


    Double getHomeLeaveAllowedDaysForNew(@Param("pisCode") String pisCode,
                                         @Param("officeCode") String officeCode,
                                         @Param("empJoinDate") LocalDate empJoinDate,
                                         @Param("empJoinDays") int empJoinDays,
                                         @Param("maximumAllocatedDays") Integer maximumAllocatedDays,
                                         @Param("leaveStatus") List<Status> leaveStatus);

    String getEmpJoinDate(@Param("pisCode") String pisCode);

    String getEmpEndDate(@Param("pisCode") String pisCode);


    List<LeavePolicyLeavePojo> getLeavePolicy(@Param("employeeStatus") Boolean employeeStatus);

    LeavePolicyLeavePojo getLeavePolicyByLeaveSetup(@Param("leaveSetupId") Long leaveSetupId);

    Double getAllLeave(@Param("pisCode") String pisCode,
                       @Param("leavePolicyId") Long leavePolicyId);

    List<String> getLeaveStatusList(@Param("id") Long id);

    List<String> leaveStatusByLeaveReqId(Long leaveReqId);

    @Select("select da.approver_pis_code as pisCode, da.status as forwardedStatus\n" +
            "        from decision_approval da\n" +
            "        where da.leave_request_detail_id = #{detailId}\n" +
            "        order by da.last_modified_date desc offset 1 limit 1")
    ForwardedPojo getReviewerPisCode(@Param("detailId") final Long detailId);

    Boolean checkNewYear();

    Integer currentYear();

    EmployeeJoinDatePojo getKararEmployeeJoin(@Param("pisCode") String pisCode);

    EmployeeJoinDatePojo findKararPeriod(@Param("pisCode") String pisCode, @Param("fromDateEn") LocalDate fromDateEn, @Param("toDateEn") LocalDate toDateEn);

    EmployeeJoinDatePojo yearStartAndEnd(@Param("year") Integer year);


    Double homeLeaveTaken(@Param("pisCode") String pisCode,
                          @Param("fromDate") LocalDate fromDate,
                          @Param("toDate") LocalDate toDate,
                          @Param("leavePolicyId") Long leavePolicyId);


    Double leaveTakenInPeriod(@Param("pisCode") String pisCode,
                              @Param("fromDate") LocalDate fromDate,
                              @Param("toDate") LocalDate toDate,
                              @Param("leavePolicyId") Long leavePolicyId);

    Integer totalMonth(@Param("fromDate") LocalDate fromDate,
                       @Param("toDate") LocalDate toDate);


}


