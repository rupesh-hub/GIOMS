package com.gerp.tms.converter;

import com.gerp.tms.pojo.request.TaskProgressStatusRequestPojo;
import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import com.gerp.tms.model.task.TaskProgressStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskProgressStatusConverter {

    public TaskProgressStatus toEntity(TaskProgressStatusRequestPojo taskProgressStatusRequestPojo){
        TaskProgressStatus entity = new TaskProgressStatus();
        return toEntity(taskProgressStatusRequestPojo,entity);
    }

    private TaskProgressStatus toEntity(TaskProgressStatusRequestPojo taskProgress, TaskProgressStatus entity) {
        entity.setStatusName(taskProgress.getStatusName().toUpperCase());
        entity.setStatusNameNp(taskProgress.getStatusNameNp());
        entity.setDeleteAble(true);
        return entity;
    }

    public TaskProgressStatusResponsePojo toResponse(TaskProgressStatus taskProgressStatus){
        TaskProgressStatusResponsePojo responsePojo = new TaskProgressStatusResponsePojo();
        responsePojo.setId(taskProgressStatus.getId());
        responsePojo.setStatusName(taskProgressStatus.getStatusName());
        responsePojo.setStatusNameNp(taskProgressStatus.getStatusNameNp());
        responsePojo.setActive(taskProgressStatus.getActive());
        responsePojo.setDeletable(taskProgressStatus.getDeleteAble());
        return responsePojo;
    }

    public List<TaskProgressStatusResponsePojo> toResponses(List<TaskProgressStatus> taskProgressStatuses){
        return taskProgressStatuses.parallelStream().map(this::toResponse).collect(Collectors.toList());
    }
}
