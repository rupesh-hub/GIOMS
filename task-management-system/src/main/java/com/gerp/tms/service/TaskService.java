package com.gerp.tms.service;

import com.gerp.tms.pojo.StatusDecisionPojo;
import com.gerp.tms.pojo.TaskRatingPojo;
import com.gerp.tms.pojo.request.TaskMemberRequestPojo;
import com.gerp.tms.pojo.request.TaskRequestPojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import com.gerp.tms.pojo.request.TaskStatusLogRequestPojo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskService {
    TaskResponsePojo saveTask(TaskRequestPojo taskRequestPojo);

    TaskResponsePojo updateTask(TaskRequestPojo taskRequestPojo);

    List<TaskResponsePojo> getAllTask(String status, Boolean isActive, Integer projectId);

    TaskResponsePojo getTaskById(Long id);

    TaskResponsePojo updateTaskStatusLog(TaskStatusLogRequestPojo taskStatusLogRequestPojo);

    TaskResponsePojo updateTaskStatusDecision(StatusDecisionPojo statusDecisionPojo);

    long addRating(TaskRatingPojo taskRatingPojo);

    long updateRating(int taskRating, long taskId);

    TaskRatingPojo getTaskRatingById(Long taskId);

    long addTaskMember(TaskMemberRequestPojo taskMemberRequestPojo);

    Long deleteTask(Long taskId);

    List<TaskResponsePojo> getAllTaskForEmployee(String employeePisCode, LocalDate fromDate, LocalDate toDate);

    List<TaskResponsePojo> getReportProjectTaskList(Long projectId);
}
