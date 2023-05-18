package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.repo.RequestedDaysRepo;
import com.gerp.attendance.service.RequestedDaysService;
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
public class RequestedDaysServiceImpl extends GenericServiceImpl<LeaveRequestDetail, Long> implements RequestedDaysService {

    private final RequestedDaysRepo requestedDaysRepo;
    private final CustomMessageSource customMessageSource;

    public RequestedDaysServiceImpl(RequestedDaysRepo requestedDaysRepo, CustomMessageSource customMessageSource) {
        super(requestedDaysRepo);
        this.requestedDaysRepo = requestedDaysRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public LeaveRequestDetail findById(Long uuid) {
        LeaveRequestDetail requestedDays = super.findById(uuid);
        if (requestedDays == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("requested.days")));

        return requestedDays;
    }

}
