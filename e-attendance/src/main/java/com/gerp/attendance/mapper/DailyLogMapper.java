package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.DailyLogPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface DailyLogMapper {

//    @Select("select dl.id, dl.date_en as dateEn, dl.in_time as timeFrom, dl.out_time as timeTo, dl.pis_code as pisCode, dl.office_code as officeCode, dl.fiscal_year_code as fiscalYearCode, dl.remarks, dl.location, dl.created_date as createdDate, da.approver_pis_code as approverPisCode, dl.status as logStatus, da.status as approvalStatus from daily_log dl inner join decision_approval da on dl.record_id = da.record_id where dl.is_active = true  and da.is_active = true")
//    List<DailyLogPojo> getAllDailyLogs();

//    @Select("select dl.id, dl.date_en as dateEn, dl.in_time as timeFrom, dl.out_time as timeTo, dl.pis_code as pisCode, dl.office_code as officeCode, dl.fiscal_year_code as fiscalYearCode, dl.remarks, dl.location, dl.created_date as createdDate, da.approver_pis_code as approverPisCode, dl.status as logStatus, da.status as approvalStatus from daily_log dl inner join decision_approval da on dl.record_id = da.record_id where dl.is_active = true and dl.id = #{id} and da.is_active = true")
//    DailyLogPojo getDailyLogById(@Param("id") Long id);

//    @Select("select dl.id, dl.date_en as dateEn, dl.in_time as timeFrom, dl.out_time as timeTo, dl.pis_code as pisCode, dl.office_code as officeCode, dl.fiscal_year_code as fiscalYearCode, dl.remarks, dl.location, dl.created_date as createdDate, da.approver_pis_code as approverPisCode, dl.status as logStatus, da.status as approvalStatus from daily_log dl inner join decision_approval da on dl.record_id = da.record_id where dl.is_active = true and dl.pis_code = #{pisCode} and da.is_active = true")
//    List<DailyLogPojo> getDailyLogByPisCode(@Param("pisCode") String pisCode);

//    @Select("select dl.id, dl.date_en as dateEn, dl.in_time as timeFrom, dl.out_time as timeTo, dl.pis_code as pisCode, dl.office_code as officeCode, dl.fiscal_year_code as fiscalYearCode, dl.remarks, dl.location, dl.created_date as createdDate, da.approver_pis_code as approverPisCode, dl.status as logStatus, da.status as approvalStatus from daily_log dl inner join decision_approval da on dl.record_id = da.record_id where dl.is_active = true and da.approver_pis_code = #{pisCode} and da.is_active = true")
//    List<DailyLogPojo> getDailyLogByApproverPisCode(@Param("pisCode") String pisCode);

//    @Select("select dl.id, dl.date_en as dateEn, dl.in_time as timeFrom, dl.out_time as timeTo, dl.pis_code as pisCode, dl.office_code as officeCode, dl.fiscal_year_code as fiscalYearCode, dl.remarks, dl.location, dl.created_date as createdDate, da.approver_pis_code as approverPisCode, dl.status as logStatus, da.status as approvalStatus from daily_log dl inner join decision_approval da on dl.record_id = da.record_id where dl.is_active = true and dl.office_code = #{officeCode} and da.is_active = true")
//    List<DailyLogPojo> getDailyLogByOfficeCode(@Param("officeCode") String officeCode);


    List<DailyLogPojo> getAllDailyLogs();

    DailyLogPojo getDailyLogById(@Param("id") Long id);

    List<DailyLogPojo> getDailyLogByPisCode(@Param("pisCode") String pisCode);

    Integer checkCurrentDateDailyLogExists(@Param("pisCode") String pisCode, @Param("inTime") LocalTime inTime, @Param("id") Long id);

    List<DailyLogPojo> getDailyLogByApproverPisCode(@Param("pisCode") String pisCode);

    List<DailyLogPojo> getDailyLogByOfficeCode(@Param("officeCode") String officeCode);

    Page<DailyLogPojo> getDailyLogDetail(Page<DailyLogPojo> page,
                                         @Param("fiscalYear") String fiscalYear,
                                         @Param("pisCodes") List<String> pisCodes,
                                         @Param("officeCode") String officeCode,
                                         @Param("currentDate") LocalDate currentDate,
                                         @Param("status") List<String> status,
                                         @Param("searchField") Map<String, Object> searchField);


    Page<DailyLogPojo> filterData(Page<DailyLogPojo> page,
                                  @Param("fiscalYear") String fiscalYear,
                                  @Param("isApprover") Boolean isApprover,
                                  @Param("pisCode") String pisCode,
                                  @Param("approverPisCode") String approverPisCode,
                                  @Param("searchField") Map<String, Object> searchField);

    Boolean checkForPendingDailyLog(@Param("pisCode") String pisCode);

    Boolean checkForApprovalDailyLog(@Param("pisCode") String pisCode);

}
