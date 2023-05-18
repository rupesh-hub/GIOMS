package com.gerp.tms.service;

import com.gerp.tms.pojo.PhaseMemberPojo;
import com.gerp.tms.pojo.request.PhaseRequestPojo;
import com.gerp.tms.pojo.response.MemberDetailsResponsePojo;
import com.gerp.tms.pojo.response.PhaseMemberResponsePojo;
import com.gerp.tms.pojo.response.PhaseResponsePojo;

import java.util.List;

public interface PhaseService {
    Long addPhase(PhaseRequestPojo phaseRequest);

    Long updatePhase(PhaseRequestPojo phaseRequest);

    void deletePhase(Long id);

    PhaseResponsePojo getPhaseDetails(Long id);

    List<PhaseResponsePojo> getPhases();

    void addMembersToProjectPhase(PhaseMemberPojo phaseMemberPojo);

    List<PhaseMemberResponsePojo> getPhaseProjectMember(Integer projectId, Integer phaseId);

    void removePhaseProjectMember(Integer projectId, Integer phaseId, Integer memberId);
}
