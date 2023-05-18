package com.gerp.tms.converter;

import com.gerp.tms.pojo.request.PhaseRequestPojo;
import com.gerp.tms.pojo.response.PhaseResponsePojo;
import com.gerp.tms.model.phase.Phase;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PhaseConverter {

    public Phase toEntity(PhaseRequestPojo phaseRequestPojo){
        Phase entity = new Phase();
        return toEntity(phaseRequestPojo,entity);
    }

    private Phase toEntity(PhaseRequestPojo phaseRequestPojo, Phase entity) {
        entity.setPhaseName(phaseRequestPojo.getPhaseName().toUpperCase());
        entity.setPhaseNameNp(phaseRequestPojo.getPhaseNameNp());
        return entity;
    }

    public PhaseResponsePojo toResponse(Phase phase){
        PhaseResponsePojo responsePojo = new PhaseResponsePojo();
        responsePojo.setId(phase.getId());
        responsePojo.setPhaseName(phase.getPhaseName());
        responsePojo.setPhaseNameNp(phase.getPhaseNameNp());
        responsePojo.setActive(phase.getActive());
        return responsePojo;
    }

    public List<PhaseResponsePojo> toResponses(List<Phase> phases){
        return phases.parallelStream().map(this::toResponse).collect(Collectors.toList());
    }
}
