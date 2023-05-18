package com.gerp.tms.mapper;

import com.gerp.tms.model.task.DynamicProgressStatus;
import com.gerp.tms.model.task.TaskProgressStatus;
import com.gerp.tms.pojo.TaskProgressStatusWithTaskDetailsResponsePojo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;


import java.sql.Date;
import java.util.List;

@Component
@Mapper
public interface TaskProgressStatusMapper {


    DynamicProgressStatus getDynamicTaskProgress(@Param("projectId") Integer projectId,@Param("taskProgressStatusId") Long taskProgressStatusId);

    @Select("select * from  task_progress_status tps inner join  dynamic_progress_status dps on dps.task_progress_status_id = tps.id  where dps.project_id = #{projectId}::INTEGER and dps.order_status = 1")
    TaskProgressStatus getFirstTaskProgressStatusOfProject(@Param("projectId")Integer projectId);


    List<TaskProgressStatusWithTaskDetailsResponsePojo> getStatusWiseTask(@Param("status") String status, @Param("projectId") Integer projectId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("assigneeId") String assigneeId,@Param("phaseId") Long phaseId);

    @Select("select * from dynamic_progress_status dps left join task_progress_status tps on tps.id = dps.task_progress_status_id  where tps.status_name = 'COMPLETED' and dps.project_id =#{projectId}::INTEGER ")
    DynamicProgressStatus getStatusCompletedId(@Param("projectId") Integer projectId);


    @Update("update dynamic_progress_status set order_status = #{orderStatus}::INTEGER where id = #{id}::INTEGER")
    void updateDynamicProgressStatus(@Param("id") Integer id, @Param("orderStatus") int orderStatus);
}
