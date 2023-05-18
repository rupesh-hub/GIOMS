package com.gerp.attendance.Converter;

import com.gerp.attendance.Pojo.PeriodicHolidayPojo;
import com.gerp.attendance.model.setup.PeriodicHoliday;
import com.gerp.attendance.repo.PeriodicHolidayRepo;
import com.gerp.attendance.repo.PublicHolidaySetupRepo;
import org.springframework.stereotype.Component;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Component
public class PeriodicHolidayConverter {

    private final PeriodicHolidayRepo periodicHolidayRepo;
    private final PublicHolidaySetupRepo publicHolidaySetupRepo;

    public PeriodicHolidayConverter(PeriodicHolidayRepo periodicHolidayRepo, PublicHolidaySetupRepo publicHolidaySetupRepo) {
        this.periodicHolidayRepo = periodicHolidayRepo;
        this.publicHolidaySetupRepo = publicHolidaySetupRepo;

    }

    public PeriodicHoliday toEntity(PeriodicHolidayPojo dto) {
        PeriodicHoliday entity = new PeriodicHoliday();
        return toEntity(dto, entity);
    }

    public PeriodicHoliday toEntity(PeriodicHolidayPojo dto, PeriodicHoliday entity) {
//        entity.setStartDate(dto.getStartDate());
//        entity.setFiscalYear(dto.getFiscalYear());
//        entity.setEndDate(dto.getEndDate());
//        entity.setPublicHolidaySetup(dto.getHolidayName() == null ? null : publicHolidaySetupRepo.getByName(dto.getHolidayName()).get());
        return entity;
    }
}
