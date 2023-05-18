package com.gerp.tms.converter;


import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.constant.Status;
import com.gerp.tms.model.committee.Committee;
import com.gerp.tms.model.project.Project;
import com.gerp.tms.pojo.request.ProjectRequestPojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import com.gerp.tms.repo.CommitteeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;

/*
 * @project gerp-main
 * @author jitesh

 */
@Component
public class ProjectConverter {
    private final CommitteeRepository committeeRepository;
    private final CommitteeConverter committeeConverter;


    public ProjectConverter(CommitteeRepository committeeRepository, CommitteeConverter committeeConverter) {
        this.committeeRepository = committeeRepository;
        this.committeeConverter = committeeConverter;
    }

    public  Project toEntity(ProjectRequestPojo dto) {
        Project entity = new Project();
        return toEntity(dto, entity);
    }

    public Project toEntity(ProjectRequestPojo dto, Project entity) {
       entity.setCode(getCode(dto.getProjectName()));
       entity.setEndDateNp(dto.getEndDateNp());
       entity.setStatus(Status.APPROVED.getValueEnglish());
       entity.setDescription(dto.getDescription());
       entity.setIsResponded(false);
       entity.setPriority(dto.getPriority());
       entity.setProjectName(dto.getProjectName());
       entity.setStartDate(dto.getStartDate());
       entity.setOfficeId(dto.getOfficeId().toString());
       entity.setStartDateNp(dto.getStartDateNp());
        entity.setEndDate(dto.getEndDate());
        entity.setColorSchema(dto.getColorSchema());
        entity.setIsCompleted(false);
        addCommittee(dto, entity);
        return entity;
    }

    public String getCode(String name) {
        Random random = new Random();
        return name.substring(0,3)+"-"+random.nextInt(100000);
    }

    public void addCommittee(ProjectRequestPojo dto, Project entity) {
        if (dto.getCommitteeId() != null){
            Optional<Committee> committeeOptional =committeeRepository.findById(dto.getCommitteeId());
            if (!committeeOptional.isPresent()){
                throw new RuntimeException(ErrorMessages.COMMITTEE_NOT_FOUND.getMessage());
            }
            entity.setCommittee(committeeOptional.orElse(new Committee()));
            entity.setIsCommittee(true);
        }else {
            entity.setIsCommittee(false);
        }
    }


    public ProjectResponsePojo toResponse(Project project) {
        ProjectResponsePojo projectResponsePojo = new ProjectResponsePojo();
        projectResponsePojo.setId(project.getId());
        projectResponsePojo.setCode(project.getCode());
        projectResponsePojo.setEndDateNp(project.getEndDateNp());
        projectResponsePojo.setStatus(project.getStatus());
        projectResponsePojo.setDescription(project.getDescription());
        projectResponsePojo.setIsCommittee(project.getIsCommittee());
        projectResponsePojo.setIsResponded(project.getIsResponded());
        projectResponsePojo.setPriority(project.getPriority());
        projectResponsePojo.setProjectName(project.getProjectName());
        projectResponsePojo.setStartDate(project.getStartDate());
        projectResponsePojo.setOfficeId(project.getOfficeId());
        projectResponsePojo.setStartDateNp(project.getStartDateNp());
        projectResponsePojo.setEndDate(project.getEndDate());
        projectResponsePojo.setIsCompleted(project.getIsCompleted());
        projectResponsePojo.setIsCommittee(project.getIsCommittee());
        projectResponsePojo.setColorSchema(project.getColorSchema());
        projectResponsePojo.setCommittee(project.getCommittee()!=null?committeeConverter.toResponse(project.getCommittee()):null);
        return projectResponsePojo;
    }
}
