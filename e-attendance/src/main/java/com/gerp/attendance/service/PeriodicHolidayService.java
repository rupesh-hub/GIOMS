package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.DateCountPojo;
import com.gerp.attendance.Pojo.HolidayResponsePojo;
import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
import com.gerp.attendance.Pojo.PeriodicHolidayRequestPojo;
import com.gerp.attendance.Pojo.holiday.HolidayCountDetailPojo;
import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.shared.generic.api.GenericService;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PeriodicHolidayService  extends GenericService<PeriodicHoliday, Long> {
    List<PeriodicHoliday> save(PeriodicHolidayRequestPojo periodicHolidayPojo);

    void update(PeriodicHolidayPojo periodicHolidayPojo);

    void createSingle(PeriodicHolidayPojo periodicHolidayPojo);

    ArrayList<HolidayResponsePojo> getAllPeriodicHoliday();
    ArrayList<HolidayResponsePojo> getAllPeriodicHolidayByYear(String year);

    ArrayList<HolidayResponsePojo> getByFiscalYear(String fiscalYear);

    void softDeleteHoliday(Long holidayId);

    List<DateCountPojo> countDistinctHolidayDates();

    List<HolidayResponsePojo> getHolidayBetween(LocalDate fromDate, LocalDate toDate);

    HolidayCountDetailPojo getHolidayCount(String pisCode,LocalDate fromDate, LocalDate toDate,Boolean forDashboard) throws ParseException;

    List<HolidayResponsePojo>getHolidayByYearAndMonth(String officeCode, String month, String year);

    List<HolidayResponsePojo>getHolidayByDateRange(String officeCode,LocalDate fromDate, LocalDate toDate);

    void toggleSpecificHoliday(Long holidayId);

    List<HolidayResponsePojo> getHolidaysNotSetUp(String year, String officeCode);

}

