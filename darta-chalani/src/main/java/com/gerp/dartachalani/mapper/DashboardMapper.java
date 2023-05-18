package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.DashboardBarChartPojo;
import com.gerp.dartachalani.dto.DashboardCountPojo;
import com.gerp.dartachalani.dto.MasterDashboardTotalPojo;
import com.gerp.dartachalani.dto.MasterDataPojo;
import com.gerp.shared.pojo.DateListPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Mapper
public interface DashboardMapper {
    DashboardCountPojo getDashboardDartaCount(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                              @Param("isSent") boolean sent, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                              @Param("officeHead") boolean officeHead, @Param("isSuperAdmin") boolean isSuperAdmin,
                                              @Param("toOfficeCode") String toOfficeCode,
                                              @Param("previousPisCodes") Set<String> previousPisCodes);

    Integer getDashBoardTipadiCount(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                    @Param("isSent") boolean equals, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                    @Param("officeHead") boolean officeHead, @Param("isSuperAdmin") boolean isSuperAdmin,
                                    @Param("toOfficeCode") String toOfficeCode, @Param("previousPisCodes") Set<String> previousPisCodes);

    Integer getDashBoardChalaniCount(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                     @Param("isSent") boolean equals, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                     @Param("officeHead") boolean officeHead, @Param("isSuperAdmin") boolean isSuperAdmin,
                                     @Param("toOfficeCode") String toOfficeCode, @Param("previousPisCodes") Set<String> previousPisCodes);

