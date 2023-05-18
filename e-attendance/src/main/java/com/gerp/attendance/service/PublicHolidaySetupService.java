package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.HolidayMapperPojo;
import com.gerp.attendance.Pojo.PublicHolidaySetupPojo;
import com.gerp.attendance.mapper.HolidayMapper;
import com.gerp.attendance.model.setup.PublicHolidaySetup;
import com.gerp.shared.generic.api.GenericService;
import java.util.ArrayList;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface PublicHolidaySetupService extends GenericService<PublicHolidaySetup, Integer> {
    PublicHolidaySetup save(PublicHolidaySetupPojo publicHolidaySetupPojo);

    ArrayList<HolidayMapperPojo> getAllHolidays();

    HolidayMapperPojo getHolidayById(Integer id);
    ArrayList<HolidayMapperPojo> getByOfficeCode(String officeCode);

    void updatePublicHoliday(PublicHolidaySetupPojo publicHolidaySetupPojo);

    void softDeleteHoliday(Integer holidayId);

    void hardDelete(Integer holidayId);
    ArrayList<HolidayMapperPojo> getHolidayFor(String holidayFor);
}
