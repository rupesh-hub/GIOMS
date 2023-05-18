package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.shift.Day;
import com.gerp.attendance.repo.DayRepo;
import com.gerp.attendance.service.DayService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class DayServiceImpl extends GenericServiceImpl<Day, Integer> implements DayService {

    private final DayRepo dayRepo;
    private final CustomMessageSource customMessageSource;

    public DayServiceImpl(DayRepo dayRepo, CustomMessageSource customMessageSource) {
        super(dayRepo);
        this.dayRepo = dayRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public Day findById(Integer uuid) {
        Day day = super.findById(uuid);
        if (day == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("day")));
        return day;
    }
}
