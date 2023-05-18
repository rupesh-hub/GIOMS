package com.gerp.attendance.service;

import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
import com.gerp.attendance.Pojo.shift.OfficeTimePojo;
import com.gerp.attendance.model.shift.OfficeTimeConfig;
import com.gerp.shared.generic.api.GenericService;

import java.util.List;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
public interface OfficeTimeConfigService extends GenericService<OfficeTimeConfig, Integer> {
    OfficeTimeConfig save(OfficeTimePojo officeTimePojo);

    OfficeTimeConfig update(OfficeTimePojo officeTimePojo);

    OfficeTimePojo getOfficeTimeByCode(String officeCode);

    List<OfficeTimePojo> getAllOfficeTime();
}
