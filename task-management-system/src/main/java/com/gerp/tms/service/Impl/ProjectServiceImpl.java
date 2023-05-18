package com.gerp.tms.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.Helper.HelperUtil;
import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.constant.Status;
import com.gerp.tms.converter.ProjectConverter;
import com.gerp.tms.mapper.ProjectMapper;
import com.gerp.tms.mapper.ProjectPhaseMapper;
import com.gerp.tms.mapper.TaskProgressStatusMapper;
import com.gerp.tms.model.authorization.ActivityLevel;
import com.gerp.tms.model.authorization.SupportOffice;
import com.gerp.tms.model.phase.Phase;
import com.gerp.tms.model.phase.PhaseMember;
import com.gerp.tms.model.project.BookMarkProject;
import com.gerp.tms.model.project.Project;
import com.gerp.tms.model.project.ProjectDocumentDetails;
import com.gerp.tms.model.project.ProjectPhase;
import com.gerp.tms.model.task.DynamicProgressStatus;
import com.gerp.tms.model.task.TaskProgressStatus;
import com.gerp.tms.pojo.StatusDecisionPojo;
import com.gerp.tms.pojo.TaskProgressStatusWithOrderStatusPojo;
import com.gerp.tms.pojo.document.DocumentMasterResponsePojo;
import com.gerp.tms.pojo.document.DocumentSavePojo;
import com.gerp.tms.pojo.request.PhaseDeactivatePojo;
import com.gerp.tms.pojo.request.ProjectDynamicProgressStatusRequestPojo;
import com.gerp.tms.pojo.request.ProjectRequestPojo;
import com.gerp.tms.pojo.response.MemberDetailsResponsePojo;
import com.gerp.tms.pojo.response.MemberWiseProjectResponsePojo;
import com.gerp.tms.pojo.response.ProjectPhasePojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import com.gerp.tms.repo.*;
import com.gerp.tms.service.ProjectService;
import com.gerp.tms.token.TokenProcessorService;
import com.gerp.tms.util.DocumentUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*
 * @project gerp-main
 * @author Diwakar

 */

@Service
@Transactional
@AllArgsConstructor
public class ProjectServiceImpl  implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectConverter projectConverter;
    private final PhaseRepository phaseRepository;
    private final ProjectMapper projectMapper;
    private final TaskProgressStatusRepository taskProgressStatusRepository;
    private final TaskProgressStatusMapper taskProgressStatusMapper;
    private final ProjectPhaseMapper projectPhaseMapper;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final BookMarkProjectRepository bookMarkProjectRepository;
    private final ProjectPhaseRepository projectPhaseRepository;
    private final DynamicProgressStatusRepository dynamicProgressStatusRepository;
    private final ActivityLevelRepo activityLevelRepo;
    private final CustomMessageSource customMessageSource;
