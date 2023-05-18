package com.gerp.tms.converter;

import com.gerp.tms.constant.Status;
import com.gerp.tms.mapper.TaskMapper;
import com.gerp.tms.model.task.Task;
import com.gerp.tms.pojo.request.TaskRequestPojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class TaskConverter {

    private final TaskMapper taskMapper;

    public TaskConverter(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    public Task toEntity(TaskRequestPojo taskRequestPojo){
        return toEntity(new Task(),taskRequestPojo);
    }

    private Task toEntity(Task task, TaskRequestPojo taskRequestPojo){
        task.setTaskName(taskRequestPojo.getTaskName());
        task.setTaskDescription(taskRequestPojo.getTaskDescription());
        task.setCode(getCode(taskRequestPojo.getTaskName()));
        task.setEndDate(taskRequestPojo.getEndDate());
        task.setEndDateNp(taskRequestPojo.getEndDateNp());
        task.setStartDate(taskRequestPojo.getStartDate());
        task.setStartDateNp(taskRequestPojo.getStartDateNp());
        task.setPriority(taskRequestPojo.getPriority());
        task.setPhaseId(taskRequestPojo.getPhaseId());
        task.setProjectId(taskRequestPojo.getProjectId());
        task.setTaskStatus(Status.APPROVED.getValueEnglish());
        task.setIsResponded(false);
        return task;
    }

    public String getCode(String name) {
        Random random = new Random();
        return name.substring(0,3)+"-"+random.nextInt(100000);
    }
    public TaskResponsePojo toResponse(Task task){
        TaskResponsePojo taskResponsePojo = new TaskResponsePojo();
        taskResponsePojo. setTaskName(task.getTaskName());
        taskResponsePojo.setId(task.getId());
        taskResponsePojo.setTaskDescription(task.getTaskDescription());
        taskResponsePojo.setCode(task.getCode());
        taskResponsePojo.setEndDate(task.getEndDate());
        taskResponsePojo.setEndDateNp(task.getEndDateNp());
        taskResponsePojo.setStartDate(task.getStartDate());
        taskResponsePojo.setEndDateNp(task.getEndDateNp());
        taskResponsePojo.setPriority(task.getPriority());
        taskResponsePojo.setIsResponded(task.getIsResponded());
        taskResponsePojo.setTaskStatus(task.getTaskStatus());
        taskResponsePojo.setResponseBy(task.getResponseBy());
        taskResponsePojo.setDocuments(taskMapper.getDocumentList(task.getId()));
        return taskResponsePojo;
    }

}
