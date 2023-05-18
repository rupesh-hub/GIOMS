package com.gerp.usermgmt.services.organization.employee.impl;

import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.shared.generic.service.GenericServiceImpl;
import com.gerp.usermgmt.mapper.organization.SectionDesignationLogMapper;
import com.gerp.usermgmt.mapper.organization.SectionDesignationMapper;
import com.gerp.usermgmt.model.employee.EmployeeSectionDesignationLog;
import com.gerp.usermgmt.pojo.SectionDesignationLogResponsePojo;
import com.gerp.usermgmt.repo.employee.EmployeeSectionDesignationLogRepo;
import com.gerp.usermgmt.services.organization.employee.EmployeeSectionDesignationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceDesignationLogServiceImpl extends GenericServiceImpl<EmployeeSectionDesignationLog, Long> implements EmployeeSectionDesignationLogService {
    private final EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo;
    private final SectionDesignationLogMapper sectionDesignationLogMapper;
    private final SectionDesignationMapper sectionDesignationMapper;

    public EmployeeServiceDesignationLogServiceImpl(EmployeeSectionDesignationLogRepo employeeSectionDesignationLogRepo, SectionDesignationLogMapper sectionDesignationLogMapper, SectionDesignationMapper sectionDesignationMapper) {
        super(employeeSectionDesignationLogRepo);
        this.employeeSectionDesignationLogRepo = employeeSectionDesignationLogRepo;
        this.sectionDesignationLogMapper = sectionDesignationLogMapper;
        this.sectionDesignationMapper = sectionDesignationMapper;
    }

    @Override
    public Long save(EmployeeSectionDesignationLog employeeSectionDesignationLog) {
        return employeeSectionDesignationLogRepo.save(employeeSectionDesignationLog).getId();
    }

    @Override
    public void deActiveOldLog(Integer id) {
         employeeSectionDesignationLogRepo.updateOldLog(id);
    }

    @Override
    public Object getPrevEmployee(String pisCode, long id) {
        return sectionDesignationLogMapper.getPrevEmployeeLog(pisCode, id);
    }

    @Override
    public List<String> getAllPrevEmployee(Integer id) {
        return sectionDesignationLogMapper.getAllPrevEmployeePisCode(id);
    }

    @Override
    public List<String> getAllPrevEmployee(String pisCode, Long sectionId) {
        Integer sectionDesignationId = sectionDesignationMapper.getSectionDesignationIdByPisCodeAndSectionId(pisCode, sectionId);
        if(sectionDesignationId == null){
            throw new
                    ServiceValidationException("Section Designation Setup Not Found");
        }
        return sectionDesignationLogMapper.getAllPrevEmployeePisCode(sectionDesignationId);
    }

    @Override
    public List<SectionDesignationLogResponsePojo> getDesignationAssignLog(Integer sectionDesignationId) {
        return sectionDesignationLogMapper.getSectionDesignationAllDetailById(sectionDesignationId);
    }
}
