package com.gerp.tms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.StatusDecisionPojo;
import com.gerp.tms.pojo.request.PhaseDeactivatePojo;
import com.gerp.tms.pojo.request.ProjectDynamicProgressStatusRequestPojo;
import com.gerp.tms.pojo.request.ProjectRequestPojo;
import com.gerp.tms.pojo.response.MemberDetailsResponsePojo;
import com.gerp.tms.pojo.response.MemberWiseProjectResponsePojo;
import com.gerp.tms.pojo.response.ProjectPhasePojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;

import java.util.ArrayList;
import java.util.List;

/*
 * @project gerp-main
 * @author Diwakar

 */

public interface ProjectService {
    ProjectResponsePojo save(ProjectRequestPojo projectPojo);

    ProjectResponsePojo update(ProjectRequestPojo projectPojo);

    List<ProjectResponsePojo> getAllProject(String sortBy, String sortByOrder, String status, Boolean isCompleted, Boolean isActive);



    ProjectResponsePojo getProjectById(Integer id);


    void deleteProject(Integer id);

    ProjectResponsePojo updateStatus(StatusDecisionPojo statusDecisionPojo);

    void addProjectTaskStatus(ProjectDynamicProgressStatusRequestPojo progressStatusRequestPojo);

    ProjectResponsePojo updateProjectToCompleted(Integer id);

    ProjectResponsePojo addPhaseToProject(List<ProjectPhasePojo> projectPhasePojo);

    Long bookMarkedProject(Integer id);

    MemberWiseProjectResponsePojo getMemberWiseProject(String status, Boolean isActive, Boolean isCompleted);


    List<MemberDetailsResponsePojo> getProjectMember(Integer projectId);

    List<ProjectResponsePojo> getBookedMarkedProjects();

    void removeBookMark(Integer projectId);

    Long removeProjectPhase(Integer projectId, Long phaseId);

    int removeTaskProgress(Integer projectId, Long taskProgressStatusId);

    Long deactivateProjectPhase(PhaseDeactivatePojo phaseDeactivatePojo);

    Page<ProjectResponsePojo> getReportProject(String status, Boolean isActive, Boolean isCompleted, int limit, int page, Boolean programActivity);
}
