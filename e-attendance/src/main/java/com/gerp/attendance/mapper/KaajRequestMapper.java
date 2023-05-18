package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.*;
import com.gerp.attendance.Pojo.kaaj.report.KaajReportData;
import com.gerp.attendance.Pojo.report.EmployeeAttendancePage;
import com.gerp.shared.pojo.IdNamePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Mapper
public interface KaajRequestMapper {

    List<KaajRequestCustomPojo> getAllKaajRequest();

    KaajRequestCustomPojo getKaajRequestById(Long id);

    List<KaajRequestCustomPojo> getKaajRequestByPisCode(String pisCode);

    List<KaajRequestCustomPojo> getKaajByApproverPisCode(String approverPisCode);

    ArrayList<KaajRequestMinimalPojo> getKaajByMonthAndYear(@Param("pisCode") String pisCode, @Param("month") Double month, @Param("year") Double year);

    ArrayList<KaajRequestMinimalPojo> getKaajByDateRange(@Param("pisCode") String pisCode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    Page<KaajHistoryPojo> getKaajHistoryByPisCode(final Page<KaajHistoryPojo> page, @Param("pisCode") String pisCode);

    KaajHistoryPojo getApproverDetail(final String pisCode);

    List<IdNamePojo> selectVehicle(final Long id);

    EmployeeAttendancePage<KaajRequestCustomPojo> filterData(EmployeeAttendancePage<KaajRequestCustomPojo> page,
                                                             @Param("fiscalYear") Long fiscalYear,
                                                             @Param("forReport") String forReport,
                                                             @Param("pisCode") String pisCode,
                                                             @Param("approverPisCode") String approverPisCode,
                                                             @Param("officeCode") String officeCode,
                                                             @Param("isApprover") Boolean isApprover,
                                                             @Param("searchField") Map<String, Object> searchField);


    EmployeeAttendancePage<KaajRequestCustomPojo> kaajFilter(EmployeeAttendancePage<KaajRequestCustomPojo> page,
                                                             @Param("fiscalYear") Long fiscalYear,
                                                             @Param("forReport") String forReport,
                                                             @Param("pisCode") String pisCode,
                                                             @Param("approverPisCode") String approverPisCode,
                                                             @Param("officeCode") String officeCode,
                                                             @Param("isApprover") Boolean isApprover,
                                                             @Param("searchField") Map<String, Object> searchField);

    EmployeeAttendancePage<KaajRequestCustomPojo> kaajFilters(EmployeeAttendancePage<KaajRequestCustomPojo> page,
                                                              @Param("fiscalYear") Long fiscalYear,
                                                              @Param("forReport") String forReport,
                                                              @Param("pisCode") String pisCode,
                                                              @Param("approverPisCode") String approverPisCode,
                                                              @Param("officeCode") String officeCode,
                                                              @Param("isApprover") Boolean isApprover,
                                                              @Param("searchField") Map<String, Object> searchField);

    /*OPTIMIZED*/
    Page<KaajResponsePojo> kaajPaginated(Page<KaajRequestCustomPojo> page,
                                         @Param("fiscalYear") Long fiscalYear,
                                         @Param("forReport") String forReport,
                                         @Param("pisCode") String pisCode,
                                         @Param("approverPisCode") String approverPisCode,
                                         @Param("officeCode") String officeCode,
                                         @Param("isApprover") Boolean isApprover,
                                         @Param("searchField") Map<String, Object> searchField,
                                         @Param("currentDate") String currentDate);

//    EmployeeAttendancePage<KaajRequestCustomPojo> filterData(EmployeeAttendancePage<KaajRequestCustomPojo> page,
//                                           @Param("fiscalYear") Long fiscalYear,
//                                           @Param("forReport") String forReport,
//                                                             @Param("pisCode") String pisCode,
//                                                             @Param("approverPisCode") String approverPisCode,
//                                                             @Param("officeCode") String officeCode,
//                                                             @Param("isApprover") Boolean isApprover,
//                                           @Param("searchField") Map<String, Object> searchField);

//    EmployeeAttendancePage<KaajRequestCustomPojo> filterData(EmployeeAttendancePage<KaajRequestCustomPojo> page,
//                                                             @Param("fiscalYear") Long fiscalYear,
//                                                             @Param("forReport") String forReport,
//                                                             @Param("pisCode") String pisCode,
//                                                             @Param("approverPisCode") String approverPisCode,
//                                                             @Param("officeCode") String officeCode,
//                                                             @Param("searchField") Map<String, Object> searchField);

    Long getKaajRequestByEmpPisCodeAndDateRange(@Param("pisCode") String pisCode, @Param("approvalStatus") List<String> approvalStatus, @Param("toDateEn") LocalDate toDateEn, @Param("fromDateEn") LocalDate fromDateEn);

    KaajRequestPojo kaajAppliedDate(@Param("kaajId") Long kaajId);

    Long countPendingKaaj(@Param("fiscalYear") Long fiscalYear,
                          @Param("forReport") Boolean forReport,
                          @Param("isApprover") Boolean isApprover,
                          @Param("pisCode") String pisCode,
                          @Param("approverPisCode") String approverPisCode,
                          @Param("searchField") Map<String, Object> searchField);

    List<KaajRequestCustomPojo> filterDataForExcel(
            @Param("fiscalYear") Long fiscalYear,
            @Param("pisCode") String pisCode,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("approvalStatus") String approvalStatus);


    List<String> getKaajStatusList(@Param("id") Long id);

    List<EmployeeOnKaajPojo> getEmployeeOnKaaj(@Param("currentDate") LocalDate currentDate, @Param("officeCode") String officeCode);

    @Select("select pis_code from kaaj_request kr where kr.id = #{id}\n" +
            "union\n" +
            "select approver_pis_code as pis_code from decision_approval da where da.kaaj_request_id = #{id}\n" +
            "union select distinct krob.pis_code from kaaj_request_on_behalf krob where krob.kaaj_request_id=#{id}" +
            "union distinct select applied_pis_code from kaaj_request kr where kr.id = #{id}")
    List<String> getPisCodeThatCanViewData(@Param("id") Long id);

    KaajReportData getPaperReportData(@Param("id") Long id);


    @Select("select * from kaaj_request where pis_code= #{pisCode} order by id desc limit 1")
    KaajRequestCustomPojo getKaajLatest(@Param("pisCode") String pisCode);

    Boolean checKForPendingKaaj(@Param("pisCode") String pisCode);

    Boolean checkForApproveKaaj(@Param("pisCode") String pisCode);

    void discardSelectedKaaj(@Param("kaajId") Long kaajId,
                             @Param("order") Integer order,
                             @Param("pisCode") String pisCode,
                             @Param("fromDateEn") LocalDate fromDateEn,
                             @Param("toDateEn") LocalDate toDateEn);


    @Select("select * from kaaj_approved_details_paginated(#{officeCode},#{pisCodes},#{fromDateEn},#{toDateEn},#{offsets},#{limits})")
    List<KaajSummaryPojo> getKaajSummaryData(@Param("officeCode") String officeCode,
                                             @Param("pisCodes") String pisCodes,
                                             @Param("fromDateEn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDateEn,
                                             @Param("toDateEn") @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDateEn,
                                             @Param("offsets") Integer offsets,
                                             @Param("limits") Integer limits);


    EmployeeDetailPojo employeeDetails(String pisCode);
}
