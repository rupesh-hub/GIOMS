package com.gerp.usermgmt.aspects;

import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.usermgmt.annotations.SectionDesignationLogExecution;
import com.gerp.usermgmt.model.employee.EmployeeSectionDesignationLog;
import com.gerp.usermgmt.model.employee.SectionDesignation;
import com.gerp.usermgmt.pojo.organization.office.SectionDesignationPojo;
import com.gerp.usermgmt.services.organization.employee.EmployeeSectionDesignationLogService;
import com.gerp.usermgmt.services.organization.office.SectionDesignationService;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SectionDesignationLogAspect {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final EmployeeSectionDesignationLogService employeeSectionDesignationLogService;
    private final SectionDesignationService sectionDesignationService;

    private Boolean isRemoved = false;

    @Autowired
    public SectionDesignationLogAspect(EmployeeSectionDesignationLogService employeeSectionDesignationLogService, SectionDesignationService sectionDesignationService) {
        this.employeeSectionDesignationLogService = employeeSectionDesignationLogService;
        this.sectionDesignationService = sectionDesignationService;
    }


    @AfterReturning("@annotation(sectionDesignationLogExecution) && args(data))")
    public Object afterReturning(Object data, SectionDesignationLogExecution sectionDesignationLogExecution) {
        SectionDesignationPojo newData;
        SectionDesignation oldData;


        switch (sectionDesignationLogExecution.value()) {
            case ASSIGN_EMPLOYEE_SECTION_DESIGNATION:
            case UPDATE_EMPLOYEE_SECTION_DESIGNATION:
                newData = (SectionDesignationPojo) data;
                if(newData.getId() ==  null) {
                    throw new ServiceValidationException("Section Designation Config Cannot be Null");
                }
                // this query will always get the latest data because getbyId will return lates data ;

//                oldData = sectionDesignationService.findById(newData.getId());
                oldData = sectionDesignationService.findPreviousSectionDesignation(newData.getId());
                isRemoved = false;
                this.logAction(oldData, newData);
                break;
            case REMOVE_EMPLOYEE_SECTION_DESIGNATION:
                Integer id = (Integer) data;
                // this will return result with null piscode in employee
//                oldData = sectionDesignationService.findById(id);
                oldData = sectionDesignationService.findPreviousSectionDesignation(id);
                isRemoved =true;
                this.logAction(oldData, new SectionDesignationPojo());
                break;

        }
        return this;
    }

    private void logAction(SectionDesignation  oldData , SectionDesignationPojo newData) {

        String oldPisCode = oldData.getEmployee() != null ? oldData.getEmployee().getPisCode():null;
        // return if the previous and new pis are same do not log
        // in case of removed do not check new and old
        if(!isRemoved && newData.getPisCode().equals(oldPisCode)) return;
        employeeSectionDesignationLogService.deActiveOldLog(oldData.getId());
        EmployeeSectionDesignationLog el = new EmployeeSectionDesignationLog();
        el.setPrevEmployeePisCode(oldData.getEmployee() != null ? oldData.getEmployee().getPisCode():null);
        el.setNewEmployeePisCode(newData.getPisCode());
        el.setSectionDesignationId(oldData.getId());
        el.setIsLatest(Boolean.TRUE);
        el.setActive(Boolean.TRUE);
        employeeSectionDesignationLogService.save(el);

    }


}
