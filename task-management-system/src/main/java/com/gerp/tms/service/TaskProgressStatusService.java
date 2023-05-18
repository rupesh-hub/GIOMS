package com.gerp.tms.service;

import com.gerp.tms.pojo.ProgressUpdatePojo;
import com.gerp.tms.pojo.request.TaskProgressStatusRequestPojo;
import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import com.gerp.tms.pojo.TaskProgressStatusWithTaskDetailsResponsePojo;


import java.sql.Date;
import java.util.List;

public interface TaskProgressStatusService {
    Long addTaskProgressStatus(TaskProgressStatusRequestPojo taskProgressStatusRequestPojo);

    Long updateTaskProgressStatus(TaskProgressStatusRequestPojo taskProgressStatusRequestPojo);

    TaskProgressStatusResponsePojo getTaskProgressDetails(Long id);

    List<TaskProgressStatusResponsePojo> getTaskProgressStatus();

    void deleteTaskProgressStatus(Long id);

    List<TaskProgressStatusWithTaskDetailsResponsePojo> getStatusWiseTask(String status, Integer projectId, Date startDate, Date endDate, String assigneeId, Long phaseId);
}
