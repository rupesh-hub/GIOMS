package com.gerp.usermgmt.services.organization.jobdetail.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.mapper.organization.EmployeeJobDetailMapper;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.model.employee.EmployeeJobDetailLog;
import com.gerp.usermgmt.pojo.organization.employee.EmployeePromotionPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.DesignationDetailPojo;
import com.gerp.usermgmt.pojo.organization.jobdetail.JobDetailPojo;
import com.gerp.usermgmt.pojo.organization.office.ServicePojo;
import com.gerp.usermgmt.repo.employee.EmployeeJobDetailLogRepository;
import com.gerp.usermgmt.services.organization.employee.ServiceGroupService;
import com.gerp.usermgmt.services.organization.jobdetail.EmployeeJobDetailService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeJobDetailLogImpl extends GenericServiceImpl<EmployeeJobDetailLog, Long> implements EmployeeJobDetailService {
    private final EmployeeJobDetailLogRepository employeeJobDetailLogRepository;
    private final EmployeeJobDetailMapper employeeJobDetailMapper;
    private final TokenProcessorService tokenProcessorService;
    private final ServiceGroupService serviceGroupService;

    public EmployeeJobDetailLogImpl(EmployeeJobDetailLogRepository employeeJobDetailLogRepository, EmployeeJobDetailMapper employeeJobDetailMapper, TokenProcessorService tokenProcessorService, ServiceGroupService serviceGroupService) {
        super(employeeJobDetailLogRepository);
        this.employeeJobDetailLogRepository = employeeJobDetailLogRepository;
        this.employeeJobDetailMapper = employeeJobDetailMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.serviceGroupService = serviceGroupService;
    }

    @Override
    public EmployeeJobDetailLog getPrevActiveLog(String pisCode) {
        return employeeJobDetailLogRepository.getPrevActiveLog(pisCode);
    }

    @Override
    public Page<EmployeePromotionPojo> getPaginatedFilteredData(GetRowsRequest paginatedRequest) {
        Page<EmployeePromotionPojo> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());

        page =  employeeJobDetailMapper.searchCurrentPromotionLog(page  ,paginatedRequest.getSearchField(), tokenProcessorService.getOfficeCode());
        return page;
    }

    @Override
    public List<Map<String, Object>> getDesignationHistory(String pisCode, String officeCode) {
        return employeeJobDetailLogRepository.getDesignationHistory(pisCode,officeCode);
    }

    @Override
    public JobDetailPojo getDesignationHistoryByPisCode(String pisCode) {
        List<DesignationDetailPojo> designationDetailPojos = employeeJobDetailMapper.findAllOfficeHistory(pisCode);
        if(designationDetailPojos == null || designationDetailPojos.size()==0) return null;

        designationDetailPojos = designationDetailPojos.stream().map(designationDetailPojo -> {
            getStartDateAndEndDateOfService(designationDetailPojo,pisCode);
            return designationDetailPojo;
        }).collect(Collectors.toList());
        return new JobDetailPojo(pisCode, designationDetailPojos);
    }

//    private JobDetailPojo getGroupAndSubGroup(JobDetailPojo jobDetailPojo){
//         List<DesignationDetailPojo>  designationDetailPojos = jobDetailPojo.getDesignationDetailPojos();
//        for (int i = 0; i < designationDetailPojos.size(); i++) {
//            Map<String, Object> map = serviceGroupService.findServiceIdWithHierarchy(designationDetailPojos.get(i).getService().getCode());
//            designationDetailPojos.get(i).setService((ServicePojo) map.get(StringConstants.SERVICE));
//            designationDetailPojos.get(i).setGroup((ServicePojo) map.get(StringConstants.GROUP));
//            designationDetailPojos.get(i).setSubGroup((ServicePojo) map.get(StringConstants.SUBGROUP));
//        }
//
//        return jobDetailPojo;
//    }

    private void getStartDateAndEndDateOfService(DesignationDetailPojo designationDetailPojo,String pisCode){
        LocalDate startDate = employeeJobDetailMapper.getStartDate(designationDetailPojo.getOfficeCode(), pisCode);
        LocalDate endDate =  employeeJobDetailMapper.getEndDate(designationDetailPojo.getOfficeCode(), pisCode);
        // todo: find the job status
        designationDetailPojo.setStartDate(startDate);
        designationDetailPojo.setEndDate(endDate);
        DesignationDetailPojo designationDetailPojo1 = employeeJobDetailMapper.getServiceDetails(designationDetailPojo.getOfficeCode(), pisCode);
        Map<String, Object> map = serviceGroupService.findServiceIdWithHierarchy(designationDetailPojo1.getNewService().getCode());
        designationDetailPojo.setNewService((ServicePojo) map.get(StringConstants.SERVICE));
        designationDetailPojo.setNewGroup((ServicePojo) map.get(StringConstants.GROUP));
        designationDetailPojo.setNewSubGroup((ServicePojo) map.get(StringConstants.SUBGROUP));
        designationDetailPojo.setJobStatus(designationDetailPojo1.getJobStatus());
        designationDetailPojo.setJobStatusNp(designationDetailPojo1.getJobStatusNp());
        designationDetailPojo.setNewPosition(designationDetailPojo1.getNewPosition());
        designationDetailPojo.setNewPositionNp(designationDetailPojo1.getNewPositionNp());
        designationDetailPojo.setNewDesignation(designationDetailPojo1.getNewDesignation());
        designationDetailPojo.setNewDesignationNp(designationDetailPojo1.getNewDesignationNp());

    }
}
