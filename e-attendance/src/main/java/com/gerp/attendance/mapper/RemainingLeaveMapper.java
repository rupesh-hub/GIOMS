package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface RemainingLeaveMapper {

    ArrayList<RemainingLeaveMinimalPojo> getAllRemainingLeave(@Param("year") String year);

    ArrayList<RemainingLeaveMinimalPojo> getRemainingLeaveByOfficeCode(@Param("officeCode") String officeCode, @Param("name") String name, @Param("year") String year);

    ArrayList<RemainingLeaveMinimalPojo> getAllRemainingLeaveByPisCode(@Param("pisCode") String pisCode, @Param("year") String year);

    List<RemainingLeaveRequestPojo> allRemainingLeave(@Param("pisCode") String pisCode, @Param("year") String year);

    Double getRemainingLeave(@Param("repetition") Integer repetition, @Param("leaveTaken") Double leaveTaken, @Param("leaveTakenFy") Double leaveTakenFy,
                             @Param("leavePolicyId") Long leavePolicyId, @Param("leaveTakenMonthly") Double leaveTakenMonthly,
                             @Param("homeLeave") Double homeLeave,
                             @Param("leaveTakenForObsequies") Double leaveTakenForObsequies,
                             @Param("accumulatedFy") Double accumulatedFy);

    void updateRemainingLeave(@Param("pisCode") String pisCode, @Param("leaveTaken") Double leaveTaken,
                              @Param("leaveTakenFy") Double leaveTakenFy,
                              @Param("accumulatedLeaveFy") Double accumulatedLeaveFy,
                              @Param("accumulatedLeave") Double accumulatedLeave,
                              @Param("policyId") Long policyId,
                              @Param("leaveMonthly") Double leaveMonthly,
                              @Param("travelDays") Integer travelDays,
                              @Param("createdDate") Date createdDate,
                              @Param("updateDate") Date updateDate,
                              @Param("homeLeave") Double homeLeave,
                              @Param("obsequiesLeave") Double obsequiesLeave,
                              @Param("additionalHomeLeave") Double additionalHomeLeave,
                              @Param("year") String year);

    RemainingMonthlyLeavePojo getMonthlyLeaveData(@Param("officeCode") List<String> officeCode,
                                                  @Param("pisCode") String pisCode);

    String getLatestDate(@Param("fromDate") LocalDate fromDate,
                         @Param("year") Integer year);

    LocalDate getLatestLeaveDate(@Param("pisCode") String pisCode,
                                 @Param("leavePolicyId") Long leavePolicyId,
                                 @Param("year") String year);

    void updateKararLeave(@Param("pisCode") String pisCode,
                          @Param("policyId") Long policyId,
                          @Param("latestDate") Date latestDate,
                          @Param("accumulatedLeaveFy") Double accumulatedLeaveFy,
                          @Param("leaveTaken") Double leaveTaken,
                          @Param("repetition") Integer repetition);


    void updateRemainingLeaveRevert(@Param("pisCode") String pisCode, @Param("leaveTaken") Double leaveTaken,
                                    @Param("leaveTakenFy") Double leaveTakenFy, @Param("accumulatedLeaveFy") Double accumulatedLeaveFy,
                                    @Param("accumulatedLeave") Double accumulatedLeave,
                                    @Param("policyId") Long policyId, @Param("leaveTakenMonthly") Double leaveTakenMonthly,
                                    @Param("travelDays") Integer travelDays,
                                    @Param("updateDate") Date updateDate,
                                    @Param("homeLeave") Double homeLeave,
                                    @Param("additionalHomeLeaved") Double additionalHomeLeave,
                                    @Param("obsequiesLeave") Double obsequiesLeave,
                                    @Param("year") String year);

    KararMonthPojo MonthlyLeaveCheck(@Param("fromDate") LocalDate fromDate,
                                     @Param("toDate") LocalDate toDate);

    Date maxEngDate(@Param("nepaliMonth") Integer nepaliMonth,
                    @Param("nepaliYear") Integer nepaliYear);

    Long getRepetition(@Param("pisCode") String pisCode,
                       @Param("fromDate") Date fromDate,
                       @Param("toDate") Date toDate);

    @Select("select rl.id," +
            "rl.pis_code," +
            "rl.leave_taken," +
            "rl.leave_taken_fy," +
            "rl.repetition," +
            "rl.travel_days," +
            "rl.accumulated_leave," +
            "rl.accumulated_leave_fy," +
            "rl.created_date as createdDate," +
            "rl.last_modified_date as lastModifiedDate," +
            "rl.upto_date as uptoDate," +
            "rl.remaining_leave," +
            "rl.leave_policy_id," +
            "rl.home_leave_additional," +
            "rl.monthly_leave_taken as leaveTakenMonth," +
            "ls.name_en as leaveNameEn," +
            "ls.name_np as leaveNameNp" +
            " from remaining_leave rl " +
            " left join leave_policy lp on lp.id=rl.leave_policy_id " +
            " left join leave_setup ls on ls.id=lp.leave_setup_id " +
            "where  rl.leave_policy_id= #{policyId} and rl.pis_code= #{pisCode} and rl.year=#{year} limit 1")
    RemainingLeaveResponsePoio getRemainingLeaveByEmpAndPolicy(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);


    @Select("select rl.id," +
            "rl.pis_code," +
            "rl.leave_taken," +
            "rl.leave_taken_fy," +
            "rl.repetition," +
            "rl.travel_days," +
            "rl.accumulated_leave," +
            "rl.accumulated_leave_fy," +
            "rl.created_date as createdDate," +
            "rl.last_modified_date as lastModifiedDate," +
            "rl.upto_date as uptoDate," +
            "rl.remaining_leave," +
            "rl.leave_policy_id," +
            "rl.home_leave_additional," +
            "rl.monthly_leave_taken as leaveTakenMonth," +
            "ls.name_en as leaveNameEn," +
            "ls.name_np as leaveNameNp" +
            " from remaining_leave rl " +
            " left join leave_policy lp on lp.id=rl.leave_policy_id " +
            " left join leave_setup ls on ls.id=lp.leave_setup_id " +
            "where rl.leave_policy_id= #{policyId} and rl.pis_code= #{pisCode} and rl.year=#{year} limit 1")
    RemainingLeaveResponsePoio getRemainingLeaveByYear(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    RemainingLeaveForLeaveRequestPojo getRemainingLeaveByPisCode(@Param("pisCode") String pisCode,
                                                                 @Param("policyId") Long policyId,
                                                                 @Param("year") String year,
                                                                 @Param("empJoinDate") LocalDate empJoinDate,
                                                                 @Param("homeAllowedLeave") Long homeAllowedLeave,
                                                                 @Param("monthlyLeave") Double monthlyLeave);

    RemainingLeaveForLeaveRequestPojo getRemainingLeaveSpecific(@Param("pisCode") String pisCode,
                                                                @Param("policyId") Long policyId,
                                                                @Param("fiscalYear") Integer fiscalYear);

    //    @Select("select rl.id," +
//            "rl.pis_code," +
//            "rl.leave_taken," +
//            "rl.leave_taken_fy," +
//            "rl.repetition," +
//            "rl.travel_days," +
//            "rl.accumulated_leave_fy," +
//            "rl.remaining_leave," +
//            "rl.leave_policy_id," +
//            "ls.id as leaveSetupId," +
//            "ls.name_en as leaveNameEn," +
//            "ls.name_np as leaveNameNp" +
//            " from remaining_leave rl " +
//            " left join leave_policy lp on lp.id=rl.leave_policy_id " +
//            " left join leave_setup ls on ls.id=lp.leave_setup_id " +
//            "where rl.is_active=true and rl.id= #{id}")
    RemainingLeaveResponsePoio getRemainingLeaveById(@Param("id") Long id, @Param("year") String year);


    @Select("select rl.id,\n" +
            "            rl.pis_code,\n" +
            "            rl.leave_taken,\n" +
            "            rl.leave_taken_fy,\n" +
            "            rl.last_modified_date as createdDate,\n" +
            "            rl.repetition,\n" +
            "            rl.travel_days,\n" +
            "            rl.accumulated_leave_fy,\n" +
            "            rl.accumulated_leave,\n" +
            "            rl.remaining_leave,\n" +
            "            rl.upto_date as uptoDate,\n" +
            "            rl.leave_policy_id,\n" +
            "            rl.monthly_leave_taken as leaveTakenMonth,\n" +
            "            ls.name_en as leaveNameEn,\n" +
            "            ls.name_np as leaveNameNp,\n" +
            "            ls.id as leaveSetupId\n" +
            "            from remaining_leave rl \n" +
            "            left join leave_policy lp on lp.id=rl.leave_policy_id \n" +
            "            left join leave_setup ls on ls.id=lp.leave_setup_id \n" +
            "            where rl.leave_policy_id= #{policyId} \n" +
            "            and rl.year= #{year} \n" +
            "            and rl.pis_code= #{pisCode} limit 1")
    RemainingLeaveResponsePoio getRemainingLeaveByEmpInFiscal(@Param("pisCode") String pisCode, @Param("policyId") Long policyId, @Param("year") String year);

    List<RemainingReportPojo> getYealyRemainingLeave(@Param("pisCode") String pisCode,
                                                     @Param("year") String year,
                                                     @Param("previousYear") String previousYear,
                                                     @Param("officeCode") String officeCode);


    List<EmployeeLeaveTakenPojo> getAllLeaveTaken(@Param("officeCode") String officeCode,
                                                  @Param("userStatus") Boolean userStatus,
                                                  @Param("name") String name);


    @Select("select count(ea.pis_code) as total_uninformed_leave\n" +
            "                         from employee_attendance ea\n" +
            "                         where ea.attendance_status ='UNINOFRMED_LEAVE_ABSENT'\n" +
            "                           and ea.date_en between #{startDate} and #{toDate} and pis_code =#{pisCode}")
    Integer getUnInformedLeave(@Param("startDate") LocalDate startDate, @Param("toDate") LocalDate toDate, @Param("pisCode") String pisCode);

    List<RemainingLeaveByOfficeCodePojo> remainingLeaveByOfficeCode(@Param("officeCode") final String officeCode,
                                                                    @Param("name") final String name,
                                                                    @Param("year") final String year);
}
