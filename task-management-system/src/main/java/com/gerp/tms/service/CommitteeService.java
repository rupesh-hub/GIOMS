package com.gerp.tms.service;

import com.gerp.tms.pojo.request.CommitteeRequestPojo;
import com.gerp.tms.pojo.response.CommitteeResponsePojo;
import com.gerp.tms.pojo.response.CommitteeWiseProjectResponsePojo;

import java.util.List;

public interface CommitteeService {
    Integer createCommittee(CommitteeRequestPojo committeeRequestPojo);

    Integer updateCommittee(CommitteeRequestPojo committeeRequestPojo);

    CommitteeResponsePojo getCommitteeDetails(Integer id);

    List<CommitteeResponsePojo> getCommittees();

    void deleteCommittee(Integer id);

    CommitteeWiseProjectResponsePojo getCommitteeWiseProject(int committeeId);
}
