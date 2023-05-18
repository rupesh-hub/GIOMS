package com.gerp.tms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.tms.pojo.response.OfficeWiseProjectResponsePojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Mapper
public interface ProjectMapper {

    List<ProjectResponsePojo> getAllProject(@Param("status") String status, @Param("isCompleted") Boolean isCompleted, @Param("isActive") Boolean isActive);

    ProjectResponsePojo getProjectDetail(Integer id);

    List<OfficeWiseProjectResponsePojo> getOfficeWiseProject(@Param("officeId") String officeId);

    List<TaskResponsePojo> getProjectTask(@Param("projectId") int projectId);

    List<ProjectResponsePojo> getMemberWiseProject(@Param("status") String status, @Param("isActive") Boolean isActive,@Param("isCompleted") Boolean isCompleted,@Param("memberId") String memberId);

    List<ProjectResponsePojo> getBookedMarkedProjects(@Param("loginEmployeeCode") String loginEmployeeCode);

    List<TaskResponsePojo> getPhaseTask(@Param("phaseId") Long phaseId,@Param("projectId") int projectId);

    Page<ProjectResponsePojo> getReportProject(Page<ProjectResponsePojo> page1, @Param("status") String status, @Param("isActive") Boolean isActive, @Param("isCompleted") Boolean isCompleted, @Param("memberId") String memberId,@Param("programActivity") Boolean programActivity);
}
