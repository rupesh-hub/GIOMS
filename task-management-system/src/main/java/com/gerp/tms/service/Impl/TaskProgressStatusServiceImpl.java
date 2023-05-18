package com.gerp.tms.service.Impl;

import com.gerp.tms.Helper.HelperUtil;
import com.gerp.tms.mapper.TaskProgressStatusMapper;
import com.gerp.tms.pojo.ProgressUpdatePojo;
import com.gerp.tms.pojo.request.TaskProgressStatusRequestPojo;
import com.gerp.tms.pojo.response.TaskProgressStatusResponsePojo;
import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.converter.TaskProgressStatusConverter;
import com.gerp.tms.model.task.TaskProgressStatus;
import com.gerp.tms.pojo.TaskProgressStatusWithTaskDetailsResponsePojo;
import com.gerp.tms.repo.TaskProgressStatusRepository;
import com.gerp.tms.service.TaskProgressStatusService;
import com.gerp.tms.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskProgressStatusServiceImpl implements TaskProgressStatusService {

    private final  TaskProgressStatusRepository taskProgressStatusRepository;
    private final TaskProgressStatusConverter taskProgressStatusConverter;
    private final TaskProgressStatusMapper taskProgressStatusMapper;
    private final TokenProcessorService tokenProcessorService;

    public TaskProgressStatusServiceImpl(TaskProgressStatusRepository taskProgressStatusRepository, TaskProgressStatusConverter taskProgressStatusConverter, TaskProgressStatusMapper taskProgressStatusMapper, TokenProcessorService tokenProcessorService) {
        this.taskProgressStatusRepository = taskProgressStatusRepository;
        this.taskProgressStatusConverter = taskProgressStatusConverter;
        this.taskProgressStatusMapper = taskProgressStatusMapper;
        this.tokenProcessorService = tokenProcessorService;
    }

    @Override
    public Long addTaskProgressStatus(TaskProgressStatusRequestPojo taskProgressStatusRequestPojo) {
        Optional<TaskProgressStatus> checkTaskProgress = taskProgressStatusRepository.findByStatusNameAndCreatedBy(taskProgressStatusRequestPojo.getStatusName().toUpperCase(),Long.parseLong(tokenProcessorService.getPisCode()));
        if (checkTaskProgress.isPresent()){
            throw new RuntimeException(ErrorMessages.TASK_PROGRESS_ALREADY_EXIST.getMessage());
        }
        TaskProgressStatus taskProgressStatus = taskProgressStatusConverter.toEntity(taskProgressStatusRequestPojo);
        taskProgressStatusRepository.save(taskProgressStatus);
        return taskProgressStatus.getId();
    }

    @Override
    public Long updateTaskProgressStatus(TaskProgressStatusRequestPojo taskProgressStatusRequestPojo) {
        if (taskProgressStatusRequestPojo.getId() == null){
            throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
        }
//       TaskProgressStatus dbTaskProgressStatus =  getTaskProgressStatus(taskProgressStatusRequestPojo.getId());
        TaskProgressStatus taskProgressStatus = taskProgressStatusConverter.toEntity(taskProgressStatusRequestPojo);
        taskProgressStatus.setId(taskProgressStatusRequestPojo.getId());
        taskProgressStatusRepository.save(taskProgressStatus);
        return taskProgressStatus.getId();
    }

    @Override
    public TaskProgressStatusResponsePojo getTaskProgressDetails(Long id) {
        return taskProgressStatusConverter.toResponse(getTaskProgressStatus(id));
    }

    @Override
    public List<TaskProgressStatusResponsePojo> getTaskProgressStatus() {
        return taskProgressStatusConverter.toResponses(taskProgressStatusRepository.findAllByCreatedBy(tokenProcessorService.getPisCode()));
    }

    @Override
    public void deleteTaskProgressStatus(Long id) {
        TaskProgressStatus taskProgressStatus = getTaskProgressStatus(id);
        taskProgressStatus.setActive(!taskProgressStatus.getActive());
        taskProgressStatusRepository.save(taskProgressStatus);
    }

    @Override
    public List<TaskProgressStatusWithTaskDetailsResponsePojo> getStatusWiseTask(String status, Integer projectId, Date startDate, Date endDate, String assigneeId, Long phaseId) {
        return taskProgressStatusMapper.getStatusWiseTask(status, projectId, startDate,endDate,assigneeId,phaseId);
    }

    private TaskProgressStatus getTaskProgressStatus(Long id) {
        Optional<TaskProgressStatus> taskProgressStatusOptional = taskProgressStatusRepository.findById(id);
        if (!taskProgressStatusOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.TASK_PROGRESS_STATUS_NOT_FOUND.getMessage());
        }
        return taskProgressStatusOptional.orElse(new TaskProgressStatus());
    }

}
