package com.gerp.tms.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.tms.mapper.ProgramMapper;
import com.gerp.tms.pojo.OfficeDetailPojo;
import com.gerp.tms.pojo.authorization.*;
import com.gerp.tms.pojo.response.ProjectAndTaskStatusPojo;
import com.gerp.tms.pojo.response.ProjectCountPojo;
import com.gerp.tms.pojo.response.ProjectResponsePojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import com.gerp.tms.repo.ActivityLevelRepo;
import com.gerp.tms.repo.ProjectRepository;
import com.gerp.tms.service.ProgramService;
import com.gerp.tms.token.TokenProcessorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Transactional
@Service
public class ProgramServiceImpl implements ProgramService {

    private final ProgramMapper programMapper;
    private final ProjectRepository projectRepository;
    private final ActivityLevelRepo activityLevelRepo;
    private final CustomMessageSource customMessageSource;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final TokenProcessorService tokenProcessorService;

    public ProgramServiceImpl(ProgramMapper programMapper, ProjectRepository projectRepository, ActivityLevelRepo activityLevelRepo, CustomMessageSource customMessageSource, EmployeeDetailsProxy employeeDetailsProxy, TokenProcessorService tokenProcessorService) {
        this.programMapper = programMapper;
        this.projectRepository = projectRepository;
        this.activityLevelRepo = activityLevelRepo;
        this.customMessageSource = customMessageSource;
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.tokenProcessorService = tokenProcessorService;
    }

//    @Override
//    public List<AuthorizationActivityPojo> getProgramsList(LocalDate fiscalYear, String activityName) {
//
//        return programMapper.getProgramList("2",activityName);
//    }

    @Override
    public Integer addToProject(ActivityTOProject activityId) {
//        Optional<ActivityLevel> activityLevelOptional = activityLevelRepo.findById(activityId.getActivityId());
//        if (!activityLevelOptional.isPresent()){
//            throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"ActivityLevel"));
//        }
//        if (activityId.getStartDateE().isAfter(activityId.getEndDateE())){
//            throw new RuntimeException(customMessageSource.get(CrudMessages.invalidDateAfter,"Start date","End date"));
//        }
//        ActivityLevel activityLevel = activityLevelOptional.orElse(new ActivityLevel());
//        activityLevel.setAuthorizationActivityList(activityLevel.getAuthorizationActivityList().parallelStream().peek(a-> a.setIsProject(true)).collect(Collectors.toList()));
//        activityLevelRepo.save(activityLevel);
//        Project project = new Project();
//        project.setProjectName(activityLevel.getActivityLevelNameE());
//        project.setCode(RandomGeneratorUtil.getAlphaNumericString(10));
//        project.setActivityId(activityId.getActivityId());
//        project.setPriority(activityId.getPriority());
//        project.setStartDate(activityId.getStartDateE());
//        project.setStartDateNp(activityId.getStartDateN());
//        project.setEndDateNp(activityId.getEndDateN());
//        project.setEndDate(activityId.getEndDateE());
//        project.setColorSchema(activityId.getColor());
//        projectRepository.save(project);
//        return project.getId();
        return null;
    }

    @Override
    public Page<ActivityLevelPojo> getActivties(String fiscalYear, String activityName, String sortByOrder, String sortBy, String filter, int page, int limit, String accountCode, String filterByHead) {
        Page<ActivityLevelPojo> pagination = new Page<>(page, limit);
        String officeCode = tokenProcessorService.getOfficeCode();
        pagination = programMapper.getActivites(pagination,fiscalYear,activityName,sortByOrder,sortBy,officeCode,accountCode,filter,filterByHead);
        return pagination;
    }

    private HeaderOfficeDetailsPojo getOfficeDetails(String id, HeaderOfficeDetailsPojo activityLevelPojo) {
        if (id !=null){
            OfficeDetailPojo officeDetailPojo = employeeDetailsProxy.getOfficeDetail(id);
            if (officeDetailPojo != null){
                activityLevelPojo.setOfficeNameEn(officeDetailPojo.getNameEn());
                activityLevelPojo.setOfficeNameNp(officeDetailPojo.getNameNp());
            }
        }
        return activityLevelPojo;
    }

    private HeaderOfficeDetailsPojo getMinistryDetails(String id, HeaderOfficeDetailsPojo activityLevelPojo) {
        if (id != null){
            OfficeDetailPojo officeDetailPojo = employeeDetailsProxy.getOfficeDetailParentOffice(id);
            if (officeDetailPojo != null){
               officeDetailPojo= findMinistry(officeDetailPojo);
                activityLevelPojo.setMinistryEn(officeDetailPojo.getNameEn());
                activityLevelPojo.setMinistryNp(officeDetailPojo.getNameNp());
            }
        }
        return activityLevelPojo;
    }

    private OfficeDetailPojo findMinistry(OfficeDetailPojo officeDetailPojo){
        OfficeDetailPojo officeDetailPojo1 = new OfficeDetailPojo();
        if (officeDetailPojo.getNameEn().toUpperCase().contains("MINISTRY") && officeDetailPojo.getNameNp().contains("मन्त्रालय")){
            officeDetailPojo1=officeDetailPojo;
            return officeDetailPojo1;
        }
        if (officeDetailPojo.getParentOffice() !=null ){
            return findMinistry(officeDetailPojo.getParentOffice());
        }
        return officeDetailPojo1;
    }

