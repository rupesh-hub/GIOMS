package com.gerp.attendance.mapper;

import com.gerp.attendance.Pojo.DateCountPojo;
import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.HolidayResponsePojo;
import com.gerp.attendance.Pojo.holiday.PublicHolidayPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface HolidayMapper {


    @Select("SELECT puh.id as holidayId, " +
            "puh.office_code as officeCode, " +
            "puh.name_en as holidayNameEn, " +
            "puh.name_np as holidayNameNp, " +
            "puh.holiday_for as holidayFor, " +
            "peh.id as id, " +
            "peh.from_date_en as fromDateEn, " +
            "peh.from_date_np as fromDateNp, " +
            "peh.to_date_en as toDateEn, " +
            "peh.to_date_np as toDateNp, " +
            "peh.fiscal_year_code as fiscalYearCode, " +
            "peh.is_active as isActive," +
            "peh.is_specific_holiday " +
            "FROM periodic_holiday peh " +
            "RIGHT JOIN public_holiday puh ON peh.public_holiday_id = puh.id WHERE puh.is_active = true and peh.id = #{id}")
    HolidayResponsePojo getAHoliday(Long id);

    ArrayList<HolidayResponsePojo> getAllHolidays(@Param("officeCode") List<String> officeCode);
    //change office wise
    ArrayList<HolidayResponsePojo> getAllHoliday();
    ArrayList<HolidayResponsePojo> getAllHolidaysByYear(@Param("officeCode") List<String> officeCode, @Param("year")String year);

    @Select("SELECT puh.id as holidayId, " +
            "puh.office_code as officeCode, " +
            "puh.name_en as holidayNameEn, " +
            "puh.name_np as holidayNameNp, " +
            "puh.holiday_for as holidayFor, " +
            "peh.id as id, " +
            "peh.from_date_en as fromDateEn, " +
            "peh.from_date_np as fromDateNp, " +
            "peh.to_date_en as toDateEn, " +
            "peh.to_date_np as toDateNp, " +
            "peh.fiscal_year_code as fiscalYearCode, " +
            "peh.is_active as isActive," +
            "peh.is_specific_holiday " +
            "FROM periodic_holiday peh " +
            "RIGHT JOIN public_holiday puh ON peh.public_holiday_id = puh.id where peh.fiscal_year_code = #{fiscalYear}")
    ArrayList<HolidayResponsePojo> getByFiscalYear(String fiscalYear);

//    @Select("select distinct(fiscal_year_code) as fiscal_year_code from periodic_holiday")
//    List<String> getAllDistinctDates();
//
//    @Select("select count(id) from periodic_holiday where fiscal_year_code=#{date}")
//    Integer getCountOfDistinctDate(String date);

    @Select("select fiscal_year_code, count(id) from periodic_holiday where is_active = true group by fiscal_year_code")
    List<DateCountPojo> getHolidayForFiscalYear();

    ArrayList<HolidayResponsePojo> getHolidayBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, @Param("officeCode") List<String> officeCode);

    Long checkHoliday(@Param("officeCode") List<String> officeCode,
                         @Param("dateEn") LocalDate dateEn,
                         @Param("year") String year,
                         @Param("gender") String gender);

    HolidayMapperPojo holidayDetail(@Param("officeCode") List<String> officeCode,
                                    @Param("dateEn") LocalDate dateEn,
                                    @Param("year") String year);


   Long getHolidayCountMinusWeekends(@Param("fromDate") LocalDate fromDate,
                                      @Param("toDate") LocalDate toDate,
                                      @Param("officeCode") List<String> officeCode,
                                      @Param("weekends") List<LocalDate> weekends,
                                      @Param("year") String year,
                                     @Param("checkWeekend") String checkWeekend,
                                     @Param("gender") String gender);


    ArrayList<HolidayResponsePojo> getHolidayByYearAndMonth(@Param("officeCode") List<String> officeCode, @Param("month") Double month, @Param("year") Double year );

    ArrayList<HolidayResponsePojo> getHolidayByDateRange(@Param("officeCode") List<String> officeCode, @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    List<LocalDate> getWeekends(@Param("fromDate") LocalDate fromDate,
                                @Param("toDate") LocalDate toDate,
                                @Param("officeCode") List<String> officeCode,
                                @Param("shiftId") Set<Long> shiftId,
                                @Param("fiscalYearCode") String fiscalYearCode);

    List<PublicHolidayPojo> getPublicHolidayDetail(@Param("fromDate") LocalDate fromDate,
                                                   @Param("toDate") LocalDate toDate,
                                                   @Param("officeCode") List<String> officeCode,
                                                   @Param("shiftId") Set<Long> shiftId,
                                                   @Param("year") String year,
                                                   @Param("gender") String gender);
@Select("  select nepali_year\n" +
        "    from date_list dl where dl.eng_date=current_date;")
    Integer currentYear();


    @Select("select count(*) from  periodic_holiday peh where id = #{id} and year_en = #{year}")
    Integer currentYearHolidayExists(Integer id, LocalDate year);


    ArrayList<HolidayResponsePojo> getAllHolidaysNotSetupByYear(@Param("officeCode") List<String> officeCode, @Param("year") String year);
}
