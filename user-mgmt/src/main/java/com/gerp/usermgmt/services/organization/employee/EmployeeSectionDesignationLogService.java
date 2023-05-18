package com.gerp.usermgmt.services.organization.employee;

import com.gerp.shared.generic.api.GenericService;
import com.gerp.usermgmt.model.employee.EmployeeSectionDesignationLog;
import com.gerp.usermgmt.pojo.SectionDesignationLogResponsePojo;

import java.util.List;

public interface EmployeeSectionDesignationLogService extends GenericService<EmployeeSectionDesignationLog, Long> {
    Long save(EmployeeSectionDesignationLog employeeSectionDesignationLog);

    void deActiveOldLog(Integer id);

    Object getPrevEmployee(String pisCode, long id);

    List<String> getAllPrevEmployee(Integer id);

    List<String> getAllPrevEmployee(String pisCode, Long sectionId);
    List<SectionDesignationLogResponsePojo> getDesignationAssignLog(Integer id);
}