    @Select(" select  distinct  count(*) from received_letter where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  office_code = #{officeCode}")
    Integer getDailyCountDartaForOfficeHead(@Param("officeCode") String officeCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select(" select  distinct  count(*) from received_letter_forward where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  receiver_pis_code = #{pisCode}")
    Integer getDailyCountDartaForEmployee(@Param("pisCode") String pisCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select(" select  distinct  count(*) from memo where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  office_code = #{officeCode}")
    Integer getDailyCountTippadiForOfficeHead(@Param("officeCode") String officeCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select(" select  distinct  count(*) from dispatch_letter where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  sender_office_code = #{officeCode}")
    Integer getDailyCountChalaniForOfficeHead(@Param("officeCode") String officeCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select(" select  distinct  count(*) from memo_approval where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  approver_pis_code = #{pisCode}")
    Integer getDailyCountTippadiForEmployee(@Param("pisCode") String pisCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Select(" select  distinct  count(*) from dispatch_letter_review where\n" +
            "        created_date >= #{startDate}  and\n" +
            "        created_date <= #{endDate}\n" +
            "      and  receiver_pis_code = #{pisCode}")
    Integer getDailyCountChalaniForEmployee(@Param("pisCode") String pisCode, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//    @Select("select * from darta_chalani_lower_office(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList})")
    @Select("select * from darta_chalani_lower_office_test_darta(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalDarta(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                       @Param("limit") Integer limit,
                                       @Param("offSet") Integer offSet,
                                       @Param("officeList") String officeList,
                                       @Param("type") String type
    );


    @Select("select * from darta_chalani_lower_office_test_chalani(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalChalani(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                         @Param("limit") Integer limit,
                                         @Param("offSet") Integer offSet,
                                         @Param("officeList") String officeList,
                                         @Param("type") String type);


    @Select("select * from darta_chalani_lower_office_test_tippani(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalTippani(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                         @Param("limit") Integer limit,
                                         @Param("offSet") Integer offSet,
                                         @Param("officeList") String officeList,
                                         @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_manual_darta(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalManualDarta(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                         @Param("limit") Integer limit,
                                         @Param("offSet") Integer offSet,
                                         @Param("officeList") String officeList,
                                             @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_auto_darta(#{fromDate},#{toDate},#{limit},#{offSet},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalAutoDarta(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                             @Param("limit") Integer limit,
                                             @Param("offSet") Integer offSet,
                                             @Param("officeList") String officeList,
                                           @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_darta_excel(#{fromDate},#{toDate},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalDartaExcel(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                       @Param("officeList") String officeList,
                                            @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_chalani_excel(#{fromDate},#{toDate},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalChalaniExcel(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                         @Param("officeList") String officeList,
                                              @Param("type") String type);


    @Select("select * from darta_chalani_lower_office_test_tippani_excel(#{fromDate},#{toDate},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalTippaniExcel(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                         @Param("officeList") String officeList,
                                              @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_manual_darta_excel(#{fromDate},#{toDate},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalManualDartaExcel(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                             @Param("officeList") String officeList,
                                                  @Param("type") String type);

    @Select("select * from darta_chalani_lower_office_test_auto_darta_excel(#{fromDate},#{toDate},#{officeList}, #{type})")
    List<MasterDataPojo> getTotalAutoDartaExcel(@Param("fromDate") String fromDate, @Param("toDate") String toDate,
                                           @Param("officeList") String officeList,
                                                @Param("type") String type);


    @Select("select * from darta_master_count_office(#{fromDate},#{toDate},#{officeList})")
    List<MasterDataPojo> getDartaMasterCount(@Param("fromDate") String fromDate, @Param("toDate") String toDate,@Param("officeList") String officeList);

    @Select("select * from count_darta_chalani_tippani(#{fromDate},#{toDate},#{officeCode})")
    MasterDataPojo getDartaCountByOfficeCode(@Param("fromDate") String fromDate, @Param("toDate") String toDate,@Param("officeCode") String officeCode);


    DashboardCountPojo getDartaStatusWiseDetail(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode,
                                                @Param("officeCode") String officeCode, @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate, @Param("officeHead") boolean b,
                                                @Param("isSent") boolean type, @Param("previousPisCodes") Set<String> previousPisCodes);

    DashboardCountPojo getDartaStatusWiseDetailHead(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                                    @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("officeHead") boolean b,
                                                    @Param("isSent") boolean type, @Param("isSuperAdmin") boolean isSuperAdmin,
                                                    @Param("toOfficeCode") String toOfficeCode);

    DashboardCountPojo getChalaniStatusWiseDetail(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                                  @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("officeHead") boolean b,
                                                  @Param("isSent") boolean type, @Param("isSuperAdmin") boolean isSuperAdmin,
                                                  @Param("toOfficeCode") String toOfficeCode, @Param("previousPisCodes") Set<String> previousPisCodes);

    DashboardCountPojo getTippadiStatusWiseDetail(@Param("sectionId") String sectionId, @Param("employeeCode") String employeeCode, @Param("officeCode") String officeCode,
                                                  @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("officeHead") boolean b,
                                                  @Param("isSent") boolean type, @Param("isSuperAdmin") boolean isSuperAdmin,
                                                  @Param("toOfficeCode") String toOfficeCode, @Param("previousPisCodes") Set<String> previousPisCodes);


    List<DashboardBarChartPojo> getBarChartDarta(@Param("startDate") LocalDate startOfLastWeek, @Param("endDate") LocalDate endOfLastWeek, @Param("period") String period,
                                                 @Param("pisCode") String pisCode, @Param("isSent") boolean type, @Param("sectionId") String sectionId,
                                                 @Param("dates") Map<LocalDate, LocalDate> dates, @Param("officeHead") boolean b, @Param("officeCode") String officeCode,
                                                 @Param("isSuperAdmin") boolean isSuperAdmin,
                                                 @Param("toOfficeCode") String toOfficeCode);

    List<DashboardBarChartPojo> getBarChartChalani(@Param("startDate") LocalDate startOfLastWeek, @Param("endDate") LocalDate endOfLastWeek, @Param("period") String period,
                                                   @Param("pisCode") String pisCode, @Param("isSent") boolean type, @Param("sectionId") String sectionId,
                                                   @Param("dates") Map<LocalDate, LocalDate> dates, @Param("officeHead") boolean b,
                                                   @Param("officeCode") String officeCode, @Param("isSuperAdmin") boolean isSuperAdmin,
                                                   @Param("toOfficeCode") String toOfficeCode);

    List<DashboardBarChartPojo> getBarChartTippani(@Param("startDate") LocalDate startOfLastWeek, @Param("endDate") LocalDate endOfLastWeek, @Param("period") String period,
                                                   @Param("pisCode") String pisCode, @Param("isSent") boolean type, @Param("sectionId") String sectionId,
                                                   @Param("dates") Map<LocalDate, LocalDate> dates, @Param("officeHead") boolean b,
                                                   @Param("officeCode") String officeCode, @Param("isSuperAdmin") boolean isSuperAdmin,
                                                   @Param("toOfficeCode") String toOfficeCode);


    List<DashboardBarChartPojo> getYearBarChartDarta(@Param("dataMap") List<DateListPojo> test, @Param("isSent") boolean type, @Param("employeeCode") String pisCode,
                                                     @Param("sectionId") String sectionId, @Param("officeHead") boolean b, @Param("officeCode") String officeCode,
                                                     @Param("isSuperAdmin") boolean isSuperAdmin,
                                                     @Param("toOfficeCode") String toOfficeCode);

    List<DashboardBarChartPojo> getYearBarChartChalani(@Param("dataMap") List<DateListPojo> test, @Param("isSent") boolean type, @Param("employeeCode") String pisCode,
                                                       @Param("sectionId") String sectionId, @Param("officeHead") boolean b, @Param("officeCode") String officeCode,
                                                       @Param("isSuperAdmin") boolean isSuperAdmin,
                                                       @Param("toOfficeCode") String toOfficeCode);

    List<DashboardBarChartPojo> getYearBarChartTippani(@Param("dataMap") List<DateListPojo> test, @Param("isSent") boolean type, @Param("employeeCode") String pisCode,
                                                       @Param("sectionId") String sectionId, @Param("officeHead") boolean b, @Param("officeCode") String officeCode,
                                                       @Param("isSuperAdmin") boolean isSuperAdmin,
                                                       @Param("toOfficeCode") String toOfficeCode);

    DashboardCountPojo sidebarCountDarta(@Param("pisCodes") Set<String> pisCodes,  @Param("sectionId") String sectionId, @Param("officeCode") String officeCode, @Param("fiscalYearCode") String fiscalYearCode);

    DashboardCountPojo sidebarCountTippadi(@Param("pisCodes") Set<String> pisCodes, @Param("sectionId") String sectionId, @Param("officeCode") String officeCode,  @Param("fiscalYearCode") String fiscalYearCode);

    DashboardCountPojo sidebarCountChalani(@Param("pisCodes") Set<String> pisCodes, @Param("sectionId") String sectionId, @Param("officeCode") String officeCode,  @Param("fiscalYearCode") String fiscalYearCode);

    DashboardCountPojo sidebarCountDartaForOfficeHead(@Param("officeCode") String officeCode,  @Param("pisCode") String pisCode,  @Param("fiscalYearCode") String fiscalYearCode);

    @Select("select * from darta_chalani_master_dashboard_total(#{fromDate},#{toDate}, #{officeList})")
    MasterDashboardTotalPojo getMasterDashboardTotal(@Param("fromDate") String fromDate, @Param("toDate") String toDate, @Param("officeList") String officeList);

}
