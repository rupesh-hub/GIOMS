package com.gerp.tms.mapper;

import com.gerp.tms.pojo.response.TaskResponsePojo;
import com.gerp.tms.pojo.document.DocumentPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@Component
@Mapper
public interface TaskMapper {

    @Select("Select document_id, document_name from task_document_details where task_id = #{id}")
    List<DocumentPojo> getDocumentList(@Param("id") Long id);

    List<TaskResponsePojo> getTaskList(@Param("status") String status, @Param("isActive") Boolean isActive,@Param("projectId") Integer projectId);

    TaskResponsePojo getTaskById(@Param("id") Long id);

    @Select("select tps.status_name from task_progress_status tps inner join task t on t.progress_status_id = tps.id where t.id = #{taskId}")
    String getTaskProgressStatus(@Param("taskId") long taskId);

    @Select("select is_member from task_member where member_id = #{psCode} and task_id = #{taskId}")
    Boolean getAdminMember(@Param("psCode") String psCode,@Param("taskId") Long taskId);

    List<TaskResponsePojo> getAllTaskForEmployee(@Param("employeePisCode") String employeePisCode, @Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);

    List<TaskResponsePojo> getReportProjectTaskList(@Param("projectId") Long projectId,@Param("pisCode") String pisCode);
}
