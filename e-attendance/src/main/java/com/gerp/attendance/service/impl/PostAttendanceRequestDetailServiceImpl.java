package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.leave.LeaveRequestDetail;
import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.attendance.repo.PostAttendanceRequestDetailRepo;
import com.gerp.attendance.repo.RequestedDaysRepo;
import com.gerp.attendance.service.PostAttendanceRequestDetailService;
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
public class PostAttendanceRequestDetailServiceImpl extends GenericServiceImpl<PostAttendanceRequestDetail, Long> implements PostAttendanceRequestDetailService {

    private final PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo;
    private final CustomMessageSource customMessageSource;

    public PostAttendanceRequestDetailServiceImpl(PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo, CustomMessageSource customMessageSource) {
        super(postAttendanceRequestDetailRepo);
        this.postAttendanceRequestDetailRepo = postAttendanceRequestDetailRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public PostAttendanceRequestDetail findById(Long uuid) {
        PostAttendanceRequestDetail postAttendanceRequestDetail = super.findById(uuid);
        if (postAttendanceRequestDetail == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("post.attendance")));

        return postAttendanceRequestDetail;
    }
}
