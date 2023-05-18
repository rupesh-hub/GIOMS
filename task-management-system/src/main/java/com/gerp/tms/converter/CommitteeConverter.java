package com.gerp.tms.converter;

import com.gerp.tms.pojo.request.CommitteeRequestPojo;
import com.gerp.tms.pojo.response.CommitteeResponsePojo;
import com.gerp.tms.model.committee.Committee;
import com.gerp.tms.model.committee.CommitteeMembers;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommitteeConverter {

    public Committee toEntity(CommitteeRequestPojo committeeRequestPojo){
      Committee committee = new Committee();
        return toEntity(committeeRequestPojo,committee);
    }

    private Committee toEntity(CommitteeRequestPojo committeeRequestPojo, Committee entity) {
        entity.setCommitteeName(committeeRequestPojo.getCommitteeName());
        entity.setCommitteeNameNp(committeeRequestPojo.getCommitteeNameNp());
       if (committeeRequestPojo.getMemberIds() != null){
           List<CommitteeMembers> committeeMembers = committeeRequestPojo.getMemberIds().stream().map(CommitteeMembers::new).collect(Collectors.toList());
           entity.setCommitteeMembers(committeeMembers);
       }
        return entity;
    }

    public CommitteeResponsePojo toResponse(Committee committee){
        CommitteeResponsePojo responsePojo = new CommitteeResponsePojo();
        responsePojo.setId(committee.getId());
        responsePojo.setCommitteeName(committee.getCommitteeName());
        responsePojo.setCommitteeNameNp(committee.getCommitteeNameNp());
        return responsePojo;
    }

    public List<CommitteeResponsePojo> toResponses(List<Committee> committees){
        return committees.parallelStream().map(this::toResponse).collect(Collectors.toList());
    }
}
