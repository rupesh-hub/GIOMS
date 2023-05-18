package com.gerp.attendance.service.impl;

import com.gerp.attendance.model.postAttendance.PostAttendanceRequestDetail;
import com.gerp.attendance.repo.PostAttendanceRequestDetailRepo;
import com.gerp.attendance.service.PostAttendanceDetailService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.service.GenericServiceImpl;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostAttendanceDetailServiceImpl extends GenericServiceImpl<PostAttendanceRequestDetail, Long> implements PostAttendanceDetailService {

    private final PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo;
    private final CustomMessageSource customMessageSource;

    public PostAttendanceDetailServiceImpl(PostAttendanceRequestDetailRepo postAttendanceRequestDetailRepo,
                                           CustomMessageSource customMessageSource) {
        super(postAttendanceRequestDetailRepo);
        this.postAttendanceRequestDetailRepo = postAttendanceRequestDetailRepo;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public PostAttendanceRequestDetail findById(Long uuid) {
        PostAttendanceRequestDetail postAttendanceRequestDetail = super.findById(uuid);
        if(postAttendanceRequestDetail == null)
            throw new RuntimeException(customMessageSource.get("error.doesn't.exist", customMessageSource.get("post.attendance.detail")));

        return postAttendanceRequestDetail;
    }
}