//    private final SupportOfficeRepo supportOfficeRepo;


    @Autowired
    private TokenProcessorService tokenProcessorService;
    @Autowired
    private DocumentUtil documentUtil;



    private  ProjectPhase toProjectPhase(ProjectPhasePojo projectPhasePojo) {
        ProjectPhase projectPhase = new ProjectPhase();
        Optional<Phase> phaseOptional = phaseRepository.findById(projectPhasePojo.getPhaseId());
        if (!phaseOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.PHASE_NOT_FOUND.getMessage());
        }
        projectPhase.setPhase(phaseOptional.orElse(new Phase()));
        projectPhase.setStartDate(projectPhasePojo.getStartDate());
        projectPhase.setEndDate(projectPhasePojo.getEndDate());
        projectPhase.setStartDateNp(projectPhasePojo.getStartDateNp());
        projectPhase.setEndDateNp(projectPhasePojo.getEndDateNp());
        projectPhase.setPhaseDescription(projectPhasePojo.getPhaseDescription());
        projectPhase.setActive(true);
        if (projectPhasePojo.getMembers() != null){
            List<PhaseMember> phaseMemberList = projectPhasePojo.getMembers().stream().map(PhaseMember::new).collect(Collectors.toList());
            projectPhase.setPhaseMemberList(phaseMemberList);
        }
        return projectPhase;
    }

    @Override
    public ProjectResponsePojo save(ProjectRequestPojo projectPojo) {
        Project project;
        if (projectPojo.getRequestFromActivity() == null){
             project= projectConverter.toEntity(projectPojo);
        }else {
            project =convertActivityToProject(projectPojo);
        }
        project.setProjectPhaseList(projectPojo.getPhaseList()!=null? toProjectPhases(projectPojo):null);
        this.processDocument(projectPojo.getDocument(), project);
        project.setDynamicProgressStatuses(convertDynamicProgressStatusToList());
        projectRepository.save(project);
        return projectConverter.toResponse(project);
    }

    private Project convertActivityToProject(ProjectRequestPojo projectRequestPojo) {
        ActivityLevel activityLevel = validateProjectActivityRequest(projectRequestPojo);
        activityLevel.setIsProject(true);
        activityLevel.setAuthorizationActivityList(activityLevel.getAuthorizationActivityList().parallelStream().peek(a-> {
            List<SupportOffice> supportOfficeList = projectRequestPojo.getSupportOffices().stream().map(b -> new SupportOffice(b.getPisCode(), b.getSupportingPisOfficeCode())).collect(Collectors.toList());
            a.setTargetedOutcomes(projectRequestPojo.getTargetOutcomes());
            a.setResponsibleCode(projectRequestPojo.getResponsibleUnitCode());
            if (a.getSupportOfficeList() == null){
                a.setSupportOfficeList(supportOfficeList);
            }else {
                a.getSupportOfficeList().addAll(supportOfficeList);
            }
        }).collect(Collectors.toList()));
        activityLevelRepo.save(activityLevel);
        Project project = new Project();
        String projectName = "";
        if (activityLevel.getActivityLevelNameE()==null){
            projectName = activityLevel.getActivityLevelNameN();
        }else {
            projectName = activityLevel.getActivityLevelNameE();
        }
        project.setProjectName(projectName);
        project.setCode(projectConverter.getCode(projectName));
        project.setActivityId(projectRequestPojo.getActivityId());
        project.setColorSchema(projectRequestPojo.getColorSchema());
        project.setPriority(projectRequestPojo.getPriority());
        project.setStartDate(projectRequestPojo.getStartDate());
        project.setStartDateNp(projectRequestPojo.getStartDateNp());
        project.setEndDateNp(projectRequestPojo.getEndDateNp());
        project.setEndDate(projectRequestPojo.getEndDate());
        project.setStatus(Status.APPROVED.getValueEnglish());
        project.setDescription(projectRequestPojo.getDescription());
        if (activityLevel.getAuthorizationActivityList().size() ==0 ||activityLevel.getAuthorizationActivityList().get(0).getPayingOffice() == null ){
            throw new RuntimeException(customMessageSource.get("PayingOffice"));
        }
        project.setOfficeId(activityLevel.getAuthorizationActivityList().get(0).getPayingOffice().getPisOfficeCode());
        project.setIsResponded(false);
        project.setColorSchema(projectRequestPojo.getColorSchema());
        project.setIsCompleted(false);
        projectConverter.addCommittee(projectRequestPojo,project);
        return project;
    }

    private ActivityLevel validateProjectActivityRequest(ProjectRequestPojo projectRequestPojo) {
        if (projectRequestPojo.getActivityId()==null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.parameterMissing,"activityId"));
        }
        Optional<ActivityLevel> activityLevelOptional = activityLevelRepo.findById(projectRequestPojo.getActivityId());
        if (!activityLevelOptional.isPresent()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"ActivityLevel"));
        }
        if (projectRequestPojo.getStartDate().isAfter(projectRequestPojo.getEndDate())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.invalidDateAfter,"Start date","End date"));
        }
        ActivityLevel activityLevel = activityLevelOptional.orElse(new ActivityLevel());
        if (activityLevel.getIsProject()!= null && activityLevel.getIsProject()){
            throw new RuntimeException(customMessageSource.get("programactivity"));
        }
        return activityLevel;
    }

    private List<DynamicProgressStatus> convertDynamicProgressStatusToList() {
        List<DynamicProgressStatus> dynamicProgressStatuses = new ArrayList<>();

        Optional<TaskProgressStatus> taskProgressStatusOptional = getTaskProgressStatusByName( Status.TODO.getValueEnglish());
        dynamicProgressStatuses.add(setDynamicProgressStatus(1,taskProgressStatusOptional.orElse(new TaskProgressStatus())));

        Optional<TaskProgressStatus> taskProgressStatusStarted =  getTaskProgressStatusByName( Status.STARTED.getValueEnglish());
        dynamicProgressStatuses.add(setDynamicProgressStatus(2,taskProgressStatusStarted.orElse(new TaskProgressStatus())));

        Optional<TaskProgressStatus> taskProgressStatusCompleted =  getTaskProgressStatusByName( Status.COMPLETED.getValueEnglish());
        dynamicProgressStatuses.add(setDynamicProgressStatus(3,taskProgressStatusCompleted.orElse(new TaskProgressStatus())));

        return dynamicProgressStatuses;
    }

    private  Optional<TaskProgressStatus> getTaskProgressStatusByName(String name) {
        return taskProgressStatusRepository.findByStatusName(name);
    }

    private List<ProjectPhase> toProjectPhases(ProjectRequestPojo projectPojo){
        return projectPojo.getPhaseList().parallelStream().map(this::toProjectPhase).collect(Collectors.toList());
    }
    @Override
    public ProjectResponsePojo update(ProjectRequestPojo projectPojo) {
        if (projectPojo.getId() == null){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"ID"));
        }
        Project project = getProject(projectPojo.getId());
        if (!project.getCreatedBy().equals(HelperUtil.getLoginEmployeeCode())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notPermit,"update"));
        }
        project.setProjectName(projectPojo.getProjectName());
        project.setColorSchema(projectPojo.getColorSchema());
        project.setDescription(projectPojo.getDescription());
        project.setStartDate(projectPojo.getStartDate());
        project.setStartDateNp(projectPojo.getStartDateNp());
        project.setEndDate(projectPojo.getEndDate());
        project.setEndDateNp(projectPojo.getEndDateNp());
        project.setPriority(projectPojo.getPriority());
        projectRepository.save(project);
        return projectConverter.toResponse(project);
    }


    private Project getProject(Integer id) {
        Optional<Project> projectDb = projectRepository.findById(id);
        if (!projectDb.isPresent()){
           throw new RuntimeException(ErrorMessages.PROJECT_NOT_FOUND.getMessage());
        }
        return projectDb.orElse(new Project());
    }

    @Override
    public List<ProjectResponsePojo> getAllProject(String sortBy, String sortByOrder, String status, Boolean isCompleted, Boolean isActive) {
        return projectMapper.getAllProject(status.toUpperCase(),isActive,isCompleted);
    }


    @Override
    public ProjectResponsePojo getProjectById(Integer id) {
        return projectMapper.getProjectDetail(id);
    }

    @Override
    public void deleteProject(Integer id) {
        if (projectMapper.getProjectTask(id).size() != 0){
            throw new RuntimeException(customMessageSource.get(CrudMessages.projectNotComplete));
        }
        Project project = getProject(id);
        project.setActive(!project.getActive());
        projectRepository.save(project);
    }

    @Override
    public ProjectResponsePojo updateStatus(StatusDecisionPojo statusDecisionPojo) {
        Project project = getProject(statusDecisionPojo.getId());
        project.setStatus(statusDecisionPojo.getStatus().getValueEnglish());
        project.setResponseBy(statusDecisionPojo.getApprovedBy());
        project.setIsResponded(true);
        projectRepository.save(project);
        return projectConverter.toResponse(project);
    }

    @Override
    public void addProjectTaskStatus(ProjectDynamicProgressStatusRequestPojo progressStatusRequestPojo) {
        Project project = getProject(progressStatusRequestPojo.getProjectId());
        DynamicProgressStatus completedStatus = taskProgressStatusMapper.getStatusCompletedId(progressStatusRequestPojo.getProjectId());
        int newHighestStatus = 0;
        List<DynamicProgressStatus> dps = new ArrayList<>();
        for ( TaskProgressStatusWithOrderStatusPojo status: progressStatusRequestPojo.getTpsIdList()){
            DynamicProgressStatus dbDynamicProgressStatus = taskProgressStatusMapper.getDynamicTaskProgress(progressStatusRequestPojo.getProjectId(),  status.getTaskProgressStatusId());
            if (dbDynamicProgressStatus == null){
                TaskProgressStatus taskProgressStatus = getTaskProgressStatus( status.getTaskProgressStatusId());
                if (taskProgressStatus.getId() != null){
                    DynamicProgressStatus dynamicProgressStatus = setDynamicProgressStatus(status.getOrderStatus(), taskProgressStatus);
                    dynamicProgressStatus.setDeleteStatus(true);
                    dps.add(dynamicProgressStatus);
                    if (newHighestStatus < status.getOrderStatus()){
                        newHighestStatus = status.getOrderStatus();
                    }
                }else {
                    throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Progress status "));
                }
            }
        }
        if (newHighestStatus >= completedStatus.getOrderStatus()){
            taskProgressStatusMapper.updateDynamicProgressStatus(completedStatus.getId(),newHighestStatus+1);
        }
//        progressStatusRequestPojo.getTpsIdList().forEach((id,orderStatus)->{
//            DynamicProgressStatus dbDynamicProgressStatus = taskProgressStatusMapper.getDynamicTaskProgress(progressStatusRequestPojo.getProjectId(), id);
//            if (dbDynamicProgressStatus == null){
//                TaskProgressStatus taskProgressStatus = getTaskProgressStatus(id);
//                if (taskProgressStatus.getId() != null){
//                    DynamicProgressStatus dynamicProgressStatus = setDynamicProgressStatus(orderStatus, taskProgressStatus);
//                    dps.add(dynamicProgressStatus);
//                    if (newHighestStatus < orderStatus){
//                        newHighestStatus = orderStatus;
//                    }
//                }else {
//                    throw new RuntimeException(ErrorMessages.TASK_PROGRESS_STATUS_NOT_FOUND.getMessage());
//                }
//            }
//        });
        if (!dps.isEmpty()){
            if (project.getDynamicProgressStatuses() == null){
                project.setDynamicProgressStatuses(dps);
            }else {
                project.getDynamicProgressStatuses().addAll(dps);
            }
            projectRepository.save(project);
        }
    }

    private DynamicProgressStatus setDynamicProgressStatus(Integer orderStatus, TaskProgressStatus taskProgressStatus) {
        DynamicProgressStatus dynamicProgressStatus = new DynamicProgressStatus();
        dynamicProgressStatus.setTaskProgressStatus(taskProgressStatus);
        dynamicProgressStatus.setOrderStatus(orderStatus);
        return dynamicProgressStatus;
    }

    @Override
    public ProjectResponsePojo updateProjectToCompleted(Integer id) {
        Project project = getProject(id);
        if (!project.getStatus().equalsIgnoreCase(Status.APPROVED.getValueEnglish())){
            throw new RuntimeException(ErrorMessages.PROJECT_IS_NOT_APPROVED.getMessage());
        }

        if (!projectMapper.getProjectTask(id).isEmpty()){
            throw new RuntimeException(ErrorMessages.PROJECT_CANNOT_BE_COMPLETED.getMessage());
        }
        project.setIsCompleted(true);
        project.setCompletedDate(LocalDate.now());
        projectRepository.save(project);
        return projectConverter.toResponse(project);
    }

    @Override
    public ProjectResponsePojo addPhaseToProject(List<ProjectPhasePojo> projectPhasePojo) {
        Project project = getProject(projectPhasePojo.get(0).getProjectId());
        List<ProjectPhase> projectPhases;
        if (project.getProjectPhaseList() == null){
            projectPhases = new ArrayList<>();
        }else {
            projectPhases = project.getProjectPhaseList();
            projectPhasePojo.parallelStream().forEach(obj->{
                ProjectPhase projectPhase = projectPhaseMapper.getProjectIdAndPhaseId(obj.getProjectId(),obj.getPhaseId());
                if (projectPhase != null){
                    throw new RuntimeException(ErrorMessages.PHASE_ALREADY_ADDED.getMessage());
                }
            });
        }
        projectPhases.addAll(projectPhasePojo.parallelStream().map(this::toProjectPhase).collect(Collectors.toList()));
        project.setProjectPhaseList(projectPhases);
        projectRepository.save(project);
        return projectConverter.toResponse(project);
    }

    @Override
    public Long bookMarkedProject(Integer id) {
        getProject(id);
      String employeeCode = HelperUtil.getLoginEmployeeCode();
      if(bookMarkProjectRepository.existsByProjectIdAndMemberId(id,employeeCode)){
          throw new RuntimeException(ErrorMessages.PROJECT_ALREADY_BOOKED_MARKED.getMessage());
      }
        BookMarkProject bookMarkProject = new BookMarkProject();
        bookMarkProject.setProjectId(id);
        bookMarkProject.setIsBookedMarked(true);
        bookMarkProject.setMemberId(employeeCode);
        bookMarkProjectRepository.save(bookMarkProject);
        return bookMarkProject.getId();
    }

    @Override
    public MemberWiseProjectResponsePojo getMemberWiseProject(String status, Boolean isActive, Boolean isCompleted) {
       List<ProjectResponsePojo> projectResponsePojoList = projectMapper.getMemberWiseProject(status,isActive,isCompleted,tokenProcessorService.getPisCode());
       List<ProjectResponsePojo> completed = new ArrayList<>();
       List<ProjectResponsePojo> inCompleted = new ArrayList<>();

       projectResponsePojoList.parallelStream().forEach(project->{
           if (project.getIsCompleted()){
               completed.add(project);
           }else {
               inCompleted.add(project);
           }
       });
        return new MemberWiseProjectResponsePojo(completed,inCompleted);

    }

    @Override
    public List<MemberDetailsResponsePojo> getProjectMember(Integer projectId) {
        List<MemberDetailsResponsePojo> memberDetailsResponsePojoList = projectPhaseMapper.getProjectMembers(projectId);
       try {
           memberDetailsResponsePojoList = memberDetailsResponsePojoList.stream().filter(Objects::nonNull).map(member->getEmployeeName(member.getMemberId())).collect(Collectors.toList());
       }catch (Exception e){
           return memberDetailsResponsePojoList;
       }
        return memberDetailsResponsePojoList;
    }

    @Override
    public List<ProjectResponsePojo> getBookedMarkedProjects() {
        return projectMapper.getBookedMarkedProjects(HelperUtil.getLoginEmployeeCode());
    }

    @Override
    public void removeBookMark(Integer projectId) {
        bookMarkProjectRepository.deleteByProjectIdAndMemberId(projectId,HelperUtil.getLoginEmployeeCode());
    }

    @Override
    public Long removeProjectPhase(Integer projectId, Long phaseId) {
       ProjectPhase projectPhase = projectPhaseMapper.getProjectIdAndPhaseId(projectId,phaseId);
       if (!projectPhase.getCreatedBy().equalsIgnoreCase(HelperUtil.getLoginEmployeeCode())){
           throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_DELETE.getMessage());
       }
       projectPhaseRepository.deleteById(projectPhase.getId());
       return projectPhase.getId();
    }

    @Override
    public int removeTaskProgress(Integer projectId, Long taskProgressStatusId) {
        DynamicProgressStatus dynamicProgressStatus = taskProgressStatusMapper.getDynamicTaskProgress(projectId,taskProgressStatusId);
        Project project = getProject(projectId);
        if (dynamicProgressStatus.getDeleteStatus() != null && !dynamicProgressStatus.getDeleteStatus()){
            throw new RuntimeException(customMessageSource.get(CrudMessages.notPermit,"delete this status "));
        }
        if (!project.getCreatedBy().equalsIgnoreCase(HelperUtil.getLoginEmployeeCode())){
            throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_DELETE.getMessage());
        }
        dynamicProgressStatusRepository.deleteById(dynamicProgressStatus.getId());
        return dynamicProgressStatus.getId();
    }

    @Override
    public Long deactivateProjectPhase(PhaseDeactivatePojo phaseDeactivatePojo) {
        ProjectPhase projectPhase = projectPhaseRepository.findByProjectAndPhase(getProject(phaseDeactivatePojo.getProjectId()),phaseRepository.findById(phaseDeactivatePojo.getPhaseId()).orElse(new Phase()));
        if (!phaseDeactivatePojo.isActive()){
            if (projectMapper.getPhaseTask(phaseDeactivatePojo.getPhaseId(), phaseDeactivatePojo.getProjectId()).size() != 0){
                throw new RuntimeException(ErrorMessages.PHASE_CANNOT_BE_COMPLETED.getMessage());
            }
            projectPhase.setActive(false);
        }else {
            projectPhase.setActive(true);
        }
       return projectPhaseRepository.save(projectPhase).getId();
    }

    @Override
    public Page<ProjectResponsePojo> getReportProject(String status, Boolean isActive, Boolean isCompleted, int limit, int page, Boolean programActivity) {
        Page<ProjectResponsePojo> page1 = new Page<>(page,limit);

        return projectMapper.getReportProject(page1,status,isActive,isCompleted,tokenProcessorService.getPisCode(),programActivity);
    }


    private MemberDetailsResponsePojo getEmployeeName(String id){
        if (id == null){
            return new MemberDetailsResponsePojo();
        }
       EmployeeMinimalPojo employeeMinimalPojo = employeeDetailsProxy.getEmployeeDetailMinimal(id);
        MemberDetailsResponsePojo memberDetailsResponsePojo = new MemberDetailsResponsePojo();
       if (employeeMinimalPojo != null){
           memberDetailsResponsePojo.setMemberId(id);
           memberDetailsResponsePojo.setMemberNameEn(employeeMinimalPojo.getEmployeeNameEn());
           memberDetailsResponsePojo.setMemberNameNp(employeeMinimalPojo.getEmployeeNameNp());
           return memberDetailsResponsePojo;
       }
       memberDetailsResponsePojo.setMemberId(id);
        return memberDetailsResponsePojo;
    }
    private TaskProgressStatus getTaskProgressStatus(Long id){
      return taskProgressStatusRepository.findById(id).orElse(new TaskProgressStatus());
    }

    private void processDocument(List<MultipartFile> document, Project project) {
        DocumentMasterResponsePojo pojo = documentUtil.saveDocuments(
                new DocumentSavePojo().builder()
                        .pisCode(tokenProcessorService.getPisCode())
                        .officeCode(tokenProcessorService.getOfficeCode())
                        .tags(Arrays.asList("project_document"))
                        .type("1")
                        .build(),
                document
        );
        if(pojo!=null){
            project.setProjectDocumentDetails(
                    pojo.getDocuments().stream().map(
                            x-> new ProjectDocumentDetails().builder()
                                    .documentId(x.getId())
                                    .documentName(x.getName())
                                    .build()
                    ).collect(Collectors.toList())
            );
        }
    }
}