    @Override
    public Integer addData(Temp activityTOProject) {
       return null;

    }

    @Override
    public ActivityLevelPojo getActivtiesById(Integer id) {
        return programMapper.getActivityById(id);
    }

    @Override
    public HeaderOfficeDetailsPojo getHeadingDetails() {
        String officeCode = tokenProcessorService.getOfficeCode();
        HeaderOfficeDetailsPojo headerOfficeDetailsPojo = new HeaderOfficeDetailsPojo();
        List<AccountPojo> accountPojos = programMapper.getHeadingDetails(officeCode);
        headerOfficeDetailsPojo.setAccountPojoList(accountPojos);
        getOfficeDetails(officeCode,headerOfficeDetailsPojo);
        getMinistryDetails(officeCode,headerOfficeDetailsPojo);
        return headerOfficeDetailsPojo;
    }

    @Override
    public ProjectCountPojo getProjectCountForDashboard(String fiscalYear, String responsibleUnit, String months) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        AtomicInteger completedCount = new AtomicInteger();
        AtomicInteger progressCount = new AtomicInteger();
        AtomicInteger todoCount = new AtomicInteger();
        FiscalYearPojo fiscalYearDetails = employeeDetailsProxy.getFiscalYear(fiscalYear);
        if (fiscalYearDetails == null){
            return new ProjectCountPojo();
        }
        if (months != null){
            List<String> strings = convertNepaliMonthToEng(months);
            startDate = LocalDate.parse(fiscalYearDetails.getStartDate().getYear()+ strings.get(0)).atStartOfDay();
            endDate = LocalDate.parse(fiscalYearDetails.getEndDate().getYear()+ strings.get(1)).atStartOfDay();
        }
        List<ProjectAndTaskStatusPojo> projectByFiscalYear = programMapper.findProjectByFiscalYear(fiscalYearDetails.getCode(), responsibleUnit, startDate,endDate);
        projectByFiscalYear.forEach(obj->{
            if (obj.getStatus().parallelStream().allMatch(obj1->obj1.equalsIgnoreCase("completed"))){
                completedCount.getAndIncrement();
            } else if (obj.getStatus().parallelStream().allMatch(obj1->obj1.equalsIgnoreCase("todo"))){
                todoCount.getAndIncrement();
            }else {
                progressCount.getAndIncrement();
            }
        });
        return new ProjectCountPojo(completedCount.get(),todoCount.get(),progressCount.get());
    }

    @Override
    public Page<ProjectResponsePojo> getProjectList(String fiscalYear, int limit, int page) {
        Page<ProjectResponsePojo> responsePojoPage = new Page<>(page,limit);
        FiscalYearPojo fiscalYearDetails = employeeDetailsProxy.getFiscalYear(fiscalYear);
        if (fiscalYearDetails == null){
            return new Page<>();
        }
        Page<ProjectResponsePojo> projectList = programMapper.getProjectList(responsePojoPage, fiscalYearDetails.getCode());
        projectList.getRecords().forEach(obj->{
           if (!obj.getIsCompleted()){
              if (obj.getTaskStatus().size()==0){
                  obj.setStatus("Not Started");
              }else {
                  if ( obj.getTaskStatus().parallelStream().allMatch(obj1->obj1.equalsIgnoreCase("todo"))){
                      obj.setStatus("Not Started");
                  }else {
                      obj.setStatus("In Progress");
                  }
              }
           }else {
               obj.setStatus("Completed");
           }
           obj.setTaskStatus(null);
           obj.setIsCompleted(null);
        });
        return projectList;
    }

    private List<String> convertNepaliMonthToEng(String month){
        List<String> list = new ArrayList<>();
        switch (month){
            case "BAISAKH":
                list.add("-04-15");
                list.add("-05-15");
                  break;
            case "JESTHA":
                list.add("-05-15");
                list.add("-06-15");
                  break;
            case "ASAR":
                list.add("-06-15");
                list.add("-07-15");
                  break;
            case "SHRAWAN":
                list.add("-07-15");
                list.add("-08-15");
                  break;
            case "BHADAU":
                list.add("-08-15");
                list.add("-09-15");
                  break;
            case "ASWIN":
                list.add("-09-15");
                list.add("-10-15");
                  break;
            case "KARTIK":
                list.add("-10-15");
                list.add("-11-15");
                  break;
            case "MANSIR":
                list.add("-11-15");
                list.add("-12-15");
                  break;
            case "POUSH":
                list.add("-12-15");
                list.add("-01-15");
                  break;
            case "MAGH":
                list.add("-01-15");
                list.add("-02-15");
                  break;
            case "FALGUN":
                list.add("-02-15");
                list.add("-03-15");
                break;
            case "CHAITRA":
                list.add("-03-15");
                list.add("-04-15");
                break;
        }
        return list;
    }
}
