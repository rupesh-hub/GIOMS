package com.gerp.tms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.authorization.*;
import com.gerp.tms.pojo.response.ProjectCountPojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;

import java.time.LocalDate;
import java.util.List;

public interface ProgramService {
//    List<AuthorizationActivityPojo> getProgramsList(LocalDate fiscalYear, String activityName);

    Integer addToProject(ActivityTOProject activityId);

    Page<ActivityLevelPojo> getActivties(String fiscalYear, String activityName, String sortByOrder, String sortBy, String filter, int page, int limit, String accountCode, String filterByHead);

    Integer addData(Temp activityTOProject);

    ActivityLevelPojo getActivtiesById(Integer id);

    HeaderOfficeDetailsPojo getHeadingDetails();

    ProjectCountPojo getProjectCountForDashboard(String fiscalYear, String responsibleUnit, String months);

    Page<ProjectResponsePojo> getProjectList(String fiscalYear, int limit, int page);
}
