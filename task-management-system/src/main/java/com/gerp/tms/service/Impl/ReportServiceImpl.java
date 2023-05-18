package com.gerp.tms.service.Impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.Status;
import com.gerp.tms.mapper.ReportMapper;
import com.gerp.tms.model.report.MonthlyRemarks;
import com.gerp.tms.model.report.Months;
import com.gerp.tms.model.report.ProgressReportDetail;
import com.gerp.tms.pojo.MonthsPojo;
import com.gerp.tms.pojo.OfficeDetailPojo;
import com.gerp.tms.pojo.ProgressUpdatePojo;
import com.gerp.tms.pojo.request.DecisionByOfficeHeadPojo;
import com.gerp.tms.pojo.response.ApprovalReportDetailPojo;
import com.gerp.tms.pojo.response.MonitoringReportsPojo;
import com.gerp.tms.pojo.response.ReportProjectTaskDetailsPojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import com.gerp.tms.repo.MonthRepo;
import com.gerp.tms.repo.ProgressReportDetailRepo;
import com.gerp.tms.service.ReportService;
import com.gerp.tms.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportMapper reportMapper;
    private final TokenProcessorService tokenProcessorService;
    private final ProgressReportDetailRepo progressReportDetailRepo;
    private final MonthRepo monthRepo;
    private final CustomMessageSource customMessageSource;
    private final EmployeeDetailsProxy employeeDetailsProxy;

    public ReportServiceImpl(ReportMapper reportMapper, TokenProcessorService tokenProcessorService, ProgressReportDetailRepo progressReportDetailRepo, MonthRepo monthRepo, CustomMessageSource customMessageSource, EmployeeDetailsProxy employeeDetailsProxy) {
        this.reportMapper = reportMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.progressReportDetailRepo = progressReportDetailRepo;
        this.monthRepo = monthRepo;
        this.customMessageSource = customMessageSource;
        this.employeeDetailsProxy = employeeDetailsProxy;
    }

    @Override
    public ReportProjectTaskDetailsPojo getProjectReport(String projectName, LocalDate startDate, LocalDate endDate, int limit, int page, Long taskId) {
        return reportMapper.getProjectTaskDetail(projectName,startDate,endDate,tokenProcessorService.getPisCode(),taskId);
    }

    @Override
    public Long addProgressMonthly(ProgressUpdatePojo dto) {
        ProgressReportDetail progressReportDetail = progressReportDetailRepo.findByTaskIdAndCreatedBy(dto.getTaskId(),Long.parseLong(tokenProcessorService.getPisCode()));
        List<MonthsPojo> monthsPojos = new ArrayList<>();
        if (progressReportDetail != null){
            progressReportDetail.getMonthlyRemarksList().parallelStream().forEach(obj->{
               dto.getProgress().parallelStream().forEach(obj1->{
                   if (obj.getMonths().getNameEn().equalsIgnoreCase(obj1.getMonthsName())){
                       obj.setStatus(obj1.getStatus());
                       obj.setRemarks(obj1.getRemarks());
                       obj.setBudget(obj1.getUsedBudget());
                       monthsPojos.add(obj1);
                   }

               });
            });
        }

        if (monthsPojos.size()>0){
            List<MonthsPojo> pojos = dto.getProgress().parallelStream().filter(obj -> !monthsPojos.contains(obj)).collect(Collectors.toList());
            dto.setProgress(pojos);
        }

        List<MonthlyRemarks> remarksList = dto.getProgress().stream().map(data -> {
            Months months = monthRepo.findByNameEn(data.getMonthsName().toUpperCase());
            if (months == null) {
                throw new RuntimeException(customMessageSource.get("failure.save"));
            }
            MonthlyRemarks monthlyRemarks = new MonthlyRemarks();
            monthlyRemarks.setRemarks(data.getRemarks());
            monthlyRemarks.setStatus(data.getStatus());
            monthlyRemarks.setMonths(months);
            return monthlyRemarks;
        }).collect(Collectors.toList());
        if (progressReportDetail == null) {
            progressReportDetail = new ProgressReportDetail();
//        progressReportDetail.setTaskStatus(dto.getRecordDetails());
            progressReportDetail.setBudget(dto.getTotalBudget());
            progressReportDetail.setTaskId(dto.getTaskId());
            progressReportDetail.setRemarks(dto.getRemarks());
            progressReportDetail.setMonthlyRemarksList(remarksList);
        }else if (remarksList.size() >0){
            progressReportDetail.getMonthlyRemarksList().addAll(remarksList);
        }
        progressReportDetailRepo.save(progressReportDetail);
        return progressReportDetail.getTaskId();
    }

    @Override
    public List<MonitoringReportsPojo> getMonitoring(String officeCode) {
        return reportMapper.getMonitoring(officeCode);
    }

    @Override
    public Page<ApprovalReportDetailPojo> getApprovalReport(String status, int limit, int page) {
        Page<ApprovalReportDetailPojo> pagination = new Page<>(page,limit);
        pagination = reportMapper.getToBeApprovalTask(pagination,status,tokenProcessorService.getPisCode());
        pagination.getRecords().parallelStream().filter(Objects::nonNull).forEach(obj->{
            obj.setEmployee(getEmployeeDetails(obj.getEmpPiscode()));
        });
        return pagination;
    }

    private EmployeeMinimalPojo getEmployeeDetails(String id){
        if (id == null){
            return new EmployeeMinimalPojo();
        }
        return employeeDetailsProxy.getEmployeeDetailMinimal(id);
    }
    @Override
    public MonitoringReportsPojo getApprovalReportDetails(Long id) {
        MonitoringReportsPojo monitoringReportsPojo =  reportMapper.getApprovalDetails(id);
        monitoringReportsPojo.getProgressReportDetails().parallelStream().forEach(obj->{
           obj.setEmployee(getEmployeeDetails(obj.getEmployeePisCode()));
        });
        return monitoringReportsPojo;
    }

    @Override
    public Long submitTaskProgressMonthly(Long taskId) {
       ProgressReportDetail progressReportDetail = progressReportDetailRepo.findByTaskIdAndCreatedBy(taskId,Long.parseLong(tokenProcessorService.getPisCode()));
       if (progressReportDetail == null){
           throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Progress"));
       }
       progressReportDetail.setApprovalStatus(Status.PENDING.getValueEnglish());
        EmployeeMinimalPojo employeeMinimalPojo = employeeDetailsProxy.getOfficeHead(tokenProcessorService.getOfficeCode());
        progressReportDetail.setApproverCode(employeeMinimalPojo.getPisCode());
       progressReportDetailRepo.save(progressReportDetail);
        return taskId;
    }

    @Override
    public int decisionByOfficeHead(List<DecisionByOfficeHeadPojo> decisionByOfficeHeadPojos) {
        decisionByOfficeHeadPojos.forEach(obj->{
            Optional<ProgressReportDetail> progressReportDetailOptional = progressReportDetailRepo.findById(obj.getId());
            if (progressReportDetailOptional.isPresent()){
                ProgressReportDetail progressReportDetail = progressReportDetailOptional.orElse(new ProgressReportDetail());
                if (obj.getStatus()){
                    progressReportDetail.setApprovalStatus(Status.APPROVED.getValueEnglish());
                }else {
                    progressReportDetail.setApprovalStatus(Status.REJECTED.getValueEnglish());
                }
                progressReportDetail.setRemarksByApprover(obj.getRemarks());
                progressReportDetailRepo.save(progressReportDetail);
            }
        });
        return decisionByOfficeHeadPojos.get(0).getId();
    }

    @Override
    public void deleteTaskProgressMonthly(int progressId, String month) {
        Optional<ProgressReportDetail> progressReportDetailLOptional = progressReportDetailRepo.findById(progressId);
       if ( !progressReportDetailLOptional.isPresent()){
           throw new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Task Progress Status"));
       }
       ProgressReportDetail progressReportDetail = progressReportDetailLOptional.orElse(new ProgressReportDetail());
       progressReportDetail.setMonthlyRemarksList(progressReportDetail.getMonthlyRemarksList().parallelStream().filter(obj->!obj.getMonths().getNameEn().equalsIgnoreCase(month)).collect(Collectors.toList()));
       progressReportDetailRepo.save(progressReportDetail);
    }
}
