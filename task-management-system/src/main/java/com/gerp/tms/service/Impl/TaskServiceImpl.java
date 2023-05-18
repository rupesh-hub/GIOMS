package com.gerp.tms.service.Impl;

import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.tms.Helper.HelperUtil;
import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.constant.Status;
import com.gerp.tms.converter.TaskConverter;
import com.gerp.tms.mapper.ProjectPhaseMapper;
import com.gerp.tms.mapper.TaskProgressStatusMapper;
import com.gerp.tms.mapper.TaskMapper;
import com.gerp.tms.model.project.Project;
import com.gerp.tms.model.project.ProjectPhase;
import com.gerp.tms.model.task.*;
import com.gerp.tms.pojo.StatusDecisionPojo;
import com.gerp.tms.pojo.TaskMembersPojo;
import com.gerp.tms.pojo.TaskRatingPojo;
import com.gerp.tms.pojo.request.TaskMemberRequestPojo;
import com.gerp.tms.pojo.request.TaskRequestPojo;
import com.gerp.tms.pojo.response.TaskResponsePojo;
import com.gerp.tms.pojo.request.TaskStatusLogRequestPojo;
import com.gerp.tms.pojo.document.DocumentMasterResponsePojo;
import com.gerp.tms.pojo.document.DocumentSavePojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import com.gerp.tms.repo.*;
import com.gerp.tms.service.TaskService;
import com.gerp.tms.token.TokenProcessorService;
import com.gerp.tms.util.DocumentUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskConverter taskConverter;
    private final TaskMapper taskMapper;
    private final TaskProgressStatusMapper taskProgressStatusMapper;
    private final TaskProgressStatusRepository taskProgressStatusRepository;
    private final TaskProgressStatusLogRepository taskProgressStatusLogRepository;
    private final ProjectRepository projectRepository;
    private final TaskRatingRepository taskRatingRepository;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final TaskMemberRepository taskMemberRepository;
    private final ProjectPhaseMapper projectPhaseMapper;


    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private DocumentUtil documentUtil;

    public TaskServiceImpl(TaskRepository taskRepository, TaskConverter taskConverter, TaskMapper taskMapper,
                           TaskProgressStatusMapper taskProgressStatusMapper, TaskProgressStatusRepository taskProgressStatusRepository,
                           TaskProgressStatusLogRepository taskProgressStatusLogRepository, ProjectRepository projectRepository,
                           TaskRatingRepository taskRatingRepository, EmployeeDetailsProxy employeeDetailsProxy, TaskMemberRepository taskMemberRepository, ProjectPhaseMapper projectPhaseMapper) {
        this.taskRepository = taskRepository;
        this.taskConverter = taskConverter;
        this.taskMapper = taskMapper;
        this.taskProgressStatusMapper = taskProgressStatusMapper;
        this.taskProgressStatusRepository = taskProgressStatusRepository;
        this.taskProgressStatusLogRepository = taskProgressStatusLogRepository;
        this.projectRepository = projectRepository;
        this.taskRatingRepository = taskRatingRepository;
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.taskMemberRepository = taskMemberRepository;
        this.projectPhaseMapper = projectPhaseMapper;
    }

    @Override
    public TaskResponsePojo saveTask(TaskRequestPojo taskRequestPojo) {
        validateProjectExistance(taskRequestPojo);
        Task task =taskConverter.toEntity(taskRequestPojo);
        processDocument(taskRequestPojo.getDocument(),task);
        addMemberToTask(task,taskRequestPojo.getMembers());
//        taskRepository.save(task);
        TaskProgressStatusLog taskProgressStatusLog = new TaskProgressStatusLog();
        TaskProgressStatus taskProgressStatus = taskProgressStatusMapper.getFirstTaskProgressStatusOfProject(task.getProjectId());
        logTaskProgress(taskProgressStatus.getId(),task.getProjectId(),taskProgressStatusLog,task, null);
//        taskRepository.save(task);
        return taskConverter.toResponse(task);
    }

    private void validateProjectExistance(TaskRequestPojo taskRequestPojo) {
        Project project = getProject(taskRequestPojo.getProjectId());
        ProjectPhase  projectPhase = projectPhaseMapper.getProjectIdAndPhaseId(taskRequestPojo.getProjectId(),taskRequestPojo.getPhaseId());

        if (projectPhase == null || projectPhase.getId() == null){
            throw new RuntimeException(ErrorMessages.PROJECT_PHASE_NOT_FOUND.getMessage());
        }

        if (!project.getStatus().equalsIgnoreCase(Status.APPROVED.getValueEnglish())){
            throw new RuntimeException(ErrorMessages.PROJECT_IS_NOT_APPROVED.getMessage());
        }
    }

    private Project getProject(Integer id) {
        Optional<Project> projectOptional = projectRepository.findById(id);
        if (!projectOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.PROJECT_NOT_FOUND.getMessage());
        }
        return projectOptional.orElse(new Project());
    }

    @Override
    public TaskResponsePojo updateTask(TaskRequestPojo taskRequestPojo) {
        if (taskRequestPojo.getId() == null){
            throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
        }
        Task task = getTask(taskRequestPojo.getId());
//        if (!taskDb.getTaskStatus().equalsIgnoreCase(Status.PENDING.getValueEnglish())){
//            throw new RuntimeException(ErrorMessages.TASK_NOT_ALLOWED_TO_UPDATE.getMessage());
//        }
        String psCode = HelperUtil.getLoginEmployeeCode();
        if (!task.getCreatedBy().equals(psCode) && !(taskMapper.getAdminMember(psCode,task.getId()) != null && taskMapper.getAdminMember(psCode, task.getId()))){
            throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_UPDATE.getMessage());
        }

        task.setTaskName(taskRequestPojo.getTaskName());
        task.setTaskDescription(taskRequestPojo.getTaskDescription());
        task.setEndDate(taskRequestPojo.getEndDate());
        task.setEndDateNp(taskRequestPojo.getEndDateNp());
        task.setStartDate(taskRequestPojo.getStartDate());
        task.setStartDateNp(taskRequestPojo.getStartDateNp());
        task.setPriority(taskRequestPojo.getPriority());
        taskRepository.save(task);
        return taskConverter.toResponse(task);
    }

    @Override
    public List<TaskResponsePojo> getAllTask(String status, Boolean isActive, Integer projectId) {
        List<TaskResponsePojo> taskResponsePojos = taskMapper.getTaskList(status,isActive,projectId);
       taskResponsePojos = taskResponsePojos.stream().peek(task -> {
            List<TaskMembersPojo> taskMembersPojoList = task.getTaskMembers().stream().map(this::getEmployeeName).collect(Collectors.toList());
            task.setTaskMembers(taskMembersPojoList);
       }).collect(Collectors.toList());
        return taskResponsePojos;
    }

    private TaskMembersPojo getEmployeeName(TaskMembersPojo taskMembersPojo){
        EmployeeMinimalPojo employeeMinimalPojo = employeeDetailsProxy.getEmployeeDetailMinimal((taskMembersPojo.getMemberId()+"").trim());
        TaskMembersPojo memberDetailsResponsePojo = new TaskMembersPojo();
        if (employeeMinimalPojo != null){
            memberDetailsResponsePojo.setMemberId(taskMembersPojo.getMemberId());
            memberDetailsResponsePojo.setMemberNameEn(employeeMinimalPojo.getEmployeeNameEn());
            memberDetailsResponsePojo.setMemberNameNp(employeeMinimalPojo.getEmployeeNameNp());
            memberDetailsResponsePojo.setAdmin(taskMembersPojo.isAdmin());
            return memberDetailsResponsePojo;
        }
        return taskMembersPojo;
    }

    @Override
    public TaskResponsePojo getTaskById(Long id) {
        TaskResponsePojo taskResponsePojo = taskMapper.getTaskById(id);
       if (taskResponsePojo.getTaskMembers() != null && taskResponsePojo.getTaskMembers().size() >0){
           List<TaskMembersPojo> taskMembersPojoList = taskResponsePojo.getTaskMembers().stream().map(this::getEmployeeName).collect(Collectors.toList());
           taskResponsePojo.setTaskMembers(taskMembersPojoList);
       }

        return taskResponsePojo;
    }

    @Override
    public TaskResponsePojo updateTaskStatusLog(TaskStatusLogRequestPojo dto) {
        saveTaskProgressStatusLog(dto.getTaskId(),dto.getTaskProgressStatusId(),dto.getProjectId(),dto.getRemarks());
        return taskMapper.getTaskById(dto.getTaskId());
    }

    private void saveTaskProgressStatusLog(Long taskId, Long taskProgressStatusId, Integer projectId, String remarks) {
        TaskProgressStatusLog taskProgressStatusLog = new TaskProgressStatusLog();
        Task task = getTask(taskId);
        if (!task.getProjectId().equals(projectId)){
            throw new RuntimeException(ErrorMessages.TASK_NOT_FOUND.getMessage());
        }
        logTaskProgress(taskProgressStatusId, projectId, taskProgressStatusLog, task,remarks);
        if (taskProgressStatusRepository.findById(taskProgressStatusId).orElse(new TaskProgressStatus()).getStatusName().equalsIgnoreCase(Status.COMPLETED.getValueEnglish())){
            task.setCompletedDate(LocalDate.now());
        }
        taskRepository.save(task);
    }

    private void logTaskProgress(Long taskProgressStatusId, Integer projectId, TaskProgressStatusLog taskProgressStatusLog, Task task, String remarks) {
        DynamicProgressStatus dynamicProgressStatus = getDynamicProgressStatus(taskProgressStatusId, projectId);
        task.setProgressStatus(dynamicProgressStatus.getTaskProgressStatus().getId());
        taskProgressStatusLog.setDynamicProgressStatus(dynamicProgressStatus);
        taskProgressStatusLog.setTask(task);
        taskProgressStatusLog.setRemarks(remarks);
        taskProgressStatusLogRepository.save(taskProgressStatusLog);
    }

    private DynamicProgressStatus getDynamicProgressStatus(Long taskProgressStatusId, Integer projectId) {
        return taskProgressStatusMapper.getDynamicTaskProgress(projectId, taskProgressStatusId);
    }


    private Task getTask(long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.TASK_NOT_FOUND.getMessage());
        }
        return  taskOptional.orElse(new Task());
    }

    @Override
    public TaskResponsePojo updateTaskStatusDecision(StatusDecisionPojo statusDecisionPojo) {
       Task task = getTask(statusDecisionPojo.getId());
       task.setIsResponded(true);
       task.setResponseBy(statusDecisionPojo.getApprovedBy());
       task.setTaskStatus(statusDecisionPojo.getStatus().getValueEnglish());

       if (statusDecisionPojo.getStatus().equals(Status.APPROVED)){
           TaskProgressStatusLog taskProgressStatusLog = new TaskProgressStatusLog();
           TaskProgressStatus taskProgressStatus = taskProgressStatusMapper.getFirstTaskProgressStatusOfProject(task.getProjectId());
           logTaskProgress(taskProgressStatus.getId(),task.getProjectId(),taskProgressStatusLog,task, null);
       }
        taskRepository.save(task);
       return taskConverter.toResponse(task);
    }

    @Override
    public long addRating(TaskRatingPojo taskRatingPojo) {
        String status = taskMapper.getTaskProgressStatus(taskRatingPojo.getTaskId());
        validateTaskRating(taskRatingPojo, status);
        TaskRating taskRating = new TaskRating();
        taskRating.setRating(taskRatingPojo.getRating());
        taskRating.setTaskId(taskRatingPojo.getTaskId());
        taskRating.setDescription(taskRatingPojo.getDescription());
        taskRatingRepository.save(taskRating);
        return taskRatingPojo.getTaskId();
    }

    private void validateTaskRating(TaskRatingPojo taskRatingPojo, String status) {
        if (status == null || status.equalsIgnoreCase("")){
           throw  new RuntimeException(ErrorMessages.TASK_NOT_FOUND.getMessage());
        }
        if (!status.equalsIgnoreCase(Status.COMPLETED.getValueEnglish())){
            throw  new RuntimeException(ErrorMessages.TASK_NOT_COMPLETED.getMessage());
        }
        if (taskRatingPojo.getRating() >5){
            throw new RuntimeException(ErrorMessages.RATING_NOT_MATCH.getMessage());
        }
        if (taskRatingRepository.findByTaskId(taskRatingPojo.getTaskId()).isPresent()){
            throw new RuntimeException(ErrorMessages.TASK_ALREADY_HAS_RATING.getMessage());
        }
    }

    @Override
    public long updateRating(int rating, long taskId) {
        TaskRating taskRating = getTaskRating(taskId);
        if (rating >5){
            throw new RuntimeException(ErrorMessages.RATING_NOT_MATCH.getMessage());
        }
        taskRating.setRating(rating);
        taskRatingRepository.save(taskRating);
        return taskId;
    }

    private TaskRating getTaskRating(long taskId) {
        Optional<TaskRating> taskRatingOptional = taskRatingRepository.findByTaskId(taskId);
        if (!taskRatingOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.TASK_RATING_NOT_FOUND.getMessage());
        }
        return taskRatingOptional.orElse(new TaskRating());
    }

    @Override
    public TaskRatingPojo getTaskRatingById(Long taskId) {
        TaskRating taskRating = getTaskRating(taskId);
        TaskRatingPojo taskRatingPojo = new TaskRatingPojo();
        taskRatingPojo.setRating(taskRating.getRating());
        taskRatingPojo.setDescription(taskRating.getDescription());
        return taskRatingPojo;
    }

    @Override
    public long addTaskMember(TaskMemberRequestPojo taskMemberRequestPojo) {
        Task task = getTask(taskMemberRequestPojo.getTaskId());
        if (task.getTaskMembers() == null){
            addMemberToTask(task,taskMemberRequestPojo.getTaskMembers());
            taskRepository.save(task);
            return task.getId();
        }
        task.getTaskMembers().forEach(member->{
            List<TaskMembersPojo> membersPojoList = taskMemberRequestPojo.getTaskMembers().parallelStream().filter(input -> input.getMemberId().equalsIgnoreCase(member.getMemberId())).collect(Collectors.toList());
            if (membersPojoList.size() == 0){
                taskMemberRepository.deleteById(member.getId());
            }else{
                 int index = taskMemberRequestPojo.getTaskMembers().indexOf(membersPojoList.get(0));
                 taskMemberRequestPojo.getTaskMembers().get(index).setId(member.getId());
            }
        });
        if (taskMemberRequestPojo.getTaskMembers().size() > 0){
            addMemberToTask(task,taskMemberRequestPojo.getTaskMembers());
            taskRepository.save(task);
        }
      return task.getId();
    }

    @Override
    public Long deleteTask(Long taskId) {
        Task task = getTask(taskId);
        Project project = getProject(task.getProjectId());
        if (!project.getCreatedBy().equalsIgnoreCase(HelperUtil.getLoginEmployeeCode())){
            throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_DELETE.getMessage());
        }
        taskRepository.delete(task);
        return task.getId();
    }

    @Override
    public List<TaskResponsePojo> getAllTaskForEmployee(String employeePisCode, LocalDate fromDate, LocalDate toDate) {
        return taskMapper.getAllTaskForEmployee(employeePisCode,fromDate,toDate);
    }

    @Override
    public List<TaskResponsePojo> getReportProjectTaskList(Long projectId) {
        return taskMapper.getReportProjectTaskList(projectId,tokenProcessorService.getPisCode());
    }


    private void addMemberToTask(Task task, List<TaskMembersPojo> members){
        if(members != null){
            List<TaskMember> taskMembers = new ArrayList<>();
             members.forEach(member->{
                TaskMember taskMember = new TaskMember();
                if (member.getId() != null){
                    taskMember.setId(member.getId());
                }
                taskMember.setMemberId(member.getMemberId());
                taskMember.setIsMember(member.isAdmin());
                taskMembers.add(taskMember);
            });
             task.setTaskMembers(taskMembers);
        }
    }
    private void processDocument(List<MultipartFile> document, Task task) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tags(Arrays.asList("task_document"))
                        .type("1")
                        .build(),
                document
        );
        if(pojo!=null){
            task.setTaskDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x-> new TaskDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }
}
