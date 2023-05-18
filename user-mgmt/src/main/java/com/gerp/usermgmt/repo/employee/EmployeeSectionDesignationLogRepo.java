package com.gerp.usermgmt.repo.employee;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.employee.EmployeeSectionDesignationLog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeSectionDesignationLogRepo extends GenericRepository<EmployeeSectionDesignationLog, Long> {

    @Modifying
    @Query(value = "update employee_section_designation_log set is_active = false , is_latest = false where section_designation_id = ?1", nativeQuery = true)
    void updateOldLog(Integer sectionDesignationId);

    @Query(value = " select  * from employee_section_designation_log esdl where esdl.section_designation_id = ?1 order by esdl.id  desc limit 1", nativeQuery = true)
    EmployeeSectionDesignationLog findLatestLogRecord(Integer id);

    @Query(value = "select esdl.new_employee_pis_code from employee_section_designation_log esdl where  esdl.section_designation_id = ?1 order by esdl.id desc  limit 1", nativeQuery = true)
    String findPreviousEmployeePis(Integer id);
}
